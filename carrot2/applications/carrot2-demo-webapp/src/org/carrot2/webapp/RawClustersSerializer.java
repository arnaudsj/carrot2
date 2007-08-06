
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.carrot2.core.clustering.RawCluster;

/**
 * A serializer for {@link RawCluster}s.
 * 
 * @author Dawid Weiss
 */
public interface RawClustersSerializer {
    public String getContentType();

    public void startResult(OutputStream os, List rawDocumentsList, HttpServletRequest request, String query) 
        throws IOException;

    public void write(RawCluster cluster) throws IOException;

    public void endResult(long clusteringTime) throws IOException;

    public void processingError(Throwable cause) throws IOException;
}
