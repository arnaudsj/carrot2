
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

package org.carrot2.filter.lingo.util.suffixarrays;

import org.carrot2.filter.lingo.util.suffixarrays.wrapper.IntWrapper;


/**
 *
 */
public interface LcpSuffixSorter extends SuffixSorter {
    /**
     *
     */
    public LcpSuffixArray lcpSuffixSort(IntWrapper intWrapper);
}
