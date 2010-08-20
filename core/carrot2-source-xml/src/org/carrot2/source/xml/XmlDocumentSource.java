
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

package org.carrot2.source.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.resource.*;

import com.google.common.collect.*;

/**
 * Fetches documents from XML files and streams. For additional flexibility, an XSLT
 * stylesheet can be applied to the XML stream before it is deserialized into Carrot2
 * data.
 * 
 * @see #xml
 */
@Bindable(prefix = "XmlDocumentSource", inherit = AttributeNames.class)
public class XmlDocumentSource extends ProcessingComponentBase implements IDocumentSource
{
    /**
     * The resource to load XML data from. You can either create instances of
     * {@link IResource} implementations directly or use {@link ResourceUtils} to look up
     * {@link IResource} instances from a variety of locations.
     * <p>
     * One special {@link IResource} implementation you can use is
     * {@link URLResourceWithParams}. It allows you to specify attribute placeholders in
     * the URL that will be replaced with actual values at runtime. The placeholder format
     * is <code>${attribute}</code>. The following common attributes will be substituted:
     * </p>
     * <ul>
     * <li><code>query</code> will be replaced with the current query being processed. If
     * the query has not been provided, this attribute will fall back to an empty string.</li>
     * <li><code>results</code> will be replaced with the number of results requested. If
     * the number of results has not been provided, this attribute will be substituted
     * with an empty string.</li>
     * </ul>
     * <p>
     * Additionally, custom placeholders can be used. Values for the custom placeholders
     * should be provided in the {@link #xmlParameters} attribute.
     * </p>
     * 
     * @label XML Resource
     * @level Basic
     * @group XML data
     */
    @Input
    @Init
    @Processing
    @Attribute
    @Required
    @Internal(configuration = true)
    @ImplementingClasses(classes =
    {
        FileResource.class, URLResourceWithParams.class, URLResource.class
    }, strict = false)
    public IResource xml;

    /**
     * The resource to load XSLT stylesheet from. The XSLT stylesheet is optional and is
     * useful when the source XML stream does not follow the Carrot2 format. The XSLT
     * transformation will be applied to the source XML stream, the transformed XML stream
     * will be deserialized into {@link Document}s.
     * <p>
     * The XSLT {@link IResource} can be provided both on initialization and processing
     * time. The stylesheet provided on initialization will be cached for the life time of
     * the component, while processing-time style sheets will be compiled every time
     * processing is requested and will override the initialization-time stylesheet.
     * </p>
     * <p>
     * To pass additional parameters to the XSLT transformer, use the
     * {@link #xsltParameters} attribute.
     * </p>
     * 
     * @label XSLT Stylesheet
     * @level Medium
     * @group XML transformation
     */
    @Input
    @Init
    @Processing
    @Attribute
    @Internal(configuration = true)
    @ImplementingClasses(classes =
    {
        FileResource.class, URLResourceWithParams.class, URLResource.class
    }, strict = false)
    public IResource xslt;

    /**
     * Values for custom placeholders in the XML URL. If the type of resource provided in
     * the {@link #xml} attribute is {@link URLResourceWithParams}, this map provides
     * values for custom placeholders found in the XML URL. Keys of the map correspond to
     * placeholder names, values of the map will be used to replace the placeholders.
     * Please see {@link #xml} for the placeholder syntax.
     * 
     * @label XML Parameters
     * @level Advanced
     * @group XML data
     */
    @Input
    @Init
    @Processing
    @Attribute
    @Internal(configuration = true)
    public Map<String, String> xmlParameters = ImmutableMap.of();

    /**
     * Parameters to be passed to the XSLT transformer. Keys of the map will be used as
     * parameter names, values of the map as parameter values.
     * 
     * @label XSLT Parameters
     * @level Advanced
     * @group XML transformation
     */
    @Input
    @Init
    @Processing
    @Attribute
    @Internal(configuration = true)
    public Map<String, String> xsltParameters = ImmutableMap.of();

