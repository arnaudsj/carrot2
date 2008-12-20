
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

package org.carrot2.webapp.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.MapUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.simplexml.SimpleXmlWrapperValue;
import org.carrot2.util.simplexml.SimpleXmlWrappers;
import org.carrot2.webapp.QueryProcessorServlet;
import org.simpleframework.xml.ElementMap;

/**
 * Represents the data the application received in the HTTP request.
 */
@Bindable
public class RequestModel
{
    @Input
    @Attribute(key = WebappConfig.SKIN_PARAM)
    @org.simpleframework.xml.Attribute
    public String skin;

    @Input
    @Attribute(key = AttributeNames.QUERY)
    @org.simpleframework.xml.Attribute(required = false)
    public String query = "";

    /**
     * Query that can be safely put into a JavaScript string.
     */
    @org.simpleframework.xml.Attribute(name = AttributeNames.QUERY + "-escaped", required = false)
    public String queryEscaped = "";

    /**
     * Note that this is the number of results user requested, the actual number may be
     * different, in particular 0.
     */
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    @org.simpleframework.xml.Attribute
    public int results;

    @Input
    @Attribute(key = WebappConfig.SOURCE_PARAM)
    @org.simpleframework.xml.Attribute
    public String source = WebappConfig.INSTANCE.components.getSources().get(0).getId();

    @Input
    @Attribute(key = WebappConfig.ALGORITHM_PARAM)
    @org.simpleframework.xml.Attribute
    public String algorithm = WebappConfig.INSTANCE.components.getAlgorithms().get(0)
        .getId();

    @Input
    @Attribute(key = WebappConfig.TYPE_PARAM)
    public RequestType type;

    @Input
    @Attribute(key = WebappConfig.VIEW_PARAM)
    @org.simpleframework.xml.Attribute
    public String view;

    @Input
    @Attribute(key = QueryProcessorServlet.STATS_KEY)
    public String statsKey;

    @org.simpleframework.xml.Attribute
    public boolean modern = true;

    public Map<String, Object> otherParameters;

    @SuppressWarnings("unused")
    @ElementMap(entry = "parameter", key = "key", attribute = true, inline = true, required = false)
    private HashMap<String, SimpleXmlWrapperValue> otherParametersToSerialize;

    @SuppressWarnings("unused")
    @ElementMap(entry = "cookie", key = "key", attribute = true, inline = true, required = false)
    private HashMap<String, SimpleXmlWrapperValue> cookies;

    public void afterParametersBound(Map<String, Object> remainingHttpParameters,
        Map<String, String> cookies)
    {
        if (type == null)
        {
            type = RequestType.PAGE;
        }

        if (!modern)
        {
            skin = "simple";
        }

        otherParameters = remainingHttpParameters;
        otherParametersToSerialize = MapUtils.asHashMap(SimpleXmlWrappers
            .wrap(remainingHttpParameters));

        this.cookies = MapUtils.asHashMap(SimpleXmlWrappers.wrap(cookies));

        this.queryEscaped = StringEscapeUtils.escapeJavaScript(query);
    }
}
