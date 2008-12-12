
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

package org.carrot2.source.boss;

import org.carrot2.core.IDocumentSource;
import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Tests Yahoo Boss {@link IDocumentSource}.
 */
@RunWith(AnnotationRunner.class)
public class BossDocumentSourceTest extends
    MultipageDocumentSourceTestBase<BossDocumentSource>
{
    @Override
    public Class<BossDocumentSource> getComponentClass()
    {
        return BossDocumentSource.class;
    }

    @Override
    protected MultipageSearchEngineMetadata getSearchEngineMetadata()
    {
        return BossSearchService.DEFAULT_METADATA;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }
}
