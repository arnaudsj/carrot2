/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.io.*;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.attribute.Init;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.ReflectionUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.*;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.load.Commit;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Descriptor of a {@link IProcessingComponent} being part of a
 * {@link ProcessingComponentSuite}.
 */
public class ProcessingComponentDescriptor
{
    @Attribute(name = "component-class")
    private String componentClassName;

    /** Cached component class instantiated from {@link #componentClassName}. */
    private Class<? extends IProcessingComponent> componentClass;

    /** If not <code>null</code>, component initialization ended with an exception. */
    private Throwable initializationException;

    @Attribute
    private String id;

    @Element
    private String label;

    @Element(required = false)
    private String mnemonic;

    @Element
    private String title;

    @Element(required = false, name = "icon-path")
    private String iconPath;

    @Element(required = false)
    private String description;

    private AttributeValueSets attributeSets;

    @Attribute(name = "attribute-sets-resource", required = false)
    private String attributeSetsResource;

    @Attribute(name = "attribute-set-id", required = false)
    private String attributeSetId;

    @Attribute(required = false)
    Position position = Position.MIDDLE;

    /**
     * The relative positioning of the component within the suite.
     */
    static enum Position
    {
        /** Component appended at the beginning */
        BEGINNING,

        /** Component appended after those at the beginning but before those at the end */
        MIDDLE,

        /** Component appended at the end */
        END;
    }

    ProcessingComponentDescriptor()
    {
    }

    public ProcessingComponentConfiguration getComponentConfiguration()
    {
        return new ProcessingComponentConfiguration(getComponentClass(), id,
            getAttributes());
    }

    private Map<String, Object> getAttributes()
    {
        Map<String, Object> result = AttributeValueSet
            .getAttributeValues(getAttributeSets().getAttributeValueSet(attributeSetId,
                true));

        if (result == null)
        {
            result = Maps.newHashMap();
        }

        return result;
    }

