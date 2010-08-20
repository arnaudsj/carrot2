
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

package org.carrot2.matrix;

import org.apache.mahout.math.matrix.DoubleMatrix1D;
import org.apache.mahout.math.matrix.*;

/**
 * FEST-style assertions for Colt matrices.
 */
@SuppressWarnings("deprecation")
public class MatrixAssertions
{
    /**
     * Creates a {@link DoubleMatrix1DAssertion}.
     * 
     * @param actualMatrix
     * @return the assertion
     */
    public static DoubleMatrix1DAssertion assertThat(DoubleMatrix1D actualMatrix)
    {
        return new DoubleMatrix1DAssertion(actualMatrix);
    }
    /**
     * Creates a {@link DoubleMatrix2DAssertion}.
     * 
     * @param actualMatrix
     * @return the assertion
     */
    public static DoubleMatrix2DAssertion assertThat(DoubleMatrix2D actualMatrix)
    {
        return new DoubleMatrix2DAssertion(actualMatrix);
    }
}
