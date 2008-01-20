
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

import org.carrot2.matrix.MatrixUtils;

import cern.colt.function.DoubleDoubleFunction;
import cern.colt.function.DoubleFunction;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;

/**
 * A concrete implementation of the MatrixFacrorization interface. This
 * implementation preforms Local Non-negative Matrix Factorization by
 * minimisation of Kullback-Leibler divergence between A and UV' using
 * multiplicative updating.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LocalNonnegativeMatrixFactorization extends
    IterativeMatrixFactorizationBase
{
    /**
     * Creates the LocalNonnegativeMatrixFactorization object for matrix A.
     * Before accessing results, perform computations by calling the
     * {@link #compute()}method.
     * 
     * @param A matrix to be factorized
     */
    public LocalNonnegativeMatrixFactorization(DoubleMatrix2D A)
    {
        super(A);
    }

    /*
     * (non-Javadoc)
     */
    public void compute()
    {
        //      Prototype Matlab code for the LNMF
        //        
        //      function [U, V, C] = lnmf(A)
        //        [m, n] = size(A);
        //        k = 2; % the desired number of base vectors
        //        maxiter = 50; % the number of iterations
        //        eps = 1e-9; % machine epsilon
        //        
        //        U = rand(m, k); % initialise U randomly
        //        V = rand(n, k); % initialise V randomly
        //        O = ones(m, m); % a matrix of ones
        //        
        //        for iter = 1:maxiter
        //            V = sqrt( V .* (((A+eps) ./ (U*V'+eps))' * U)); % update V
        //            U = U .* (((A+eps)./(U*V'+eps)) * V); % update U
        //            U = U ./ (O * U); % normalise U's columns
        //            C(1, iter) = norm((A-U*V'), 'fro'); % approximation quality
        //        end
        //

        double eps = 1e-9;

        // Seed U and V with initial values
        U = doubleFactory2D.make(A.rows(), k);
        V = doubleFactory2D.make(A.columns(), k);
        seedingStrategy.seed(A, U, V);

        // Temporary matrices
        DoubleMatrix2D Aeps = A.copy().assign(Functions.plus(eps));
        DoubleMatrix2D UV = doubleFactory2D.make(A.rows(), A.columns());
        DoubleMatrix2D VT = doubleFactory2D.make(A.columns(), k);
        DoubleMatrix2D UT = doubleFactory2D.make(A.rows(), k);
        double [] work = new double[U.columns()];
        
        // Colt functions
        DoubleDoubleFunction invDiv = Functions.swapArgs(Functions.div);
        DoubleDoubleFunction sqrtMult = Functions.chain(Functions.sqrt,
            Functions.mult);
        DoubleFunction plusEps = Functions.plus(eps);

        if (stopThreshold >= 0)
        {
            updateApproximationError();
        }

        for (int i = 0; i < maxIterations; i++)
        {
            // Update V
            U.zMult(V, UV, 1, 0, false, true); // UV <- U*V'
            UV.assign(plusEps); // UV <- UV + eps
            UV.assign(Aeps, invDiv); // UV <- Aeps ./ UV
            UV.zMult(U, VT, 1, 0, true, false); // VT <- UV' * U
            V.assign(VT, sqrtMult); // V <- sqrt(V .* VT)

            // Update U
            U.zMult(V, UV, 1, 0, false, true); // UV <- U*V'
            UV.assign(plusEps); // UV <- UV + eps
            UV.assign(Aeps, invDiv); // UV <- Aeps ./ UV
            UV.zMult(V, UT, 1, 0, false, false); // UT <- UV * V
            U.assign(UT, Functions.mult); // U <- U .* UT

            MatrixUtils.normaliseColumnL1(U, work);

            iterationsCompleted++;
            if (stopThreshold >= 0)
            {
                if (updateApproximationError())
                {
                    break;
                }
            }
        }
        
        if (ordered)
        {
            order();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "LNMF-" + seedingStrategy.toString();
    }
}