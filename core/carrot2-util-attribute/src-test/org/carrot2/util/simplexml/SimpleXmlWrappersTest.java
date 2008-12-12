
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

import static org.fest.assertions.Assertions.assertThat;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.util.*;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.resource.FileResource;
import org.junit.Test;
import org.simpleframework.xml.*;
import org.simpleframework.xml.load.*;

import com.google.common.collect.*;

/**
 * Test cases for {@link SimpleXmlWrappers}.
 */
public class SimpleXmlWrappersTest
{
    @Test
    public void testByte() throws Exception
    {
        check((byte) -10);
        check((byte) 0);
        check((byte) 10);
    }

    @Test
    public void testShort() throws Exception
    {
        check((short) -5);
        check((short) 0);
        check((short) 16);
    }

    @Test
    public void testInt() throws Exception
    {
        check(-120);
        check(0);
        check(20);
    }

    @Test
    public void testLong() throws Exception
    {
        check(-2093847298347L);
        check(0L);
        check(209293847298347L);
    }

    @Test
    public void testFloat() throws Exception
    {
        check(-0.25f);
        check(0f);
        check(0.5f);
    }

    @Test
    public void testDouble() throws Exception
    {
        check(-0.125);
        check(0);
        check(8.5);
    }

    @Test
    public void testBoolean() throws Exception
    {
        check(true);
        check(false);
    }

    @Test
    public void testChar() throws Exception
    {
        check('x');
        check('ą');
    }

    @Test
    public void testString() throws Exception
    {
        check("test");
        check("żółć");
    }

    @Test
    public void testClass() throws Exception
    {
        check(File.class);
        check(MapContainer.class);
    }

    @Test
    public void testFileResource() throws Exception
    {
        check(new FileResource(new File(".").getAbsoluteFile()));
    }

    enum TestEnum
    {
        TEST1, TEST2;

        @Override
        public String toString()
        {
            return name().toLowerCase();
        }
    }

    @Test
    public void testEnum() throws Exception
    {
        check(AttributeLevel.ADVANCED);
        check(TestEnum.TEST1);
    }

    @Test
    public void testNull() throws Exception
    {
        check(null);
    }

    @Root(name = "annotated")
    static class AnnotatedClass
    {
        @Attribute(required = false)
        String string;

        @Attribute(required = false)
        Integer integer;

        AnnotatedClass()
        {
        }

