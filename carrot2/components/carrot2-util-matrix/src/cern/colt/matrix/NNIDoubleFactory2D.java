
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

package cern.colt.matrix;

import cern.colt.matrix.impl.NNIDenseDoubleMatrix2D;

/**
 * A factory for convenient construction of 2D matrices backed by the NNI
 * interface. Use the <code>nni</code> static field to obtain the instance of
 * the NNI matrix factory. For more details on using this factory see
 * {@link cern.colt.matrix.DoubleFactory2D}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class NNIDoubleFactory2D extends DoubleFactory2D
{

    /**
     * A factory producing NNI native matrices
     */
    public static final DoubleFactory2D nni = new NNIDoubleFactory2D();

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleFactory2D#make(double[][])
     */
    public DoubleMatrix2D make(double [][] values)
    {
        if (this == nni)
        {
            return new NNIDenseDoubleMatrix2D(values);
        }
        else
        {
            return super.make(values);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleFactory2D#make(int, int)
     */
    public DoubleMatrix2D make(int rows, int columns)
    {
        if (this == nni)
        {
            return new NNIDenseDoubleMatrix2D(rows, columns);
        }
        else
        {
            return super.make(rows, columns);
        }
    }
    
    /**
     * @param A
     */
    public static DoubleMatrix2D asNNIMatrix(DoubleMatrix2D A)
    {
        return nni.make(A.rows(), A.columns()).assign(A);
    }
}