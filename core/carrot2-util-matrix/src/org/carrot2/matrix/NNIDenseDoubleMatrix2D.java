
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

import nni.BLAS;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * A very crude native implementation of Colt's @link cern.colt.matrix.DoubleMatrix2D
 * based on the Native Numerical Interface (NNI). For the time being, the only method that
 * uses the native routines is the Level 3 zMult(). The other methods use the
 * implementations provided in DenseDoubleMatrix2D.
 */
public class NNIDenseDoubleMatrix2D extends DenseDoubleMatrix2D
{
    private static final long serialVersionUID = 1L;

    public NNIDenseDoubleMatrix2D(double [][] values)
    {
        super(values);
    }

    public NNIDenseDoubleMatrix2D(int rows, int columns)
    {
        super(rows, columns);
    }

    public NNIDenseDoubleMatrix2D(int rows, int columns, double [] elements, int rowZero,
        int columnZero, int rowStride, int columnStride)
    {
        super(rows, columns, elements, rowZero, columnZero, rowStride, columnStride);
    }

    public DoubleMatrix1D zMult(DoubleMatrix1D y, DoubleMatrix1D z, double alpha,
        double beta, boolean transposeA)
    {
        return super.zMult(y, z, alpha, beta, transposeA);

    }

    /**
     * A native implementation of Colt's original multiplication method method.
     * <p>
     * NOTE: this method will use native calls only when:
     * <ul>
     * <li>all input matrices are @link DenseDoubleMatrix2D or its subclasses (e.g. @link
     * NNIDenseDoubleMatrix2D)
     * <li>none of the input matrices is a view
     * <li>the dynamic libraries required by the NNI are available
     * </ul>
     */
    public DoubleMatrix2D zMult(DoubleMatrix2D B, DoubleMatrix2D C, double alpha,
        double beta, boolean transposeA, boolean transposeB)
    {
        // A workaround for a bug in DenseDoubleMatrix2D.
        // If B is a SelectedDenseDoubleMatrix the implementation of this method
        // throws a ClassCastException. The workaround is to swap and transpose
        // the arguments and then transpose the result. As SelectedDenseDoubleMatrix2D is
        // package-private, if it was loaded with a different class loader than
        // the one used for this class it would give a VerificationError if we referred
        // to it directly here. Hence the hacky string comparison here.
        // 
        if (B.getClass().getName().endsWith("SelectedDenseDoubleMatrix2D"))
        {
            return B.zMult(this, C, alpha, beta, !transposeB, !transposeA).viewDice();
        }

        // Check the sizes
        int rowsB = (transposeB ? B.columns() : B.rows());
        int columnsB = (transposeB ? B.rows() : B.columns());
        int rowsA = (transposeA ? columns() : rows());
        int columnsA = (transposeA ? rows() : columns());

        if (C == null)
        {
            C = new NNIDenseDoubleMatrix2D(rowsA, columnsB);
        }

        if (this == C || B == C)
        {
            throw new IllegalArgumentException("Matrices must not be identical");
        }

        final int rowsC = C.rows();
        final int columnsC = C.columns();

        if (rowsB != columnsA)
        {
            throw new IllegalArgumentException("Matrix2D inner dimensions must agree:"
                + toStringShort() + ", " + B.toStringShort());
        }

        if (rowsC != rowsA || columnsC != columnsB)
        {
            throw new IllegalArgumentException("Incompatibile result matrix: "
                + toStringShort() + ", " + B.toStringShort() + ", " + C.toStringShort());
        }

        // Need native BLAS, dense matrices and no views to operate
        // Default to Colt's implementation otherwise
        if (!NNIInterface.isNativeBlasAvailable()
            || (!(B instanceof NNIDenseDoubleMatrix2D))
            || (!(C instanceof NNIDenseDoubleMatrix2D)) || isView()
            || ((NNIDenseDoubleMatrix2D) B).isView()
            || ((NNIDenseDoubleMatrix2D) C).isView())
        {
            return super.zMult(B, C, alpha, beta, transposeA, transposeB);
        }

        // Get the matrices data. It is in row-major format.
        final double [] dataA = this.elements;
        final double [] dataB = ((NNIDenseDoubleMatrix2D) B).getData();
        final double [] dataC = ((NNIDenseDoubleMatrix2D) C).getData();

        // Multiply
        BLAS.gemm(BLAS.RowMajor, transposeA ? BLAS.Trans : BLAS.NoTrans,
            transposeB ? BLAS.Trans : BLAS.NoTrans, C.rows(), C.columns(), columnsA,
            alpha, dataA, Math.max(1, columns), dataB, Math.max(1, B.columns()), beta,
            dataC, Math.max(1, C.columns()));

        return C;
    }

    /**
     * Checks if the provided {@link DenseDoubleMatrix2D} is a view.
     */
    public boolean isView()
    {
        return super.isView();
    }

    /**
     * Exposes the internal representation of the contents of this matrix.
     */
    public double [] getData()
    {
        return elements;
    }

    /**
     * Transposes the matrix in place.
     */
    public void transpose()
    {
        int tmp;

        tmp = rows;
        rows = columns;
        columns = tmp;

        tmp = rowStride;
        rowStride = columnStride;
        columnStride = tmp;

        tmp = rowZero;
        rowZero = columnZero;
        columnZero = tmp;
    }

    //
    // The superclass overrides only equals(), so override both methods below
    // to make equals() and hashCode() consistent at least for this subclass.
    // 
    
    @Override
    public boolean equals(Object matrix)
    {
        return this == matrix;
    }

    @Override
    public int hashCode()
    {
        return System.identityHashCode(this);
    }
}
