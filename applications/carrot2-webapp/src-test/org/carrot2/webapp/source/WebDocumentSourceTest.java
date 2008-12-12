
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

package org.carrot2.webapp.source;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

/**
 *
 */
@RunWith(AnnotationRunner.class)
public class WebDocumentSourceTest extends
    QueryableDocumentSourceTestBase<WebDocumentSource>
{
    @Override
    public Class<WebDocumentSource> getComponentClass()
    {
        return WebDocumentSource.class;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testFirstResults()
    {
        runQuery("obama", getSmallQuerySize());
        final List<Document> documents = getDocuments();
        assertThat(documents.size()).isGreaterThanOrEqualTo(8);
        for (int i = 0; i < 8; i++)
        {
            final List<String> sources = documents.get(i).getField(Document.SOURCES);
            assertThat(sources).as("sources[" + i + "]").contains("Google");
        }

        for (int i = 0; i < documents.size(); i++)
        {
            assertThat(documents.get(i).getId()).isEqualTo(i);
        }
    }
}
