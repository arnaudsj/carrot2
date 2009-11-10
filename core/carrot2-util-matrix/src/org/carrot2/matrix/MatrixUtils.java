
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

import java.util.Arrays;

import org.carrot2.util.DoubleComparators;

import bak.pcj.DoubleComparator;
import cern.colt.function.DoubleFunction;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;

/**
 * A set of {@link DoubleMatrix2D} shorthands and utility methods.
 */
public class MatrixUtils
{
    /**
     * Normalizes column vectors of matrix <code>A</code> so that their L2 norm (Euclidean
     * distance) is equal to 1.0.
     * 
     * @param A matrix to normalize
     * @param work a temporary array of <code>A.columns()</code> doubles that will be
     *            overwritten with column's original L2 norms. Supply a non-null pointer
     *            to avoid continuous allocation/freeing of memory when doing calculations
     *            in a loop. If this parameter is <code>null</code>, a new array will be
     *            allocated every time this method is called.
     * @return A with length-normalized columns (for convenience only)
     */
    public static DoubleMatrix2D normalizeColumnL2(DoubleMatrix2D A, double [] work)
    {
        work = prepareWork(A, work);

        // Calculate the L2 norm for each column
        for (int r = 0; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                work[c] += A.getQuick(r, c) * A.getQuick(r, c);
            }
        }

        // Take the square root
        for (int c = 0; c < A.columns(); c++)
        {
            work[c] = Math.sqrt(work[c]);
        }

        // Normalize
        normalizeColumns(A, work);

