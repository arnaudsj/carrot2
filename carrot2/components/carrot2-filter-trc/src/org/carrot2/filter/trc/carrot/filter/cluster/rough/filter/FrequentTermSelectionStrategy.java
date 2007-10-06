
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.filter;



/**
 * Select terms that have corpus' frequency over given threshold
 */
public class FrequentTermSelectionStrategy implements TermSelectionStrategy{

    private double frequencyThreshold;

    public FrequentTermSelectionStrategy(double threshold) {
        this.frequencyThreshold = threshold;
    }

    /**
     * Select term's with frequencies over given threshold
     * @param termFrequencies array of term's frequencies indexed by term's id
     * @return array of term's ids
     */
    public int[] select(int[] termFrequencies) {
        int[] tmp = new int[termFrequencies.length];
        int j=0;
        for (int i=0; i < termFrequencies.length; i++) {
            if (termFrequencies[i] > frequencyThreshold) {
                tmp[j++] = i;
            }
        }
        //j = size of selected terms
        int[] selected = new int[j];
        System.arraycopy(tmp,0,selected,0,j);
        return selected;
    }


}
