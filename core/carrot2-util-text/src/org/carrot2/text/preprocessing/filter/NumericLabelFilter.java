
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

package org.carrot2.text.preprocessing.filter;

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.analysis.ITokenType;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attribute.*;

/**
 * Accepts labels that do not consist only of numeric tokens and start with a non-numeric
 * token.
 */
@Bindable(prefix = "NumericLabelFilter")
public class NumericLabelFilter extends SingleLabelFilterBase
{
    /**
     * Remove numeric labels. Remove labels that consist only of or start with numbers.
     * 
     * @level Basic
     * @group Label filtering
     * @label Remove numeric labels
     */
    @Input
    @Processing
    @Attribute
    public boolean enabled = true;

    @Override
    public boolean acceptPhrase(PreprocessingContext context, int phraseIndex)
    {
        final int [] wordIndices = context.allPhrases.wordIndices[phraseIndex];
        final int [] type = context.allWords.type;

        return !isNumeric(type[wordIndices[0]]);
    }

    @Override
    public boolean acceptWord(PreprocessingContext context, int wordIndex)
    {
        return !isNumeric(context.allWords.type[wordIndex]);
    }

    private final boolean isNumeric(int type)
    {
        return (type & ITokenType.TT_NUMERIC) != 0;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
