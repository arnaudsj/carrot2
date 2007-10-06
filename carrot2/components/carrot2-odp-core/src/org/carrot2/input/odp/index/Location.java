
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.odp.index;

import java.io.*;

/**
 * Defines a single location in a
 * {@link org.carrot2.input.odp.index.PrimaryTopicIndex}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface Location
{
    /**
     * Serializes the location into an {@link ObjectOutputStream}. The stream
     * must <bold>not </bold> be closed by this method.
     * 
     * @param objectOutputStream
     * @throws IOException
     */
    public void serialize(ObjectOutputStream objectOutputStream)
        throws IOException;

    /**
     * Deserializes the contents of this Location from the provided
     * {@link ObjectInputStream}. The stream must <bold>not </bold> be closed
     * by this method.
     * 
     * @param objectInputStream
     * @throws IOException
     */
    public void deserialize(ObjectInputStream objectInputStream)
        throws IOException;
}