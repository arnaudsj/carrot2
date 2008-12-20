
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

package org.carrot2.util.attribute.test.assertions;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.util.attribute.CommonMetadata;

/**
 * Assertions on {@link CommonMetadata}.
 */
public class CommonMetadataAssertion
{
    private final CommonMetadata actual;

    public CommonMetadataAssertion(CommonMetadata actual)
    {
        this.actual = actual;
    }

    public CommonMetadataAssertion isEquivalentTo(CommonMetadata expected)
    {
        assertThat(actual.getDescription()).as("description").isEqualTo(
            expected.getDescription());
        assertThat(actual.getTitle()).as("title").isEqualTo(expected.getTitle());
        assertThat(actual.getLabel()).as("label").isEqualTo(expected.getLabel());
        return this;
    }
}
