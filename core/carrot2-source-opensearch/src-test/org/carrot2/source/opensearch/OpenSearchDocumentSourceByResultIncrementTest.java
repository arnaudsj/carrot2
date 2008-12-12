
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

package org.carrot2.source.opensearch;

import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Test cases for {@link OpenSearchDocumentSource} with feeds where start result index is
 * specified.
 */
@RunWith(AnnotationRunner.class)
public class OpenSearchDocumentSourceByResultIncrementTest extends
    QueryableDocumentSourceTestBase<OpenSearchDocumentSource>
{
    @Override
    public Class<OpenSearchDocumentSource> getComponentClass()
    {
        return OpenSearchDocumentSource.class;
    }

    @Override
    protected int getLargeQuerySize()
    {
        return 120;
    }

    @Override
    protected int getSmallQuerySize()
    {
        return 30;
    }

    @Override
    protected boolean hasTotalResultsEstimate()
    {
        return false;
    }

    @Override
    public void prepareComponent()
    {
        super.prepareComponent();

        initAttributes
            .put(
                AttributeUtils.getKey(OpenSearchDocumentSource.class, "feedUrlTemplate"),
                "http://www.indeed.com/opensearch?q=${searchTerms}&start=${startIndex}&limit=${count}");
        initAttributes.put(AttributeUtils.getKey(OpenSearchDocumentSource.class,
            "resultsPerPage"), 50);
        initAttributes.put(AttributeUtils.getKey(OpenSearchDocumentSource.class,
            "maximumResults"), 200);
    }
}
