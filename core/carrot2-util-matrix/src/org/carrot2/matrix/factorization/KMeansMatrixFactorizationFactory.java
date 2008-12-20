
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

import cern.colt.matrix.DoubleMatrix2D;

/**
 * {@link KMeansMatrixFactorization} factory.
 */
public class KMeansMatrixFactorizationFactory extends IterativeMatrixFactorizationFactory
{
    public IMatrixFactorization factorize(DoubleMatrix2D A)
    {
        KMeansMatrixFactorization factorization = new KMeansMatrixFactorization(A);
        factorization.setK(k);
        factorization.setMaxIterations(maxIterations);
        factorization.setStopThreshold(stopThreshold);
        factorization.setDoubleFactory2D(getDoubleFactory2D());

        factorization.compute();

        return factorization;
    }
}
