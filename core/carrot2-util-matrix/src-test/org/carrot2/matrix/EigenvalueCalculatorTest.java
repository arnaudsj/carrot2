
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix;

import static org.carrot2.matrix.NNITestAssumptions.nativeLapackAvailable;
import static org.carrot2.util.test.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;

import org.junit.Test;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

/**
 * Test cases for {@link EigenvalueCalculator}.
 */
public class EigenvalueCalculatorTest
{
    /** Default delta for comparisons */
    private static final double DELTA = 1e-6;

    /** The test input matrix */
    private NNIDenseDoubleMatrix2D A = (NNIDenseDoubleMatrix2D) NNIDoubleFactory2D.nni
        .make(new double [] []
        {
            {
                1.00, 7.00, 0.00, 1.00, 0.00
            },
            {
                4.00, 2.00, 0.00, 0.00, 0.00
            },
            {
                0.00, 2.00, 3.00, 7.00, 9.00
            },
            {
                1.00, 5.00, 4.00, 4.00, 3.00
            },
            {
                0.00, 0.00, 6.00, 3.00, 5.00
            }
        });

    @Test
    public void testSymmetrical()
    {
        DoubleMatrix2D Asym = A.zMult(A, null, 1, 0, true, false);
        double [] expectedEigenvalues = new EigenvalueDecomposition(Asym)
            .getRealEigenvalues().toArray();
        Arrays.sort(expectedEigenvalues);

        double [] eigenvalues = EigenvalueCalculator.computeEigenvaluesSymmetrical(Asym);
        Arrays.sort(eigenvalues);

        assertThat(expectedEigenvalues).isEqualTo(expectedEigenvalues, DELTA);
    }

    @Test
    public void testAsymmetrical()
    {
        assumeTrue(nativeLapackAvailable());

        double [] eigenvalues = EigenvalueCalculator.computeEigenvaluesNNI(A);
        Arrays.sort(eigenvalues);

        double [] expectedEigenvalues = new EigenvalueDecomposition(A)
            .getRealEigenvalues().toArray();
        Arrays.sort(expectedEigenvalues);

        assertThat(expectedEigenvalues).isEqualTo(expectedEigenvalues, DELTA);
    }
}
