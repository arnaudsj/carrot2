
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

package org.carrot2.filter.lingo.common;

import org.carrot2.core.linguistic.Language;
import org.carrot2.core.linguistic.Stemmer;

import org.carrot2.filter.lingo.local.LingoLocalFilterComponent;
import org.carrot2.filter.lingo.lsicluster.LsiClusteringStrategy;
import org.carrot2.filter.lingo.lsicluster.LsiConstants;
import org.carrot2.filter.lingo.util.log.TimeLogger;

import org.apache.log4j.Logger;

import java.io.*;

import java.util.*;


/**
 * Stores all data needed during clustering process.
 */
public class MultilingualClusteringContext extends AbstractClusteringContext {
    /**
     * Logger
     */
    protected static final Logger logger = Logger.getLogger(MultilingualClusteringContext.class);

    /**
     * Language processing
     */
    private HashMap stopWordSets;

    /** DOCUMENT ME! */
    private HashMap nonStopWordSets;

    /** DOCUMENT ME! */
    private HashMap stemSets;

    /** DOCUMENT ME! */
    private HashMap inflectedSets;

    /** DOCUMENT ME! */
    private HashMap languages;
    
    /** The language to get the tokenizer from. */
    private Language tokenizerLanguage;

    /**
     * unidentified language
     */
    public static final String UNIDENTIFIED_LANGUAGE_NAME = "unidentified";

    /** If true, disables stemming */
    boolean DISABLE_STEMMING = LsiConstants.DEFAULT_DISABLE_STEMMING;

    public MultilingualClusteringContext(Map params) {
        setParameters( params );

        stopWordSets = new HashMap();
        nonStopWordSets = new HashMap();
        stemSets = new HashMap();
        inflectedSets = new HashMap();
        setLanguages(new Language[0]);

        nonStopWordSets.put(MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME, new HashSet());

        clusteringStrategy = new LsiClusteringStrategy();
    }

    /**
     *  Sets parameters and checks for DISABLE_STEMMING
     */
    public void setParameters(Map params ) {
        if ( params == null ) return;
        super.setParameters( params );
        Object value = null;

        if ((value = this.getParameter("preprocessing.class")) != null) {
            if (value instanceof List) {
                value = ((List) value).get(0);
            }

            try {
                preprocessingStrategy = (PreprocessingStrategy) Thread.currentThread().getContextClassLoader().loadClass((String) value).newInstance();
            } catch (Exception e) {
                logger.warn("Preprocessing strategy instantiation error", e);
                throw new RuntimeException(
                    "Preprocessing strategy could not be loaded: " + value +
                    ", " + e.toString());
            }
        } else {
            preprocessingStrategy = new CarrotLibTokenizerPreprocessingStrategy();
        }

        if ((value = this.getParameter("feature.extraction.strategy")) != null) {
            try {
                featureExtractionStrategy = (FeatureExtractionStrategy) Thread.currentThread().getContextClassLoader().loadClass((String) value).newInstance();
            } catch (Exception e) {
                logger.warn("Feature extraction strategy instantiation error", e);
                throw new RuntimeException(
                    "Feature extraction strategy could not be loaded: " +
                    value + ", " + e.toString());
            }
        } else {
            featureExtractionStrategy = new MultilingualFeatureExtractionStrategy();
        }

        if ((value = this.getParameter(LsiConstants.DISABLE_STEMMING)) != null) {
            DISABLE_STEMMING = value.toString().equalsIgnoreCase( "true" );
        }

    }

    /**
     * Method cluster.
     *
     * @return ClusteringResults
     */
    public Cluster[] cluster() {
        TimeLogger timeLogger = new TimeLogger();
        TimeLogger totalTimeLogger = new TimeLogger();

        totalTimeLogger.start();
        timeLogger.start();

        preprocess();
        timeLogger.logElapsedAndStart(logger, "preprocess()");
        extractFeatures();
        timeLogger.logElapsedAndStart(logger, "extractFeatures()");

        Cluster[] clusteringResults = clusteringStrategy.cluster(this);
        timeLogger.logElapsed(logger, "cluster()");

        totalTimeLogger.logElapsed(logger, "TOTAL");

        return clusteringResults;
    }

