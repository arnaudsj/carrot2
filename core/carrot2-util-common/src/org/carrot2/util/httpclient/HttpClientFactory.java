
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.httpclient;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang.StringUtils;

/**
 * Prepare instances of {@link HttpClient} with desired socket configuration settings.
 * This class takes into account two legacy Java properties:
 * <ul>
 * <li>http.proxyhost</li>
 * <li>http.proxyport</li>
 * </ul>
 * These properties configure HTTP proxy through which all requests are tunneled (there is
 * no need to configure per-connection proxy). No custom proxy authorization methods are
 * implemented as of yet.
 */
public final class HttpClientFactory
{
    private static final String PROPERTY_NAME_PROXY_HOST = "http.proxyhost";
    private static final String PROPERTY_NAME_PROXY_PORT = "http.proxyport";

    /**
     * Default timeout for {@link #getTimeoutingClient()} in milliseconds.
     */
    private static int DEFAULT_TIMEOUT = 8 * 1000;

    /*
     * 
     */
    private HttpClientFactory()
    {
        // no instances
    }

    /**
     * @param timeout Timeout in milliseconds.
     * @return Returns a client with sockets configured to timeout after some sensible
     *         time.
     */
    public static HttpClient getTimeoutingClient(int timeout)
    {
        final HttpClient httpClient = new HttpClient(new SingleHttpConnectionManager());

        configureProxy(httpClient);

        // Setup default timeouts.
        httpClient.getParams().setSoTimeout(timeout);
        httpClient.getParams().setIntParameter("http.connection.timeout", timeout);

        // Not important (single http connection manager), but anyway.
        httpClient.getParams().setConnectionManagerTimeout(timeout);

        return httpClient;
    }

    /**
     * Configure HTTP proxy from system properties, if available.
     */
    private static void configureProxy(HttpClient httpClient)
    {
        final String proxyHost = System.getProperty(PROPERTY_NAME_PROXY_HOST);
        final String proxyPort = System.getProperty(PROPERTY_NAME_PROXY_PORT);
        
        if (!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort))
        {
            try
            {
                final int port = Integer.parseInt(proxyPort);
                httpClient.getHostConfiguration().setProxy(proxyHost, port);
            }
            catch (NumberFormatException e)
            {
                // Ignore.
            }
        }
    }

    /**
     * @see #getTimeoutingClient(int)
     * @see #DEFAULT_TIMEOUT
     */
    public static HttpClient getTimeoutingClient()
    {
        return getTimeoutingClient(DEFAULT_TIMEOUT);
    }
}
