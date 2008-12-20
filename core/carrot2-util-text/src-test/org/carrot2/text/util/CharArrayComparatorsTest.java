
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

package org.carrot2.text.util;

import static org.carrot2.util.test.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

/**
 * Test cases for {@link CharArrayComparators}. 
 */
public class CharArrayComparatorsTest
{
    @Test
    public void testNormalizingComparatorPL()
    {
        char [][] testWords = new char [] []
        {
            "\u0142an".toCharArray(),
            "demo".toCharArray(),
            "demos".toCharArray(),
            "DEMO".toCharArray(),
            "\u0141AN".toCharArray(),
            "Demos".toCharArray(),
            "demo".toCharArray(),
            "\u0141an".toCharArray(),
            "DEMOS".toCharArray()
        };

        char [][] expectedOrderedWords = new char [] []
        {
            "\u0142an".toCharArray(),
            "\u0141an".toCharArray(),
            "\u0141AN".toCharArray(),
            "demo".toCharArray(),
            "demo".toCharArray(),
            "DEMO".toCharArray(),
            "demos".toCharArray(),
            "Demos".toCharArray(),
            "DEMOS".toCharArray()
        };

        check(testWords, expectedOrderedWords);
    }

    @Test
    public void testNormalizingComparator()
    {
        char [][] testWords = new char [] []
        {
            "use".toCharArray(),
            "UAE".toCharArray(),
            "Use".toCharArray()
        };

        char [][] expectedOrderedWords = new char [] []
        {
            "UAE".toCharArray(),
            "use".toCharArray(),
            "Use".toCharArray()
        };

        check(testWords, expectedOrderedWords);
    }

    private void check(char [][] testWords, char [][] expectedOrderedWords)
    {
        Arrays.sort(testWords,
            CharArrayComparators.NORMALIZING_CHAR_ARRAY_COMPARATOR);
        assertThat(testWords).isEqualTo(expectedOrderedWords);
    }
}
