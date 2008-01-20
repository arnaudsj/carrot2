
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

package org.carrot2.util.suffixarrays.wrapper;

/**
 * A base class for int wrappers.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public abstract class IntWrapperBase implements IntWrapper
{

    /** Integer data */
    protected int [] intData;

    /**
     * Creates an empty IntWrapper with a null intData array.
     */
    protected IntWrapperBase()
    {
        // do nothing
    }

    /**
     * Creates an IntWrapper for given int data. Note: the int data must be
     * terminated with a '-1' value.
     * 
     * @param intData
     */
    protected IntWrapperBase(int [] intData)
    {
        this.intData = intData;
    }

    /*
     * (non-Javadoc)
     */
    public int [] asIntArray()
    {
        return intData;
    }

    /*
     * (non-Javadoc)
     */
    public int length()
    {
        return intData.length - 1;
    }

    /*
     * (non-Javadoc)
     */
    public void reverse()
    {
        int temp;

        for (int i = 0; i < ((intData.length - 1) / 2); i++)
        {
            temp = intData[i];
            intData[i] = intData[intData.length - i - 2];
            intData[intData.length - i - 2] = temp;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer("[ ");

        for (int i = 0; i < intData.length; i++)
        {
            stringBuffer.append(Integer.toString(intData[i]));
            stringBuffer.append(" ");
        }

        stringBuffer.append("]");

        return stringBuffer.toString();
    }
}