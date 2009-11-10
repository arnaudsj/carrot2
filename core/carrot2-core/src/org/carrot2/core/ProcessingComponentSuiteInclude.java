
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

package org.carrot2.core;

import org.simpleframework.xml.Attribute;

/**
 * An include directive within the {@link ProcessingComponentSuite} specification.
 */
class ProcessingComponentSuiteInclude
{
    @Attribute
    String suite;
}
