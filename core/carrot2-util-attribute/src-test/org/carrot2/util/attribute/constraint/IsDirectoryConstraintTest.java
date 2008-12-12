
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

package org.carrot2.util.attribute.constraint;

import org.junit.Test;

/**
 * Test cases for {@link IsDirectory}.
 */
public class IsDirectoryConstraintTest extends FileConstraintTestBase<IsDirectory>
{
    static class AnnotationContainer
    {
        @IsDirectory(mustExist = true)
        String mustExist;

        @IsDirectory(mustExist = false)
        String doesNotHaveToExist;
    }

    private static final String MUST_EXIST_FIELD_NAME = "mustExist";
    private static final String DOES_NOT_HAVE_TO_EXIST_FIELD_NAME = "doesNotHaveToExist";

    @Override
    Class<?> getAnnotationContainerClass()
    {
        return AnnotationContainer.class;
    }

    @Override
    Class<IsDirectory> getAnnotationType()
    {
        return IsDirectory.class;
    }

    String getInvalidTypeCheckFieldName()
    {
        return DOES_NOT_HAVE_TO_EXIST_FIELD_NAME;
    }

    @Test
    public void testNull() throws Exception
    {
        assertNotMet(null, MUST_EXIST_FIELD_NAME);
    }

    @Test
    public void testMustExistMet() throws Exception
    {
        assertMet(existingDirectory, MUST_EXIST_FIELD_NAME);
    }

    @Test
    public void testMustExistNotMetExistingFile() throws Exception
    {
        assertNotMet(existingFile, MUST_EXIST_FIELD_NAME);
    }

    @Test
    public void testMustExistNotMetNonExisting() throws Exception
    {
        assertNotMet(nonExisting, MUST_EXIST_FIELD_NAME);
    }

    @Test
    public void testDoesNotHaveToExistMetExistingDirectory() throws Exception
    {
        assertMet(existingDirectory, DOES_NOT_HAVE_TO_EXIST_FIELD_NAME);
    }

    @Test
    public void testDoesNotHaveToExistMetNonExisting() throws Exception
    {
        assertMet(nonExisting, DOES_NOT_HAVE_TO_EXIST_FIELD_NAME);
    }

    @Test
    public void testDoesNotHaveToExistNotMetExistingFile() throws Exception
    {
        assertNotMet(existingFile, DOES_NOT_HAVE_TO_EXIST_FIELD_NAME);
    }
}
