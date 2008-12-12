
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

package org.carrot2.workbench.core.ui.adapters;

import java.util.Arrays;

import org.carrot2.core.Cluster;
import org.carrot2.core.ClusterWithParent;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;

/**
 * Adapters for transforming {@link ClusterWithParent} to {@link Cluster}
 * objects.
 */
@SuppressWarnings("unchecked")
public final class ClusterAdapterFactory implements IAdapterFactory
{
    private final static Class [] adapted = new Class []
    {
        Cluster.class
    };

    /*
     * 
     */
    public Object getAdapter(Object adaptableObject, Class adapterType)
    {
        if (adaptableObject == null || !(Arrays.asList(adapted).contains(adapterType)))
        {
            return null;
        }

        if (adaptableObject instanceof ClusterWithParent)
        {
            adaptableObject = ((ClusterWithParent) adaptableObject).cluster;
        }

        return adaptableObject;
    }

    /*
     * 
     */
    public Class [] getAdapterList()
    {
        return adapted;
    }

    /**
     * Register 
     */
    public static void register(IAdapterManager adapterManager)
    {
        final ClusterAdapterFactory factory = new ClusterAdapterFactory();
        adapterManager.registerAdapters(factory, ClusterWithParent.class);
    }
}