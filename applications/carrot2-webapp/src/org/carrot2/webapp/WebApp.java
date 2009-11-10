
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

package org.carrot2.webapp;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebApp
{
    /**
     * Starts embedded JETTY server.
     */
    private void startJetty(final int port) throws Exception
    {
        final Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        connector.setAcceptQueueSize(20);
        server.addConnector(connector);

        WebAppContext wac = new WebAppContext();
        wac.setClassLoader(Thread.currentThread().getContextClassLoader());
        wac.setContextPath("/");
        wac.setWar("web");
        wac.setDefaultsDescriptor("etc/webdefault.xml");
        server.setHandler(wac);
        server.setStopAtShutdown(true);

        server.start();
    }

    /**
     * Command-line entry point.
     */
    public static void main(String [] args) throws Exception
    {
        /*
         * This enables LOG4J logging in Jetty (specify on command-line, if possible).
         */
        System.setProperty("org.mortbay.log.LogFactory.noDiscovery", "false");

        /*
         * Specify URI decoding codepage.
         */
        System.setProperty("org.mortbay.util.URI.charset", "UTF-8");

        final int port = 8080;
        new WebApp().startJetty(port);
    }
}
