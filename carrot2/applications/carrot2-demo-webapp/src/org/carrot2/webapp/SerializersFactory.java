
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

package org.carrot2.webapp;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A factory for objects serializing results.
 * 
 * @author Dawid Weiss
 */
public interface SerializersFactory {
    
    public void configure(ServletConfig config);

    public PageSerializer createPageSerializer(HttpServletRequest request);
    public RawDocumentsSerializer createRawDocumentSerializer(HttpServletRequest request);
    public RawClustersSerializer createRawClustersSerializer(HttpServletRequest request);

    /**
     * This method is invoked before any clustering or document fetching
     * takes place. If <code>true</code> is returned, processing continues,
     * otherwise processing finishes immediately (set appropriate
     * <code>response</code> HTTP headers if you wish).
     */
    public boolean acceptRequest(HttpServletRequest request, HttpServletResponse response);
}
