
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

package org.carrot2.text.preprocessing;

import bak.pcj.map.IntKeyIntMap;

/**
 * Represents a general substring. Contains information on the substring's boundaries and
 * absolute frequency.
 */
class Substring
{
    /** The substring's unique id */
    public int id;

    /** Substring's start position */
    public int from;

    /** Substring's end position */
    public int to;

    /** Substring's absolute frequency */
    public int frequency;

    /** This substring's frequency across documents */
    public IntKeyIntMap tfByDocument;

    /** Used to properly aggregate phrase frequencies */
    public int documentIndexToOffset = -1;

    public Substring(int id, int from, int to, int frequency)
    {
        this.id = id;
        this.from = from;
        this.to = to;
        this.frequency = frequency;
    }

    public boolean isEquivalentTo(Substring substring, int [] tokensWordIndex,
        int [] wordsStemIndex)
    {
        if ((substring.to - substring.from) != (to - from))
        {
            return false;
        }

        for (int i = 0; i < (to - from); i++)
        {
            if (wordsStemIndex[tokensWordIndex[substring.from + i]] != wordsStemIndex[tokensWordIndex[from
                + i]])
            {
                return false;
            }
        }

        return true;
    }
}
