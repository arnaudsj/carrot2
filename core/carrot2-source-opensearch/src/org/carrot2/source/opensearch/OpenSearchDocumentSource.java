
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

package org.carrot2.source.opensearch;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.attribute.Init;
import org.carrot2.source.*;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.resource.URLResourceWithParams;

import com.google.common.collect.Maps;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

/**
 * A {@link IDocumentSource} fetching {@link Document}s (search results) from an OpenSearch
 * feed.
 * <p>
 * Based on code donated by Julien Nioche.
 * 
 * @see <a href="http://www.opensearch.org">OpenSearch.org</a>
 */
@Bindable(prefix = "OpenSearchDocumentSource")
public class OpenSearchDocumentSource extends MultipageSearchEngine
{
    /** Logger for this class. */
    final static Logger logger = Logger.getLogger(OpenSearchDocumentSource.class);

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * URL to fetch the search feed from. The URL template can contain variable place
     * holders as defined by the OpenSearch specification that will be replaced during
     * runtime. The format of the place holder is <code>${variable}</code>. The following
     * variables are supported:
     * <ul>
     * <li><code>searchTerms</code> will be replaced by the query</li> <li><code>
     * startIndex</code> index of the first result to be searched. Mutually exclusive with
     * <code>startPage</code></li>. <li><code>startPage</code> index of the first result
     * to be searched. Mutually exclusive with <code>startIndex</code>.</li><li><code>
     * count</code> the number of search results per page</li>
     * </ul>
     * 
     * @label Feed URL template
     * @level Medium
     * @group Service
     */
    @Input
    @Init
    @Attribute
    @Required
    public String feedUrlTemplate;

    /**
     * Results per page. The number of results per page the document source will expect
     * the feed to return.
     * 
     * @label Results per page
     * @level Medium
     * @group Service
     */
    @Input
    @Init
    @Attribute
    @Required
    @IntRange(min = 1)
    public int resultsPerPage;

    /**
     * Maximum number of results. The maximum number of results the document source can
     * deliver.
     * 
     * @label Maximum results
     * @level Medium
     * @group Service
     */
    @Input
    @Init
    @Attribute
    @IntRange(min = 1)
    public int maximumResults = 1000;

    /**
     * Search engine metadata create upon initialization.
     */
    private MultipageSearchEngineMetadata metadata;

    /** Fetcher for OpenSearch feed. */
    private FeedFetcher feedFetcher;

    /** searchTerms variable */
    private static final String SEARCH_TERMS_VARIABLE_NAME = "searchTerms";

    /** startIndex variable */
    private static final String START_INDEX_VARIABLE_NAME = "startIndex";

    /** startPage variable */
    private static final String START_PAGE_VARIABLE_NAME = "startPage";

    /** count variable */
    private static final String COUNT_VARIABLE_NAME = "count";

    @Override
    public void init(IControllerContext context)
    {
        super.init(context);

        // Verify that the attributes are legal
        final boolean hasStartPage = URLResourceWithParams
            .containsAttributePlaceholder(feedUrlTemplate, START_PAGE_VARIABLE_NAME);
        final boolean hasStartIndex = URLResourceWithParams
            .containsAttributePlaceholder(feedUrlTemplate, START_INDEX_VARIABLE_NAME);

        if (!(hasStartPage ^ hasStartIndex))
        {
            throw new ComponentInitializationException(
                "The feedUrlTemplate must contain either "
                    + URLResourceWithParams
                        .formatAttributePlaceholder(START_INDEX_VARIABLE_NAME)
                    + " or "
                    + URLResourceWithParams
                        .formatAttributePlaceholder(START_PAGE_VARIABLE_NAME)
                    + " variable");
        }

        if (!URLResourceWithParams.containsAttributePlaceholder(feedUrlTemplate,
            SEARCH_TERMS_VARIABLE_NAME))
        {
            throw new ComponentInitializationException(
                "The feedUrlTemplate must contain "
                    + URLResourceWithParams
                        .formatAttributePlaceholder(SEARCH_TERMS_VARIABLE_NAME)
                    + " variable");
        }

        if (resultsPerPage == 0)
        {
            throw new ComponentInitializationException("resultsPerPage must be set");
        }

        this.metadata = new MultipageSearchEngineMetadata(resultsPerPage, maximumResults,
            hasStartPage);
        this.feedFetcher = new HttpURLFeedFetcher();
    }

    @Override
    public void process() throws ProcessingException
    {
        super.process(metadata, getSharedExecutor(MAX_CONCURRENT_THREADS, this.getClass()));
    }

    @Override
    protected Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new SearchEngineResponseCallable()
        {
            @SuppressWarnings("unchecked")
            public SearchEngineResponse search() throws Exception
            {
                // Replace variables in the URL
                final Map<String, Object> values = Maps.newHashMap();
                values.put(SEARCH_TERMS_VARIABLE_NAME, query);
                values.put(START_INDEX_VARIABLE_NAME, bucket.start + 1);
                values.put(START_PAGE_VARIABLE_NAME, bucket.start + 1);
                values.put(COUNT_VARIABLE_NAME, bucket.results);

                final String url = URLResourceWithParams.substituteAttributes(feedUrlTemplate, values);

                logger.debug("Fetching URL: " + url);

                /*
                 * TODO: Rome fetcher uses SUN's HttpClient and opens a persistent HTTP connection
                 * (background thread that keeps reference to the class loader). This causes minor
                 * memory leaks when reloading Web applications. Consider: 1) patching rome fetcher
                 * sources and adding Connection: close to request headers, 2) using Apache HttpClient,
                 * 3) using manual fetch of the syndication feed.
                 */
                final SyndFeed feed = feedFetcher.retrieveFeed(new URL(url));
                final SearchEngineResponse response = new SearchEngineResponse();

                // The documentation does not mention that null value can be returned
                // but we've seen a NPE here: http://builds.carrot2.org/browse/C2HEAD-SOURCES-4.
                if (feed != null)
                {
                    final List entries = feed.getEntries();
                    for (Iterator it = entries.iterator(); it.hasNext();)
                    {
                        final SyndEntry entry = (SyndEntry) it.next();
                        final Document document = new Document();

                        document.addField(Document.TITLE, clean(entry.getTitle()));
                        document.addField(Document.SUMMARY, clean(entry.getDescription()
                            .getValue()));
                        document.addField(Document.CONTENT_URL, entry.getLink());

                        response.results.add(document);
                    }
                }

                return response;
            }
        };
    }

    private String clean(String string)
    {
        return StringUtils.removeHtmlTags(StringEscapeUtils.unescapeHtml(string));
    }
}
