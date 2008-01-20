
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

package org.carrot2.filter.lingo.lsicluster;

/**
 * Names of properties used to drive the algorithm and their default values.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LsiConstants
{

    /**
     * No instantiation of this class.
     */
    private LsiConstants()
    {
    }

    /**
     * Determines the similarity threshold that must be exceeded in order for a
     * document to be added to a cluster. The larger the value, the less
     * documents in a cluster and the larger assignment precission.
     * 
     * <P>
     * Range: 0.0 - 1.0, Property type: <code>java.lang.String</code>
     */
    public final static String CLUSTER_ASSIGNMENT_THRESHOLD = "lsi.threshold.clusterAssignment";

    /**
     * Default value of the {@link #CLUSTER_ASSIGNMENT_THRESHOLD}.
     */
    public final static double DEFAULT_CLUSTER_ASSIGNMENT_THRESHOLD = 0.225;

    /**
     * Determines the maximum number of candidate clusters. The larger the
     * value, the more candidate clusters.
     * 
     * <p>
     * Range: 0.0 - 1.0, Property type: <code>java.lang.String</code>
     */
    public final static String CANDIDATE_CLUSTER_THRESHOLD = "lsi.threshold.candidateCluster";

    /**
     * Default value of the {@link #CANDIDATE_CLUSTER_THRESHOLD}.
     */
    public final static double DEFAULT_CANDIDATE_CLUSTER_THRESHOLD = 0.775;

    /**
     * Determines the preferred number of clusters. Lingo will create not more
     * than {@link #PREFERRED_CLUSTER_COUNT} clusters. It is not guaranteed,
     * however, that exactly {@link #PREFERRED_CLUSTER_COUNT} will be produced.
     * 
     * <p>
     * Range: 2 - infinity, Property type: <code>java.lang.Integer</code>
     */
    public final static String PREFERRED_CLUSTER_COUNT = "clusters.num";

    /**
     * Default value of the {@link #PREFERRED_CLUSTER_COUNT}.
     */
    public final static int DEFAULT_PREFERRED_CLUSTER_COUNT = -1;

    /**
     *  Stemming can be disabled with this parameter.
     */
    public final static String DISABLE_STEMMING = "lsi.disable.stemming";

    /**
     * Default value of the {@link #DISABLE_STEMMING}.
     *
     * If true, disables stemming, otherwise not
     */
    public final static boolean DEFAULT_DISABLE_STEMMING = false; 

    /**
     * Document relevance scores can be used to modify cluster scores.
     */
    public final static String WEIGHT_DOCUMENT_SCORE = "lsi.weight.document.score";

    /**
     * Default value of the {@link #WEIGHT_DOCUMENT_SCORE}.
     *
     * If true, uses document relevance scores to calculate the cluster scores,
     * otherwise ignores them.
     */
    public final static boolean DEFAULT_WEIGHT_DOCUMENT_SCORE = false;

    /**
     *  Limits the size of the term document matrix.
     */
    public final static String MAX_SIZE_TD_MATRIX = "lsi.max.size.tdmatrix";

    /**
     *  Default value of the {@link #MAX_SIZE_TD_MATRIX}.
     */
    public final static int DEFAULT_MAX_SIZE_TD_MATRIX = 250 * 150;
}
