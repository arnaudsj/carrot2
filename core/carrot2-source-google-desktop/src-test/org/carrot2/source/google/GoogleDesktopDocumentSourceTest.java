
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

package org.carrot2.source.google;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Test cases for {@link GoogleDesktopDocumentSource}. The test cases are ignored by
 * default.
 */
@RunWith(AnnotationRunner.class)
@Ignore
public class GoogleDesktopDocumentSourceTest extends
    QueryableDocumentSourceTestBase<GoogleDesktopDocumentSource>
{
    @Override
    public Class<GoogleDesktopDocumentSource> getComponentClass()
    {
        return GoogleDesktopDocumentSource.class;
    }

    @Override
    protected String getLargeQueryText()
    {
        return "test";
    }

    @Override
    protected String getSmallQueryText()
    {
        return "test";
    }

    @Override
    protected boolean hasTotalResultsEstimate()
    {
        return false;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return false;
    }

    @Test
    public void testQueryUrlFromRegistry()
    {
        assertThat(GoogleDesktopDocumentSource.getQueryUrlFromRegistry()).isNotNull()
            .contains("http://");
    }
}
