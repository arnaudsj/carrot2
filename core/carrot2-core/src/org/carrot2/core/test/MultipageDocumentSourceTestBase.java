
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

package org.carrot2.core.test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.carrot2.core.IDocumentSource;
import org.carrot2.source.*;
import org.carrot2.source.MultipageSearchEngine.SearchMode;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Test;
import org.junitext.Prerequisite;

/**
 * Common tests for {@link IDocumentSource}s that can make more than one search requests to
 * get results for one query.
 */
public abstract class MultipageDocumentSourceTestBase<T extends IDocumentSource> extends
    QueryableDocumentSourceTestBase<T>
{
    /**
     * Metadata for the {@link MultipageSearchEngine} being tested.
     */
    protected abstract MultipageSearchEngineMetadata getSearchEngineMetadata();

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testQueryLargerThanPage() throws Exception
    {
        final int needed = getSearchEngineMetadata().resultsPerPage * 2
            + getSearchEngineMetadata().resultsPerPage / 2;

        // Allow some slack (duplicated URLs).
        final int documentsReturned = runQuery("test", needed);

        assertThat(documentsReturned).isGreaterThan((int) (needed / slack()));
    }

    protected double slack()
    {
        return 1.25;
    }
    
    
    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testConservativeMode() throws Exception
    {
        processingAttributes.put("search-mode", SearchMode.CONSERVATIVE);

        runAndCheckNoResultsQuery();
        assertEquals(1, processingAttributes.get(AttributeUtils.getKey(
            SearchEngineStats.class, "pageRequests")));
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testSpeculativeMode() throws Exception
    {
        processingAttributes.put("search-mode", SearchMode.SPECULATIVE);

        runAndCheckNoResultsQuery(getSearchEngineMetadata().resultsPerPage + 1);
        assertEquals(2, processingAttributes.get(AttributeUtils.getKey(
            SearchEngineStats.class, "pageRequests")));
    }
}
