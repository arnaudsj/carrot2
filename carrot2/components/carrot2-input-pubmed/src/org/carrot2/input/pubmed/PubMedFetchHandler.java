
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

package org.carrot2.input.pubmed;

import org.carrot2.core.ProcessingException;
import org.xml.sax.*;

/**
 * A SAX content handler that collects the contents of PubMed abstracts.
 * 
 * @author Stanislaw Osinski
 */
public class PubMedFetchHandler implements ContentHandler
{
    /** Collects PubMed results */
    private PubMedSearchResultConsumer consumer;

    /** For locating/storing IDs */
    private StringBuffer text;
    private boolean inArticleTitle;
    private boolean inArticleAbstract;
    private boolean inArticleId;
    private String title;
    private String snippet;
    private String id;

    public PubMedFetchHandler(PubMedSearchResultConsumer consumer)
    {
        this.consumer = consumer;
        this.text = new StringBuffer();
    }

    public void startDocument() throws SAXException
    {
        this.text.setLength(0);
    }

    public void startElement(String namespaceURI, String localName,
        String qName, Attributes atts) throws SAXException
    {
        if ("PubmedArticle".equals(localName))
        {
            title = snippet = id = null;
        }

        inArticleTitle = "ArticleTitle".equals(localName);
        inArticleAbstract = "AbstractText".equals(localName);
        inArticleId = "PMID".equals(localName);
    }

    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException
    {
        if (inArticleTitle)
        {
            title = text.toString();
            inArticleTitle = false;
        }
        else if (inArticleAbstract)
        {
            snippet = text.toString();
            inArticleAbstract = false;
        }
        else if (inArticleId)
        {
            id = text.toString();
            inArticleId = false;
        }
        else if ("PubmedArticle".equals(localName))
        {
            // Push the result
            try
            {
                consumer.add(new PubMedSearchResult(id, title, snippet,
                    PubMedSearchService.E_FETCH_URL + "?db=pubmed&id=" + id
                        + "&retmode=html&rettype=abstract"));
            }
            catch (ProcessingException e)
            {
                throw new SAXException(
                    "Problem with the serach results consumer", e);

            }
        }
        text.setLength(0);
    }

    public void characters(char [] ch, int start, int length)
        throws SAXException
    {
        text.append(ch, start, length);
    }

    public void endDocument() throws SAXException
    {
    }

    public void endPrefixMapping(String prefix) throws SAXException
    {
    }

    public void ignorableWhitespace(char [] ch, int start, int length)
        throws SAXException
    {
    }

    public void processingInstruction(String target, String data)
        throws SAXException
    {
    }

    public void setDocumentLocator(Locator locator)
    {
    }

    public void skippedEntity(String name) throws SAXException
    {
    }

    public void startPrefixMapping(String prefix, String uri)
        throws SAXException
    {
    }

}
