
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

package org.carrot2.source.microsoft;

import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Tests Microsoft Live! document source.
 */
@RunWith(AnnotationRunner.class)
public class MicrosoftLiveDocumentSourceTest extends
    MultipageDocumentSourceTestBase<MicrosoftLiveDocumentSource>
{
    @Override
    public Class<MicrosoftLiveDocumentSource> getComponentClass()
    {
        return MicrosoftLiveDocumentSource.class;
    }

    @Override
    protected MultipageSearchEngineMetadata getSearchEngineMetadata()
    {
        return MicrosoftLiveDocumentSource.metadata;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }
    
    protected double slack()
    {
        return 1.4;
    }
}
