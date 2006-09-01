
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.util;

import java.lang.reflect.Array;

/**
 * Convert arrays to String
 */
public class ArrayFormatter implements StringFormatter{
    private String[] separators;
    private StringFormatter objectFormatter;
    public ArrayFormatter(String[] separators) {
        this.separators = separators;
        this.objectFormatter = new StringFormatter() {
            public String toString(Object obj) {
                if (obj == null)
                    return "null";
                return obj.toString();
            }
        };
    }

    public ArrayFormatter(String[] separators, StringFormatter objectFormatter) {
        this.separators = separators;
        this.objectFormatter = objectFormatter;
    }

    /**
     * Create string representation of given array using defined separators
     * @param array
     */
    public String toString(Object array) {
        StringBuffer buf = new StringBuffer();
        String sep = "";
        buf.append(separators[0]);
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            buf.append(sep + objectFormatter.toString(Array.get(array, i)));
            sep = separators[1];
        }
        buf.append(separators[2]);
        return buf.toString();
    }

}
