
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

package org.carrot2.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;

/**
 * Provides a number of useful method operating on {@link String}s that are not available
 * in {@link org.apache.commons.lang.StringUtils}.
 */
public final class StringUtils
{
    private static final Pattern CAMEL_CASE_SEGMENT_PATTERN = Pattern
        .compile("([A-Z]*)([A-Z0-9][a-z0-9]*)");

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<.+?>",
        Pattern.CASE_INSENSITIVE);

    private StringUtils()
    {
    }

    public static <T> String toString(Iterable<T> iterable, String separator)
    {
        final StringBuilder stringBuilder = new StringBuilder();

        for (final Iterator<T> iterator = iterable.iterator(); iterator.hasNext();)
        {
            final T object = iterator.next();
            stringBuilder.append(object);
            if (iterator.hasNext())
            {
                stringBuilder.append(separator);
            }
        }

        return stringBuilder.toString();
    }

    public static String splitCamelCase(String camelCaseString)
    {
        final Matcher matcher = CAMEL_CASE_SEGMENT_PATTERN.matcher(camelCaseString);
        final List<String> parts = new ArrayList<String>();
        while (matcher.find())
        {
            for (int i = 1; i <= matcher.groupCount(); i++)
            {
                final String group = matcher.group(i);
                if (group.length() == 0) continue;

                parts.add(group);
            }
        }
        return org.apache.commons.lang.StringUtils.join(parts, ' ');
    }

    public static String urlEncodeWrapException(String string, String encoding)
    {
        try
        {
            return URLEncoder.encode(string, encoding);
        }
        catch (UnsupportedEncodingException e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }

    public static String removeHtmlTags(String string)
    {
        return HTML_TAG_PATTERN.matcher(string).replaceAll("");
    }

    public static String identifierToHumanReadable(String string)
    {
        return WordUtils.capitalizeFully(string.replace('_', ' '));
    }
}