    /**
     * @return Returns the {@link Class} object for this component.
     * @throws RuntimeException if the class cannot be defined for some reason (class
     *             loader issues).
     */
    @SuppressWarnings("unchecked")
    public synchronized Class<? extends IProcessingComponent> getComponentClass()
    {
        if (this.componentClass == null)
        {
            try
            {
                this.componentClass = (Class<? extends IProcessingComponent>) ReflectionUtils
                    .classForName(componentClassName);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Component class cannot be acquired: "
                    + componentClassName, e);
            }
        }
        return this.componentClass;
    }

    public String getId()
    {
        return id;
    }

    public String getLabel()
    {
        return label;
    }

    public String getMnemonic()
    {
        return mnemonic;
    }

    public String getTitle()
    {
        return title;
    }

    /**
     * @return Returns (optional) path to the icon of this component. The interpretation
     *         of this path is up to the application (icon resources may be placed in
     *         various places).
     */
    public String getIconPath()
    {
        return iconPath;
    }

    public String getDescription()
    {
        return description;
    }

    /**
     * @return Return the name of a resource from which {@link #getAttributeSets()} were
     *         read or <code>null</code> if there was no such resource.
     */
    public String getAttributeSetsResource()
    {
        return attributeSetsResource;
    }

    public AttributeValueSets getAttributeSets()
    {
        return attributeSets;
    }

    public String getAttributeSetId()
    {
        return attributeSetId;
    }

    /**
     * Creates a new initialized instance of the processing component corresponding to
     * this descriptor. The instance will be initialized with the {@link Init} attributes
     * from this descriptor's default attribute set. Checking whether all {@link Required}
     * attribute have been provided will not be made, which, when attributes of
     * {@link Bindable} are <code>null</code>, may cause {@link #getBindableDescriptor()}
     * to return incomplete descriptor.
     * <p>
     * The instance may or may not be usable for processing because the
     * {@link IControllerContext} on which it is initialized is disposed before the value
     * is returned.
     * </p>
     */
    @SuppressWarnings("unchecked")
    private IProcessingComponent newInitializedInstance(boolean init)
        throws InstantiationException, IllegalAccessException
    {
        final IProcessingComponent instance = getComponentClass().newInstance();
        final Map<String, Object> initAttributes = Maps.newHashMap();
        final AttributeValueSet defaultAttributeValueSet = attributeSets
            .getDefaultAttributeValueSet();
        if (defaultAttributeValueSet != null)
        {
            initAttributes.putAll(defaultAttributeValueSet.getAttributeValues());
        }

        final ControllerContextImpl context = new ControllerContextImpl();
        try
        {
            AttributeBinder
                .bind(instance, initAttributes, false, Input.class, Init.class);

            if (init)
            {
                instance.init(context);
            }

            AttributeBinder.bind(instance, initAttributes, false, Output.class,
                Init.class);
        }
        finally
        {
            context.dispose();
        }

        return instance;
    }

    /**
     * Builds and returns a {@link BindableDescriptor} for an instance of this
     * descriptor's {@link IProcessingComponent}, with default {@link Init} attributes
     * initialized with the default attribute set. If the default attribute set does not
     * provide values for some required {@link Bindable} {@link Init} attributes, the
     * returned descriptor may be incomplete.
     */
    public BindableDescriptor getBindableDescriptor() throws InstantiationException,
        IllegalAccessException
    {
        return getBindableDescriptor(true);
    }

    /**
     * Builds and returns a {@link BindableDescriptor} for an instance of this
     * descriptor's {@link IProcessingComponent}, with default {@link Init} attributes
     * initialized with the default attribute set. If the default attribute set does not
     * provide values for some required {@link Bindable} {@link Init} attributes, the
     * returned descriptor may be incomplete.
     * 
     * @param init if <code>true</code>, the component will be initialized by calling
     *            {@link IProcessingComponent#init(IControllerContext)}. Otherwise, the
     *            {@link Init} attributes will be bound but
     *            {@link IProcessingComponent#init(IControllerContext)} will not be
     *            called.
     */
    public BindableDescriptor getBindableDescriptor(boolean init)
        throws InstantiationException, IllegalAccessException
    {
        return BindableDescriptorBuilder.buildDescriptor(newInitializedInstance(init));
    }

    /**
     * @return Return <code>true</code> if instances of this descriptor are available
     *         (class can be resolved, instances can be created).
     */
    public boolean isComponentAvailable()
    {
        return this.initializationException == null;
    }

    /**
     * Invoked by the XML loading framework when the object is deserialized.
     */
    private void loadAttributeSets() throws Exception
    {
        attributeSets = new AttributeValueSets();

        final ResourceUtils resourceUtils = ResourceUtilsFactory
            .getDefaultResourceUtils();

        final Class<?> clazz = getComponentClass();
        IResource resource = null;

        if (!StringUtils.isBlank(attributeSetsResource))
        {
            // Try to load from the directly provided location
            resource = resourceUtils.getFirst(attributeSetsResource, clazz);

            if (resource == null)
            {
                throw new IOException("Attribute set resource not found: "
                    + attributeSetsResource);
            }
        }

        if (resource == null)
        {
            // Try className.id.attributes.xml
            resource = resourceUtils.getFirst(getComponentClass().getName() + "."
                + getId() + ".attributes.xml", clazz);
        }

        if (resource == null)
        {
            // Try className.attributes.xml
            resource = resourceUtils.getFirst(getComponentClass().getName()
                + ".attributes.xml", clazz);
        }

        if (resource != null)
        {
            final InputStream inputStream = resource.open();
            try
            {
                attributeSets = AttributeValueSets.deserialize(new InputStreamReader(
                    inputStream, "UTF-8"));
            }
            finally
            {
                CloseableUtils.close(inputStream);
            }
        }

        if (getAttributeSets() == null)
        {
            attributeSets = new AttributeValueSets();
        }
    }

    /**
     * On commit, attempt to verify component class and instance availability.
     */
    @Commit
    @SuppressWarnings("unused")
    private void onCommit()
    {
        this.initializationException = null;
        try
        {
            loadAttributeSets();
            newInitializedInstance(true);
        }
        catch (Throwable e)
        {
            org.slf4j.LoggerFactory.getLogger(this.getClass()).warn(
                "Component availability failure: " + componentClassName, e);
            this.initializationException = e;
        }
    }

    /**
     * Transforms a {@link ProcessingComponentDescriptor} to its identifier.
     */
    public static final class ProcessingComponentDescriptorToId implements
        Function<ProcessingComponentDescriptor, String>
    {
        public static final ProcessingComponentDescriptorToId INSTANCE = new ProcessingComponentDescriptorToId();

        private ProcessingComponentDescriptorToId()
        {
        }

        public String apply(ProcessingComponentDescriptor descriptor)
        {
            return descriptor.id;
        }
    }

    /**
     * Returns initialization failure ({@link Throwable}) or <code>null</code>.
     */
    public Throwable getInitializationFailure()
    {
        return this.initializationException;
    }
}
