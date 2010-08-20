
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

package org.carrot2.clustering.lingo;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.carrot2.core.test.SampleDocumentData;
import org.carrot2.text.preprocessing.CaseNormalizer;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test cases for the {@link LingoClusteringAlgorithm}
 */
public class LingoClusteringAlgorithmTest extends
    ClusteringAlgorithmTestBase<LingoClusteringAlgorithm>
{
    @Override
    public Class<LingoClusteringAlgorithm> getComponentClass()
    {
        return LingoClusteringAlgorithm.class;
    }

    @Test
    public void testNoRequiredDocuments()
    {
        try
        {
            getSimpleController(initAttributes).process(
                processingAttributes, getComponentClass());

            fail("Should fail with an exception.");
        }
        catch (ProcessingException e)
        {
            assertThat(e.getMessage()).contains("No value for required attribute");
        }
    }

    @Test
    public void testClusteringWithDfThreshold()
    {
        processingAttributes.put(AttributeUtils.getKey(CaseNormalizer.class,
            "dfThreshold"), 20);
        final Collection<Cluster> clustersWithThreshold = cluster(
            SampleDocumentData.DOCUMENTS_DATA_MINING).getClusters();

        // Clustering with df threshold must not fail
        assertThat(clustersWithThreshold.size()).isGreaterThan(0);
    }

    @Test
    public void testNoLabelCandidates()
    {
        final List<Document> documents = Lists.newArrayList();
        documents.add(new Document("test"));
        documents.add(new Document("test"));
        documents.add(new Document("test"));
        processingAttributes.put(AttributeNames.QUERY, "test");

        final List<Cluster> clusters = cluster(documents).getClusters();

        assertNotNull(clusters);
        assertEquals(1, clusters.size());
        assertThat(clusters.get(0).size()).isEqualTo(documents.size());
    }
}