    /**
     * After processing this field may hold the query read from the XML data, if any. For
     * the semantics of this field on input, see {@link #xml}.
     */
    @Input
    @Output
    @Processing
    @Attribute(key = AttributeNames.QUERY, inherit = true)
    public String query;

    /**
     * The maximum number of documents to read from the XML data if {@link #readAll} is
     * <code>false</code>.
     */
    @Input
    @Processing
    @Attribute(key = AttributeNames.RESULTS, inherit = true)
    @IntRange(min = 1)
    public int results = 100;

    /**
     * If <code>true</code>, all documents are read from the input XML stream, regardless
     * of the limit set by {@link #results}.
     * 
     * @label Read all documents
     * @level Basic
     * @group Search query
     */
    @Input
    @Processing
    @Attribute
    public boolean readAll = true;

    /**
     * The title (file name or query attribute, if present) for the search result fetched
     * from the resource.
     */
    @Output
    @Processing
    @Attribute(key = AttributeNames.PROCESSING_RESULT_TITLE, inherit = true)
    public String title;

    /**
     * Documents read from the XML data.
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS, inherit = true)
    public List<Document> documents;

    /**
     * The XSLT resource provided at init. If we want to allow specifying the XSLT both on
     * init and processing, and want to cache the XSLT template provided on init, we must
     * store this reference.
     */
    private IResource initXslt;

    /** A template defined at initialization time, can be null */
    private Templates instanceLevelXslt;

    /** A helper class that groups common functionality for XML/XSLT based data sources. */
    private final XmlDocumentSourceHelper xmlDocumentSourceHelper = new XmlDocumentSourceHelper();

    @Override
    public void init(IControllerContext context)
    {
        super.init(context);

        // Try to initialize the XSLT template, if provided in init attributes
        if (xslt != null)
        {
            initXslt = xslt;
            instanceLevelXslt = xmlDocumentSourceHelper.loadXslt(xslt);
        }
    }

    @Override
    public void process() throws ProcessingException
    {
        try
        {
            title = null;

            final ProcessingResult processingResult = xmlDocumentSourceHelper
                .loadProcessingResult(openResource(xml), resolveStylesheet(),
                    xsltParameters);

            query = (String) processingResult.getAttributes().get(AttributeNames.QUERY);
            documents = processingResult.getDocuments();

            /*
             * Override the result title if query is present.
             */
            if (!StringUtils.isEmpty(query)) title = null;

            if (documents == null)
            {
                documents = Lists.newArrayList();
            }

            // Truncate to the requested number of documents if needed
            if (readAll == false && documents.size() > results)
            {
                documents = documents.subList(0, results);
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("Could not process query: " + e.getMessage(), e);
        }
    }

    /**
     *
     */
    private Templates resolveStylesheet()
    {
        // Resolve the stylesheet to use
        Templates stylesheet = instanceLevelXslt;
        if (xslt != null)
        {
            if (!ObjectUtils.equals(xslt, initXslt))
            {
                stylesheet = xmlDocumentSourceHelper.loadXslt(xslt);
            }
        }
        else
        {
            stylesheet = null;
        }
        return stylesheet;
    }

    /**
     * Opens a {@link IResource}, also handles {@link URLResourceWithParams}s.
     */
    private InputStream openResource(IResource resource) throws IOException
    {
        title = resource.toString();

        if (resource instanceof URLResourceWithParams)
        {
            if (StringUtils.isNotBlank(query))
            {
                title = query;
            }

            // If we got a specialized implementation of the Resource interface,
            // perform substitution of known attributes
            final Map<String, Object> attributes = Maps.newHashMap();

            attributes.put("query", (query != null ? query : ""));
            attributes.put("results", (results != -1 ? results : ""));
            attributes.putAll(xmlParameters);

            return ((URLResourceWithParams) resource).open(attributes);
        }

        if (resource instanceof FileResource)
        {
            title = ((FileResource) resource).getFile().getName();
        }

        // Open the generic Resource instance
        return resource.open();
    }
}
