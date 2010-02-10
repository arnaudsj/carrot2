
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.util.Arrays;

import org.apache.commons.lang.ObjectUtils;

import com.carrotsearch.hppc.*;

/**
 * PCJ compatibility routines.
 */
public final class PcjCompat
{
    public static byte [] toByteArray(BitSet set)
    {
        if (set.length() > 0xff)
            throw new RuntimeException("BitSet conversion to index byte array failed for" 
                + " bitset length: " + set.length());
        
        final int card = (int) set.cardinality();
        final byte [] ba = new byte [card];
        for (int bi = 0, b = set.nextSetBit(0); 
            b >= 0; 
            b = set.nextSetBit(b + 1), bi++)
        {
            ba[bi] = (byte) b;
        }

        return ba;
    }

    public static int [] toIntArray(BitSet set)
    {
        final int card = (int) set.cardinality();
        final int [] ba = new int [card];
        for (int bi = 0, b = set.nextSetBit(0); 
            b >= 0; 
            b = set.nextSetBit(b + 1), bi++)
        {
            ba[bi] = b;
        }

        return ba;
    }

    public static IntIntOpenHashMap clone(IntIntOpenHashMap map)
    {
        IntIntOpenHashMap clone = new IntIntOpenHashMap(map.keys.length);

        clone.keys = new int [map.keys.length];
        System.arraycopy(map.keys, 0, clone.keys, 0, clone.keys.length);

        clone.values = new int [map.values.length];
        System.arraycopy(map.values, 0, clone.values, 0, clone.values.length);

        clone.states = new byte [map.states.length];
        System.arraycopy(map.states, 0, clone.states, 0, clone.states.length);

        clone.assigned = map.assigned;
        clone.deleted = map.deleted;

        return clone;
    }

    public static boolean equals(IntArrayList o1, IntArrayList o2)
    {
        if (o1 == o2) return true;
        if (o1.size() != o2.size()) return false;

        final int [] b1 = o1.buffer;
        final int [] b2 = o2.buffer;
        for (int i = o1.size() - 1; i >= 0; i--)
        {
            if (b1[i] != b2[i]) return false;
        }

        return true;
    }

    public static boolean equals(IntIntOpenHashMap o1, IntIntOpenHashMap o2)
    {
        if (o1 == o2) return true;
        if (o1.size() != o2.size()) return false;

        // In case of open-addressed hash maps, there is no other way to check
        // than just by iteration over keys.
        for (IntIntCursor c1 : o1) 
        {
            if (o2.containsKey(c1.key) && o2.lget() == c1.value)
                continue;
            return false;
        }

        return true;
    }

    public static boolean equals(LongIntOpenHashMap o1,
        LongIntOpenHashMap o2)
    {
        if (o1 == o2) return true;
        if (o1.size() != o2.size()) return false;

        // In case of open-addressed hash maps, there is no other way to check
        // than just by iteration over keys.
        for (LongIntCursor c1 : o1) 
        {
            if (o2.containsKey(c1.key) && o2.lget() == c1.value)
                continue;
            return false;
        }

        return true;
    }

    public static BitSet newIntBitSet(int... setBits)
    {
        final BitSet bs = new BitSet();
        for (int bit : setBits) bs.set(bit);
        return bs;
    }

    public static boolean equals(
        IntObjectOpenHashMap<?> o1, IntObjectOpenHashMap<?> o2)
    {
        if (o1 == o2) return true;
        if (o1.size() != o2.size()) return false;

        // In case of open-addressed hash maps, there is no other way to check
        // than just by iteration over keys.
        for (IntObjectCursor<?> c1 : o1) 
        {
            if (o2.containsKey(c1.key) && ObjectUtils.equals(o2.lget(), c1.value))
                continue;
            return false;
        }

        return true;
    }

    public static int [][] toIntArray(BitSet [] bsets)
    {
        int [][] result = new int [bsets.length][];
        for (int i = 0; i < bsets.length; i++)
            result[i] = toIntArray(bsets[i]);
        return result;
    }

    public static int [][] toIntArray(IntOpenHashSet [] sets)
    {
        int [][] result = new int [sets.length][];
        for (int i = 0; i < sets.length; i++)
        {
            result[i] = sets[i].toArray();
            Arrays.sort(result[i]);
        }
        return result;
    }
}
