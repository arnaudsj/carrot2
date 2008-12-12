
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

package org.carrot2.examples.core;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.SimpleController;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.clustering.ClusteringDataFromDocumentSources;
import org.carrot2.source.microsoft.MicrosoftLiveDocumentSource;
import org.carrot2.util.attribute.AttributeUtils;

/**
 * This example shows how to save clustering results to XML.
 * <p>
 * It is assumed that you are familiar with {@link ClusteringDataFromDocumentSources}
 * example.
 * </p>
 */
public class SavingResultsToXml
{
    public static void main(String [] args) throws Exception
    {
        // Let's fetch some results from MSN first
        final SimpleController controller = new SimpleController();
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeUtils.getKey(MicrosoftLiveDocumentSource.class, "appid"),
            MicrosoftLiveDocumentSource.CARROTSEARCH_APPID);
        attributes.put(AttributeNames.QUERY, "data mining");
        attributes.put(AttributeNames.RESULTS, 25);

        final ProcessingResult result = controller.process(attributes,
            MicrosoftLiveDocumentSource.class, LingoClusteringAlgorithm.class);

        // Now, we can serialize the entire result to XML like this
        result.serialize(new PrintWriter(System.out));
        System.out.println();

        // Optionally, we can choose whether we want to serialize documents and clusters
        result.serialize(new PrintWriter(System.out), false /* don't save documents */,
            true /* save clusters */);

    }
}
