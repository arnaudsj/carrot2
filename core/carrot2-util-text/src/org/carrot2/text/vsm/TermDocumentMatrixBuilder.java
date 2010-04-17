/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.vsm;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mahout.math.GenericPermuting;
import org.apache.mahout.math.matrix.DoubleFactory2D;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.matrix.impl.SparseDoubleMatrix2D;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.matrix.MatrixUtils;
import org.carrot2.matrix.NNIDoubleFactory2D;
import org.carrot2.text.analysis.TokenTypeUtils;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.PcjCompat;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.constraint.IntRange;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.sorting.IndirectComparator;
import com.carrotsearch.hppc.sorting.IndirectSort;

/**
 * Builds a term document matrix based on the provided {@link PreprocessingContext}.
 */
@Bindable(prefix = "TermDocumentMatrixBuilder")
public class TermDocumentMatrixBuilder
{
    /**
     * Title word boost. Gives more weight to words that appeared in
     * {@link Document#TITLE} fields.
     * 
     * @level Medium
     * @group Labels
     * @label Title word boost
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0, max = 10)
    public double titleWordsBoost = 2.0;

    /**
     * Maximum matrix size. The maximum number of the term-document matrix elements. The
     * larger the size, the more accurate, time- and memory-consuming clustering.
     * 
     * @level Medium
     * @group Matrix model
     * @label Maximum matrix size
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 50 * 100)
    @Internal(configuration = true)
    public int maximumMatrixSize = 250 * 150;

    /**
     * Maximum word document frequency. The maximum document frequency allowed for words
     * as a fraction of all documents. Words with document frequency larger than
     * <code>maxWordDf</code> will be ignored. For example, when <code>maxWordDf</code> is
     * <code>0.4</code>, words appearing in more than 40% of documents will be be ignored.
     * The default value of <code>1.0</code> means that all words will be taken into
     * account, no matter in how many documents they appear.
     * <p>
     * This attribute may be useful when certain words appear in most of the input
     * documents (e.g. company name from header or footer) and such words dominate the
     * cluster labels. In such case, setting <code>maxWordDf</code> to a value lower than
     * <code>1.0</code>, e.g. <code>0.9</code> may improve the clusters. 
     * </p>
     * <p>
     * Another useful application of this attribute is when there is a need to generate
     * only very specific clusters, i.e. clusters containing small numbers of documents.
     * This can be achieved by setting <code>maxWordDf</code> to extremely low values,
     * e.g. <code>0.1</code> or <code>0.05</code>.
     * </p>
     * 
     * @level Advanced
     * @group Matrix model
     * @label Maximum word document frequency
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.00, max = 1.0)
    public double maxWordDf = 1.0;

    /**
     * Term weighting. The method for calculating weight of words in the term-document
     * matrices.
     * 
     * @level Advanced
     * @group Matrix model
     * @label Term weighting
     */
    @Input
    @Processing
    @Attribute
    @Required
    @ImplementingClasses(classes =
    {
        LogTfIdfTermWeighting.class, LinearTfIdfTermWeighting.class,
        TfTermWeighting.class
    }, strict = false)
    public ITermWeighting termWeighting = new LogTfIdfTermWeighting();

