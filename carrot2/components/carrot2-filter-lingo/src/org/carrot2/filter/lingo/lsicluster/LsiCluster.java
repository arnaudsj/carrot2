
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

package org.carrot2.filter.lingo.lsicluster;

import org.carrot2.filter.lingo.common.Cluster;
import org.carrot2.filter.lingo.common.Snippet;


/**
 * @author stachoo
 */
public class LsiCluster extends Cluster {
    /** */

    /** DOCUMENT ME! */
    private int[] primaryFeatures;

    /**
     *
     */
    public LsiCluster() {
        this((Snippet[]) null);
    }

    /**
     * @param snippets
     */
    public LsiCluster(Snippet[] snippets) {
        this(snippets, (String[]) null);
    }

    /**
     * @param snippets
     * @param labels
     */
    public LsiCluster(Snippet[] snippets, String[] labels) {
        this(snippets, null, labels);
    }

    /**
     * @param snippets
     * @param clusters
     */
    public LsiCluster(Snippet[] snippets, Cluster[] clusters) {
        this(snippets, clusters, null);
    }

    /**
     * @param snippets
     * @param clusters
     * @param labels
     */
    public LsiCluster(Snippet[] snippets, Cluster[] clusters, String[] labels) {
        super(snippets, clusters, labels);
    }

    /**
     * Creates a new LsiCluster object.
     *
     * @param clusters DOCUMENT ME!
     */
    public LsiCluster(Cluster[] clusters) {
        this(null, clusters, null);
    }

    /**
     * @return int[]
     */
    public int[] getPrimaryFeatures() {
        return primaryFeatures;
    }

    /**
     * Sets the primaryFeatures.
     *
     * @param primaryFeatures The primaryFeatures to set
     */
    public void setPrimaryFeatures(int[] primaryFeatures) {
        this.primaryFeatures = primaryFeatures;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public LsiCluster[] getLsiClusters() {
        return (LsiCluster[]) clusters.toArray(new LsiCluster[clusters.size()]);
    }
}
