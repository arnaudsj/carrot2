
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

package org.carrot2.filter.lingo.util.suffixarrays;

import org.carrot2.filter.lingo.util.suffixarrays.wrapper.IntWrapper;


/**
 * Defines the functionality of a suffix sorting algorithm.
 */
public interface SuffixSorter {
    /**
     * Soffix-sorts given input.
     *
     * @param intWrapper integer representation of the input to be sorted
     *
     * @return SuffixArray suffix-sorted input data
     */
    public SuffixArray suffixSort(IntWrapper intWrapper);
}