    /**
     * Builds a term document matrix from data provided in the <code>context</code>,
     * stores the result in there.
     */
    public void buildTermDocumentMatrix(VectorSpaceModelContext vsmContext)
    {
        final PreprocessingContext preprocessingContext = vsmContext.preprocessingContext;

        final int documentCount = preprocessingContext.documents.size();
        final int [] stemsTf = preprocessingContext.allStems.tf;
        final int [][] stemsTfByDocument = preprocessingContext.allStems.tfByDocument;
        final byte [] stemsFieldIndices = preprocessingContext.allStems.fieldIndices;

        if (documentCount == 0)
        {
            vsmContext.termDocumentMatrix = NNIDoubleFactory2D.nni.make(0, 0);
            vsmContext.stemToRowIndex = new IntIntOpenHashMap();
            return;
        }

        // Determine the index of the title field
        int titleFieldIndex = -1;
        final String [] fieldsName = preprocessingContext.allFields.name;
        for (int i = 0; i < fieldsName.length; i++)
        {
            if (Document.TITLE.equals(fieldsName[i]))
            {
                titleFieldIndex = i;
                break;
            }
        }

        // Determine the stems we, ideally, should include in the matrix
        int [] stemsToInclude = computeRequiredStemIndices(preprocessingContext);

        // Sort stems by weight, so that stems get included in the matrix in the order
        // of frequency
        final double [] stemsWeight = new double [stemsToInclude.length];
        for (int i = 0; i < stemsToInclude.length; i++)
        {
            final int stemIndex = stemsToInclude[i];
            stemsWeight[i] = termWeighting.calculateTermWeight(stemsTf[stemIndex],
                stemsTfByDocument[stemIndex].length / 2, documentCount)
                * getWeightBoost(titleFieldIndex, stemsFieldIndices[stemIndex]);
        }
        final int [] stemWeightOrder = IndirectSort.sort(0, stemsWeight.length,
            new IndirectComparator.DescendingDoubleComparator(stemsWeight));

        // Calculate the number of terms we can include to fulfill the max matrix size
        final int maxRows = maximumMatrixSize / documentCount;
        final DoubleMatrix2D tdMatrix = NNIDoubleFactory2D.nni.make(Math.min(maxRows,
            stemsToInclude.length), documentCount);

        for (int i = 0; i < stemWeightOrder.length && i < maxRows; i++)
        {
            final int stemIndex = stemsToInclude[stemWeightOrder[i]];
            final int [] tfByDocument = stemsTfByDocument[stemIndex];
            final int df = tfByDocument.length / 2;
            final byte fieldIndices = stemsFieldIndices[stemIndex];

            int tfByDocumentIndex = 0;
            for (int documentIndex = 0; documentIndex < documentCount; documentIndex++)
            {
                if (tfByDocumentIndex * 2 < tfByDocument.length
                    && tfByDocument[tfByDocumentIndex * 2] == documentIndex)
                {
                    double weight = termWeighting.calculateTermWeight(
                        tfByDocument[tfByDocumentIndex * 2 + 1], df, documentCount);

                    weight *= getWeightBoost(titleFieldIndex, fieldIndices);
                    tfByDocumentIndex++;

                    tdMatrix.set(i, documentIndex, weight);
                }
            }
        }

        // Convert stemsToInclude into tdMatrixStemIndices
        GenericPermuting.permute(stemsToInclude, stemWeightOrder);
        stemsToInclude = ArrayUtils.subarray(stemsToInclude, 0, tdMatrix.rows());

        final IntIntOpenHashMap stemToRowIndex = new IntIntOpenHashMap();
        for (int i = 0; i < stemsToInclude.length; i++)
        {
            stemToRowIndex.put(stemsToInclude[i], i);
        }

        // Store the results
        vsmContext.termDocumentMatrix = tdMatrix;
        vsmContext.stemToRowIndex = stemToRowIndex;
    }

    /**
     * Builds a term-phrase matrix in the same space as the main term-document matrix. If
     * the processing context contains no phrases,
     * {@link VectorSpaceModelContext#termPhraseMatrix} will remain <code>null</code>.
     */
    public void buildTermPhraseMatrix(VectorSpaceModelContext context)
    {
        final PreprocessingContext preprocessingContext = context.preprocessingContext;
        final IntIntOpenHashMap stemToRowIndex = context.stemToRowIndex;
        final int [] labelsFeatureIndex = preprocessingContext.allLabels.featureIndex;
        final int firstPhraseIndex = preprocessingContext.allLabels.firstPhraseIndex;

        if (firstPhraseIndex >= 0 && stemToRowIndex.size() > 0)
        {
            // Build phrase matrix
            int [] phraseFeatureIndices = new int [labelsFeatureIndex.length
                - firstPhraseIndex];
            for (int featureIndex = 0; featureIndex < phraseFeatureIndices.length; featureIndex++)
            {
                phraseFeatureIndices[featureIndex] = labelsFeatureIndex[featureIndex
                    + firstPhraseIndex];
            }

            final DoubleMatrix2D phraseMatrix = TermDocumentMatrixBuilder
                .buildAlignedMatrix(context, phraseFeatureIndices, termWeighting);
            MatrixUtils.normalizeColumnL2(phraseMatrix, null);
            context.termPhraseMatrix = phraseMatrix.viewDice();
        }
    }

    /**
     * Calculates the boost we should apply to a stem, based on the field indices array.
     */
    private double getWeightBoost(int titleFieldIndex, final byte fieldIndices)
    {
        if ((fieldIndices & (1 << titleFieldIndex)) != 0)
        {
            return titleWordsBoost;
        }

        return 1;
    }

