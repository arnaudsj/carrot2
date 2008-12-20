
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

package org.carrot2.util;

import java.util.*;

/**
 * Utility methods for working with {@link Set}s.
 */
public final class SetUtils
{
    private SetUtils()
    {
    }

    public static <E> HashSet<E> asHashSet(Set<E> set)
    {
        if (set instanceof HashSet)
        {
            return (HashSet<E>) set;
        }
        else
        {
            return new HashSet<E>(set);
        }
    }
}
