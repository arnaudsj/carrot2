
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering;



public abstract class AbstractClusterer {

    protected Object[] objects;
    protected Cluster[] clusters;



    public final void doClustering(Object[] objs) {
        this.objects = objs;
        clusters = initialization(objects);
//        System.out.println("CLUS="+clus[0]);
        do {
            clusters = clustering(clusters, objects);
        } while (!stopCondition());

        clusters = postProcessing(clusters, objects);

    }

    protected abstract Cluster[] initialization(Object[] objects);

    protected abstract Cluster[] postProcessing(Cluster[] clusters, Object[] objects);

    protected abstract Cluster[] clustering(Cluster[] clusters, Object[] objects);

    protected abstract boolean stopCondition();


    public Cluster[] getClusters() {
        return clusters;
    }
}
