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

package org.carrot2.clustering.lingo;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.matrix.NNIInterface;
import org.carrot2.text.preprocessing.*;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.VectorSpaceModelContext;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.DoubleRange;

import bak.pcj.IntIterator;
import bak.pcj.set.IntSet;

import com.google.common.collect.Lists;

/**
 * Lingo clustering algorithm.
 */
@Bindable(prefix = "LingoClusteringAlgorithm")
public class LingoClusteringAlgorithm extends ProcessingComponentBase implements
    IClusteringAlgorithm
{
    private static final Logger log = Logger.getLogger(LingoClusteringAlgorithm.class);

    /**
     * Report the warning about native libraries only once.
     */
    private static boolean nativeLibrariesReported;

    /**
     * Query that produced the documents. The query will help the algorithm to create
     * better clusters. Therefore, providing the query is optional but desirable.
     * 
     * @group Search query
     * @level Medium
     * @label Search query
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.QUERY)
    public String query = null;

    /**
     * Documents to cluster.
     */
    @Processing
    @Input
    @Required
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    @Processing
    @Output
    @Internal
    @Attribute(key = AttributeNames.CLUSTERS)
    public List<Cluster> clusters = null;

    /**
     * Indicates whether Lingo used fast native matrix computation routines. Value of this
     * attribute is equal to {@link NNIInterface#isNativeBlasAvailable()} at the time of
     * running the algorithm.
     * 
     * @group Matrix model
     * @label Native matrix operations used
     */
    @Processing
    @Output
    @Attribute
    public boolean nativeMatrixUsed;

    /**
     * Balance between cluster score and size during cluster sorting. Value equal to 0.0
     * will cause Lingo to sort clusters based only on cluster size. Value equal to 1.0
     * will cause Lingo to sort clusters based only on cluster score.
     * 
     * @label Size-Score sorting ratio
     * @level Medium
     * @group Clusters
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 1.0)
    public double scoreWeight = 0.0;

    /**
     * Common preprocessing tasks handler.
     */
    public PreprocessingPipeline preprocessingPipeline = new PreprocessingPipeline();

    /**
     * Term-document matrix builder for the algorithm, contains bindable attributes.
     */
    public TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();

    /**
     * Term-document matrix reducer for the algorithm, contains bindable attributes.
     */
    public TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();

    /**
     * Cluster label builder, contains bindable attributes.
     */
    public ClusterBuilder clusterBuilder = new ClusterBuilder();

    /**
     * Cluster label formatter, contains bindable attributes.
     */
    public LabelFormatter labelFormatter = new LabelFormatter();

    @Override
    public void init(IControllerContext context)
    {
        synchronized (LingoClusteringAlgorithm.class)
        {
            if (!nativeLibrariesReported)
            {
                if (NNIInterface.isNativeBlasAvailable())
                {
                    log.info("Native BLAS routines available");
                }
                else
                {
                    log.info("Native BLAS routines not available");
                }
                nativeLibrariesReported = true;
            }
        }
    }

    /**
     * Performs Lingo clustering of {@link #documents}.
     */
    @Override
    public void process() throws ProcessingException
    {
        nativeMatrixUsed = NNIInterface.isNativeBlasAvailable();

        // Preprocessing of documents
        final PreprocessingContext context = preprocessingPipeline.preprocess(documents,
            query);

        // Further processing only if there are words to process
        clusters = Lists.newArrayList();
        if (context.hasLabels())
        {
            // Term-document matrix building and reduction
            final VectorSpaceModelContext vsmContext = new VectorSpaceModelContext(
                context);
            matrixBuilder.buildTermDocumentMatrix(vsmContext);
            matrixBuilder.buildTermPhraseMatrix(vsmContext);

            LingoProcessingContext lingoContext = new LingoProcessingContext(vsmContext);
            matrixReducer.reduce(lingoContext);

            // Cluster label building
            clusterBuilder.buildLabels(lingoContext, matrixBuilder.termWeighting);

            // Document assignment
            clusterBuilder.assignDocuments(lingoContext);

            // Cluster merging
            clusterBuilder.merge(lingoContext);

            // Format final clusters
            final int [] clusterLabelIndex = lingoContext.clusterLabelFeatureIndex;
            final IntSet [] clusterDocuments = lingoContext.clusterDocuments;
            final double [] clusterLabelScore = lingoContext.clusterLabelScore;
            for (int i = 0; i < clusterLabelIndex.length; i++)
            {
                final Cluster cluster = new Cluster();

                final int labelFeature = clusterLabelIndex[i];
                if (labelFeature < 0)
                {
                    // Cluster removed during merging
                    continue;
                }

                // Add label and score
                cluster.addPhrases(labelFormatter.format(context, labelFeature));
                cluster.setAttribute(Cluster.SCORE, clusterLabelScore[i]);

                // Add documents
                for (IntIterator it = clusterDocuments[i].iterator(); it.hasNext();)
                {
                    cluster.addDocuments(documents.get(it.next()));
                }

                // Add cluster
                clusters.add(cluster);
            }

            Collections.sort(clusters, Cluster
                .byReversedWeightedScoreAndSizeComparator(scoreWeight));
        }

        Cluster.appendOtherTopics(documents, clusters);
    }
}
