
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

package org.carrot2.workbench.vis.aduna;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;

import javax.swing.*;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.Cluster;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.PostponableJob;
import org.carrot2.workbench.core.ui.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;

import biz.aduna.map.cluster.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A single {@link AdunaClusterMapViewPage} page embeds Aduna's Swing component with
 * visualization of clusters.
 */
final class AdunaClusterMapViewPage extends Page
{
    /*
     * 
     */
    private final int REFRESH_DELAY = 500;

    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(AdunaClusterMapViewPage.class);

    /**
     * Classification root.
     */
    private DefaultClassification root;

    /**
     * A map of the most recently shown {@link Cluster}s.
     */
    private Map<Integer, DefaultClassification> clusterMap = Maps.newHashMap();

    /**
     * A map of the most recently shown {@link Document}s.
     */
    private Map<Integer, DefaultObject> documentMap = Maps.newHashMap();

    /**
     * UI job for applying selection to the cluster map component.
     */
    private PostponableJob selectionJob = new PostponableJob(new UIJob(
        "Aduna ClusterMap (selection)...")
    {
        private IStructuredSelection lastSelection = null;

        @SuppressWarnings("unchecked")
        @Override
        public IStatus runInUIThread(IProgressMonitor monitor)
        {
            final VisualizationMode mode = VisualizationMode
                .valueOf(AdunaActivator.plugin.getPreferenceStore().getString(
                    PreferenceConstants.VISUALIZATION_MODE));

            if (root != null)
            {
                final IStructuredSelection currentSelection;
                switch (mode)
                {
                    case SHOW_ALL_CLUSTERS:
                        currentSelection = getAll();
                        break;

                    case SHOW_FIRST_LEVEL_CLUSTERS:
                        currentSelection = getFirstLevel();
                        break;

                    case SHOW_SELECTED_CLUSTERS:
                        currentSelection = getSelected();
                        break;

                    default:
                        throw new RuntimeException("Unhanded case: " + mode);
                }

                if (!currentSelection.equals(lastSelection))
                {
                    final java.util.List selected = 
                        selectionToClassification(currentSelection);
                    logger.debug("Applying selection: " + selected);
                    mapMediator.visualize(selected);

                    this.lastSelection = currentSelection;
                }
            }

            return Status.OK_STATUS;
        }

        /*
         * 
         */
        private java.util.List<Classification> selectionToClassification(
            IStructuredSelection s)
        {
            final IAdapterManager mgr = Platform.getAdapterManager();

            final java.util.List<Classification> selected = Lists.newArrayList();
            for (Object o : s.toList())
            {
                if (o != null && o instanceof Classification)
                {
                    selected.add((Classification) o);
                }
                else
                {
                    final Cluster c = (Cluster) mgr.getAdapter(o, Cluster.class);
                    if (c != null)
                    {
                        final Classification object = clusterMap.get(c.getId());
                        if (object != null) selected.add(object);
                    }
                }
            }

            return selected;
        }

        /**
         * Return the currently selected clusters.
         */
        private IStructuredSelection getSelected()
        {
            final ISelectionProvider sProvider = editor.getSite().getSelectionProvider();
            final ISelection selection = sProvider.getSelection();
            return (IStructuredSelection) selection;
        }

        /**
         * Return the first level of clusters as the selection.
         */
        private IStructuredSelection getFirstLevel()
        {
            if (root == null)
            {
                return StructuredSelection.EMPTY;
            }
            else
            {
                return new StructuredSelection(root.getChildren().toArray());
            }
        }

        /**
         * Return All clusters as the selection. 
         */
        @SuppressWarnings("unchecked")
        protected IStructuredSelection getAll()
        {
            if (root == null)
            {
                return StructuredSelection.EMPTY;
            }
            else
            {
                final java.util.List<Classification> clusters = Lists.newArrayList();
                final java.util.List<Classification> left = Lists.newLinkedList();

                left.add(root);
                while (!left.isEmpty())
                {
                    final Classification c = left.remove(0);
                    clusters.add(c);

                    left.addAll(c.getChildren());
                }

                return new StructuredSelection(clusters);
            }
        }
    });

