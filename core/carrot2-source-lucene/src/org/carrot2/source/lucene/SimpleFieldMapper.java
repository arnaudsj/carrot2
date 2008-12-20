
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

package org.carrot2.source.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * A simple {@link IFieldMapper} with one-to-one mapping between the default title, url and
 * summary fields.
 */
@Bindable
public class SimpleFieldMapper implements IFieldMapper
{
    /**
     * Each {@link org.carrot2.core.Document} will have an additional
     * field named like this constant, pointing to the original
     * Lucene {@link Document}.
     */
    public static final String LUCENE_DOCUMENT = "lucene.doc";

    /**
     * Document title field name.
     * 
     * @label Document title field
     * @group Index field mapping
     * @level Medium
     */
    @Input
    @Attribute
    @Init
    @Processing
    public String titleField;

    /**
     * Document content field name.
     * 
     * @label Document content field
     * @group Index field mapping
     * @level Medium
     */
    @Input
    @Attribute
    @Init
    @Processing
    public String contentField;

    /**
     * Document URL field name.
     * 
     * @label Document URL field
     * @group Index field mapping
     * @level Medium
     */
    @Input
    @Attribute
    @Init
    @Processing
    public String urlField;

    /**
     * Index search field names. If not specified, title and content fields are used.
     * 
     * @label Search fields
     * @group Index field mapping
     * @level Medium
     */
    @Input
    @Attribute
    @Init
    @Processing
    public List<String> searchFields;

    /**
     * Snippet formatter for the highlighter. Highlighter is not used if <code>null</code>.
     * 
     * @label Formatter
     * @group Highlighter
     * @level Advanced
     */
    @Input
    @Attribute
    @Init
    @Processing
    @ImplementingClasses(classes =
    {
        PlainTextFormatter.class, SimpleHTMLFormatter.class
    }, strict = false)
    public Formatter formatter = new PlainTextFormatter();

    /**
     * Number of context fragments for the highlighter.
     * 
     * @label Context fragments
     * @group Highlighter
     * @level Advanced
     */
    @Input
    @Attribute
    @Init
    @Processing
    @IntRange(min = 1)
    public int contextFragments = 3;

    /**
     * A string used to join context fragments when highlighting.
     * 
     * @label Join string
     * @group Highlighter
     * @level Advanced
     */
    @Input
    @Attribute
    @Init
    @Processing
    public String fragmentJoin = "...";

    /**
     * Last initialized highlighter.
     */
    private Highlighter highlighter;

    /**
     * Last received {@link Query} object in
     * {@link #map(Query, Document, org.carrot2.core.Document)}.
     */
    private Query query;

    /*
     * 
     */
    public String [] getSearchFields()
    {
        if (searchFields == null || searchFields.size() == 0)
        {
            ArrayList<String> fields = new ArrayList<String>();
            if (!StringUtils.isEmpty(titleField))
            {
                fields.add(titleField);
            }

            if (!StringUtils.isEmpty(contentField))
            {
                fields.add(contentField);
            }

            return fields.toArray(new String [fields.size()]);
        }
        return searchFields.toArray(new String [searchFields.size()]);
    }

    /*
     * 
     */
    public void map(Query luceneQuery, Analyzer analyzer, Document luceneDoc,
        org.carrot2.core.Document doc)
    {
        if (luceneQuery != query)
        {
            this.query = luceneQuery;
            resetHighlighter();
        }

        /*
         * Map title and url
         */
        String value = fieldValue(titleField, luceneDoc);
        if (value != null)
        {
            doc.addField(org.carrot2.core.Document.TITLE, value);
        }

        value = fieldValue(urlField, luceneDoc);
        if (value != null)
        {
            doc.addField(org.carrot2.core.Document.CONTENT_URL, value);
        }

        /*
         * Map content field.
         */
        value = fieldValue(contentField, luceneDoc);
        if (value != null)
        {
            try
            {
                final String summary;
                if (this.highlighter != null)
                {
                    final String [] fragments = highlighter.getBestFragments(analyzer,
                        contentField, value, contextFragments);

                    summary = StringUtils.join(fragments, fragmentJoin);
                }
                else
                {
                    summary = value;
                }
                doc.addField(org.carrot2.core.Document.SUMMARY, summary);
            }
            catch (IOException e)
            {
                throw ExceptionUtils.wrapAsRuntimeException(e);
            }
        }
        
        /*
         * Add a reference to Lucene document.
         */
        doc.addField(LUCENE_DOCUMENT, luceneDoc);
    }

    /*
     * 
     */
    private String fieldValue(String fieldName, Document doc)
    {
        if (StringUtils.isEmpty(fieldName))
        {
            return null;
        }

        final String value = doc.get(fieldName);
        return value;
    }

    /*
     * 
     */
    private void resetHighlighter()
    {
        if (formatter != null)
        {
            this.highlighter = new Highlighter(formatter, new QueryScorer(query));
            this.highlighter.setEncoder(new DefaultEncoder());
        }
        else
        {
            this.highlighter = null;
        }
    }
}
