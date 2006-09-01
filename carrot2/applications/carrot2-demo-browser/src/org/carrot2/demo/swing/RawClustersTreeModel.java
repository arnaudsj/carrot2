
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.carrot2.core.clustering.RawCluster;

/**
 * Implements a tree model that displays the results
 * of clustering.
 */
public class RawClustersTreeModel implements TreeModel {

    private String ROOT_NODE = "<root>";
    
    /** A list of RawCluster objects. */
    private List clusters;

    private ArrayList listeners = new ArrayList(); 

    public RawClustersTreeModel(List clusters) {
        this.clusters = clusters;
    }
    
    public Object getRoot() {
        return ROOT_NODE;
    }

    public int getChildCount(Object node) {
        if (node == ROOT_NODE) {
            return clusters.size();
        } else {
            List l = ((RawCluster) node).getSubclusters();
            if (l==null) return 0;
            else return l.size();
        }
    }

    public boolean isLeaf(Object node) {
        if (node == ROOT_NODE) {
            return false;
        } else {
            List l = ((RawCluster) node).getSubclusters();
            if (l==null) return true;
            else return l.size() == 0;
        }        
    }

    public synchronized void addTreeModelListener(TreeModelListener l) {
        this.listeners.add(l);
    }

    public synchronized void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public Object getChild(Object node, int index) {
        if (node == ROOT_NODE) {
            return clusters.get(index);
        } else {
            List l = ((RawCluster) node).getSubclusters();
            return l.get(index);
        }        
    }

    public int getIndexOfChild(Object parent, Object node) {
        if (parent == ROOT_NODE) {
            return clusters.indexOf(node);
        } else {
            List l = ((RawCluster) parent).getSubclusters();
            return l.indexOf(node);
        }        
    }

    public void valueForPathChanged(TreePath arg0, Object arg1) {
        // ignore
    }
}