    /**
     * Refresh the entire structure of clusters.
     */
    private PostponableJob refreshJob = new PostponableJob(new UIJob(
        "Aduna ClusterMap (full refresh)...")
    {
        public IStatus runInUIThread(IProgressMonitor monitor)
        {
            final ProcessingResult result = editor.getSearchResult()
                .getProcessingResult();

            if (result != null)
            {
                root = new DefaultClassification("All clusters");
                clusterMap = Maps.newHashMap();
                documentMap = Maps.newHashMap();
                toClassification(root, result.getClusters());

                mapMediator.setClassificationTree(root);
                selectionJob.reschedule(0);
            }

            return Status.OK_STATUS;
        }

        private void toClassification(DefaultClassification parent,
            java.util.List<Cluster> clusters)
        {
            for (Cluster cluster : clusters)
            {
                if (clusterMap.containsKey(cluster.getId())) continue;

                final DefaultClassification cc = new DefaultClassification(cluster
                    .getLabel(), parent);
                clusterMap.put(cluster.getId(), cc);

                for (Document d : cluster.getAllDocuments())
                {
                    if (!documentMap.containsKey(d.getId()))
                    {
                        String dt = (String) (String) d.getField(Document.TITLE);
                        String title = "[" + d.getId() + "]";
                        if (!StringUtils.isEmpty(dt))
                        {
                            title = title + " " + dt;
                        }

                        documentMap.put(d.getId(), new DefaultObject(title));
                    }

                    cc.add(documentMap.get(d.getId()));
                }

                toClassification(cc, cluster.getSubclusters());
            }
        }
    });

    /*
     * Sync with search result updated event.
     */
    private final SearchResultListenerAdapter editorSyncListener = new SearchResultListenerAdapter()
    {
        public void processingResultUpdated(ProcessingResult result)
        {
            refreshJob.reschedule(REFRESH_DELAY);
        }
    };

    /**
     * Editor selection listener.
     */
    private final ISelectionChangedListener selectionListener = new ISelectionChangedListener()
    {
        /* */
        public void selectionChanged(SelectionChangedEvent event)
        {
            if (VisualizationMode.SHOW_SELECTED_CLUSTERS.name().equals(
                AdunaActivator.plugin.getPreferenceStore().getString(
                    PreferenceConstants.VISUALIZATION_MODE)))
            {
                selectionJob.reschedule(REFRESH_DELAY);
            }
        }
    };

    /*
     * 
     */
    private SearchEditor editor;

    /**
     * SWT's composite inside which Aduna is embedded (AWT/Swing).
     */
    private Composite embedder;

    /**
     * Resource disposal.
     */
    private DisposeBin disposeBin = new DisposeBin();

    /**
     * Aduna's GUI mediator component.
     */
    private ClusterMapMediator mapMediator;

    /**
     * @see VisualizationMode
     */
    private IPropertyChangeListener viewModeListener = new PropertyChangeListenerAdapter(
        PreferenceConstants.VISUALIZATION_MODE)
    {
        protected void propertyChangeFiltered(PropertyChangeEvent event)
        {
            selectionJob.reschedule(REFRESH_DELAY);
        }
    };

    /*
     *
     */
    public AdunaClusterMapViewPage(SearchEditor editor)
    {
        this.editor = editor;
    }

