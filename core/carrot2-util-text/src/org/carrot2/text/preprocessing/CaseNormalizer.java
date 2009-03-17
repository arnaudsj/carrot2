
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.util.Arrays;
import java.util.List;

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.analysis.ITokenType;
import org.carrot2.text.preprocessing.PreprocessingContext.AllTokens;
import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.text.util.CharArrayComparators;
import org.carrot2.util.IndirectSort;
import org.carrot2.util.IntArrayUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;

import bak.pcj.list.IntArrayList;
import bak.pcj.list.IntList;
import bak.pcj.set.*;

import com.google.common.collect.Lists;

/**
 * Performs case normalization and calculates a number of frequency statistics for words.
 * The aim of case normalization is to find the most frequently appearing variants of
 * words in terms of case. For example, if in the input documents <i>MacOS</i> appears 20
 * times, <i>Macos</i> 5 times and <i>macos</i> 2 times, case normalizer will select
 * <i>MacOS</i> to represent all variants and assign the aggregated term frequency of 27
 * to it.
 * <p>
 * This class saves the following results to the {@link PreprocessingContext}:
 * <ul>
 * <li>{@link AllTokens#wordIndex}</li>
 * <li>{@link AllWords#image}</li>
 * <li>{@link AllWords#tf}</li>
 * <li>{@link AllWords#tfByDocument}</li>
 * </ul>
 * <p>
 * This class requires that {@link Tokenizer} be invoked first.
 */
