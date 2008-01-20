
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

import java.util.*;


/**
 *
 */
public class DefaultPreprocessingStrategy implements PreprocessingStrategy {
    /**
     * The sentence delimiters over which phrases cannot be spanned
     */
    private static final String[] DEFAULT_SENTENCE_DELIMITERS = {
            ".", "?", "!", ";", "|"
        };

    /**
     * Sentence delimiters
     */
    private String[] sentenceDelimiters;

    /** */

    /** DOCUMENT ME! */
    protected HashMap stems;

    /** DOCUMENT ME! */
    protected HashSet strongWords;

    /**
     * @see java.lang.Object#Object()
     */
    public DefaultPreprocessingStrategy() {
        this(DEFAULT_SENTENCE_DELIMITERS);
    }

    /**
     * Method DefaultPreprocessingStrategy.
     *
     * @param sentenceDelimiters
     */
    public DefaultPreprocessingStrategy(String[] sentenceDelimiters) {
        this.sentenceDelimiters = sentenceDelimiters;
    }

    public Snippet[] preprocess(AbstractClusteringContext clusteringContext) {
        Snippet[] snippets = clusteringContext.getSnippets();
        Snippet[] preprocessedSnippets = new Snippet[snippets.length];

        stems = ((DefaultClusteringContext) clusteringContext).getStems();
        strongWords = clusteringContext.getStrongWords();

        for (int i = 0; i < snippets.length; i++) {
            preprocessedSnippets[i] = preprocess(snippets[i]);
        }

        return preprocessedSnippets;
    }

    protected Snippet preprocess(Snippet snippet) {
        Snippet s = new Snippet(snippet.getSnippetId(),
                preprocess(snippet.getTitle(), true),
                preprocess(snippet.getBody(), false));
        s.setLocation(snippet.getLocation());

        return s;
    }

    private String preprocess(String text, boolean isTitle) {
        StringBuffer stringBuffer = new StringBuffer();
        StringTokenizer stringTokenizer = new StringTokenizer(text);

        boolean appendDelimiter = false;
        boolean prependDelimiter = false;
        boolean delimiterAdded = false;

        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();

            // Make the token lowercase
            token = token.toLowerCase();

            // Remove delimiters from the beginning
            boolean moreDelimiters = true;

            while (moreDelimiters) {
                moreDelimiters = false;

                for (int i = 0; i < sentenceDelimiters.length; i++) {
                    if (token.startsWith(sentenceDelimiters[i])) {
                        prependDelimiter = true;
                        moreDelimiters = true;
                        token = token.substring(1);

                        break;
                    }
                }
            }

            // Remove delimiters from the end
            moreDelimiters = true;

            while (moreDelimiters) {
                moreDelimiters = false;

                for (int i = 0; i < sentenceDelimiters.length; i++) {
                    if (token.endsWith(sentenceDelimiters[i])) {
                        token = token.substring(0, token.length() - 1);
                        moreDelimiters = true;
                        appendDelimiter = true;

                        break;
                    }
                }
            }

            if (stems.containsKey(token)) {
                token = (String) stems.get(token);
            }

            if (isTitle) {
                strongWords.add(token);
            }

            if (prependDelimiter && (stringBuffer.length() != 0) &&
                    !delimiterAdded) // don't prepend the "." at the beginning of the string
             {
                stringBuffer.append(". ");
                delimiterAdded = true;
            }

            if (token.length() > 0) {
                stringBuffer.append(token);
                stringBuffer.append(" ");
                delimiterAdded = false;
            }

            if (appendDelimiter && stringTokenizer.hasMoreTokens() &&
                    !delimiterAdded) // don't append the "." at the end of the string
             {
                stringBuffer.append(". ");
                delimiterAdded = true;
            }

            prependDelimiter = false;
            appendDelimiter = false;
        }

        return stringBuffer.toString().trim();
    }
}
