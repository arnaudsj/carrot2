
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

package org.carrot2.source;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

/**
 * Usage statistics for an instance of {@link MultipageSearchEngine}. 
 */
@Bindable(prefix = "SearchEngineStats")
public final class SearchEngineStats
{
    /**
     * Number queries handled successfully by this data source.
     * 
     * @label Successful Queries
     * @group Data source status
     */
    @Processing
    @Output
    @Attribute
    public int queries;

    /**
     * Number of individual page requests issued by this data source.
     * 
     * @label Page Requests
     * @group Data source status
     */
    @Processing
    @Output
    @Attribute
    public int pageRequests;
    
    /* */
    public synchronized void incrQueryCount()
    {
        this.queries++;
    }

    /* */
    public synchronized void incrPageRequestCount()
    {
        this.pageRequests++;
    }    
}