@Bindable(prefix = "CaseNormalizer")
public final class CaseNormalizer
{
    /**
     * Word Document Frequency threshold. Words appearing in fewer than
     * <code>dfThreshold</code> documents will be ignored.
     * 
     * @level Advanced
     * @group Preprocessing
     * @label Word Document Frequency threshold
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = 100)
    public int dfThreshold = 1;

    /**
     * Performs normalization and saves the results to the <code>context</code>.
     */
    public void normalize(PreprocessingContext context)
    {
        // Local references to already existing arrays
        final char [][] tokenImages = context.allTokens.image;
        final int [] tokenTypesArray = context.allTokens.type;
        final int [] documentIndexesArray = context.allTokens.documentIndex;
        final byte [] tokensFieldIndex = context.allTokens.fieldIndex;
        final int tokenCount = tokenImages.length;
        final int documentCount = context.documents.size();

        // Sort token images
        final int [] tokenImagesOrder = IndirectSort.sort(tokenImages, 0, tokenImages.length,
            CharArrayComparators.NORMALIZING_CHAR_ARRAY_COMPARATOR);

        // Create holders for new arrays
        final List<char []> normalizedWordImages = Lists.newArrayList();
        final IntList normalizedWordTf = new IntArrayList();
        final List<int []> wordTfByDocumentList = Lists.newArrayList();
        final List<byte []> fieldIndexList = Lists.newArrayList();
        final IntList types = new IntArrayList();

        final int [] wordIndexes = new int [tokenCount];
        Arrays.fill(wordIndexes, -1);

        // Initial values for counters
        int tf = 1;
        int maxTf = 1;
        int maxTfVariantIndex = tokenImagesOrder[0];
        int totalTf = 1;
        int variantStartIndex = 0;

        // An int set for document frequency calculation
        final IntSet documentIndices = new IntBitSet(documentCount);

        // A byte set for word fields tracking
        final ByteSet fieldIndices = new ByteBitSet((byte) context.allFields.name.length);

        // An array for tracking words' tf across documents
        int [] wordTfByDocument = new int [documentCount];

        if (documentIndexesArray[tokenImagesOrder[0]] >= 0)
        {
            documentIndices.add(documentIndexesArray[tokenImagesOrder[0]]);
            wordTfByDocument[documentIndexesArray[tokenImagesOrder[0]]] = 1;
        }

        // Go through the ordered token images
        for (int i = 0; i < tokenImagesOrder.length - 1; i++)
        {
            final char [] image = tokenImages[tokenImagesOrder[i]];
            final char [] nextImage = tokenImages[tokenImagesOrder[i + 1]];
            final int tokenType = tokenTypesArray[tokenImagesOrder[i]];
            final int documentIndex = documentIndexesArray[tokenImagesOrder[i + 1]];

            // Reached the end of non-null tokens?
            if (image == null)
            {
                break;
            }

            // Check if we want to index this token at all
            if (isIndexed(tokenType))
            {
                variantStartIndex = i + 1;
                maxTfVariantIndex = tokenImagesOrder[i + 1];

                final int nextTokenType = tokenTypesArray[tokenImagesOrder[i]];
                if (isIndexed(nextTokenType))
                {
                    resetForNewTokenImage(documentIndexesArray, tokenImagesOrder,
                        documentIndices, fieldIndices, wordTfByDocument, i);
                }
                continue;
            }

            fieldIndices.add(tokensFieldIndex[tokenImagesOrder[i]]);

            // Now check if image case is changing
            final boolean sameCase = CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR
                .compare(image, nextImage) == 0;
            if (sameCase)
            {
                // Case has not changed, just increase counters
                tf++;
                totalTf++;

                documentIndices.add(documentIndex);
                wordTfByDocument[documentIndex] += 1;
                continue;
            }

            // Case (or even token image) has changed. Update most frequent case
            // variant
            if (maxTf < tf)
            {
                maxTf = tf;
                maxTfVariantIndex = tokenImagesOrder[i];
                tf = 1;
            }

            final boolean sameImage = CharArrayComparators.CASE_INSENSITIVE_CHAR_ARRAY_COMPARATOR
                .compare(image, nextImage) == 0;

            // Check if token image has changed
            if (sameImage)
            {
                totalTf++;
                documentIndices.add(documentIndex);
                wordTfByDocument[documentIndex] += 1;
            }
            else
            {
                // The image has changed completely.
                // Before we start processing the new image, we need to
                // see if we want to store the previous image, and if so
                // we need add some data about it to the arrays
                int wordDf = documentIndices.size();
                if (wordDf >= dfThreshold)
                {
                    // Add the word to the word list
                    normalizedWordImages.add(tokenImages[maxTfVariantIndex]);
                    normalizedWordTf.add(totalTf);
                    fieldIndexList.add(fieldIndices.toArray());
                    types.add(tokenType);

                    // Add this word's index in AllWords to all its instances
                    // in the AllTokens multiarray
                    for (int j = variantStartIndex; j < i + 1; j++)
                    {
                        wordIndexes[tokenImagesOrder[j]] = normalizedWordImages.size() - 1;
                    }

                    // Flatten the wordTfByDocument map and add to the list
                    wordTfByDocumentList.add(IntArrayUtils
                        .toSparseEncoding(wordTfByDocument));
                }

                // Reinitialize counters
                totalTf = 1;
                tf = 1;
                maxTf = 1;
                maxTfVariantIndex = tokenImagesOrder[i + 1];
                variantStartIndex = i + 1;

                // Re-initialize int set used for document frequency calculation
                resetForNewTokenImage(documentIndexesArray, tokenImagesOrder,
                    documentIndices, fieldIndices, wordTfByDocument, i);
            }
        }

        // Mapping from allTokens
        context.allTokens.wordIndex = wordIndexes;

        context.allWords.image = normalizedWordImages
            .toArray(new char [normalizedWordImages.size()] []);
        context.allWords.tf = normalizedWordTf.toArray();
        context.allWords.tfByDocument = wordTfByDocumentList
            .toArray(new int [wordTfByDocumentList.size()] []);
        context.allWords.fieldIndices = fieldIndexList.toArray(new byte [fieldIndexList
            .size()] []);
        context.allWords.type = types.toArray();
        context.allWords.flag = new int [types.size()];
    }

    /**
     * Initializes the counters for the a token image.
     */
    private void resetForNewTokenImage(final int [] documentIndexesArray,
        final int [] tokenImagesOrder, final IntSet documentIndices,
        final ByteSet fieldIndices, int [] wordTfByDocument, int i)
    {
        documentIndices.clear();
        fieldIndices.clear();
        Arrays.fill(wordTfByDocument, 0);
        if (documentIndexesArray[tokenImagesOrder[i + 1]] >= 0)
        {
            documentIndices.add(documentIndexesArray[tokenImagesOrder[i + 1]]);
            wordTfByDocument[documentIndexesArray[tokenImagesOrder[i + 1]]] += 1;
        }
    }

    /**
     * Determines whether we should include the token in AllWords.
     */
    private boolean isIndexed(final int tokenType)
    {
        return tokenType == ITokenType.TT_PUNCTUATION
            || tokenType == ITokenType.TT_FULL_URL
            || (tokenType & ITokenType.TF_SEPARATOR_SENTENCE) != 0;
    }
}