        return A;
    }

    /**
     * Normalizes column vectors of a sparse matrix <code>A</code> so that their L2 norm
     * (Euclidean distance) is equal to 1.0.
     * 
     * @param A matrix to normalize
     * @param work a temporary array of <code>A.columns()</code> doubles that will be
     *            overwritten with column's original L2 norms. Supply a non-null pointer
     *            to avoid continuous allocation/freeing of memory when doing calculations
     *            in a loop. If this parameter is <code>null</code>, a new array will be
     *            allocated every time this method is called.
     * @return A with length-normalized columns (for convenience only)
     */
    public static DoubleMatrix2D normalizeSparseColumnL2(DoubleMatrix2D A, double [] work)
    {
        IntArrayList rows = new IntArrayList(A.cardinality());
        IntArrayList columns = new IntArrayList(A.cardinality());
        DoubleArrayList values = new DoubleArrayList(A.cardinality());
        A.getNonZeros(rows, columns, values);

        work = prepareWork(A, work);

        // Calculate columns' length
        for (int i = 0; i < values.size(); i++)
        {
            work[columns.get(i)] += values.get(i) * values.get(i);
        }

        // Take the square root
        for (int c = 0; c < A.columns(); c++)
        {
            work[c] = Math.sqrt(work[c]);
        }

        // Normalize
        for (int i = 0; i < values.size(); i++)
        {
            A.setQuick(rows.get(i), columns.get(i), values.get(i) / work[columns.get(i)]);
        }

        return A;
    }

    /**
     * Normalizes column vectors of matrix <code>A</code> so that their L1 norm is equal
     * to 1.0.
     * 
     * @param A matrix to normalize
     * @param work a temporary array of <code>A.columns()</code> doubles that will be
     *            overwritten with column's original L1 norms. Supply a non-null pointer
     *            to avoid continuous allocation/freeing of memory when doing calculations
     *            in a loop. If this parameter is <code>null</code>, a new array will be
     *            allocated every time this method is called.
     * @return A with L1-normalized columns (for convenience only)
     */
    public static DoubleMatrix2D normalizeColumnL1(DoubleMatrix2D A, double [] work)
    {
        work = prepareWork(A, work);

        // Calculate the L1 norm for each column
        for (int r = 0; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                work[c] += A.getQuick(r, c);
            }
        }

        // Normalize
        normalizeColumns(A, work);

        return A;
    }

    /**
     * Prepares a temporary array for normalizing matrix columns.
     */
    private static double [] prepareWork(DoubleMatrix2D A, double [] work)
    {
        // Colt's dense matrices are stored in a row-major format, so the
        // processor's cache will be better used when the rows counter is in the
        // outer loop. To do that we need a temporary double vector
        if (work == null || work.length != A.columns())
        {
            work = new double [A.columns()];
        }
        else
        {
            Arrays.fill(work, 0);
        }
        return work;
    }

    /**
     * A common routine for normalizing columns of a matrix.
     */
    private static void normalizeColumns(DoubleMatrix2D A, double [] work)
    {
        for (int r = A.rows() - 1; r >= 0; r--)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                if (work[c] != 0)
                {
                    A.setQuick(r, c, A.getQuick(r, c) / work[c]);
                }
            }
        }
    }

    /**
     * Computes the orthogonality of matrix A. The orthogonality is computed as a sum of
     * k*(k-1)/2 inner products of A's column vectors, k being the number of columns of A,
     * and then normalized to the 0.0 - 1.0 range.
     * 
     * @param A matrix to compute orthogonality for, must be column length-normalized
     * @return orthogonality of matrix A. 0.0 denotes a perfect orthogonality between
     *         every pair of A's column. 1.0 indicates that all columns of A are parallel.
     */
    public static double computeOrthogonality(DoubleMatrix2D A)
    {
        double orthogonality = 0;

        // Compute pairwise inner products
        DoubleMatrix2D cosines = A.zMult(A, null, 1, 0, true, false);

        for (int r = 0; r < cosines.rows(); r++)
        {
            for (int c = r + 1; c < cosines.columns(); c++)
            {
                orthogonality += cosines.getQuick(r, c);
            }
        }

        return orthogonality / ((cosines.rows() - 1) * cosines.rows() / 2.0);
    }

    /**
     * Computers sparseness of matrix <code>A</code> as a fraction of non-zero elements to
     * the total number of elements.
     * 
     * @return sparseness of <code>A</code>, which is a value between 0.0 (all elements
     *         are zero) and 1.0 (all elements are non-zero)
     */
    public static double computeSparseness(DoubleMatrix2D A)
    {
        int count = 0;

        for (int r = 0; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                if (A.getQuick(r, c) != 0)
                {
                    count++;
                }
            }
        }

        return count / (double) (A.rows() * A.columns());
    }

    /**
     * Finds the first minimum element in each column of matrix A. When calculating
     * minimum values for each column this version should perform better than scanning
     * each column separately.
     * 
     * @param indices an array of <code>A.columns()</code> integers in which indices of
     *            the first minimum element will be stored. If this parameter is
     *            <code>null</code> a new array will be allocated.
     * @param minValues an array of <code>A.columns()</code> doubles in which values of
     *            each column's minimum elements will be stored. If this parameter is
     *            <code>null</code> a new array will be allocated.
     * @return for each column of A the index of the minimum element
     */
    public static int [] minInColumns(DoubleMatrix2D A, int [] indices,
        double [] minValues)
    {
        return inColumns(A, indices, minValues, DoubleComparators.REVERSED_ORDER,
            Functions.identity);
    }

    /**
     * Finds the first maximum element in each column of matrix A. When calculating
     * maximum values for each column this version should perform better than scannning
     * each column separately.
     * 
     * @param A
     * @param indices an array of <code>A.columns()</code> integers in which indices of
     *            the first maximum element will be stored. If this parameter is
     *            <code>null<code> a new array will be allocated.
     * @param maxValues an array of <code>A.columns()</code> doubles in which values of
     *            each column's maximum elements will be stored. If this parameter is
     *            <code>null<code> a new array will be 
     *            allocated.
     * @return for each column of A the index of the maximum element
     */
    public static int [] maxInColumns(DoubleMatrix2D A, int [] indices,
        double [] maxValues)
    {
        return maxInColumns(A, indices, maxValues, Functions.identity);
    }

    public static int [] maxInColumns(DoubleMatrix2D A, int [] indices,
        double [] maxValues, DoubleFunction transform)
    {
        return inColumns(A, indices, maxValues, DoubleComparators.NATURAL_ORDER,
            transform);
    }

    /**
     * Common implementation of finding extreme elements in columns.
     */
    static int [] inColumns(DoubleMatrix2D A, int [] indices, double [] extValues,
        DoubleComparator doubleComparator, DoubleFunction transform)
    {
        if (indices == null)
        {
            indices = new int [A.columns()];
        }

        if (A.columns() == 0 || A.rows() == 0)
        {
            return indices;
        }

        if (extValues == null)
        {
            extValues = new double [A.columns()];
        }

        for (int c = 0; c < A.columns(); c++)
        {
            extValues[c] = transform.apply(A.getQuick(0, c));
        }
        Arrays.fill(indices, 0);

        for (int r = 1; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                final double transformed = transform.apply(A.getQuick(r, c));
                if (doubleComparator.compare(transformed, extValues[c]) > 0)
                {
                    extValues[c] = transformed;
                    indices[c] = r;
                }
            }
        }

        return indices;
    }

    /**
     * Finds the index of the first maximum element in given row of <code>A</code>.
     * 
     * @param A the matrix to search
     * @param row the row to search
     * @return index of the first maximum element or -1 if the input matrix is
     *         <code>null</code> or has zero size.
     */
    public static int maxInRow(DoubleMatrix2D A, int row)
    {
        int index = 0;
        double max = A.getQuick(row, index);
        for (int c = 1; c < A.columns(); c++)
        {
            if (max < A.getQuick(row, c))
            {
                max = A.getQuick(row, c);
                index = c;
            }
        }

        return index;
    }

    /**
     * Calculates the sum of rows of matrix <code>A</code>.
     * 
     * @param sums an array to store the results. If the array is <code>null</code> or
     *            does not match the number of rows in matrix <code>A</code>, a new array
     *            will be created.
     * @return sums of rows of <code>A</code>
     */
    public static double [] sumRows(DoubleMatrix2D A, double [] sums)
    {
        if (sums == null || A.rows() != sums.length)
        {
            sums = new double [A.rows()];
        }
        else
        {
            Arrays.fill(sums, 0);
        }

        for (int r = 0; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                sums[r] += A.getQuick(r, c);
            }
        }

        return sums;
    }
    
    public static double l1Norm(DoubleMatrix1D vector)
    {
        return vector.aggregate(Functions.plus, Functions.identity);
    }
    
    
    public static DoubleMatrix1D columnCentroid(DoubleMatrix2D matrix)
    {
        return null;
    }
}
