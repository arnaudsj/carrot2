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

package org.carrot2.text.preprocessing;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.text.analysis.ITokenType;
import org.carrot2.text.linguistic.ILanguageModel;
import org.carrot2.text.linguistic.IStemmer;

import bak.pcj.set.IntSet;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Document preprocessing context provides low-level (usually integer-coded) data
 * structures useful for further processing.
 */
public final class PreprocessingContext
{
    /** Predicate for splitting on document separator. */
    public static final Predicate<Integer> ON_DOCUMENT_SEPARATOR = Predicates
        .isEqualTo(ITokenType.TF_SEPARATOR_DOCUMENT);

    /** Predicate for splitting on field separator. */
    public static final Predicate<Integer> ON_FIELD_SEPARATOR = Predicates
        .isEqualTo(ITokenType.TF_SEPARATOR_FIELD);

    /** Predicate for splitting on sentence separator. */
    public static final Predicate<Integer> ON_SENTENCE_SEPARATOR = new Predicate<Integer>()
    {
        public boolean apply(Integer tokenType)
        {
            return (tokenType.intValue() & ITokenType.TF_SEPARATOR_SENTENCE) != 0;
        }
    };

    /** Query used to perform processing, may be <code>null</code> */
    public final String query;

    /** A list of documents to process. */
    public final List<Document> documents;

    /** Language model to be used */
    public final ILanguageModel language;

    /**
     * Creates a preprocessing context for the provided <code>documents</code> and with
     * the provided <code>languageModel</code>.
     */
    public PreprocessingContext(ILanguageModel languageModel, List<Document> documents,
        String query)
    {
        this.query = query;
        this.documents = documents;
        this.language = languageModel;
    }

    /**
     * Information about all tokens of the input {@link PreprocessingContext#documents}.
     * Each element of each of the arrays corresponds to one individual token from the
     * input or a synthetic separator inserted between documents, fields and sentences.
     * Last element of this array is a special terminator entry.
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllTokens
    {
        /**
         * Token image as it appears in the input. On positions where {@link #type} is
         * equal to one of {@link ITokenType#TF_TERMINATOR},
         * {@link ITokenType#TF_SEPARATOR_DOCUMENT} or
         * {@link ITokenType#TF_SEPARATOR_FIELD} , image is <code>null</code>.
         * <p>
         * This array is produced by {@link Tokenizer}.
         */
        public char [][] image;

        /**
         * Token's {@link ITokenType} bit flags.
         * <p>
         * This array is produced by {@link Tokenizer}.
         */
        public int [] type;

        /**
         * Document field the token came from. The index points to arrays in
         * {@link AllFields}, equal to <code>-1</code> for document and field separators.
         * <p>
         * This array is produced by {@link Tokenizer}.
         */
        public byte [] fieldIndex;

        /**
         * Index of the document this token came from, points to elements of
         * {@link PreprocessingContext#documents}. Equal to <code>-1</code> for document
         * separators.
         * <p>
         * This array is produced by {@link Tokenizer}.
         */
        public int [] documentIndex;

        /**
         * A pointer to {@link AllWords} arrays for this token. Equal to <code>-1</code>
         * for document, field and {@link ITokenType#TT_PUNCTUATION} tokens (including
         * sentence separators).
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public int [] wordIndex;

        /**
         * The suffix order of tokens. Suffixes starting with a separator come at the end
         * of the array.
         * <p>
         * This array is produced by {@link PhraseExtractor}.
         */
        public int [] suffixOrder;

