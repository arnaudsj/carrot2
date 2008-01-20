
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

import java.text.NumberFormat;


/**
 * Represents a single feature (key word/phrase) found by Lingo in the input
 * snippets.
 * 
 * @author Stanislaw Osinski
 */
public class Feature {

    /** A unique integer code of this feature */
    private int code;

    /** String representation of this feature */
    private String text;

    /** ISO code of the language to which Lingo believes this feature belongs */
    private String language;

    /** The number of occurrences of this feature in all input snippets */
    private int tf;

    /** Inverse-document frequency of this feature */
    private double idf;

    /** Length (in words) of this feature */
    private int length;

    /** True if this feature is a stop word */
    private boolean stopWord;

    /** True if this feature is among query words */
    private boolean queryWord;

    /** True if this feature appeared in some snippet's title */
    private boolean strong;

    /**
     * Used for phrase features only. An array pointing to features
     * representing the phrase's individual words. 
     */
    private int[] phraseFeatureIndices;

    /**
     * Indexes of snippets in which this feature was found. 
     */
    private int[] snippetIndices;

    /**
     * The frequency of this feature across individual snippets. If the
     * <code>n</code>-th element of this array is equal to <code>k</code>,
     * it means that this feature appeared <code>k</code> times in snippet
     * <code>n</code>.
     */
    private int[] snippetTf;

    /**
     * @param text
     * @param code
     * @param length
     * @param tf
     */
    public Feature(String text, int code, int length, int tf) {
        this.text = text;
        this.length = length;
        this.tf = tf;
        this.code = code;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer text = new StringBuffer();
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);

        text.append(this.text);
        text.append(" tf=" + getTf() + " tfidf=" + format.format(getTfidf()) +
            " ");

        if (stopWord) {
            text.append("SW ");
        }

        if (strong) {
            text.append("ST ");
        }

        text.append(language);

        return text.toString();
    }

    /**
     * @param by
     */
    public void increaseTf(int by) {
        tf += by;
    }

    /**
     * @param snippetIndices
     */
    public void setSnippetIndices(int[] snippetIndices) {
        this.snippetIndices = snippetIndices;
    }

    /**
     * @param phraseFeatureIndices
     */
    public void setPhraseFeatureIndices(int[] phraseFeatureIndices) {
        this.phraseFeatureIndices = phraseFeatureIndices;
    }

    /**
     * @return boolean
     */
    public boolean isStopWord() {
        return stopWord;
    }

    /**
     * Sets the stopWord.
     *
     * @param stopWord The stopWord to set
     */
    public void setStopWord(boolean stopWord) {
        this.stopWord = stopWord;
    }

    /**
     * @return String
     */
    public String getText() {
        return text;
    }

    /**
     * @return int
     */
    public int getTf() {
        return tf;
    }

    /**
     * @return int
     */
    public int getCode() {
        return code;
    }

    /**
     * @return int
     */
    public int getLength() {
        return length;
    }

    /**
     * @return double
     */
    public double getTfidf() {
        return tf * idf;
    }

    /**
     * @return int[]
     */
    public int[] getPhraseFeatureIndices() {
        return phraseFeatureIndices;
    }

    /**
     * @return int[]
     */
    public int[] getSnippetIndices() {
        return snippetIndices;
    }

    /**
     * @return int[]
     */
    public int[] getSnippetTf() {
        return snippetTf;
    }

    /**
     * Sets the snippetTf.
     *
     * @param snippetTf The snippetTf to set
     */
    public void setSnippetTf(int[] snippetTf) {
        this.snippetTf = snippetTf;
    }

    public double getIdf() {
        return idf;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isStrong() {
        return strong;
    }

    public void setStrong(boolean strong) {
        this.strong = strong;
    }

    public boolean isQueryWord() {
        return queryWord;
    }

    public void setQueryWord(boolean b) {
        queryWord = b;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String string) {
        language = string;
    }
}
