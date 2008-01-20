
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.measure;


/**
 * Factory and utilities methods for similarity measure
 */
public class SimilarityFactory {

    public static final String COSINE = "Cosine";
    public static final String DICE = "Dice";

    private static final Similarity cosine = new CosineCoefficient();
    private static final Similarity dice = new DiceCoefficient();
    private SimilarityFactory(){};

    public static Similarity getSimilarity(String type) {
        if (COSINE.equals(type))
            return cosine;
        if (DICE.equals(type))
            return dice;
        throw new IllegalArgumentException("Unknowy type "+type);
    }

    public static Similarity getCosine() {
        return cosine;
    }
    /**
     * Construct distance(similarity) matrix for a set of vectors.
     * The results is a squared, symmetric matrix, in which diagonal cells are all 0.
     * Cell(i,j) contains similarity between i-th and j-th vector
     * @param vectors set of vectors (rows)
     * @param similarity similarity measure
     */
    public static double[][] distance(double[][] vectors, Similarity similarity) {
        int size = vectors.length;
        double[][] sim = new double[size][size];
        for(int i=0; i < size; i++) {
            for(int j=i+1; j < size; j++) {
                double s = similarity.measure(vectors[i], vectors[j]);
                sim[i][j] = s;
                sim[j][i] = s;
            }
        }
        return sim;
    }
}
