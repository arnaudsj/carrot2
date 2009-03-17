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

package org.carrot2.output.metrics;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.Cluster;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test cases for {@link IClusteringMetric}.
 */
public class PrecisionRecallMetricTest extends IdealPartitioningBasedMetricTest
{
    @Test
    public void testCalculatePrecisionRecallEmptyCluster()
    {
        check(new Cluster(), null, null, null);
    }

    @Test
    public void testCalculatePrecisionRecallTrivialCluster()
    {
        check(new Cluster("test", documentWithTopic("test")), 1.0, 1.0, 1.0);
    }

    @Test
    public void testCalculatePrecisionRecallPartiallyContaminatedCluster()
    {
        check(partiallyContaminatedCluster(), 0.75, 1.0, 2 * 0.75 / (1 + 0.75));
    }

    @Test
    public void testCalculatePrecisionRecallFullyContaminatedCluster()
    {
        check(fullyContaminatedCluster(), 0.25, 1.0, 2 * 0.25 / (1 + 0.25));
    }

    @Test
    public void testCalculateContaminationPureCluster()
    {
        check(pureCluster(), 1.0, 1.0, 1.0);
    }

    private void check(Cluster cluster, Double expectedPrecision, Double expectedRecall,
        Double expectedFMeasure)
    {
        final PrecisionRecallMetric metric = new PrecisionRecallMetric();
        metric.documents = cluster.getAllDocuments();
        metric.clusters = Lists.newArrayList(cluster);
        metric.calculate();
        assertThat(cluster.<Double> getAttribute(PrecisionRecallMetric.PRECISION)).as(
            "precision").isEqualTo(expectedPrecision);
        assertThat(cluster.<Double> getAttribute(PrecisionRecallMetric.RECALL)).as(
            "recall").isEqualTo(expectedRecall);
        assertThat(cluster.<Double> getAttribute(PrecisionRecallMetric.F_MEASURE)).as(
            "f-measure").isEqualTo(expectedFMeasure);
    }

    @Override
    protected String [] getClusterMetricKeys()
    {
        return new String []
        {
            PrecisionRecallMetric.PRECISION, PrecisionRecallMetric.RECALL,
            PrecisionRecallMetric.F_MEASURE
        };
    }
}
