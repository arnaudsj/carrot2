
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

package org.carrot2.source.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;

import org.apache.commons.lang.ObjectUtils;
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
 */
@Bindable(prefix = "XmlDocumentSource")
public class XmlDocumentSource extends ProcessingComponentBase implements IDocumentSource
{
    /**
     * The resource to load XML data from. You can either create instances of
     * {@link IResource} implementations directly or use {@link ResourceUtils} to look up
     * {@link IResource} instances from a variety of locations.
     * <p>
     * One special {@link IResource} implementation you can use is
     * {@link URLResourceWithParams}. It allows you to specify attribute place holders
     * in the URL that will be replaced during runtime. The place holder format is
     * <code>${attribute}</code>. The following attributes will be resolved:
     * </p>
     * <ul>
     * <li><code>query</code> will be replaced with the current query being processed. If
     * the query has not been provided, this attribute will be substituted with an empty
     * string.</li>
     * <li><code>results</code> will be replaced with the number of results requested. If
     * the number of results has not been provided, this attribute will be substituted
     * with an empty string.</li>
     * </ul>
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
    @ImplementingClasses(classes =
    {
        FileResource.class, URLResourceWithParams.class, URLResource.class
    }, strict = false)
    public IResource xslt;

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
    public Map<String, String> xsltParameters = ImmutableMap.of();

    /**
     * Before processing: query to be used to fetch the from a remote XML stream. After
     * processing: the query read from the XML data, if any.
     */
    @Input
    @Output
    @Processing
    @Attribute(key = AttributeNames.QUERY)
    public String query;

    /**
     * The maximum number of documents to read from the XML data.
     */
    @Input
    @Processing
    @Attribute(key = AttributeNames.RESULTS)
    @IntRange(min = 1)
    public int results = 100;

    /**
     * Documents read from the XML data.
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
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

    /**
     * Creates a new {@link XmlDocumentSource}.
     */
    public XmlDocumentSource()
    {
    }

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
            final ProcessingResult processingResult = xmlDocumentSourceHelper
                .loadProcessingResult(openResource(xml), resolveStylesheet(),
                    xsltParameters);

            query = (String) processingResult.getAttributes().get(AttributeNames.QUERY);
            documents = processingResult.getDocuments();

            if (documents == null)
            {
                documents = Lists.newArrayList();
            }
            
            // Truncate to the requested number of documents if needed
            if (documents.size() > results)
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
        InputStream inputStream;
        if (resource instanceof URLResourceWithParams)
        {
            // If we got a specialized implementation of the Resource interface,
            // perform substitution of known attributes
            Map<String, Object> attributes = Maps.newHashMap();

            attributes.put("query", (query != null ? query : ""));
            attributes.put("results", (results != -1 ? results : ""));

            inputStream = ((URLResourceWithParams) resource).open(attributes);
        }
        else
        {
            // Open the generic Resource instance
            inputStream = resource.open();
        }
        return inputStream;
    }
}
