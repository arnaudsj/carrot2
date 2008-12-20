
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

package org.carrot2.examples.clustering;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.ExampleUtils;
import org.carrot2.source.boss.*;
import org.carrot2.source.microsoft.MicrosoftLiveDocumentSource;
import org.carrot2.util.attribute.AttributeUtils;

/**
 * This example shows how to cluster {@link Document}s retrieved from
 * {@link DocumentSource}s. There are a number of implementations of this interface in the
 * Carrot2 project, in this example we will cluster results from Microsoft Live (Web
 * search) and Yahoo Boss (news search).
 * <p>
 * It is assumed that you are familiar with {@link ClusteringDocumentList} example.
 * </p>
 *  
 * @see ClusteringDocumentList
 * @see UsingCachingController
 */
public class ClusteringDataFromDocumentSources
{
    public static void main(String [] args)
    {
        /*
         * EXAMPLE 1: fetching documents from Microsoft Live, clustering with Lingo.
         * Attributes for the first query: query, number of results to fetch from the
         * source. Note that the API key defaults to the one assigned for the Carrot2
         * project. Please use your own key for production use.
         */
        SimpleController controller = new SimpleController();

        /*
         * As the simple controller discards component instances after processing, the
         * @Init attributes can be provided at the same time as the @Processing ones. For
         * the same reason, you don't need to initialize the simple controller. Please
         * check CachingController for more advanced handling of component life cycle.
         */
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeUtils.getKey(MicrosoftLiveDocumentSource.class, "appid"),
            MicrosoftLiveDocumentSource.CARROTSEARCH_APPID);
        attributes.put(AttributeNames.QUERY, "data mining");
        attributes.put(AttributeNames.RESULTS, 100);

        ProcessingResult result = controller.process(attributes,
            MicrosoftLiveDocumentSource.class, LingoClusteringAlgorithm.class);

        ExampleUtils.displayResults(result);

        /*
         * EXAMPLE 2: fetching from Yahoo BOSS (news search), clustering with Lingo.
         * Attributes for the first query: query, number of results to fetch from the
         * source. Again, note the API key. Please use your own key for production use.
         */
        attributes = new HashMap<String, Object>();

        /*
         * Boss document source is generic and can retrieve Web search, news and image
         * results. Pick the service to use by passing the right service implementation.
         */
        attributes.put(AttributeUtils.getKey(BossDocumentSource.class, "service"),
            BossNewsSearchService.class);

        /*
         * Other attributes.
         */
        attributes.put(AttributeUtils.getKey(BossSearchService.class, "appid"),
            BossSearchService.CARROTSEARCH_APPID);
        attributes.put(AttributeNames.QUERY, "war");
        attributes.put(AttributeNames.RESULTS, 50);

        result = controller.process(attributes, BossDocumentSource.class,
            LingoClusteringAlgorithm.class);

        ExampleUtils.displayResults(result);
    }
}
