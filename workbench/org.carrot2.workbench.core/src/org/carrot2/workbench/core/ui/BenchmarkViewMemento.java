
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.simpleframework.xml.*;

/**
 * Persistent state for {@link BenchmarkView}.
 */
@Root
public final class BenchmarkViewMemento
{
    @Element
    public BenchmarkSettings settings;

    @ElementMap
    public Map<String, Boolean> sectionsExpansionState;
}
