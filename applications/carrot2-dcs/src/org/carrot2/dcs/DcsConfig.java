
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

package org.carrot2.dcs;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.IResource;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.Persister;

/**
 * Configuration of the Document Clustering Server. This configuration is initialized
 * either from command line arguments (when launched from the console) or from the
 * config.xml file when lauched in a Servlet container.
 */
@Root(name = "config")
class DcsConfig
{
    /** DCS application name */
    final static String DCS_APP_NAME = "dcs";

    @Attribute(name = "cache-documents", required = false)
    boolean cacheDocuments = true;

    @Attribute(name = "cache-clusters", required = false)
    boolean cacheClusters = false;

    final Logger logger;

    DcsConfig()
    {
        logger = Logger.getLogger(DCS_APP_NAME);
    }

    static DcsConfig deserialize(IResource configResource) throws Exception
    {
        final InputStream stream = configResource.open();
        try
        {
            return new Persister().read(DcsConfig.class, stream);
        }
        finally
        {
            CloseableUtils.close(stream);
        }
    }
}
