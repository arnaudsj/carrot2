
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

package org.carrot2.source.boss;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Document;
import org.carrot2.source.SearchEngineResponse;
import org.simpleframework.xml.*;

import com.google.common.collect.Lists;

/**
 * Search response model for Yahoo Boss.
 */
@Root(name = "ysearchresponse", strict = false)
final class YSearchResponse
{
    @Attribute(name = "responsecode", required = false)
    public Integer responseCode;

    @Element(name = "nextpage", required = false)
    public String nextPageURI;

    @Element(name = "resultset_web", required = false)
    public WebResultSet webResultSet;

    @Element(name = "resultset_news", required = false)
    public NewsResultSet newsResultSet;

    @Element(name = "resultset_images", required = false)
    public ImagesResultSet imagesResultSet;

    /**
     * Populate {@link SearchEngineResponse} depending on the type of the search result
     * returned.
     */
    public void populate(SearchEngineResponse response)
    {
        if (webResultSet != null)
        {
            response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
                webResultSet.deephits);

            if (webResultSet.results != null)
            {
                for (WebResult result : webResultSet.results)
                {
                    final Document document = new Document(result.title, result.summary,
                        result.url);

                    document.setField(Document.CLICK_URL, result.clickURL);

                    try
                    {
                        document.setField(Document.SIZE, Long.parseLong(result.size));
                    }
                    catch (NumberFormatException e)
                    {
                        // Ignore if cannot parse.
                    }

                    response.results.add(document);
                }
            }
        }
        else if (newsResultSet != null)
        {
            response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
                newsResultSet.deephits);

            if (newsResultSet.results != null)
            {
                for (NewsResult result : newsResultSet.results)
                {
                    final Document document = new Document(result.title, result.summary,
                        result.url);

                    document.setField(Document.CLICK_URL, result.clickURL);
                    if (StringUtils.isNotBlank(result.source))
                    {
                        document.setField(Document.SOURCES, Lists.newArrayList(result.source));
                    }
                    response.results.add(document);
                }
            }
        }
        else if (imagesResultSet != null)
        {
            response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
                imagesResultSet.deephits);

            if (imagesResultSet.results != null)
            {
                for (ImageResult result : imagesResultSet.results)
                {
                    final Document document = new Document(result.title, result.summary, result.refererURL);

                    // We use the image's referer page as the target click for the title.
                    document.setField(Document.CLICK_URL, result.refererClickURL);

                    // Attach thumbnail URL.
                    document.setField(Document.THUMBNAIL_URL, result.thumbnailURL);
                    response.results.add(document);
                }
            }
        }
    }
}
