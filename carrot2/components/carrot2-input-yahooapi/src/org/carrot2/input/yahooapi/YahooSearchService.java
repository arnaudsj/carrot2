
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

package org.carrot2.input.yahooapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.httpform.*;
import org.xml.sax.*;


/**
 * A Yahoo Search Service wrapper/ reader.
 * 
 * @author Dawid Weiss
 */
public class YahooSearchService {
    private final static Logger log = Logger.getLogger(YahooSearchService.class);

    /**
     * 
     */
    private YahooSearchServiceDescriptor descriptor;
    
    /**
     * Determines the results parser used by default. 
     * @see #PARSER_SAX
     * @see #PARSER_REGEXP
     */
    private int parserType = PARSER_SAX;
    
    /** @see #RESULT_PARSER_TYPE */
    private final static int PARSER_SAX = 1;

    /** @see #RESULT_PARSER_TYPE */
    private final static int PARSER_REGEXP = 2;

    public YahooSearchService(YahooSearchServiceDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Searches Yahoo and retrieves a maximum of <code>requestedResults</code>
     * snippets. May throw an exception if service is no longer available.
     * 
     * @throws IOException If an I/O exception occurred.
     */ 
    public YahooSearchResult [] query(final String query, final int requestedResults)
        throws IOException
    {
        return query(query, requestedResults, 0);
    }

    /**
     * Searches Yahoo and retrieves a maximum of <code>requestedResults</code>
     * snippets. May throw an exception if service is no longer available.
     * 
     * @throws IOException If an I/O exception occurred.
     */ 
    public YahooSearchResult [] query(final String query, final int requestedResults, 
            int startAt)
        throws IOException 
    {
        final ArrayList result = new ArrayList();
        this.query(query, requestedResults, new YahooSearchResultConsumer() {
            public void add(YahooSearchResult sr) {
                result.add(sr);
            }

            public void estimatedResultsReceived(long estimatedResults)
            {
            }
        }, startAt);
        return (YahooSearchResult []) result.toArray(new YahooSearchResult [result.size()]);
    }

    /**
     * Searches Yahoo and retrieves a maximum of <code>requestedResults</code>
     * snippets. May throw an exception if service is no longer available.
     * 
     * @throws IOException If an I/O exception occurred.
     */
    public void query(final String query, 
        final int requestedResults, final YahooSearchResultConsumer consumer)
        throws IOException
    {
        query(query, requestedResults, consumer, 0);
    }
    
    /**
     * Searches Yahoo and retrieves a maximum of <code>requestedResults</code>
     * snippets (unless it's more than {@link YahooSearchServiceDescriptor#getMaxResultsPerQuery()}.
     * May throw an exception if service is no longer available.
     * 
     * @throws IOException If an I/O exception occurred.
     */
    public void query(final String query, final int requestedResults, 
            YahooSearchResultConsumer consumer, final int startAt) 
        throws IOException
    {
        final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        final HttpClient client = new HttpClient(connectionManager);
        client.getParams().setVersion(HttpVersion.HTTP_1_1);

        final FormActionInfo formActionInfo = descriptor.getFormActionInfo();

        InputStream is = null; 

        final int startFrom = startAt + 1;

        try {
            final YahooResponseHandler handler = new YahooResponseHandler(consumer);

            final XMLReader reader;
            switch (parserType) {
                case PARSER_REGEXP:
                    reader = new RegExpYahooXMLReader();
                    break;
                case PARSER_SAX:
                    reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
                    break;
                default:
                    throw new RuntimeException("Unknown parser type: " + parserType);
            }

            reader.setFeature("http://xml.org/sax/features/validation", false);
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setContentHandler(handler);

            final long perQueryResults = Math.min(descriptor.getMaxResultsPerQuery(), requestedResults);
            final HashMap mappedParameters = new HashMap();
            mappedParameters.put("query.string", query);
            mappedParameters.put("query.startFrom", Integer.toString(startFrom));
            mappedParameters.put("query.results", Long.toString(perQueryResults));

            // Convert from FormActionInfo/FormParameters to HttpClient. This could
            // be implemented in a nicer way perhaps.
            final String method = descriptor.getFormActionInfo().getMethod();
            final FormParameters parameters = descriptor.getFormParameters();
            final HttpMethodBase httpMethod;
            if ("GET".equalsIgnoreCase(method)) {
                httpMethod = new GetMethod();
            } else {
                httpMethod = new PostMethod();
            }
            final String url = formActionInfo.getServiceURL().toExternalForm();
            httpMethod.setURI(new URI(url, false));
            httpMethod.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");

            try {
                HashMap httpHeaders = formActionInfo.getHttpHeaders();
                for (Iterator i = httpHeaders.keySet().iterator(); i.hasNext();) {
                    String header = (String) i.next();
                    String value = (String) httpHeaders.get(header);
                    httpMethod.addRequestHeader(new Header(header, value));
                }
                httpMethod.addRequestHeader(new Header("Accept-Encoding", "gzip"));

                // Now convert parameters.
                ArrayList nameValues = new ArrayList();
                for (Iterator i = parameters.getParametersIterator(); i.hasNext();) {
                    final Parameter p = (Parameter) i.next();
                    final String name = p.getName();
                    final Object value = p.getValue(mappedParameters);
                    if (value instanceof String) {
                        nameValues.add(new NameValuePair(name, (String) value));
                    } else {
                        throw new RuntimeException("Only String mapped parameters supported.");
                    }
                }

                if ("GET".equalsIgnoreCase(method)) {
                    final NameValuePair [] nameValueArray = (NameValuePair [])
                        nameValues.toArray(new NameValuePair[nameValues.size()]);
                    ((GetMethod) httpMethod).setQueryString(nameValueArray);
                } else {
                    for (int i = 0; i < nameValues.size(); i++) {
                        ((PostMethod) httpMethod).addParameter((NameValuePair) nameValues.get(i));
                    }
                }

                log.debug("Querying Yahoo API: " + httpMethod.getURI());
                final int statusCode = client.executeMethod(httpMethod);
                if (statusCode == HttpStatus.SC_OK 
                    || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE 
                    || statusCode == HttpStatus.SC_BAD_REQUEST)
                {
                    is = httpMethod.getResponseBodyAsStream();
                    Header encoded = httpMethod.getResponseHeader("Content-Encoding");
                    if (encoded != null && "gzip".equals(encoded.getValue())) {
                        is = new GZIPInputStream(is);
                    }
                    reader.parse(new InputSource(is));
                    is.close();
                    is = null;
                } else {
                    is = httpMethod.getResponseBodyAsStream();
                    Header encoded = httpMethod.getResponseHeader("Content-Encoding");
                    if (encoded != null && "gzip".equals(encoded.getValue())) {
                        is = new GZIPInputStream(is);
                    }
                    final byte [] message = StreamUtils.readFully(is);
                    throw new IOException("Yahoo returned HTTP Error: "
                            + statusCode + ", HTTP payload: "
                            + new String(message, "iso8859-1"));
                }
            } finally {
                httpMethod.releaseConnection();
            }

            if (handler.isErraneous()) {
                throw new IOException("Yahoo service error: " + handler.getErrorText());
            }

            if (handler.firstResultPosition != startFrom && handler.resultsReturned > 0) {
                log.warn("Returned startFrom different then expected, expected=" 
                        + startFrom + " got: " + handler.firstResultPosition);
            }

            // Correct the number of requested results to the maximum available.
            consumer.estimatedResultsReceived(handler.totalResults);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Problems setting up XML parser: " + e.toString(), e);
        } catch (SAXException e) {
            log.warn("Yahoo API response XML invalid.", e);
            throw new IOException("Problems parsing Yahoo API response: " + e.getMessage());
        } finally {
            connectionManager.shutdown();
        }
    }

    /**
     * 
     */
    final void setUseSaxParser(boolean useSAX) {
        if (useSAX) { 
            this.parserType = PARSER_SAX;
        } else {
            this.parserType = PARSER_REGEXP;
        }
    }

    /**
     * 
     */
    public int getMaxResultsPerQuery()
    {
        return descriptor.getMaxResultsPerQuery();
    }
}
