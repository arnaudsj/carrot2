
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.data;

import java.text.NumberFormat;
import java.util.*;

public class TermUtils {

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    static {
        numberFormat.setMinimumFractionDigits(4);
    }
    
    /**
     * Normalize weight for set of terms
     * @param terms
     */
    public static void normalizeTermWeight(Set terms) {
        double acc = 0;
        for(Iterator i = terms.iterator(); i.hasNext(); ) {
            WeightedTerm term = (WeightedTerm) i.next();
            acc += term.getWeight() * term.getWeight();
        }
        acc = Math.sqrt(acc);
        for(Iterator i = terms.iterator(); i.hasNext(); ) {
            WeightedTerm term = (WeightedTerm) i.next();
            term.setWeight(term.getWeight() / acc);
        }
    }

    public static String format(double d) {
        return numberFormat.format(d);
    }

    public static Set sortByWeight(Set terms) {
        SortedSet sorted = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((WeightedTerm)o1).getWeight() > ((WeightedTerm)o2).getWeight() ? -1 : 1;
            }
        });
        sorted.addAll(terms);
        return sorted;
    }

    public static String[] extractLabels(Term[] terms) {
        String[] labels = new String[terms.length];
        for (int i=0; i<labels.length; i++) {
            labels[i] = terms[i].getOriginalTerm();
        }
        return labels;
    }
    
}
