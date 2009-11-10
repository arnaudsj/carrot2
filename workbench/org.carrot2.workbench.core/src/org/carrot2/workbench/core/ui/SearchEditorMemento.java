
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

package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.carrot2.workbench.core.ui.SearchEditor.PanelName;
import org.carrot2.workbench.core.ui.SearchEditor.PanelState;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 * Persistent state for {@link SearchInputView}.
 */
@Root
public final class SearchEditorMemento
{
    @ElementMap(required = false)
    public Map<PanelName, PanelState> panels;

    /**
     * Expansion state for sections inside {@link PanelName#ATTRIBUTES} panel.
     */
    @ElementMap
    public Map<String, Boolean> sectionsExpansionState;
}
