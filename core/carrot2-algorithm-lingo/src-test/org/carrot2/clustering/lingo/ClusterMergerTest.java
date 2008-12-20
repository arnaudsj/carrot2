
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

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.matrix.factorization.LocalNonnegativeMatrixFactorizationFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for cluster merging in {@link ClusterBuilder}.
 */
public class ClusterMergerTest extends TermDocumentMatrixBuilderTestBase
{
    /** Matrix reducer needed for test */
    private TermDocumentMatrixReducer reducer;

    /** Label builder under tests */
    private ClusterBuilder clusterBuilder;

    @Before
    public void setUpClusterLabelBuilder()
    {
        clusterBuilder = new ClusterBuilder();
        reducer = new TermDocumentMatrixReducer();
        reducer.factorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
        reducer.desiredClusterCountBase = 25;
    }

    @Test
    public void testEmpty()
    {
        check(new int [0] []);
    }

    @Test
    public void testNoMerge()
    {
        reducer.desiredClusterCountBase = 30;
        createDocuments("", "aa . aa", "", "bb . bb", "", "cc . cc");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0
            },

            new int []
            {
                2
            },

            new int []
            {
                1
            }
        };

        check(expectedDocumentIndices);
    }

    @Test
    public void testSimpleMerge()
    {
        createDocuments("aa", "aa", "aa bb", "aa bb");
        reducer.desiredClusterCountBase = 20;
        clusterBuilder.phraseLabelBoost = 0.08;
        clusterBuilder.clusterMergingThreshold = 0.4;
        labelFilterProcessor.minLengthLabelFilter.enabled = false;

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1
            },

            null
        };

        check(expectedDocumentIndices);
    }

    @Test
    public void testMultiMerge()
    {
        createDocuments("aa", "aa", "aa bb", "aa bb", "aa bb cc", "aa bb cc", "dd dd");
        reducer.desiredClusterCountBase = 20;
        clusterBuilder.phraseLabelBoost = 0.05;
        clusterBuilder.clusterMergingThreshold = 0.2;
        labelFilterProcessor.minLengthLabelFilter.enabled = false;
        labelFilterProcessor.completeLabelFilter.enabled = false;

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1, 2
            },

            null,

            null,

            new int []
            {
                3
            }
        };

        check(expectedDocumentIndices);
    }

    private void check(int [][] expectedDocumentIndices)
    {
        buildTermDocumentMatrix();
        reducer.reduce(lingoContext);
        final TfTermWeighting termWeighting = new TfTermWeighting();
        clusterBuilder.buildLabels(lingoContext, termWeighting);
        clusterBuilder.assignDocuments(lingoContext);
        clusterBuilder.merge(lingoContext);

        for (int i = 0; i < expectedDocumentIndices.length; i++)
        {
            final String description = "clusterDocuments[" + i + "]";
            if (expectedDocumentIndices[i] != null)
            {
                assertThat(lingoContext.clusterDocuments[i]).as(description).isNotNull();
                assertThat(lingoContext.clusterDocuments[i].toArray()).as(description)
                    .containsOnly(expectedDocumentIndices[i]);
            }
            else
            {
                assertThat(lingoContext.clusterDocuments[i]).as(description).isNull();
            }
        }
    }
}