    /**
     * Reads a set of stop words and returns it as a HashSet
     *
     * @param stopWordsFile
     */
    private HashSet readStopWordsSet(InputStream stopWordsFile)
        throws IOException {
        Reader r = null;
        HashSet stopWordsSet = new HashSet();

        try {
            r = new InputStreamReader(stopWordsFile, "UTF-8");

            StreamTokenizer st = new StreamTokenizer(r);

            int token;

            while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
                switch (token) {
                case StreamTokenizer.TT_WORD:
                    stopWordsSet.add(st.sval);

                    break;

                default:}
            }
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException x) {
                    logger.error("Cannot close file."); /* not much we can do. */
                }
            }
        }

        return stopWordsSet;
    }

    /**
     *
     */
    void extractFeatures() {
        features = featureExtractionStrategy.extractFeatures(this);
        
        if (parameters
            .get(LingoLocalFilterComponent.PARAMETER_LEAVE_FEATURES_IN_CONTEXT) != null)
        {
            parameters.put(LingoLocalFilterComponent.LINGO_EXTRACTED_FEATURES,
                features);
        }
    }

    /**
     *
     */
    void preprocess() {
        preprocessedSnippets = preprocessingStrategy.preprocess(this);
    }

    /**
     * @return HashMap
     */
    public HashSet getQueryWords() {
        if (queryWords == null) {
            super.getQueryWords();

            HashSet queryWordsStemmed = new HashSet();

            for (Iterator words = queryWords.iterator(); words.hasNext();) {
                String word = (String) words.next();

                // Stem with every possilble stemmer. Is it good?
                // [dw] dunno... kind of doesn't make sense...
                Iterator keys = languages.keySet().iterator();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Language language = (Language) languages.get(key);

                    Stemmer stemmer = language.borrowStemmer();
                    if (stemmer == null) {
                        continue;
                    }

                    try {
                        String stemmed = stemmer.getStem(word.toCharArray(), 0,
                                word.length());

                        if ((stemmed != null) && !stemmed.equals(word)) {
                            queryWordsStemmed.add(stemmed);
                        }
                    } finally {
                        language.returnStemmer(stemmer);
                    }
                }
            }

            queryWords.addAll(queryWordsStemmed);
        }

        return queryWords;
    }

    /**
     * Sets the clusteringStrategy.
     *
     * @param clusteringStrategy The clusteringStrategy to set
     */
    public void setClusteringStrategy(ClusteringStrategy clusteringStrategy) {
        this.clusteringStrategy = clusteringStrategy;
    }

    public HashMap getInflectedSets() {
        return inflectedSets;
    }

    public HashMap getStopWordSets() {
        return stopWordSets;
    }

    public HashMap getStemSets() {
        return stemSets;
    }

    public void setLanguages(Language[] languages) {
        HashMap map = new HashMap();
        stopWordSets = new HashMap();
        
        if (languages == null)
        	languages = new Language [0];

        for (int i = 0; i < languages.length; i++) {
            String language = languages[i].getIsoCode();
            map.put(language, languages[i]);
            stemSets.put(language, new HashMap());
            inflectedSets.put(language, new HashMap());
            nonStopWordSets.put(language, new HashSet());

            // now initialize stopwords. 
            // TODO: this should be removed and replaced by
            // checking whether a token is a stopword...
            Set stopwords = languages[i].getStopwords();

            if (stopwords != null) {
                stopWordSets.put(languages[i].getIsoCode(), stopwords);
            } else {
                stopWordSets = new HashMap();
            }
        }

        this.languages = map;
    }

    public void setTokenizerLanguage(Language tokenizerLanguage)
    {
        this.tokenizerLanguage = tokenizerLanguage;
    }
    
    public Language getTokenizerLanguage()
    {
        return tokenizerLanguage;
    }
    
    public Map getLanguages() {
        return this.languages;
    }

    public HashMap getNonStopWordSets() {
        return nonStopWordSets;
    }

    /**
     * Ensure the language of the snippet is in the internal arrays.
     *
     * @see org.carrot2.filter.lingo.common.AbstractClusteringContext#addSnippet(org.carrot2.filter.lingo.common.Snippet)
     */
    public void addSnippet(Snippet snippet) {
        String lang = snippet.getLanguage();

        if ((lang != null) && (lang != UNIDENTIFIED_LANGUAGE_NAME)) {
            if (!languages.containsKey(lang)) {
                snippet.setLanguage(UNIDENTIFIED_LANGUAGE_NAME);
            }
        }

        super.addSnippet(snippet);
    }
}