    /**
     * Computes stem indices of words that are one-word label candidates or are non-stop
     * words from phrase label candidates.
     */
    private int [] computeRequiredStemIndices(PreprocessingContext context)
    {
        final int [] labelsFeatureIndex = context.allLabels.featureIndex;
        final int [] wordsStemIndex = context.allWords.stemIndex;
        final short [] wordsTypes = context.allWords.type;
        final int [][] phrasesWordIndices = context.allPhrases.wordIndices;
        final int wordCount = wordsStemIndex.length;

        final int [][] stemsTfByDocument = context.allStems.tfByDocument;
        int documentCount = context.documents.size();
        final BitSet requiredStemIndices = new BitSet(labelsFeatureIndex.length);

        for (int i = 0; i < labelsFeatureIndex.length; i++)
        {
            final int featureIndex = labelsFeatureIndex[i];
            if (featureIndex < wordCount)
            {
                addStemIndex(wordsStemIndex, documentCount, stemsTfByDocument,
                    requiredStemIndices, featureIndex);
            }
            else
            {
                final int [] wordIndices = phrasesWordIndices[featureIndex - wordCount];
                for (int j = 0; j < wordIndices.length; j++)
                {
                    final int wordIndex = wordIndices[j];
                    if (!TokenTypeUtils.isCommon(wordsTypes[wordIndex]))
                    {
                        addStemIndex(wordsStemIndex, documentCount, stemsTfByDocument,
                            requiredStemIndices, wordIndex);
                    }
                }
            }
        }

        return PcjCompat.toIntArray(requiredStemIndices);
    }

    /**
     * Adds stem index to the set with a check on the stem's document frequency.
     */
    private void addStemIndex(final int [] wordsStemIndex, int documentCount,
        int [][] stemsTfByDocument, final BitSet requiredStemIndices,
        final int featureIndex)
    {
        final int stemIndex = wordsStemIndex[featureIndex];
        final int df = stemsTfByDocument[stemIndex].length / 2;
        if (((double) df / documentCount) <= maxWordDf)
        {
            requiredStemIndices.set(stemIndex);
        }
    }

    /**
     * Builds a sparse term-document-like matrix for the provided matrixWordIndices in the
     * same term space as the original term-document matrix.
     */
    static DoubleMatrix2D buildAlignedMatrix(VectorSpaceModelContext vsmContext,
        int [] featureIndex, ITermWeighting termWeighting)
    {
        final IntIntOpenHashMap stemToRowIndex = vsmContext.stemToRowIndex;
        if (featureIndex.length == 0)
        {
            return DoubleFactory2D.dense.make(stemToRowIndex.size(), 0);
        }

        final DoubleMatrix2D phraseMatrix = new SparseDoubleMatrix2D(stemToRowIndex
            .size(), featureIndex.length);

        final PreprocessingContext preprocessingContext = vsmContext.preprocessingContext;
        final int [] wordsStemIndex = preprocessingContext.allWords.stemIndex;
        final int [] stemsTf = preprocessingContext.allStems.tf;
        final int [][] stemsTfByDocument = preprocessingContext.allStems.tfByDocument;
        final int [][] phrasesWordIndices = preprocessingContext.allPhrases.wordIndices;
        final int documentCount = preprocessingContext.documents.size();
        final int wordCount = wordsStemIndex.length;

        for (int i = 0; i < featureIndex.length; i++)
        {
            final int feature = featureIndex[i];
            final int [] wordIndices;
            if (feature < wordCount)
            {
                wordIndices = new int []
                {
                    feature
                };
            }
            else
            {
                wordIndices = phrasesWordIndices[feature - wordCount];
            }

            for (int wordIndex = 0; wordIndex < wordIndices.length; wordIndex++)
            {
                final int stemIndex = wordsStemIndex[wordIndices[wordIndex]];
                if (stemToRowIndex.containsKey(stemIndex))
                {
                    final int rowIndex = stemToRowIndex.lget();

                    double weight = termWeighting.calculateTermWeight(stemsTf[stemIndex],
                        stemsTfByDocument[stemIndex].length / 2, documentCount);

                    phraseMatrix.setQuick(rowIndex, i, weight);
                }
            }
        }

        return phraseMatrix;
    }
}
