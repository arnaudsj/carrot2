
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core;

import junit.framework.TestSuite;

import org.carrot2.workbench.core.helpers.SimpleXmlMementoTest;
import org.carrot2.workbench.editors.factory.*;

public class AllTests extends TestSuite
{
    public static TestSuite suite()
    {
        return new AllTests();
    }

    public AllTests()
    {
        this.addTestSuite(ProcessingJobTest.class);
        this.addTestSuite(TypeEditorWrapperTest.class);
        this.addTestSuite(DedicatedEditorWrapperTest.class);
        this.addTestSuite(FactoryTest.class);
        this.addTestSuite(SimpleXmlMementoTest.class);
    }

}
