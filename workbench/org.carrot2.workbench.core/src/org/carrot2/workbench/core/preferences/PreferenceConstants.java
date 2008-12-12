
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

package org.carrot2.workbench.core.preferences;

import org.carrot2.workbench.core.ui.*;

/**
 * Constant definitions for plug-in preferences
 */
public final class PreferenceConstants
{
    /**
     * Automatically re-render editors after attributes change.
     */
    public static final String AUTO_UPDATE = "auto-update.enabled"; 

    /**
     * {@link #AUTO_UPDATE} delay. 
     */
    public static final String AUTO_UPDATE_DELAY = "auto-update.delay";

    /**
     * Attribute grouping layout, attributes view.
     */
    public static final String GROUPING_ATTRIBUTE_VIEW = "attributes-view.layout";

    /**
     * Attribute grouping layout, input view.
     */
    public static final String GROUPING_INPUT_VIEW = "search-input-view.layout"; 

    /**
     * Attribute grouping layout, {@link SearchEditorSections#ATTRIBUTES} section
     * of {@link SearchEditor}.
     */
    public static final String GROUPING_EDITOR_PANEL = "search-editor.layout";

    /**
     * Show only required attributes in the {@link SearchInputView}.
     */
    public static final String SHOW_OPTIONAL = "search-input-view.show-required";

    /**
     * Automatically show extended attribute info when tooltip is shown. 
     */
    public static final String ATTRIBUTE_INFO_SYNC = "attribute-info-view.sync";

    /*
     * 
     */
    private PreferenceConstants()
    {
        // No instances.
    }

    /**
     * Returns preference key for a given editor section's visibility.
     */
    public static String getSectionVisibilityKey(SearchEditorSections s)
    {
        return s.name + ".visible";
    }

    /**
     * Returns preference key for a given editor section's weight.
     */
    public static String getSectionWeightKey(SearchEditorSections s)
    {
        return s.name + ".weight";
    }
}
