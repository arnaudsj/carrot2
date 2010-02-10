
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

package org.carrot2.util.attribute;

import static org.carrot2.util.attribute.test.assertions.AttributeAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.test.binder.*;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BindableDescriptorBuilderTest
{
    @Test
    public void testSimpleComponent() throws Exception
    {
        final Object instance = new SingleClass();
        final Class<?> clazz = SingleClass.class;

        final BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        assertThat(bindableDescriptor.bindableDescriptorsInternal).isEmpty();

        assertThat(bindableDescriptor).contains(
            AttributeUtils.getKey(SingleClass.class, "initInputInt"),
            new AttributeDescriptor(clazz.getDeclaredField("initInputInt"), 10, Lists
                .<Annotation> newArrayList(), new AttributeMetadata(
                "Init input int attribute", "Init Input Int", null, "Group A",
                AttributeLevel.BASIC)));

        assertThat(bindableDescriptor).contains(
            AttributeUtils.getKey(SingleClass.class, "processingInputString"),
            new AttributeDescriptor(clazz.getDeclaredField("processingInputString"),
                "test", Lists.<Annotation> newArrayList(), new AttributeMetadata(
                    "Processing input string attribute", "Processing Input String",
                    "Some description.", "Group B", AttributeLevel.ADVANCED)));

        assertThat(bindableDescriptor.attributeDescriptors.keySet()).excludes(
            clazz.getName() + ".notAnAttribute");
    }

    @Test
    public void testNonprimitiveAttribute() throws Exception
    {
        final NonprimitiveAttribute instance = new NonprimitiveAttribute();
        final Class<?> clazz = NonprimitiveAttribute.class;

        final BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        assertThat(bindableDescriptor).contains(
            AttributeUtils.getKey(clazz, "resource"),
            new AttributeDescriptor(clazz.getDeclaredField("resource"),
                instance.resource, Lists
                    .<Annotation> newArrayList(NonprimitiveAttribute.class.getField(
                        "resource").getAnnotation(ImplementingClasses.class)),
                new AttributeMetadata("Nonprimitive", null, null, null, null)));
    }

    @Test
    public void testSubClass() throws Exception
    {
        final Object instance = new SubClass();
        final Class<?> subClass = SubClass.class;
        final Class<?> superClass = SuperClass.class;

        final BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        assertThat(bindableDescriptor.bindableDescriptorsInternal).isEmpty();
        assertThat(bindableDescriptor).contains(
            AttributeUtils.getKey(SuperClass.class, "initInputInt"),
            new AttributeDescriptor(superClass.getDeclaredField("initInputInt"), 5, Lists
                .<Annotation> newArrayList(), new AttributeMetadata(
                "Super class init input int", null, null)));
        assertThat(bindableDescriptor).contains(
            AttributeUtils.getKey(SubClass.class, "processingInputString"),
            new AttributeDescriptor(subClass.getDeclaredField("processingInputString"),
                "input", Lists.<Annotation> newArrayList(), new AttributeMetadata(
                    "Subclass processing input", null, null)));
    }

    @Test
    public void testBindableReferenceWithDefaultValue() throws Exception
    {
        final Object instance = new BindableReferenceContainer();
        final Class<?> bindableReferenceClass = BindableReferenceContainer.class;
        final Class<?> referenceClass = BindableReferenceImpl1.class;

        final BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        assertThat(bindableDescriptor).contains(
            AttributeUtils.getKey(BindableReferenceContainer.class, "bindableAttribute"),
            new AttributeDescriptor(bindableReferenceClass
                .getDeclaredField("bindableAttribute"), null, Lists
                .<Annotation> newArrayList(BindableReferenceContainer.class
                    .getDeclaredField("bindableAttribute").getAnnotation(
                        ImplementingClasses.class)), new AttributeMetadata(
                "Test Bindable", null, null)));

        // Referenced attributes
        assertThat(bindableDescriptor.bindableDescriptorsInternal).isNotEmpty();
        assertThat(
            bindableDescriptor.bindableDescriptorsInternal
                .get(BindableReferenceContainer.class.getDeclaredField("bindableField")))
            .isNotNull().contains(
                AttributeUtils.getKey(referenceClass, "processingInputInt"),
                new AttributeDescriptor(referenceClass
                    .getDeclaredField("processingInputInt"), 10, Lists
                    .<Annotation> newArrayList(), new AttributeMetadata(
                    "Processing input int", null, null))).hasMetadata(
                new BindableMetadata());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBindableReferenceWithBoundValue() throws Exception
    {
        final Object instance = new BindableReferenceContainer();
        final Class<?> bindableReferenceClass = BindableReferenceContainer.class;
        final Class<?> referenceClass1 = BindableReferenceImpl1.class;
        final Class<?> referenceClass2 = BindableReferenceImpl2.class;

        // Bind init input attributes
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(AttributeUtils.getKey(BindableReferenceContainer.class,
            "bindableAttribute"), BindableReferenceImpl2.class);
        AttributeBinder.bind(instance, attributes, Input.class, TestInit.class);

        final BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        assertThat(bindableDescriptor).contains(
            AttributeUtils.getKey(BindableReferenceContainer.class, "bindableAttribute"),
            new AttributeDescriptor(bindableReferenceClass
                .getDeclaredField("bindableAttribute"), new BindableReferenceImpl2(),
                Lists.<Annotation> newArrayList(BindableReferenceContainer.class
                    .getDeclaredField("bindableAttribute").getAnnotation(
                        ImplementingClasses.class)), new AttributeMetadata(
                    "Test Bindable", null, null)));

        // Referenced attributes -- regular field
        assertThat(bindableDescriptor.bindableDescriptorsInternal).isNotEmpty();
        assertThat(
            bindableDescriptor.bindableDescriptorsInternal
                .get(BindableReferenceContainer.class.getDeclaredField("bindableField")))
            .isNotNull()
            .contains(
                AttributeUtils.getKey(BindableReferenceImpl1.class, "processingInputInt"),
                new AttributeDescriptor(referenceClass1
                    .getDeclaredField("processingInputInt"), 10, Lists
                    .<Annotation> newArrayList(), new AttributeMetadata(
                    "Processing input int", null, null)));

        // Referenced attributes -- attribute field
        assertThat(
            bindableDescriptor.bindableDescriptorsInternal
                .get(BindableReferenceContainer.class
                    .getDeclaredField("bindableAttribute"))).isNotNull().contains(
            AttributeUtils.getKey(BindableReferenceImpl2.class, "initInputInt"),
            new AttributeDescriptor(referenceClass2.getDeclaredField("initInputInt"), 12,
                Lists.<Annotation> newArrayList(), new AttributeMetadata(
                    "Init input int", null, null)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCircularReference()
    {
        final CircularReferenceContainer instance = new CircularReferenceContainer();
        instance.circular = instance;

        BindableDescriptorBuilder.buildDescriptor(instance);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotBindable()
    {
        BindableDescriptorBuilder.buildDescriptor(new NotBindable());
    }
}
