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

package org.carrot2.workbench.core.ui;

import java.io.File;
import java.util.*;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchActionFactory;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.*;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.actions.GroupingMethodAction;
import org.carrot2.workbench.core.ui.actions.SaveAsXMLActionDelegate;
import org.carrot2.workbench.core.ui.sash.SashForm;
import org.carrot2.workbench.core.ui.widgets.CScrolledComposite;
import org.carrot2.workbench.editors.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;

/**
 * Editor accepting {@link SearchInput} and performing operations on it. The editor also
 * exposes the model of processing results.
 */
public final class SearchEditor extends EditorPart implements IPersistableEditor,
    IPostSelectionProvider
{
    /**
     * Public identifier of this editor.
     */
    public static final String ID = "org.carrot2.workbench.core.editors.searchEditor";

    /**
     * Options required for {@link #doSave(IProgressMonitor)}.
     */
    public static final class SaveOptions implements Cloneable
    {
        public String directory;
        public String fileName;

        public boolean includeDocuments = true;
        public boolean includeClusters = true;

        public String getFullPath()
        {
            return new File(new File(directory), fileName).getAbsolutePath();
        }

        public static String sanitizeFileName(String anything)
        {
            String result = anything.replaceAll("[^a-zA-Z0-9_\\-.\\s]", "");
            result = result.trim().replaceAll("[\\s]+", "-");
            result = result.toLowerCase();
            if (StringUtils.isEmpty(result))
            {
                result = "unnamed";
            }
            return result;
        }
    }

    /**
     * Most recent save options.
     */
    private SaveOptions saveOptions;

    /*
     * Memento attributes and sections.
     */

    private static final String MEMENTO_SECTIONS = "sections";
    private static final String MEMENTO_SECTION = "section";
    private static final String SECTION_NAME = "name";
    private static final String SECTION_WEIGHT = "weight";
    private static final String SECTION_VISIBLE = "visible";

    /**
     * Part property indicating current grouping of attributes on the
     * {@link SearchEditorSections#ATTRIBUTES}.
     */
    private static final String GROUPING_LOCAL = PreferenceConstants.GROUPING_EDITOR_PANEL
        + ".local";

    /**
     * All attributes of a single panel.
     */
    public final static class SectionReference
    {
        public final Section section;
        public final int sashIndex;
        public boolean visibility;
        public int weight;

        SectionReference(Section self, int sashIndex, boolean v, int w)
        {
            this.section = self;
            this.sashIndex = sashIndex;
            this.visibility = v;
            this.weight = w;
        }

        public SectionReference(SectionReference other)
        {
            this(null, -1, other.visibility, other.weight);
        }
    }

    /**
     * Sections (panels) present inside the editor.
     */
    private EnumMap<SearchEditorSections, SectionReference> sections;

    /**
     * Search result model is the core model around which all other views revolve
     * (editors, views, actions). It can perform transformation of {@link SearchInput}
     * into a {@link ProcessingResult} and inform listeners about changes going on in the
     * model.
     */
    private SearchResult searchResult;

    /**
     * Resources to be disposed of in {@link #dispose()}.
     */
    private DisposeBin resources;

    /**
     * Synchronization between {@link DocumentList} and the current workbench's selection.
     */
    private DocumentListSelectionSync documentListSelectionSync;

    /**
     * Image from the {@link SearchInput} used to run the query.
     */
    private Image sourceImage;

    /**
     * If <code>true</code>, then the editor's {@link #searchResult} contain a stale value
     * with regard to its input.
     */
    private boolean dirty = true;

    /*
     * GUI layout, state restoration.
     */

    private FormToolkit toolkit;
    private Form rootForm;
    private SashForm sashForm;

    /**
     * This editor's restore state information.
     */
    private IMemento state;

    /**
     * {@link SearchEditor} forwards its selection provider methods to this component (
     * {@link SearchEditorSections#CLUSTERS} panel).
     */
    private IPostSelectionProvider selectionProvider;

    /**
     * There is only one {@link SearchJob} assigned to each editor. The job is
     * re-scheduled when re-processing is required.
     * 
     * @see #reprocess()
     */
    private SearchJob searchJob;

    /**
     * Auto-update listener calls {@link #reprocess()} after
     * {@link PreferenceConstants#AUTO_UPDATE} property changes.
     */
    private IAttributeListener autoUpdateListener = new AttributeListenerAdapter()
    {
        /** Postponable reschedule job. */
        private PostponableJob job = new PostponableJob(new UIJob("Auto update...")
        {
            public IStatus runInUIThread(IProgressMonitor monitor)
            {
                reprocess();
                return Status.OK_STATUS;
            }
        });

        public void valueChanged(AttributeEvent event)
        {
            final IPreferenceStore store = WorkbenchCorePlugin.getDefault()
                .getPreferenceStore();
            if (store.getBoolean(PreferenceConstants.AUTO_UPDATE))
            {
                final int delay = store.getInt(PreferenceConstants.AUTO_UPDATE_DELAY);
                job.reschedule(delay);
            }
        }
    };

    /**
     * When auto-update key in the preference store changes, force re-processing in case
     * the editor is dirty.
     */
    private IPropertyChangeListener autoUpdateListener2 = new IPropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent event)
        {
            if (PreferenceConstants.AUTO_UPDATE.equals(event.getProperty()))
            {
                if (isDirty())
                {
                    reprocess();
                }
            }
        }
    };

    /**
     * Attribute editors.
     */
    private AttributeGroups attributesPanel;

    /**
     * Create main GUI components, hook up events, schedule initial processing.
     */
    @Override
    public void createPartControl(Composite parent)
    {
        this.resources = new DisposeBin(WorkbenchCorePlugin.getDefault());

        sourceImage = getEditorInput().getImageDescriptor().createImage();
        resources.add(sourceImage);

        toolkit = new FormToolkit(parent.getDisplay());
        resources.add(toolkit);

        rootForm = toolkit.createForm(parent);
        rootForm.setText(getPartName());
        rootForm.setImage(getTitleImage());

        toolkit.decorateFormHeading(rootForm);

        sashForm = new SashForm(rootForm.getBody(), SWT.HORIZONTAL)
        {
            protected boolean onDragSash(Event event)
            {
                final boolean modified = super.onDragSash(event);
                if (modified)
                {
                    /*
                     * Update globally remembered weights.
                     */
                    final int [] weights = sashForm.getWeights();
                    for (SearchEditorSections s : sections.keySet())
                    {
                        SectionReference sr = sections.get(s);
                        sr.weight = weights[sr.sashIndex];
                    }

                    WorkbenchCorePlugin.getDefault().storeSectionsState(getSections());
                }
                return modified;
            }
        };
        toolkit.adapt(sashForm);

        final GridLayout layout = GridLayoutFactory.swtDefaults().margins(
            sashForm.SASH_WIDTH, sashForm.SASH_WIDTH).create();
        rootForm.getBody().setLayout(layout);

        createControls(sashForm);
        updatePartHeaders();

        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createActions();

        /*
         * Hook to post-update events.
         */
        final ISearchResultListener rootFormTitleUpdater = new SearchResultListenerAdapter()
        {
            public void processingResultUpdated(ProcessingResult result)
            {
                updatePartHeaders();
                setSelection(StructuredSelection.EMPTY);
            }
        };
        this.searchResult.addListener(rootFormTitleUpdater);

        /*
         * Create jobs and schedule initial processing after the editor is shown.
         */
        createJobs();

        getSite().getPage().addPartListener(new PartListenerAdapter()
        {
            public void partClosed(IWorkbenchPart part)
            {
                getSite().getPage().removePartListener(this);
            }

            public void partOpened(IWorkbenchPart part)
            {
                if (part == SearchEditor.this)
                {
                    reprocess();
                }
            }
        });
    }

    /**
     * Update part name and root form's title
     */
    private void updatePartHeaders()
    {
        final SearchResult searchResult = getSearchResult();
        final SearchInput input = getSearchResult().getInput();

        final String full = getFullInputTitle(searchResult);
        final String abbreviated = getAbbreviatedInputTitle(searchResult);

        setPartName(abbreviated);
        setTitleToolTip(full);

        /*
         * Add the number of documents and clusters to the root form's title.
         */
        final ProcessingResult result = searchResult.getProcessingResult();
        if (result != null)
        {
            final int documents = result.getDocuments().size();
            final int clusters = result.getClusters().size();

            rootForm.setText(abbreviated + " (" + pluralize(documents, "document")
                + " from " + componentName(input.getSourceId()) + ", "
                + pluralize(clusters, "cluster") + " from "
                + componentName(input.getAlgorithmId()) + ")");
        }
        else
        {
            rootForm.setText(abbreviated);
        }
    }

    /**
     * Returns the component label or id if not available
     */
    private String componentName(String componentId)
    {
        final ProcessingComponentDescriptor component = WorkbenchCorePlugin.getDefault()
            .getComponent(componentId);

        if (component != null && !StringUtils.isEmpty(component.getLabel()))
        {
            return component.getLabel();
        }

        return componentId;
    }

    /**
     * Pluralize a given number.
     */
    private String pluralize(int value, String title)
    {
        return Integer.toString(value) + " " + title + (value != 1 ? "s" : "");
    }

    /**
     * Abbreviates the input's title (and adds an ellipsis at end if needed).
     */
    private String getAbbreviatedInputTitle(SearchResult searchResult)
    {
        final int MAX_WIDTH = 40;
        return StringUtils.abbreviate(getFullInputTitle(searchResult), MAX_WIDTH);
    }

    /**
     * Attempts to construct an input title from either query attribute or attributes
     * found in processing results.
     */
    private String getFullInputTitle(SearchResult searchResult)
    {
        /*
         * Initially, set to dummy name.
         */
        String title = ObjectUtils.toString(this.searchResult.getInput().getAttribute(
            AttributeNames.QUERY), null);

        /*
         * If we have a processing result...
         */
        if (searchResult.hasProcessingResult())
        {
            final ProcessingResult result = searchResult.getProcessingResult();

            /*
             * Check if there is a query in the output attributes (may be different from
             * the one set on input).
             */

            title = ObjectUtils.toString(
                result.getAttributes().get(AttributeNames.QUERY), null);

            /*
             * Override with custom title, if present.
             */

            title = ObjectUtils.toString(result.getAttributes().get(
                AttributeNames.PROCESSING_RESULT_TITLE), title);
        }

        if (StringUtils.isEmpty(title))
        {
            title = "(empty query)";
        }

        return title;
    }

    /*
     * 
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        if (!(input instanceof SearchInput)) throw new PartInitException(
            "Invalid input: must be an instance of: " + SearchInput.class.getName());

        setSite(site);
        setInput(input);

        /*
         * Set default local grouping if not already restored. We must set it here because
         * it is used to create components later (and before restoreState()).
         */
        if (StringUtils.isEmpty(getPartProperty(GROUPING_LOCAL)))
        {
            final IPreferenceStore preferenceStore = WorkbenchCorePlugin.getDefault()
                .getPreferenceStore();

            setPartProperty(GROUPING_LOCAL, preferenceStore
                .getString(PreferenceConstants.GROUPING_EDITOR_PANEL));
        }

        this.searchResult = new SearchResult((SearchInput) input);
    }

    /*
     * 
     */
    @Override
    public Image getTitleImage()
    {
        return sourceImage;
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        rootForm.setFocus();
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        this.resources.dispose();
        super.dispose();
    }

    /*
     *
     */
    public void saveState(IMemento memento)
    {
        if (memento != null)
        {
            saveSectionsState(memento, sections);
        }
    }

    /**
     * Creates a custom child in a given memento and persists information from a set of
     * {@link SectionReference}s.
     */
    final static void saveSectionsState(IMemento memento,
        EnumMap<SearchEditorSections, SectionReference> sections)
    {
        final IMemento sectionsMemento = memento.createChild(MEMENTO_SECTIONS);
        for (SearchEditorSections section : sections.keySet())
        {
            final SectionReference sr = sections.get(section);

            final IMemento sectionMemento = sectionsMemento.createChild(MEMENTO_SECTION);
            sectionMemento.putString(SECTION_NAME, section.name());
            sectionMemento.putInteger(SECTION_WEIGHT, sr.weight);
            sectionMemento.putString(SECTION_VISIBLE, Boolean.toString(sr.visibility));
        }
    }

    /**
     * Restores partial attributes saved by {@link #saveSectionsState()}
     */
    final static void restoreSectionsState(IMemento memento,
        EnumMap<SearchEditorSections, SectionReference> sections)
    {
        final IMemento sectionsMemento = memento.getChild(MEMENTO_SECTIONS);
        if (sectionsMemento != null)
        {
            for (IMemento sectionMemento : sectionsMemento.getChildren(MEMENTO_SECTION))
            {
                final SearchEditorSections section = SearchEditorSections
                    .valueOf(sectionMemento.getString(SECTION_NAME));

                if (sections.containsKey(section))
                {
                    final SectionReference r = sections.get(section);
                    r.weight = sectionMemento.getInteger(SECTION_WEIGHT);
                    r.visibility = Boolean.valueOf(sectionMemento
                        .getString(SECTION_VISIBLE));
                }
            }
        }
    }

    /*
     * 
     */
    private void restoreState()
    {
        /*
         * Assign default section weights.
         */
        for (SearchEditorSections s : sections.keySet())
        {
            sections.get(s).weight = s.weight;
        }

        /*
         * Restore global sections attributes, if possible.
         */
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        core.restoreSectionsState(sections);

        /*
         * Restore weights from editor's memento, if possible.
         */
        if (state != null)
        {
            restoreSectionsState(state, sections);
        }

        /*
         * Update weights and visibility.
         */
        final int [] weights = sashForm.getWeights();
        for (SearchEditorSections s : sections.keySet())
        {
            final SectionReference sr = sections.get(s);
            weights[sr.sashIndex] = sr.weight;
            setSectionVisibility(s, sr.visibility);
        }
        sashForm.setWeights(weights);
    }

    /*
     * 
     */
    public void restoreState(IMemento memento)
    {
        state = memento;
    }

    /*
     * 
     */
    public void doSave(IProgressMonitor monitor)
    {
        if (saveOptions == null)
        {
            doSaveAs();
            return;
        }

        doSave(saveOptions);
    }

    /**
     * Show a dialog prompting for file name and options and save the result to an XML
     * file.
     */
    public void doSaveAs()
    {
        if (isDirty() || this.searchJob.getState() == Job.RUNNING)
        {
            final MessageDialog dialog = new MessageDialog(getEditorSite().getShell(),
                "Modified parameters", null, "Search parameters"
                    + " have been changed. Save stale results?", MessageDialog.WARNING,
                new String []
                {
                    IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL
                }, 0);

            if (dialog.open() == MessageDialog.CANCEL)
            {
                return;
            }
        }

        SaveOptions newOptions = saveOptions;
        if (newOptions == null)
        {
            newOptions = new SaveOptions();
            newOptions.fileName = SaveOptions
                .sanitizeFileName(getFullInputTitle(getSearchResult()))
                + ".xml";
        }

        final Shell shell = this.getEditorSite().getShell();
        if (new SearchEditorSaveAsDialog(shell, newOptions).open() == Window.OK)
        {
            this.saveOptions = newOptions;
            doSave(saveOptions);
        }
    }

    /**
     * 
     */
    private void doSave(SaveOptions options)
    {
        final ProcessingResult result = getSearchResult().getProcessingResult();
        if (result == null)
        {
            Utils.showError(new Status(Status.ERROR, WorkbenchCorePlugin.PLUGIN_ID,
                "No search result yet."));
            return;
        }

        final IAction saveAction = new SaveAsXMLActionDelegate(result, options);
        final Job job = new Job("Saving search result...")
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                saveAction.run();
                return Status.OK_STATUS;
            }
        };
        job.setPriority(Job.SHORT);
        job.schedule();
    }

    /*
     * 
     */
    public boolean isSaveAsAllowed()
    {
        return true;
    }

    /*
     * Don't require save-on-close.
     */
    @Override
    public boolean isSaveOnCloseNeeded()
    {
        return false;
    }

    /*
     * 
     */
    @Override
    public boolean isDirty()
    {
        return dirty;
    }

    /**
     * Mark the editor status as dirty (input parameters changed, for example).
     */
    private void setDirty(boolean value)
    {
        this.dirty = value;
        firePropertyChange(PROP_DIRTY);
    }

    /**
     * {@link SearchEditor} adaptations.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class adapter)
    {
        return super.getAdapter(adapter);
    }

    /**
     * Returns a map of this editor's panels ({@link SectionReference}s). This map and its
     * objects are considered <b>read-only</b>.
     * 
     * @see #setSectionVisibility(SearchEditorSections, boolean)
     */
    EnumMap<SearchEditorSections, SectionReference> getSections()
    {
        return sections;
    }

    /**
     * Shows or hides a given panel.
     */
    public void setSectionVisibility(SearchEditorSections section, boolean visible)
    {
        sections.get(section).visibility = visible;
        sections.get(section).section.setVisible(visible);
        sashForm.layout();
    }

    /**
     * Schedule a re-processing job to update {@link #searchResult}.
     */
    public void reprocess()
    {
        /*
         * There is a race condition between the search job and the dirty flag. The editor
         * becomes 'clean' when the search job is initiated, so that further changes of
         * parameters will simply re-schedule another job after the one started before
         * ends. This may lead to certain inconsistencies between the view and the
         * attributes (temporal), but is much simpler than trying to pool/ cache/ stack
         * dirty tokens and manage them in synchronization with running jobs.
         */
        setDirty(false);
        searchJob.schedule();
    }

    /**
     * 
     */
    public SearchResult getSearchResult()
    {
        return searchResult;
    }

    /**
     * Add actions to the root form's toolbar.
     */
    private void createActions()
    {
        final IToolBarManager toolbar = rootForm.getToolBarManager();

        // Auto update action.
        final IWorkbenchWindow window = getSite().getWorkbenchWindow();
        toolbar.add(WorkbenchActionFactory.AUTO_UPDATE_ACTION.create(window));

        // Attribute grouping.
        final String globalPreferenceKey = PreferenceConstants.GROUPING_EDITOR_PANEL;

        toolbar.add(new GroupingMethodAction(GROUPING_LOCAL, this));

        // Update global preferences when local change.
        addPartPropertyListener(new PropertyChangeListenerAdapter(GROUPING_LOCAL)
        {
            protected void propertyChangeFiltered(PropertyChangeEvent event)
            {
                final IPreferenceStore prefStore = WorkbenchCorePlugin.getDefault()
                    .getPreferenceStore();

                final String currentValue = getPartProperty(GROUPING_LOCAL);
                prefStore.setValue(globalPreferenceKey, currentValue);

                updateGroupingState(GroupingMethod.valueOf(currentValue));
                Utils.adaptToFormUI(toolkit, attributesPanel);

                if (!sections.get(SearchEditorSections.ATTRIBUTES).visibility)
                {
                    setSectionVisibility(SearchEditorSections.ATTRIBUTES, true);
                }
            }
        });

        // Choose visible panels.
        final IAction selectSectionsAction = new SearchEditorPanelsAction(
            "Choose visible panels", this);
        toolbar.add(selectSectionsAction);

        toolbar.update(true);
    }

    /**
     * Update grouping state of the {@link #attributesPanel} and reset its editor values.
     */
    private void updateGroupingState(GroupingMethod grouping)
    {
        attributesPanel.setGrouping(grouping);
        attributesPanel.setAttributes(getSearchResult().getInput().getAttributeValueSet()
            .getAttributeValues());
    }

    /**
     * Create internal panels and hook up listener infrastructure.
     */
    private void createControls(SashForm parent)
    {
        /*
         * Create and add sections in order of their declaration in the enum type.
         */
        this.sections = new EnumMap<SearchEditorSections, SectionReference>(
            SearchEditorSections.class);

        int index = 0;
        for (final SearchEditorSections s : EnumSet.allOf(SearchEditorSections.class))
        {
            final Section section;
            switch (s)
            {
                case CLUSTERS:
                    section = createClustersPart(parent, getSite());
                    break;

                case DOCUMENTS:
                    section = createDocumentsPart(parent, getSite());
                    break;

                case ATTRIBUTES:
                    section = createAttributesPart(parent, getSite());
                    break;

                default:
                    throw new RuntimeException("Unhandled section: " + s);
            }

            final SectionReference sr = new SectionReference(section, index, true, 0);
            sections.put(s, sr);

            index++;
        }

        /*
         * Set up selection event forwarding. Install the editor as selection provider for
         * the part.
         */
        final ClusterTree tree = (ClusterTree) getSections().get(
            SearchEditorSections.CLUSTERS).section.getClient();

        this.selectionProvider = new SearchEditorSelectionProxy(this, tree);
        this.getSite().setSelectionProvider(this);

        /*
         * Set up an event callback making editor dirty when attributes change.
         */
        this.getSearchResult().getInput().addAttributeListener(
            new AttributeListenerAdapter()
            {
                public void valueChanged(AttributeEvent event)
                {
                    setDirty(true);
                }
            });

        /*
         * Set up an event callback to spawn auto-update jobs on changes to attributes.
         */
        resources.registerAttributeChangeListener(this.getSearchResult().getInput(),
            autoUpdateListener);

        /*
         * Set up an event callback to restart processing after auto-update is enabled and
         * the editor is dirty.
         */
        resources.registerPropertyChangeListener(WorkbenchCorePlugin.getDefault()
            .getPreferenceStore(), autoUpdateListener2);

        /*
         * Install a synchronization agent between the current selection in the editor and
         * the document list panel.
         */
        final DocumentList documentList = (DocumentList) getSections().get(
            SearchEditorSections.DOCUMENTS).section.getClient();
        documentListSelectionSync = new DocumentListSelectionSync(documentList, this);
        resources.registerPostSelectionChangedListener(this, documentListSelectionSync);

        /*
         * Restore state information.
         */
        restoreState();
    }

    /*
     * 
     */
    private Section createSection(Composite parent)
    {
        return toolkit.createSection(parent, ExpandableComposite.EXPANDED
            | ExpandableComposite.TITLE_BAR);
    }

    /**
     * Create internal panel with the list of clusters (if present).
     */
    private Section createClustersPart(Composite parent, IWorkbenchSite site)
    {
        final SearchEditorSections section = SearchEditorSections.CLUSTERS;
        final Section sec = createSection(parent);
        sec.setText(section.name);

        final ClusterTree clustersTree = new ClusterTree(sec, SWT.NONE);
        resources.add(clustersTree);

        /*
         * Hook the clusters tree to search result's events.
         */
        this.searchResult.addListener(new SearchResultListenerAdapter()
        {
            public void processingResultUpdated(ProcessingResult result)
            {
                final List<Cluster> clusters = result.getClusters();
                if (clusters != null && clusters.size() > 0)
                {
                    clustersTree.show(clusters);
                }
                else
                {
                    clustersTree.show(Collections.<Cluster> emptyList());
                }
            }
        });

        sec.setClient(clustersTree);
        return sec;
    }

    /**
     * Create internal panel with the document list.
     */
    private Section createDocumentsPart(Composite parent, IWorkbenchSite site)
    {
        final SearchEditorSections section = SearchEditorSections.DOCUMENTS;
        final Section sec = createSection(parent);
        sec.setText(section.name);

        final DocumentList documentList = new DocumentList(sec, SWT.NONE);
        resources.add(documentList);

        /*
         * Hook the document list to search result's events.
         */
        this.searchResult.addListener(new SearchResultListenerAdapter()
        {
            public void processingResultUpdated(ProcessingResult result)
            {
                documentList.show(result);
            }
        });

        sec.setClient(documentList);
        return sec;
    }

    /**
     * Create internal panel with the set of algorithm component's attributes.
     */
    private Section createAttributesPart(Composite parent, IWorkbenchSite site)
    {
        final SearchEditorSections section = SearchEditorSections.ATTRIBUTES;
        final Section sec = createSection(parent);
        sec.setText(section.name);

        final BindableDescriptor descriptor = getAlgorithmDescriptor();

        final CScrolledComposite scroller = new CScrolledComposite(sec, SWT.H_SCROLL
            | SWT.V_SCROLL);
        resources.add(scroller);

        final Composite spacer = GUIFactory.createSpacer(scroller);
        resources.add(spacer);

        final String groupingValue = getPartProperty(GROUPING_LOCAL);
        final GroupingMethod grouping = GroupingMethod.valueOf(groupingValue);
        final Map<String, Object> defaultValues = getSearchResult().getInput()
            .getAttributeValueSet().getAttributeValues();

        attributesPanel = new AttributeGroups(spacer, descriptor, grouping, null,
            defaultValues);
        attributesPanel.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
            .create());
        resources.add(attributesPanel);

        toolkit.paintBordersFor(scroller);
        toolkit.adapt(scroller);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(false);
        scroller.setContent(spacer);

        /*
         * Link attribute value changes: attribute panel -> search result
         */
        final IAttributeListener panelToEditorSync = new AttributeListenerAdapter()
        {
            public void valueChanged(AttributeEvent event)
            {
                getSearchResult().getInput().setAttribute(event.key, event.value);
            }
        };
        attributesPanel.addAttributeListener(panelToEditorSync);

        /*
         * Link attribute value changes: search result -> attribute panel
         */
        final IAttributeListener editorToPanelSync = new AttributeListenerAdapter()
        {
            public void valueChanged(AttributeEvent event)
            {
                /*
                 * temporarily unsubscribe from events from the attributes list to avoid
                 * event looping.
                 */
                attributesPanel.removeAttributeListener(panelToEditorSync);
                attributesPanel.setAttribute(event.key, event.value);
                attributesPanel.addAttributeListener(panelToEditorSync);
            }
        };
        getSearchResult().getInput().addAttributeListener(editorToPanelSync);

        /*
         * Perform GUI adaptations.
         */
        Utils.adaptToFormUI(toolkit, attributesPanel);
        Utils.adaptToFormUI(toolkit, scroller);

        sec.setClient(scroller);
        return sec;
    }

    /**
     * Get hold of the algorithm instance, extract its attribute descriptors.
     */
    @SuppressWarnings("unchecked")
    BindableDescriptor getAlgorithmDescriptor()
    {
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        final String algorithmID = getSearchResult().getInput().getAlgorithmId();

        return core.getComponentDescriptor(algorithmID).only(Input.class,
            Processing.class).not(Internal.class);
    }

    /**
     * Creates reusable jobs used in the editor.
     */
    private void createJobs()
    {
        /*
         * Create search job.
         */

        final String title = getAbbreviatedInputTitle(searchResult);
        this.searchJob = new SearchJob("Searching for '" + title + "'...", searchResult);

        // Try to push search jobs into the background, if possible.
        this.searchJob.setPriority(Job.DECORATE);

        /*
         * Add a job listener to update root form's busy state.
         */
        searchJob.addJobChangeListener(new JobChangeAdapter()
        {
            @Override
            public void aboutToRun(IJobChangeEvent event)
            {
                setBusy(true);
            }

            @Override
            public void done(IJobChangeEvent event)
            {
                setBusy(false);
            }

            private void setBusy(final boolean busy)
            {
                Utils.asyncExec(new Runnable()
                {
                    public void run()
                    {
                        if (!rootForm.isDisposed())
                        {
                            rootForm.setBusy(busy);
                        }
                    }
                });
            }
        });
    }

    /*
     * 
     */
    @SuppressWarnings("unused")
    private IToolBarManager createToolbarManager(Section section)
    {
        ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
        ToolBar toolbar = toolBarManager.createControl(section);

        final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
        toolbar.setCursor(handCursor);

        // Cursor needs to be explicitly disposed
        toolbar.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                if ((handCursor != null) && (handCursor.isDisposed() == false))
                {
                    handCursor.dispose();
                }
            }
        });

        section.setTextClient(toolbar);
        return toolBarManager;
    }

    /*
     * Selection provider implementation forwards to the internal tree panel.
     */

    public void addPostSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.selectionProvider.addPostSelectionChangedListener(listener);
    }

    public void removePostSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.selectionProvider.removePostSelectionChangedListener(listener);
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.selectionProvider.addSelectionChangedListener(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.selectionProvider.removeSelectionChangedListener(listener);
    }

    public ISelection getSelection()
    {
        return this.selectionProvider.getSelection();
    }

    public void setSelection(ISelection selection)
    {
        this.selectionProvider.setSelection(selection);
    }
}