    /*
     * 
     */
    @Override
    public void createControl(Composite parent)
    {
        embedder = createAdunaControl(parent);
        disposeBin.add(embedder);

        /*
         * Add listeners.
         */
        disposeBin.registerPropertyChangeListener(AdunaActivator.plugin
            .getPreferenceStore(), viewModeListener);

        /*
         * Add a listener to the editor to update the view after new clusters are
         * available.
         */
        if (editor.getSearchResult().getProcessingResult() != null)
        {
            refreshJob.reschedule(REFRESH_DELAY);
        }

        editor.getSearchResult().addListener(editorSyncListener);
        editor.getSite().getSelectionProvider()
            .addSelectionChangedListener(selectionListener);
    }

    /*
     * 
     */
    @SuppressWarnings("serial")
    private Composite createAdunaControl(Composite parent)
    {
        /*
         * If <code>true</code>, try some dirty hacks to avoid flicker on Windows.
         */
        final boolean windowsFlickerHack = true;
        if (windowsFlickerHack)
        {
            System.setProperty("sun.awt.noerasebackground", "true");
        }

        final ScrolledComposite scroll = new ScrolledComposite(parent, SWT.H_SCROLL
            | SWT.V_SCROLL);
        scroll.setAlwaysShowScrollBars(true);

        final Composite embedded = new Composite(scroll, SWT.EMBEDDED);
        scroll.setContent(embedded);

        final Color swtBackground = parent.getDisplay().getSystemColor(SWT.COLOR_GRAY);
        final java.awt.Color awtBackground = new java.awt.Color(swtBackground.getRed(),
            swtBackground.getGreen(), swtBackground.getBlue());
        scroll.setBackground(swtBackground);

        final Frame frame = SWT_AWT.new_Frame(embedded);
        frame.setLayout(new BorderLayout());

        final Panel frameRootPanel = new Panel(new BorderLayout())
        {
            public void update(java.awt.Graphics g)
            {
                if (windowsFlickerHack)
                {
                    paint(g);
                }
                else
                {
                    super.update(g);
                }
            }
        };
        frame.add(frameRootPanel);

        final JRootPane rootPane = new JRootPane();
        frameRootPanel.add(rootPane);

        /*
         * We embed ClusterMap inside a JScrollPane that never shows scrollbars because we
         * want our scrollbars to be drawn by SWT components.
         */
        final JScrollPane scrollPanel = new JScrollPane(
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.setBackground(awtBackground);
        scrollPanel.setBorder(BorderFactory.createLineBorder(awtBackground));
        rootPane.getContentPane().add(scrollPanel, BorderLayout.CENTER);

        final ClusterMapFactory factory = ClusterMapFactory.createFactory();
        final ClusterMap clusterMap = factory.createClusterMap();
        final ClusterMapMediator mapMediator = factory.createMediator(clusterMap);

        this.mapMediator = mapMediator;

        final ClusterGraphPanel graphPanel = mapMediator.getGraphPanel();
        scrollPanel.setViewportView(graphPanel);

        graphPanel.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(final ComponentEvent e)
            {
                embedded.getDisplay().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        final Dimension preferredSize = e.getComponent()
                            .getPreferredSize();
                        final Rectangle clientArea = scroll.getClientArea();

                        embedded.setSize(Math.max(preferredSize.width, clientArea.width),
                            Math.max(preferredSize.height, clientArea.height));
                    }
                });
            }
        });

        scroll.addControlListener(new ControlAdapter()
        {
            public void controlResized(ControlEvent e)
            {
                // This is not thread-safe here, is it?
                final Dimension preferredSize = graphPanel.getPreferredSize();
                final Rectangle clientArea = scroll.getClientArea();

                embedded.setSize(Math.max(preferredSize.width, clientArea.width), Math
                    .max(preferredSize.height, clientArea.height));
            }
        });

        return scroll;
    }

    /*
     * 
     */
    @Override
    public Control getControl()
    {
        return embedder;
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        editor.getSearchResult().removeListener(editorSyncListener);
        editor.getSite().getSelectionProvider().removeSelectionChangedListener(selectionListener);

        disposeBin.dispose();

        super.dispose();
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        // Ignore.
    }
}
