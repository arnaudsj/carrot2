
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

package org.carrot2.matrix.factorization;

import org.carrot2.matrix.NNIDoubleFactory2D;
import org.carrot2.matrix.factorization.seeding.RandomSeedingStrategy;
import org.carrot2.matrix.factorization.seeding.ISeedingStrategy;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Sorting;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;

/**
 * Base functionality for {@link IIterativeMatrixFactorization}s.
 */
abstract class IterativeMatrixFactorizationBase extends MatrixFactorizationBase implements
    IIterativeMatrixFactorization
{
    /** The desired number of base vectors */
    protected int k;
    protected static int DEFAULT_K = 15;

    /** The maximum number of iterations the algorithm is allowed to run */
    protected int maxIterations;
    protected static final int DEFAULT_MAX_ITERATIONS = 15;

    /**
     * If the percentage decrease in approximation error becomes smaller than
     * <code>stopThreshold</code>, the algorithm will stop. Note: calculation of
     * approximation error is quite costly. Setting the threshold to -1 turns off
     * approximation error calculation and hence makes the algorithm do the maximum number
     * of iterations.
     */
    protected double stopThreshold;
    protected static double DEFAULT_STOP_THRESHOLD = -1.0;

    /** Seeding strategy */
    protected ISeedingStrategy seedingStrategy;
    protected static final ISeedingStrategy DEFAULT_SEEDING_STRATEGY = new RandomSeedingStrategy(
        0);

    /** Order base vectors according to their 'activity'? */
    protected boolean ordered;
    protected static final boolean DEFAULT_ORDERED = false;

    /** Current approximation error */
    protected double approximationError;

    /** Approximation errors during subsequent iterations */
    protected double [] approximationErrors;

    /** Iteration counter */
    protected int iterationsCompleted;

    /** Sorting aggregates */
    protected double [] aggregates;

    /**
     * @param A
     */
    public IterativeMatrixFactorizationBase(DoubleMatrix2D A)
    {
        super(A);

        this.k = DEFAULT_K;
        this.maxIterations = DEFAULT_MAX_ITERATIONS;
        this.stopThreshold = DEFAULT_STOP_THRESHOLD;
        this.seedingStrategy = DEFAULT_SEEDING_STRATEGY;
        this.ordered = DEFAULT_ORDERED;
        this.approximationErrors = null;
        this.approximationError = -1;
        this.iterationsCompleted = 0;
    }

    /**
     * Sets the number of base vectors <i>k </i>.
     * 
     * @param k the number of base vectors
     */
    public void setK(int k)
    {
        this.k = k;
    }

    /**
     * Returns the number of base vectors <i>k </i>.
     */
    public int getK()
    {
        return k;
    }

    /**
     * @return true if the decrease in the approximation error is smaller than the
     *         <code>stopThreshold</code>
     */
    protected boolean updateApproximationError()
    {
        if (approximationErrors == null)
        {
            approximationErrors = new double [maxIterations + 1];
        }

        // Approximation error
        double newApproximationError = Algebra.DEFAULT.normF(U.zMult(V, null, 1, 0,
            false, true).assign(A, Functions.minus));
        approximationErrors[iterationsCompleted] = newApproximationError;

        if ((approximationError - newApproximationError) / approximationError < stopThreshold)
        {
            approximationError = newApproximationError;
            return true;
        }
        else
        {
            approximationError = newApproximationError;
            return false;
        }
    }

    /**
     * Orders U and V matrices according to the 'activity' of base vectors.
     */
    protected void order()
    {
        DoubleMatrix2D VT = V.viewDice();
        aggregates = new double [VT.rows()];

        for (int i = 0; i < aggregates.length; i++)
        {
            // we take -aggregate to do descending sorting
            aggregates[i] = -VT.viewRow(i).aggregate(Functions.plus, Functions.square);
        }

        // Need to make a copy of aggregates because they get sorted as well
        double [] aggregatesCopy = aggregates.clone();

        V = NNIDoubleFactory2D.asNNIMatrix(Sorting.quickSort.sort(VT, aggregates)
            .viewDice());
        U = NNIDoubleFactory2D.asNNIMatrix(Sorting.quickSort.sort(U.viewDice(),
            aggregatesCopy).viewDice());

        // Revert back to positive values of aggregates
        for (int i = 0; i < aggregates.length; i++)
        {
            aggregates[i] = -aggregates[i];
        }
    }

    /**
     * Returns current {@link ISeedingStrategy}.
     */
    public ISeedingStrategy getSeedingStrategy()
    {
        return seedingStrategy;
    }

    /**
     * Sets new {@link ISeedingStrategy}.
     */
    public void setSeedingStrategy(ISeedingStrategy seedingStrategy)
    {
        this.seedingStrategy = seedingStrategy;
    }

    /**
     * Returns the maximum number of iterations the algorithm is allowed to run.
     */
    public int getMaxIterations()
    {
        return maxIterations;
    }

    /**
     * Sets the maximum number of iterations the algorithm is allowed to run.
     */
    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    /**
     * Returns the algorithms <code>stopThreshold</code>. If the percentage decrease in
     * approximation error becomes smaller than <code>stopThreshold</code>, the algorithm
     * will stop.
     */
    public double getStopThreshold()
    {
        return stopThreshold;
    }

    /**
     * Sets the algorithms <code>stopThreshold</code>. If the percentage decrease in
     * approximation error becomes smaller than <code>stopThreshold</code>, the algorithm
     * will stop.
     * <p>
     * Note: calculation of approximation error is quite costly. Setting the threshold to
     * -1 turns off calculation of the approximation error and hence makes the algorithm
     * do the maximum allowed number of iterations.
     */
    public void setStopThreshold(double stopThreshold)
    {
        this.stopThreshold = stopThreshold;
    }

    /**
     * Returns final approximation error or -1 if the approximation error calculation has
     * been turned off (see {@link #setMaxIterations(int)}.
     * 
     * @return final approximation error or -1
     */
    public double getApproximationError()
    {
        return approximationError;
    }

    /**
     * Returns an array of approximation errors during after subsequent iterations of the
     * algorithm. Element 0 of the array contains the approximation error before the first
     * iteration. The array is <code>null</code> if the approximation error calculation
     * has been turned off (see {@link #setMaxIterations(int)}.
     */
    public double [] getApproximationErrors()
    {
        return approximationErrors;
    }

    public int getIterationsCompleted()
    {
        return iterationsCompleted;
    }

    /**
     * Returns <code>true</code> when the factorization is set to generate an ordered
     * basis.
     */
    public boolean isOrdered()
    {
        return ordered;
    }

    /**
     * Set to <code>true</code> to generate an ordered basis.
     */
    public void setOrdered(boolean ordered)
    {
        this.ordered = ordered;
    }

    /**
     * Returns column aggregates for a sorted factorization, and <code>null</code> for an
     * unsorted factorization.
     */
    public double [] getAggregates()
    {
        return aggregates;
    }
}