        /**
         * The Longest Common Prefix for the adjacent suffix-sorted token sequences.
         * <p>
         * This array is produced by {@link PhraseExtractor}.
         */
        public int [] lcp;
    }

    /**
     * Information about all tokens of the input {@link PreprocessingContext#documents}.
     */
    public final AllTokens allTokens = new AllTokens();

    /**
     * Information about all fields processed for the input
     * {@link PreprocessingContext#documents}.
     */
    public static class AllFields
    {
        /**
         * Name of the document field. Entries of {@link AllTokens#fieldIndex} point to
         * this array.
         * <p>
         * This array is produced by {@link Tokenizer}.
         */
        public String [] name;
    }

    /**
     * Information about all fields processed for the input
     * {@link PreprocessingContext#documents}.
     */
    public final AllFields allFields = new AllFields();

    /**
     * Information about all unique words found in the input
     * {@link PreprocessingContext#documents}. Each entry in each array corresponds to one
     * unique word with respect to case, e.g. <em>data</em> and <em>DATA</em> will be
     * conflated to one entry in the arrays. Different grammatical forms of one word, e.g.
     * e.g <em>computer</em> and <em>computers</em>, will have different entries in the
     * arrays (see {@link AllStems} for inflection-conflated versions).
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllWords
    {
        /** Flags words that are contained in the query (inflection-insensitive) */
        public static final int FLAG_QUERY = 1;

        /**
         * The most frequently appearing variant of the word with respect to case. E.g. if
         * a token <em>MacOS</em> appeared 12 times in the input and <em>macos</em>
         * appeared 3 times, the image will be equal to <em>MacOS</em>.
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public char [][] image;

        /**
         * Term Frequency of the word, aggregated across all variants with respect to
         * case. Frequencies for each variant separately are not available.
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public int [] tf;

        /**
         * Term Frequency of the word for each document. The length of this array is equal
         * to the number of documents this word appeared in (Document Frequency)
         * multiplied by 2. Elements at even indices contain document indices pointing to
         * {@link PreprocessingContext#documents}, elements at odd indices contain the
         * frequency of the word in the document. For example, an array with 4 values:
         * <code>[2, 15, 138, 7]</code> means that the word appeared 15 times in document
         * at index 2 and 7 times in document at index 138.
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public int [][] tfByDocument;

        /**
         * Common word flag for the word, equal to <code>true</code> if the word is a stop
         * word. <b>This array will be replaced with a more generic word flags array in
         * the near future.</b>
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public boolean [] commonTermFlag;

        /**
         * Token type of this word. See {@link ITokenType} for available types.
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public int [] type;

        /**
         * Additional flags for this word.
         * <p>
         * This array is produced by {@link CaseNormalizer}. The
         * {@link AllWords#FLAG_QUERY} is set by {@link LanguageModelStemmer}.
         */
        public int [] flag;

        /**
         * A pointer to the {@link AllStems} arrays for this word.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}.
         */
        public int [] stemIndex;

        /**
         * Indices of all fields in which this word appears at least once. Values are
         * pointers to the {@link AllFields} arrays.
         * <p>
         * This array is produced by {@link CaseNormalizer}.
         */
        public byte [][] fieldIndices;
    }

    /**
     * Information about all unique words found in the input
     * {@link PreprocessingContext#documents}.
     */
    public final AllWords allWords = new AllWords();

    /**
     * Information about all unique stems found in the input
     * {@link PreprocessingContext#documents}. Each entry in each array corresponds to one
     * base form different words can be transformed to by the {@link IStemmer} used while
     * processing. E.g. the English <em>mining</em> and <em>mine</em> will be aggregated
     * to one entry in the arrays, while they will have separate entries in
     * {@link AllWords}.
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllStems
    {
        /**
         * Stem image as produced by the {@link IStemmer}, may not correspond to any
         * correct word.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}.
         */
        public char [][] image;

        /**
         * Pointer to the {@link AllWords} arrays, to the most frequent original form of
         * the stem. Pointers to the less frequent variants are not available.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}.
         */
        public int [] mostFrequentOriginalWordIndex;

        /**
         * Term frequency of the stem, i.e. the sum of all words from {@link AllWords}
         * pointing to the stem.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}.
         */
        public int [] tf;

        /**
         * Term frequency of the stem for each document. For the encoding of this array,
         * see {@link AllWords#tfByDocument}.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}.
         */
        public int [][] tfByDocument;

        /**
         * Indices of all fields in which this stem appears at least once. Values are
         * pointers to the {@link AllFields} arrays.
         * <p>
         * This array is produced by {@link LanguageModelStemmer}
         */
        public byte [][] fieldIndices;
    }

    /**
     * Information about all unique stems found in the input
     * {@link PreprocessingContext#documents}.
     */
    public final AllStems allStems = new AllStems();

    /**
     * Information about all frequently appearing sequences of words found in the input
     * {@link PreprocessingContext#documents}. Each entry in each array corresponds to one
     * sequence.
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllPhrases
    {
        /**
         * Pointers to {@link AllWords} for each word in the sequence.
         * <p>
         * This array is produced by {@link PhraseExtractor}.
         */
        public int [][] wordIndices;

        /**
         * Term frequency of the word sequence.
         * <p>
         * This array is produced by {@link PhraseExtractor}.
         */
        public int [] tf;

        /**
         * Term frequency of the word sequence for each document. For the encoding of this
         * array, see {@link AllWords#tfByDocument}.
         * <p>
         * This array is produced by {@link PhraseExtractor}.
         */
        public int [][] tfByDocument;
    }

    /**
     * Information about all frequently appearing sequences of words found in the input
     * {@link PreprocessingContext#documents}.
     */
    public AllPhrases allPhrases = new AllPhrases();

    /**
     * Information about words and phrases that might be good cluster label candidates.
     * Each entry in each array corresponds to one label candidate.
     * <p>
     * All arrays in this class have the same length and values across different arrays
     * correspond to each other for the same index.
     */
    public static class AllLabels
    {
        /**
         * Feature index of the label candidate. Features whose values are less than the
         * size of {@link AllWords} arrays are single word features and point to entries
         * in {@link AllWords}. Features whose values are larger or equal to the size of
         * {@link AllWords}, after subtracting the size of {@link AllWords}, point to
         * {@link AllPhrases}.
         * <p>
         * This array is produced by {@link LabelFilterProcessor}.
         */
        public int [] featureIndex;

        /**
         * Indices of documents assigned to the label candidate.
         * <p>
         * This array is produced by {@link DocumentAssigner}.
         */
        public IntSet [] documentIndices;

        /**
         * Index of the first phrase in {@link AllLabels}, or -1 if there are no phrases
         * in {@link AllLabels}.
         * <p>
         * This value is set by {@link LabelFilterProcessor}.
         */
        public int firstPhraseIndex;
    }

    /**
     * Information about words and phrases that might be good cluster label candidates.
     */
    public final AllLabels allLabels = new AllLabels();

    /**
     * Returns <code>true</code> if this context contains any words.
     */
    public boolean hasWords()
    {
        return allWords.image.length > 0;
    }

    /**
     * Returns <code>true</code> if this context contains any label candidates.
     */
    public boolean hasLabels()
    {
        return allLabels.featureIndex != null && allLabels.featureIndex.length > 0;
    }
}
