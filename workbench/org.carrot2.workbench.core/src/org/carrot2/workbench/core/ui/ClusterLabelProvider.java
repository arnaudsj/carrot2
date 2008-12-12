
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

package org.carrot2.workbench.core.ui;

import org.carrot2.core.Cluster;
import org.carrot2.core.ClusterWithParent;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provides labels for {@link Cluster} or {@link ClusterWithParent}.
 */
public final class ClusterLabelProvider extends LabelProvider
{
    private Image folderImage = 
        WorkbenchCorePlugin.getImageDescriptor("icons/folder.gif").createImage();

    /*
     * 
     */
    @Override
    public String getText(Object element)
    {
        final String label;
        final int documentCount;

        if (element instanceof Cluster)
        {
            final Cluster cluster = (Cluster) element;
            label = cluster.getLabel();
            documentCount = cluster.size();
        }
        else if (element instanceof ClusterWithParent)
        {
            final Cluster cluster = ((ClusterWithParent) element).cluster;
            label = cluster.getLabel();
            documentCount = cluster.size();
        }
        else
        {
            return "<unknown node: " + element + ">";
        }

        return String.format("%s (%d)", label, documentCount);
    }

    /*
     * 
     */
    @Override
    public Image getImage(Object element)
    {
        return folderImage;
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        folderImage.dispose();
        super.dispose();
    }
}