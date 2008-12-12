
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

import java.io.*;
import java.util.Map;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpStatus;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingResult;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.httpclient.HttpUtils;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.xml.TemplatesPool;
import org.xml.sax.SAXException;

/**
 * Exposes the common functionality a {@link IDocumentSource} based on XML/XSLT is likely
 * to need. This helper does note expose any attributes, so that different implementations
 * can decide which attributes they expose.
 */
public class XmlDocumentSourceHelper
{
    /** Precompiled XSLT templates. */
    private final TemplatesPool pool;

    /**
     * URI resolver. Does nothing.
     */
    private final static URIResolver uriResolver = new URIResolver()
    {
        public Source resolve(String href, String base) throws TransformerException
        {
            return null;
        }
    };

    /**
     *
     */
    public XmlDocumentSourceHelper()
    {
        try
        {
            // No template caching.
            this.pool = new TemplatesPool(false);
            this.pool.tFactory.setURIResolver(uriResolver);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads a {@link ProcessingResult} from the provided remote URL, applying XSLT
     * transform if specified. This method can handle gzip-compressed streams if supported
     * by the data source.
     * 
     * @param metadata if a non-<code>null</code> map is provided, request metadata
     *            will be put into the map.
     */
    public ProcessingResult loadProcessingResult(String url, Templates stylesheet,
        Map<String, String> xsltParameters, Map<String, Object> metadata)
        throws Exception
    {
        final HttpUtils.Response response = HttpUtils.doGET(url, null, null);
        final InputStream carrot2XmlStream = response.getPayloadAsStream();

        final int statusCode = response.status;

        if (statusCode == HttpStatus.SC_OK
            || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE
            || statusCode == HttpStatus.SC_BAD_REQUEST)
        {
            metadata.put(SearchEngineResponse.COMPRESSION_KEY, response.compression);
            return loadProcessingResult(carrot2XmlStream, stylesheet, xsltParameters);
        }
        else
        {
            throw new IOException("HTTP error, status code: " + statusCode);
        }
    }

    /**
     * Loads a {@link ProcessingResult} from the provided {@link InputStream}, applying
     * XSLT transform if specified. The provided {@link InputStream} will be closed.
     */
    public ProcessingResult loadProcessingResult(InputStream xml, Templates stylesheet,
        Map<String, String> xsltParameters) throws Exception
    {
        InputStream carrot2XmlStream = null;
        try
        {
            carrot2XmlStream = getCarrot2XmlStream(xml, stylesheet, xsltParameters);
            return ProcessingResult.deserialize(new InputStreamReader(carrot2XmlStream,
                "utf-8"));
        }
        finally
        {
            CloseableUtils.close(carrot2XmlStream, xml);
        }
    }

    /**
     * Returns a Carrot2 XML stream, applying an XSLT transformation if the stylesheet is
     * provided.
     */
    private InputStream getCarrot2XmlStream(InputStream xmlInputStream,
        Templates stylesheet, Map<String, String> xsltParameters)
        throws TransformerConfigurationException, IOException, TransformerException
    {
        // Perform transformation if stylesheet found.
        InputStream carrot2XmlInputStream;
        if (stylesheet != null)
        {
            try
            {
                // Initialize transformer
                final Transformer transformer = pool.newTransformer(stylesheet);
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                // Set XSLT parameters, if any
                if (xsltParameters != null)
                {
                    for (Map.Entry<String, String> entry : xsltParameters.entrySet())
                    {
                        transformer.setParameter(entry.getKey(), entry.getValue());
                    }
                }

                // Perform transformation
                transformer.transform(new StreamSource(xmlInputStream), new StreamResult(
                    outputStream));
                carrot2XmlInputStream = new ByteArrayInputStream(outputStream
                    .toByteArray());
            }
            finally
            {
                CloseableUtils.close(xmlInputStream);
            }
        }
        else
        {
            carrot2XmlInputStream = xmlInputStream;
        }

        return carrot2XmlInputStream;
    }

    /**
     * Loads the XSLT stylesheet from the provided {@link IResource}.
     */
    public Templates loadXslt(IResource xslt)
    {
        InputStream is = null;
        try
        {
            is = xslt.open();
            return pool.compileTemplate(is);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (SAXException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            CloseableUtils.close(is);
        }
    }

}
