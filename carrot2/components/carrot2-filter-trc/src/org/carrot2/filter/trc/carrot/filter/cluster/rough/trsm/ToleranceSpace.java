
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.trsm;



/**
 * Tolerance space for a closed set of object
 */
public interface ToleranceSpace {
    /**
     * Return tolerance class for specified object
     * @param id object's id
     * @return set of objects forming tolerance class
     */
    public Object getToleranceClass(int id);


    /**
     * Return all tolerance classes in this tolerance space
     * @return array of objects representing tolerance classes
     */
    public Object[] getToleranceClasses();

    /**
     * Return binary (n * n) matrix defining tolerance classes.
     * Cell[i,j] = 1 iff object i and j are in the same tolerance classes
     */
//    public int[][] getToleranceMatrix();

}
