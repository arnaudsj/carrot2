
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.carrot2.core.test.SampleDocumentData.*;

import java.util.*;
import java.util.concurrent.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.fest.assertions.Assertions;
import org.junit.Test;

import com.google.common.collect.*;

/**
 * Simple baseline tests that apply to all clustering algorithms.
 */
public abstract class ClusteringAlgorithmTestBase<T extends IClusteringAlgorithm> extends
    ProcessingComponentTestBase<T>
{
    /**
     * A test to check if the algorithm does not fail with no documents.
     */
    @Test
    public void testNoDocuments()
    {
        final Collection<Cluster> clusters = cluster(Collections.<Document> emptyList())
            .getClusters();

        assertNotNull(clusters);
        assertEquals(0, clusters.size());
    }

    /**
     * @see "http://issues.carrot2.org/browse/CARROT-400"
     */
    @Test
    public void testEmptyDocuments()
    {
        final List<Document> documents = Lists.newArrayList();
        final int documentCount = 100;
        for (int i = 0; i < documentCount; i++)
        {
            documents.add(new Document());
        }

        final List<Cluster> clusters = cluster(documents).getClusters();

        assertNotNull(clusters);
        assertEquals(1, clusters.size());
        assertThat(clusters.get(0).size()).isEqualTo(documentCount);
    }

    @Test
    public void testClusteringDataMining()
    {
        final ProcessingResult processingResult = cluster(DOCUMENTS_DATA_MINING);
        final Collection<Cluster> clusters = processingResult.getClusters();

        assertThat(clusters.size()).isGreaterThan(0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRepeatedClusteringWithCache()
    {
        final IController controller = new CachingController(IDocumentSource.class);
        controller.init(new HashMap());

        final HashMap processingAttributes = Maps.newHashMap();
        processingAttributes.put(AttributeNames.DOCUMENTS, DOCUMENTS_DATA_MINING);

        controller.process(processingAttributes, getComponentClass());
        controller.process(processingAttributes, getComponentClass());
    }

    /**
     * Performs a very simple stress test using {@link CachingController}. The test is
     * performed with default init attributes.
     */
    @Test
    public void testStress() throws InterruptedException, ExecutionException
    {
        final int numberOfThreads = 4;
        final int queriesPerThread = 25;

        final CachingController controller = getCachingController(initAttributes);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<ProcessingResult>> callables = Lists.newArrayList();
        for (int i = 0; i < numberOfThreads * queriesPerThread; i++)
        {
            final int dataSetIndex = i;
            callables.add(new Callable<ProcessingResult>()
            {
                public ProcessingResult call() throws Exception
                {
                    Map<String, Object> localAttributes = Maps.newHashMap();
                    localAttributes.put(AttributeNames.DOCUMENTS, SampleDocumentData.ALL
                        .get(dataSetIndex % SampleDocumentData.ALL.size()));
                    localAttributes.put("dataSetIndex", dataSetIndex);
                    return controller.process(localAttributes, getComponentClass());
                }
            });
        }

        try
        {
            List<Future<ProcessingResult>> results = executorService.invokeAll(callables);
            Multimap<Integer, List<Cluster>> clusterings = ArrayListMultimap.create();

            // Group results by query
            for (Future<ProcessingResult> future : results)
            {
                final ProcessingResult processingResult = future.get();
                final Integer dataSetIndex = (Integer) processingResult.getAttributes()
                    .get("dataSetIndex");

                clusterings.put(dataSetIndex, processingResult.getClusters());
            }

            // Make sure results are the same within each data set
            for (Integer dataSetIndex : clusterings.keySet())
            {
                Collection<List<Cluster>> clustering = clusterings.get(dataSetIndex);
                Iterator<List<Cluster>> iterator = clustering.iterator();
                if (!iterator.hasNext())
                {
                    continue;
                }

                final List<Cluster> firstClusterList = iterator.next();
                Assertions.assertThat(firstClusterList).isNotEmpty();
                while (iterator.hasNext())
                {
                    assertThatClusters(firstClusterList).isEquivalentTo(iterator.next());
                }
            }
        }
        finally
        {
            executorService.shutdown();
        }
    }

    /**
     * Performs clustering using {@link SimpleController}.
     * 
     * @param documents Documents to be clustered.
     * @return {@link ProcessingResult} returned from the controller.
     */
    public ProcessingResult cluster(Collection<Document> documents)
    {
        processingAttributes.put(AttributeNames.DOCUMENTS, documents);
        return getSimpleController(initAttributes).process(processingAttributes,
            getComponentClass());
    }

    /**
     * Recursively collects documents from clusters.
     */
    public Collection<Document> collectDocuments(Collection<Cluster> clusters)
    {
        return collectDocuments(clusters, new HashSet<Document>());
    }

    /*
     * 
     */
    private Collection<Document> collectDocuments(Collection<Cluster> clusters,
        Collection<Document> documents)
    {
        for (final Cluster cluster : clusters)
        {
            documents.addAll(cluster.getDocuments());
            collectDocuments(cluster.getSubclusters());
        }

        return documents;
    }
}