        AnnotatedClass(Integer integer, String string)
        {
            this.integer = integer;
            this.string = string;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof AnnotatedClass))
            {
                return false;
            }
            return ObjectUtils.equals(((AnnotatedClass) obj).string, string)
                && ObjectUtils.equals(((AnnotatedClass) obj).integer, integer);
        }

        @Override
        public int hashCode()
        {
            return (string != null ? string.hashCode() : 0)
                ^ (integer != null ? integer.hashCode() : 0);
        }
    }

    @Test
    public void testSimpleXmlAnnotatedClass() throws Exception
    {
        check(new AnnotatedClass(10, "test"));
        check(new AnnotatedClass(-5, null));
        check(new AnnotatedClass(null, "test"));
        check(new AnnotatedClass(null, null));
    }

    static class ClassWithWrapper
    {
        String string;
        Integer integer;

        public ClassWithWrapper(Integer integer, String string)
        {
            this.integer = integer;
            this.string = string;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ClassWithWrapper))
            {
                return false;
            }
            return ObjectUtils.equals(((ClassWithWrapper) obj).string, string)
                && ObjectUtils.equals(((ClassWithWrapper) obj).integer, integer);
        }

        @Override
        public int hashCode()
        {
            return (string != null ? string.hashCode() : 0)
                ^ (integer != null ? integer.hashCode() : 0);
        }
    }

    @Root(name = "with-wrapper")
    static class ClassWithWrapperWrapper implements ISimpleXmlWrapper<ClassWithWrapper>
    {
        ClassWithWrapper classWithWrapper;

        @Element
        AnnotatedClass forSerialization;

        public ClassWithWrapper getValue()
        {
            return classWithWrapper;
        }

        public void setValue(ClassWithWrapper value)
        {
            this.classWithWrapper = value;
        }

        @Persist
        void beforeSerialization()
        {
            forSerialization = new AnnotatedClass(classWithWrapper.integer,
                classWithWrapper.string);
        }

        @Commit
        void afterDeserialization()
        {
            classWithWrapper = new ClassWithWrapper(forSerialization.integer,
                forSerialization.string);
        }
    }

    @Test
    public void testClassWithWrapper() throws Exception
    {
        SimpleXmlWrappers.addWrapper(ClassWithWrapper.class,
            ClassWithWrapperWrapper.class);
        check(new ClassWithWrapper(10, "test"));
        check(new ClassWithWrapper(-5, null));
        check(new ClassWithWrapper(null, "test"));
        check(new ClassWithWrapper(null, null));
    }

    public void check(Object value) throws Exception
    {
        checkMap(Long.toString(value != null ? value.hashCode() : 0), value);
        checkList(value);
        checkSet(value);
    }

    public void checkMap(String key, Object value) throws Exception
    {
        final Map<String, Object> original = Maps.newHashMap();
        original.put(key, value);
        final StringWriter writer = new StringWriter();
        new Persister().write(new MapContainer(original), writer);
        final MapContainer deserialized = new Persister().read(MapContainer.class,
            new StringReader(writer.getBuffer().toString()));
        assertThat(deserialized.map).isEqualTo(original);
    }

    public void checkList(Object value) throws Exception
    {
        final List<Object> original = Lists.newArrayList();
        original.add(value);
        final StringWriter writer = new StringWriter();
        new Persister().write(new ListContainer(original), writer);
        final ListContainer deserialized = new Persister().read(ListContainer.class,
            new StringReader(writer.getBuffer().toString()));
        assertThat(deserialized.list).isEqualTo(original);
    }

    public void checkSet(Object value) throws Exception
    {
        final Set<Object> original = Sets.newHashSet();
        original.add(value);
        final StringWriter writer = new StringWriter();
        new Persister().write(new SetContainer(original), writer);
        final SetContainer deserialized = new Persister().read(SetContainer.class,
            new StringReader(writer.getBuffer().toString()));
        assertThat(deserialized.set).isEqualTo(original);
    }

    @Root(name = "map")
    private static class MapContainer
    {
        private Map<String, Object> map;

        @ElementMap(entry = "attribute", inline = true, value = "value", attribute = true, key = "key")
        private HashMap<String, SimpleXmlWrapperValue> mapToSerialize;

        MapContainer()
        {
        }

        MapContainer(Map<String, Object> map)
        {
            this.map = map;
        }

        @Persist
        void wrap()
        {
            mapToSerialize = MapUtils.asHashMap(SimpleXmlWrappers.wrap(map));
        }

        @Commit
        void unwrap()
        {
            map = SimpleXmlWrappers.unwrap(mapToSerialize);
        }
    }

    @Root(name = "list")
    private static class ListContainer
    {
        private List<Object> list;

        @ElementList(entry = "attribute", inline = true)
        private ArrayList<SimpleXmlWrapperValue> listToSerialize;

        public ListContainer()
        {
        }

        public ListContainer(List<Object> list)
        {
            this.list = list;
        }

        @Persist
        void wrap()
        {
            listToSerialize = ListUtils.asArrayList(SimpleXmlWrappers.wrap(list));
        }

        @Commit
        void unwrap()
        {
            list = SimpleXmlWrappers.unwrap(listToSerialize);
        }
    }

    @Root(name = "set")
    private static class SetContainer
    {
        private Set<Object> set;

        @ElementList(entry = "attribute", inline = true)
        private HashSet<SimpleXmlWrapperValue> setToSerialize;

        public SetContainer()
        {
        }

        public SetContainer(Set<Object> set)
        {
            this.set = set;
        }

        @Persist
        void wrap()
        {
            setToSerialize = SetUtils.asHashSet(SimpleXmlWrappers.wrap(set));
        }

        @Commit
        void unwrap()
        {
            set = SimpleXmlWrappers.unwrap(setToSerialize);
        }
    }
}
