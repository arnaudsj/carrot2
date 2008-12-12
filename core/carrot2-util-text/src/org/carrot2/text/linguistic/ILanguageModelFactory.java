
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

package org.carrot2.text.linguistic;

/**
 * A factory for {@link ILanguageModel} implementations.
 */
public interface ILanguageModelFactory
{
    /**
     * @return Returns {@link ILanguageModel} for the current language or
     *         <code>null</code> if such language model is not available.
     */
    public ILanguageModel getCurrentLanguage();

    /**
     * @return Return a {@link ILanguageModel} associated with the given code or
     *         <code>null</code> if this language is not supported or its resources are
     *         not available.
     */
    public ILanguageModel getLanguage(LanguageCode language);

}