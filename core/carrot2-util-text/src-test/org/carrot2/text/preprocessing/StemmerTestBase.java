
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import static org.carrot2.util.test.Assertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;

/**
 * Base class for {@link LanguageModelStemmer} tests.
 */
public class StemmerTestBase extends PreprocessingComponentTestBase
{
    /** Stemmer under tests */
    private LanguageModelStemmer languageModelStemmer;

    /** Other preprocessing components required for the test */
    private Tokenizer tokenizer;
    private CaseNormalizer caseNormalizer;

    @Before
    public void setUpPreprocessingComponents()
    {
        tokenizer = new Tokenizer();
        caseNormalizer = new CaseNormalizer();
        languageModelStemmer = new LanguageModelStemmer();
    }

    protected void check(char [][] expectedStemImages, int [] expectedStemTf,
        int [] expectedStemIndices, int [][] expectedStemTfByDocument,
        byte [][] expectedFieldIndices)
    {
        performProcessing();

        assertThat(context.allWords.stemIndex).as("allWords.stemIndices").isEqualTo(
            expectedStemIndices);
        assertThat(context.allStems.image).as("allStems.images").isEqualTo(
            expectedStemImages);
        assertThat(context.allStems.tf).as("allStems.tf").isEqualTo(expectedStemTf);
        assertThat(context.allStems.tfByDocument).as("allStems.tfByDocument").isEqualTo(
            expectedStemTfByDocument);
        assertThat(context.allStems.fieldIndices).as("allStems.fieldIndices").isEqualTo(
            expectedFieldIndices);
    }

    protected void check(String query, int [] expectedWordsFlag)
    {
        createPreprocessingContext(query);
        performProcessing();

        assertThat(context.allWords.flag).as("allWords.flag")
            .isEqualTo(expectedWordsFlag);
    }

    private void performProcessing()
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
    }
}
