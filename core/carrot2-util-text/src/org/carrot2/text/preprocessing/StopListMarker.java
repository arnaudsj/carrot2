
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

import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.CharArrayUtils;
import org.carrot2.util.attribute.Bindable;

/**
 * Marks stop words based on the current language model.
 * <p>
 * This class saves the following results to the {@link PreprocessingContext}:
 * <ul>
 * <li>{@link AllWords#commonTermFlag}</li>
 * </ul>
 * <p>
 * This class requires that {@link Tokenizer} and {@link CaseNormalizer} be invoked first.
 */
@Bindable(prefix = "StopListMarker")
public final class StopListMarker
{
    /**
     * Marks stop words and saves the results to the <code>context</code>.
     */
    public void mark(PreprocessingContext context)
    {
        final char [][] wordImages = context.allWords.image;
        final boolean [] commonTermFlags = new boolean [wordImages.length];

        final MutableCharArray mutableCharArray = new MutableCharArray("");
        char [] buffer = new char [128];

        for (int i = 0; i < commonTermFlags.length; i++)
        {
            final char [] word = wordImages[i];
            if (buffer.length < word.length) buffer = new char [word.length];

            CharArrayUtils.toLowerCase(word, buffer);
            mutableCharArray.reset(buffer, 0, word.length);
            commonTermFlags[i] = context.language.isCommonWord(mutableCharArray);
        }

        context.allWords.commonTermFlag = commonTermFlags;
    }
}
