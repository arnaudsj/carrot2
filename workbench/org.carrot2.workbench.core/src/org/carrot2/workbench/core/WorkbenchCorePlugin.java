
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

package org.carrot2.workbench.core;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.text.linguistic.LanguageCode;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.resource.*;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.core.ui.SearchEditorSections;
import org.carrot2.workbench.core.ui.SearchEditor.SectionReference;
import org.carrot2.workbench.core.ui.adapters.ClusterAdapterFactory;
import org.carrot2.workbench.core.ui.adapters.PropertySourceAdapterFactory;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The activator class (plug-in's entry point), controls the life-cycle and contains a
 * reference to the Carrot2 {@link IController}.
 */
public class WorkbenchCorePlugin extends AbstractUIPlugin
{
    /** Plug-in ID. */
    public static final String PLUGIN_ID = "org.carrot2.workbench.core";

    /** Source extension identifier. */
    public static final String COMPONENT_SUITE_EXTENSION_ID = "org.carrot2.core.componentSuite";

    /** The shared instance. */
    private static WorkbenchCorePlugin plugin;

    /**
     * Shared, thread-safe caching controller instance.
     */
    private CachingController controller;

    /**
     * All loaded components ({@link IDocumentSource}s and {@link IClusteringAlgorithm}.
     */
    private ProcessingComponentSuite componentSuite;

    /**
     * Cached {@link BindableDescriptor}s of all available components in
     * {@link #componentSuite}.
     */
    private HashMap<String, BindableDescriptor> bindableDescriptors = Maps.newHashMap();

    /**
     * Cached component bindableDescriptors of all available components in
     * {@link #componentSuite}.
     */
    private HashMap<String, ProcessingComponentDescriptor> processingDescriptors = Maps
        .newHashMap();

    /**
     * Cached image descriptors of components.
     */
    private HashMap<String, ImageDescriptor> componentImages = Maps.newHashMap();

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;

        /*
         * Copy linguistic resources at first launch (or if deleted from workspace). Add
         * workspace to the list of resource locations.
         */
        installWorkbenchResourceLocator();

        // Scan the list of suite extension points.
        scanSuites();

        controller = new CachingController(IDocumentSource.class);
        controller.init(new HashMap<String, Object>(), componentSuite);

        /*
         * Register adapters.
         */
        PropertySourceAdapterFactory.register(Platform.getAdapterManager());
        ClusterAdapterFactory.register(Platform.getAdapterManager());
    }

    /*
     * 
     */
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;

        controller.dispose();
        controller = null;

        super.stop(context);
    }

    /**
     * Returns an initialized shared controller instance.
     */
    public CachingController getController()
    {
        return controller;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * Returns all loaded components ({@link IClusteringAlgorithm} and
     * {@link IDocumentSource}.
     */
    public ProcessingComponentSuite getComponentSuite()
    {
        return componentSuite;
    }

    /**
     * Returns a {@link BindableDescriptor} for a given component ID or <code>null</code>
     * if this component is not available.
     */
    public BindableDescriptor getComponentDescriptor(String componentID)
    {
        return bindableDescriptors.get(componentID);
    }

    /**
     * Returns a {@link ProcessingComponentDescriptor} for a given component ID or
     * <code>null<code>.
     */
    public ProcessingComponentDescriptor getComponent(String componentID)
    {
        return processingDescriptors.get(componentID);
    }

    /**
     * Returns a {@link ImageDescriptor} for a given component or a default image if the
     * component did not contain any icon.
     */
    public ImageDescriptor getComponentImageDescriptor(String componentID)
    {
        ImageDescriptor d = componentImages.get(componentID);
        if (d == null)
        {
            d = getImageDescriptor("icons/carrot2-16x16.png");
        }
        return d;
    }

    /**
     * Returns the shared instance.
     */
    public static WorkbenchCorePlugin getDefault()
    {
        return plugin;
    }

    /**
     * Scan all declared extensions of {@link #COMPONENT_SUITE_EXTENSION_ID} extension
     * point.
     */
    private void scanSuites()
    {
        final List<ProcessingComponentSuite> suites = Lists.newArrayList();

        final IExtension [] extensions = Platform.getExtensionRegistry()
            .getExtensionPoint(COMPONENT_SUITE_EXTENSION_ID).getExtensions();

        // Load suites from extension points.
        extLoop: for (IExtension extension : extensions)
        {
            final IConfigurationElement [] configElements = extension
                .getConfigurationElements();
            if (configElements.length == 1 && "suite".equals(configElements[0].getName()))
            {
                String suiteRoot = configElements[0].getAttribute("resourceRoot");
                if (StringUtils.isEmpty(suiteRoot)) suiteRoot = "";
                final String suiteResource = configElements[0].getAttribute("resource");

                if (StringUtils.isEmpty(suiteResource))
                {
                    continue;
                }

                final IContributor c = extension.getContributor();
                final Bundle b = Platform.getBundle(c.getName());

                if (b.getState() != Bundle.ACTIVE)
                {
                    try
                    {
                        b.start();
                    }
                    catch (BundleException e)
                    {
                        continue extLoop;
                    }
                }

                final URL bundleURL = b.getEntry(suiteRoot + suiteResource);
                
                if (bundleURL == null)
                {
                    String message = "Suite extension resource not found: "
                        + suiteRoot + suiteResource;
                    Logger.getRootLogger().error(message);
                    Utils.logError(message, false);
                    continue;
                }

                /* This piece of code is currently quite fragile and hacky, but works. 
                 * 
                 * First, we rely on Eclipse-BuddyPolicy declared on the simplexml framework
                 * to instantiate arbitrary classes (from sources and algorithms). 
                 * This policy could be removed if we passed an explicit Persister
                 * with a strategy substituting the context class loader with the given
                 * Bundle's loadClass() call. I leave it for now.
                 * 
                 * Second, the suite inclusion mechanism uses resource lookup, but the suite
                 * plugin does not export suites as part of the classpath (it
                 * contains plugin resources, but they are not on the classpath). We temporarily
                 * add a custom resource locator that searches the contributing
                 * plugin for resources matching the included resource.
                 */
                try
                {
                    /*
                     * Add temporary resource locator.
                     */
                    final IResourceLocator bundleLocator = 
                        new PrefixDecoratorLocator(new BundleResourceLocator(b), suiteRoot);
                    ResourceUtilsFactory.addFirst(bundleLocator);
                    try
                    {
                        final ProcessingComponentSuite suite = ProcessingComponentSuite
                            .deserialize(new URLResource(bundleURL));

                        /*
                         * Remove invalid descriptors, cache icons.
                         */
                        suite.removeUnavailableComponents();
                        for (ProcessingComponentDescriptor d : suite.getComponents())
                        {
                            final String iconPath = d.getIconPath();
                            if (StringUtils.isEmpty(iconPath))
                            {
                                continue;
                            }
    
                            componentImages.put(d.getId(), imageDescriptorFromPlugin(c
                                .getName(), iconPath));
                        }
    
                        suites.add(suite);
                    }
                    finally
                    {
                        ResourceUtilsFactory.remove(bundleLocator);
                    }
                }
                catch (Exception e)
                {
                    Logger.getRootLogger().error("Failed to load extension.", e);
                    // Skip errors, logging them.
                    Utils.logError("Failed to load suite extension.", e, false);
                }
            }
        }

        // Merge all available suites
        final ArrayList<DocumentSourceDescriptor> sources = Lists.newArrayList();
        final ArrayList<ProcessingComponentDescriptor> algorithms = Lists.newArrayList();

        for (ProcessingComponentSuite s : suites)
        {
            sources.addAll(s.getSources());
            algorithms.addAll(s.getAlgorithms());
        }

        this.componentSuite = new ProcessingComponentSuite(sources, algorithms);

        // Extract and cache bindableDescriptors.
        for (ProcessingComponentDescriptor pcd : componentSuite.getComponents())
        {
            try
            {
                final String id = pcd.getId();
                processingDescriptors.put(id, pcd);
                bindableDescriptors.put(id, pcd.getBindableDescriptor());
            }
            catch (Exception e)
            {
                Utils.logError("Could not extract descriptor from: " + pcd.getId(), e,
                    false);
            }
        }
    }

    /**
     * Adds workspace to the list of resource locations.
     */
    private void installWorkbenchResourceLocator()
    {
        final IPath instanceLocation = Platform.getLocation();
        if (instanceLocation == null)
        {
            // Issue a warning about read-only location.
            Utils.logError("Instance location not available.", false);
            return;
        }

        /*
         * Check if the workspace directory exists. It may happen the workspace has just
         * been created.
         */
        final File workspacePath = instanceLocation.toFile().getAbsoluteFile();
        if (!workspacePath.exists())
        {
            workspacePath.mkdirs();
        }

        if (!workspacePath.exists())
        {
            // Issue a warning about read-only location.
            Utils.logError("Instance location does not exist: " + workspacePath, false);
            return;
        }

        /*
         * Copy linguistic resources to the given location on first launch (or if
         * missing). Make sure this is done <b>before</b> installing the new resource
         * locator because we'd be chasing our tail here.
         */
        final Map<String, IResource> resources = Maps.newLinkedHashMap();
        final ResourceUtils resUtils = ResourceUtilsFactory.getDefaultResourceUtils();

        for (LanguageCode language : LanguageCode.values())
        {
            addResourceToCopy(resources, resUtils, language, "stopwords");
            addResourceToCopy(resources, resUtils, language, "stoplabels");
        }

        for (Map.Entry<String, IResource> e : resources.entrySet())
        {
            final String fileName = e.getKey();
            final File targetFile = new File(workspacePath, fileName);

            if (targetFile.exists())
            {
                // Skip if exists.
                continue;
            }

            InputStream in = null;
            OutputStream out = null;
            try
            {
                in = e.getValue().open();
                out = new FileOutputStream(targetFile);
                StreamUtils.copy(in, out, 8 * 1024);
            }
            catch (IOException x)
            {
                Utils.logError("Could not copy resource: " + fileName, x, false);
            }
            finally
            {
                CloseableUtils.close(in, out);
            }
        }

        /*
         * Install a resource locator pointing to the workspace.
         */
        ResourceUtilsFactory.addFirst(new DirLocator(workspacePath.getAbsolutePath()));
    }

    private void addResourceToCopy(final Map<String, IResource> resources,
        final ResourceUtils resUtils, LanguageCode language, final String prefix)
    {
        final String stopwords = prefix + "." + language.getIsoCode();
        final IResource resource = resUtils.getFirst(stopwords);
        if (resource != null)
        {
            resources.put(stopwords, resource);
        }
    }

    /**
     * Restore the state of {@link SearchEditor}'s sections from the most recent global
     * state.
     */
    public void restoreSectionsState(
        EnumMap<SearchEditorSections, SearchEditor.SectionReference> sections)
    {
        final IPreferenceStore store = getPreferenceStore();
        for (Map.Entry<SearchEditorSections, SearchEditor.SectionReference> s : sections
            .entrySet())
        {
            final SearchEditorSections section = s.getKey();
            final SectionReference ref = s.getValue();

            ref.weight = store.getInt(PreferenceConstants.getSectionWeightKey(section));
            ref.visibility = store.getBoolean(PreferenceConstants
                .getSectionVisibilityKey(section));
        }
    }

    /**
     * Keep a reference to the most recently updated {@link SearchEditor}'s sections.
     */
    public void storeSectionsState(
        EnumMap<SearchEditorSections, SectionReference> sections)
    {
        final IPreferenceStore store = getPreferenceStore();
        for (Map.Entry<SearchEditorSections, SearchEditor.SectionReference> s : sections
            .entrySet())
        {
            final SearchEditorSections section = s.getKey();
            final SectionReference ref = s.getValue();

            final String key = PreferenceConstants.getSectionWeightKey(section);
            final String key2 = PreferenceConstants.getSectionVisibilityKey(section);
            store.setValue(key, ref.weight);
            store.setValue(key2, ref.visibility);
        }
    }
}
