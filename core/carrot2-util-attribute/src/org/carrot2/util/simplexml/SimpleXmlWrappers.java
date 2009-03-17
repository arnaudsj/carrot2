
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

package org.carrot2.util.simplexml;

import java.util.*;

import org.simpleframework.xml.Root;

import com.google.common.collect.Maps;

/**
 * Enables SimpleXML-based serialization of collections of arbitrary types, also those
 * contained in libraries. Depending on the actual type, 3 wrapping scenarios are
 * possible:
 * <ul>
 * <li><strong>Primitive types.</strong> Primitive types, and also {@link Class},
 * {@link String} and {@link Enum}, are handled directly by {@link SimpleXmlWrapperValue},
 * no extra code is required for serialization / deserialization.</li>
 * <li><strong>SimpleXML-annotated types.</strong> Types annotated with SimpleXML's
 * {@link Root} annotation will not be wrapped and serialized / deserialized directly by
 * SimpleXML</li>
 * <li><strong>Other types.</strong> For any other types, a {@link ISimpleXmlWrapper}
 * implementation must be registered using the {@link #addWrapper(Class, Class)} method.</li>
 * </ul>
 */
public class SimpleXmlWrappers
{
    /**
     * The supported {@link ISimpleXmlWrapper}s.
     */
    private static final Map<Class<?>, Class<? extends ISimpleXmlWrapper<?>>> wrappers = Maps
        .newHashMap();

    /**
     * Registers a new {@link ISimpleXmlWrapper}. If a wrapper for the provide
     * <code>wrappedClass</code> already exists, it will be replaced with the new value.
     * 
     * @param wrappedClass type to be wrapped
     * @param wrapperClass class name of the {@link ISimpleXmlWrapper} implementation to
     *            wrap <code>wrappedClass</code>
     */
    public static synchronized <T> void addWrapper(Class<T> wrappedClass,
        Class<? extends ISimpleXmlWrapper<? super T>> wrapperClass)
    {
        wrappers.put(wrappedClass, wrapperClass);
    }

    /**
     * Wraps the provided map for serialization.
     * 
     * @return map for SimpleXML serialization
     */
    public static <K> Map<K, SimpleXmlWrapperValue> wrap(Map<K, ?> toWrap)
    {
        final HashMap<K, SimpleXmlWrapperValue> wrapped = Maps.newHashMap();
        for (Map.Entry<K, ?> entry : toWrap.entrySet())
        {
            wrapped.put(entry.getKey(), SimpleXmlWrapperValue.wrap(entry.getValue()));
        }
        return wrapped;
    }

    /**
     * Unwraps the provided map after deserialization.
     * 
     * @return map with original values
     */
    public static <K> Map<K, Object> unwrap(Map<K, SimpleXmlWrapperValue> wrapped)
    {
        final HashMap<K, Object> result = Maps.newHashMap();
        for (Map.Entry<K, SimpleXmlWrapperValue> entry : wrapped.entrySet())
        {
            result.put(entry.getKey(), unwrap(entry.getValue()));
        }
        return result;
    }

    /**
     * Wraps the provided list for serialization.
     * 
     * @return list for SimpleXML serialization
     */
    public static List<SimpleXmlWrapperValue> wrap(List<?> toWrap)
    {
        return (List<SimpleXmlWrapperValue>) wrap(toWrap,
            new ArrayList<SimpleXmlWrapperValue>());
    }

    /**
     * Unwraps the provided list after deserialization.
     * 
     * @return list with original values
     */
    public static List<Object> unwrap(List<SimpleXmlWrapperValue> wrapped)
    {
        return (List<Object>) unwrap(wrapped, new ArrayList<Object>());
    }

    /**
     * Wraps the provided set for serialization.
     * 
     * @return set for SimpleXML serialization
     */
    public static Set<SimpleXmlWrapperValue> wrap(Set<?> toWrap)
    {
        return (Set<SimpleXmlWrapperValue>) wrap(toWrap,
            new HashSet<SimpleXmlWrapperValue>());
    }

    /**
     * Unwraps the provided set after deserialization.
     * 
     * @return set with original values
     */
    public static Set<Object> unwrap(Set<SimpleXmlWrapperValue> wrapped)
    {
        return (Set<Object>) unwrap(wrapped, new HashSet<Object>());
    }

    /**
     * Wraps the provided collection for serialization.
     * 
     * @param toWrap collection to wrap
     * @param wrapped collection to which wrapped values will be added
     * @return the wrapped collection for convenience
     */
    public static Collection<SimpleXmlWrapperValue> wrap(Collection<?> toWrap,
        Collection<SimpleXmlWrapperValue> wrapped)
    {
        for (Object value : toWrap)
        {
            wrapped.add(SimpleXmlWrapperValue.wrap(value));
        }
        return wrapped;
    }

    /**
     * Unwraps the provided collection after deserialization.
     * 
     * @param wrapped the SimpleXML-deserialized collection
     * @param unwrapped collection to which the original values should be added
     * @return the unwrapped collection for convenience
     */
    public static Collection<Object> unwrap(Collection<SimpleXmlWrapperValue> wrapped,
        Collection<Object> unwrapped)
    {
        for (SimpleXmlWrapperValue wrappedValue : wrapped)
        {
            unwrapped.add(unwrap(wrappedValue));
        }
        return unwrapped;
    }

    /**
     * Wraps an individual object for serialization.
     */
    public static SimpleXmlWrapperValue wrap(Object value)
    {
        return SimpleXmlWrapperValue.wrap(value);
    }
    
    /**
     * Unwraps an object after deserialization.
     * 
     * @return original value
     */
    public static Object unwrap(final SimpleXmlWrapperValue value)
    {
        if (value != null)
        {
            return value.unwrap();
        }
        else
        {
            return null;
        }
    }

    static synchronized <T> Class<? extends ISimpleXmlWrapper<?>> getWrapper(T value)
    {
        final Class<? extends ISimpleXmlWrapper<?>> clazz = wrappers.get(value.getClass());
        
        // Check for some common fallback cases
        if (clazz == null)
        {
            if (value instanceof List)
            {
                return ListSimpleXmlWrapper.class;
            }
            
            if (value instanceof Map)
            {
                return MapSimpleXmlWrapper.class;
            }
        }
        
        return clazz;
    }
}
