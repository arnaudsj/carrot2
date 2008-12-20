
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

package org.carrot2.examples.research;

import java.text.MessageFormat;
import java.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.output.metrics.*;
import org.carrot2.source.ambient.AmbientDocumentSource;
import org.carrot2.source.ambient.AmbientDocumentSource.AmbientTopic;
import org.carrot2.util.attribute.AttributeUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Runs a clustering quality benchmark based on the data set embedded in
 * {@link AmbientDocumentSource}.
 */
public class ClusteringQualityBenchmark
{
    public static void main(String [] args)
    {
        // Disable excessive logging
        LogManager.getRootLogger().setLevel(Level.WARN);

        final AmbientTopic [] topics = AmbientDocumentSource.AmbientTopic.values();
        SimpleController controller = new SimpleController();

        // List of algorithms to test
        final ArrayList<Class<? extends IProcessingComponent>> algorithms = Lists
            .newArrayList();
        algorithms.add(LingoClusteringAlgorithm.class);
        algorithms.add(STCClusteringAlgorithm.class);

        // List of metrics to output
        final ArrayList<String> metrics = Lists.newArrayList(AttributeUtils.getKey(
            ContaminationMetric.class, "weightedAverageContamination"), AttributeUtils
            .getKey(CoverageMetric.class, "absoluteTopicCoverage"), AttributeUtils
            .getKey(CoverageMetric.class, "topicCoverage"), AttributeUtils.getKey(
            CoverageMetric.class, "documentCoverage"));

        final Map<String, Object> attributes = Maps.newHashMap();

        System.out
            .println("Topic\tAlgorithm\tOrder\tContamination\tATCoverage\tTCoverage\tDCoverage");
        for (AmbientTopic topic : topics)
        {
            for (Class<? extends IProcessingComponent> algorithm : algorithms)
            {
                attributes.put(AttributeUtils
                    .getKey(AmbientDocumentSource.class, "topic"), topic);

                controller.process(attributes, AmbientDocumentSource.class, algorithm,
                    ClusteringMetricsCalculator.class);

                System.out.print(topic.name() + "\t" + algorithm.getSimpleName());
                for (String metricKey : metrics)
                {
                    System.out.print("\t"
                        + MessageFormat.format("{0,number,#.####}", attributes
                            .get(metricKey)));
                }
                System.out.println();
            }
        }

    }
}
