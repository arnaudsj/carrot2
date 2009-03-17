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

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.carrot2.text.linguistic.DefaultLanguageModelFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link DocumentAssigner}.
 */
public class DocumentAssignerTest extends LabelFilterTestBase
{
    /** Document assigner under tests */
    private DocumentAssigner documentAssigner;

    @Before
    public void setUpDocumentAssigner()
    {
        documentAssigner = new DocumentAssigner();
    }

    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.stopWordLabelFilter.enabled = true;
        filterProcessor.completeLabelFilter.enabled = true;
    }

    @Test
    public void testEmpty()
    {
        final int [][] expectedDocumentIndices = new int [] [] {};
        check(expectedDocumentIndices, -1);
    }

    @Test
    public void testSingleWordLabels()
    {
        createDocuments("data is", "data is", "mining", "mining");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0
            },

            new int []
            {
                1
            }
        };

        documentAssigner.minClusterSize = 1;
        check(expectedDocumentIndices, -1);
    }

    @Test
    public void testMinClusterSize()
    {
        createDocuments("test data", "test data", "data test . mining",
            "data test . mining");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1
            },

            new int []
            {
                0, 1
            },

            new int []
            {
                0, 1
            },

            new int []
            {
                0, 1
            }
        };

        documentAssigner.minClusterSize = 2;
        check(expectedDocumentIndices, 2);
    }

    @Test
    public void testPhraseLabelsExactMatch()
    {
        createDocuments("data is cool", "data is cool", "data is cool", "", "cool",
            "cool");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1, 2
            },

            new int []
            {
                0, 1
            }
        };

        check(expectedDocumentIndices, 1);
    }

    @Test
    public void testPhraseLabelsNonExactMatch()
    {
        createDocuments("data is cool", "data is cool", "cool data", "", "cool", "data",
            "data", "");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1, 2
            },

            new int []
            {
                0, 1, 2, 3
            },

            new int []
            {
                0, 1, 2
            }
        };

        check(expectedDocumentIndices, 2);
    }

    @Test
    public void testPhraseLabelsNonExactMatchOtherLabels()
    {
        createDocuments("aa bb cc dd", "aa bb cc dd", "dd . cc . bb . aa",
            "dd . cc . bb . aa", "cc . bb . aa", "aa . bb . cc");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1, 2
            },

            new int []
            {
                0, 1, 2
            },

            new int []
            {
                0, 1, 2
            },

            new int []
            {
                0, 1
            },

            new int []
            {
                0, 1
            }
        };

        check(expectedDocumentIndices, 4);
    }

    private void check(int [][] expectedDocumentIndices, int expectedFirstPhraseIndex)
    {
        runPreprocessing();
        documentAssigner.assign(context);

        assertThat(context.allLabels.firstPhraseIndex).as("allLabels.firstPhraseIndex")
            .isEqualTo(expectedFirstPhraseIndex);
        assertThat(context.allLabels.documentIndices).as("allLabels.documentIndices")
        .hasSize(expectedDocumentIndices.length);
        for (int i = 0; i < expectedDocumentIndices.length; i++)
        {
            assertThat(context.allLabels.documentIndices[i].toArray()).as(
                "allLabels.documentIndices[" + i + "]").isEqualTo(
                expectedDocumentIndices[i]);
        }
    }

    @Override
    protected ILanguageModelFactory createLanguageModelFactory()
    {
        return new DefaultLanguageModelFactory();
    }
}
