
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

package org.carrot2.tools.odp;

import java.io.File;
import java.util.Random;

/**
 * A number of useful methods for working with temporary files and directories.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TempUtils
{

    /**
     * Creates a temporary directory with given prefix.
     * 
     */
    public static File createTemporaryDirectory(String prefix)
    {
        Random random = new Random();
        File temporaryDirectory;

        do
        {
            temporaryDirectory = new File(System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator") + prefix
                + random.nextInt(1 << 30));
        }
        while (temporaryDirectory.exists());

        temporaryDirectory.mkdirs();

        return temporaryDirectory;
    }

    /**
     * Deletes a directory along with its contents (i.e. the directory can
     * contain files and other directories).
     * 
     * @param directory
     */
    public static void deleteDirectory(File directory)
    {
        if (directory == null || !directory.exists())
        {
            return;
        }

        File [] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isFile())
            {
                files[i].delete();
            }
            else if (files[i].isDirectory())
            {
                deleteDirectory(files[i]);
            }
        }

        directory.delete();
    }
}