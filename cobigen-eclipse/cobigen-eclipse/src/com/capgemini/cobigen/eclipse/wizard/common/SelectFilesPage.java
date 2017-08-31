package com.capgemini.cobigen.eclipse.wizard.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.to.IncrementTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.eclipse.Activator;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.eclipse.generator.entity.ComparableIncrement;
import com.capgemini.cobigen.eclipse.wizard.common.control.ButtonListener;
import com.capgemini.cobigen.eclipse.wizard.common.control.CheckStateListener;
import com.capgemini.cobigen.eclipse.wizard.common.model.SelectFileContentProvider;
import com.capgemini.cobigen.eclipse.wizard.common.model.SelectFileLabelProvider;
import com.capgemini.cobigen.eclipse.wizard.common.model.SelectIncrementContentProvider;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.OffWorkspaceResourceTreeNode;
import com.capgemini.cobigen.eclipse.wizard.common.widget.CustomizedCheckboxTreeViewer;
import com.capgemini.cobigen.eclipse.wizard.common.widget.SimulatedCheckboxTreeViewer;
import com.capgemini.cobigen.impl.config.entity.Template;
import com.google.common.collect.Lists;

/**
 * The {@link SelectFilesPage} displays a resource tree of all resources that may be change by the generation
 * process
 */
public class SelectFilesPage extends WizardPage {

    /** Assigning logger to SelectFilesPage */
    private final static Logger LOG = LoggerFactory.getLogger(SelectFilesPage.class);

    /** {@link TreeViewer} of the simulated generation targets */
    private CheckboxTreeViewer resourcesTree;

    /** List of increments */
    private CheckboxTreeViewer incrementSelector;

    /** Container holding the right site of the UI, containing a label and the resources tree */
    private Composite containerRight;

    /** Checkbox for "Remember selection" functionality */
    private Button rememberSelection;

    /** Current used {@link CobiGenWrapper} instance */
    private CobiGenWrapper cobigenWrapper;

    /** Defines whether the {@link CobiGenWrapper} is in batch mode. */
    private boolean batch;

    /** Value of {@link #setDisplayedfilePathToTemplateMapping(Map)} */
    private Map<String, Set<TemplateTo>> displayedfilePathToTemplateMapping;

    /** Possible check states */
    public static enum CHECK_STATE {
        /** checked */
        CHECKED,
        /** unchecked */
        UNCHECKED
    }

    /**
     * Creates a new {@link SelectFilesPage} which displays a resource tree of all resources that may be
     * change by the generation process
     *
     * @param cobigenWrapper
     *            the {@link CobiGenWrapper} instance
     * @param batch
     *            states whether the generation will run in batch mode
     */
    public SelectFilesPage(CobiGenWrapper cobigenWrapper, boolean batch) {

        super("GenerateHandler");
        setTitle("Select the Resources, which should be generated.");
        this.cobigenWrapper = cobigenWrapper;
        this.batch = batch;
    }

    @Override
    public void createControl(Composite parent) {

        Composite container = new Composite(parent, SWT.FILL);
        container.setLayout(new GridLayout());

        SashForm sash = new SashForm(container, SWT.HORIZONTAL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        sash.setLayoutData(gd);

        Composite containerLeft = new Composite(sash, SWT.FILL);
        containerLeft.setLayout(new GridLayout(1, false));

        Label label = new Label(containerLeft, SWT.NONE);
        label.setText("Filter (increments):");

        incrementSelector = new CustomizedCheckboxTreeViewer(containerLeft);
        incrementSelector.setContentProvider(new SelectIncrementContentProvider());
        incrementSelector.setInput(cobigenWrapper.getAllIncrements());
        gd = new GridData(GridData.FILL_BOTH);
        gd.grabExcessVerticalSpace = true;
        incrementSelector.getTree().setLayoutData(gd);
        incrementSelector.expandAll();

        containerRight = new Composite(sash, SWT.FILL);
        containerRight.setLayout(new GridLayout(1, false));

        boolean initiallyCustomizable = false;
        buildResourceTreeViewer(initiallyCustomizable);

        CheckStateListener checkListener = new CheckStateListener(cobigenWrapper, this, batch);
        incrementSelector.addCheckStateListener(checkListener);

        sash.setWeights(new int[] { 1, 3 });

        rememberSelection = new Button(container, SWT.CHECK);
        rememberSelection.setText("Remember my selection");
        gd = new GridData();
        gd.horizontalAlignment = SWT.BEGINNING;
        rememberSelection.setLayoutData(gd);
        rememberSelection.addSelectionListener(checkListener);

        Button but = new Button(container, SWT.PUSH);
        but.setText("Customize");
        gd = new GridData();
        gd.horizontalAlignment = SWT.END;
        but.setLayoutData(gd);
        but.addListener(SWT.Selection, new ButtonListener(initiallyCustomizable, this));

        setControl(container);
        loadSelection();
    }

    /**
     * Returns the resources tree
     * @return current {@link CheckboxTreeViewer} instance
     */
    public CheckboxTreeViewer getResourcesTree() {

        return resourcesTree;
    }

    /**
     * Returns the package selector
     * @return current {@link CheckboxTableViewer} instance
     */
    public CheckboxTreeViewer getPackageSelector() {

        return incrementSelector;
    }

    /**
     * Disposes all children of the container control which holds the resource tree
     */
    private void disposeContainerRightChildren() {

        for (Control c : containerRight.getChildren()) {
            c.dispose();
        }
    }

    /**
     * Builds the {@link TreeViewer} providing the tree of resources to be generated
     * @param customizable
     *            states whether the checkboxes of the tree should be displayed or not
     */
    public void buildResourceTreeViewer(boolean customizable) {

        IContentProvider cp;
        IBaseLabelProvider lp;
        Object[] checkedElements;
        if (resourcesTree != null) {
            cp = resourcesTree.getContentProvider();
            lp = resourcesTree.getLabelProvider();
            checkedElements = resourcesTree.getCheckedElements();
        } else {
            cp = new SelectFileContentProvider();
            lp = new SelectFileLabelProvider(cobigenWrapper, batch);
            incrementSelector.addCheckStateListener((SelectFileLabelProvider) lp);
            checkedElements = new Object[0];
        }

        disposeContainerRightChildren();

        Label label = new Label(containerRight, SWT.NONE);
        label.setText("Resources to be generated (selected):");

        if (customizable) {
            resourcesTree = new CustomizedCheckboxTreeViewer(containerRight);
        } else {
            resourcesTree = new SimulatedCheckboxTreeViewer(containerRight);
        }

        resourcesTree.setContentProvider(cp);
        resourcesTree.setLabelProvider(lp);
        resourcesTree.setInput(ResourcesPlugin.getWorkspace().getRoot().getProjects());
        resourcesTree.expandToLevel(AbstractTreeViewer.ALL_LEVELS);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        resourcesTree.getTree().setLayoutData(gd);

        CheckStateListener listener = new CheckStateListener(cobigenWrapper, this, batch);
        resourcesTree.addCheckStateListener(listener);
        resourcesTree.setCheckedElements(checkedElements);

        containerRight.layout();
    }

    @Override
    public boolean canFlipToNextPage() {
        return resourcesTree.getCheckedElements().length > 0;
    }

    /**
     * @return the set of all paths to files which should be generated
     */
    private Set<String> getFilePathsToBeGenerated() {

        Set<String> filesToBeGenerated = new HashSet<>();
        Object[] selectedElements = resourcesTree.getCheckedElements();
        for (Object e : selectedElements) {
            if (e instanceof ICompilationUnit) {
                filesToBeGenerated.add(((ICompilationUnit) e).getPath().toString());
            } else if (e instanceof IFile) {
                filesToBeGenerated.add(((IFile) e).getFullPath().toString());
            } else if (e instanceof OffWorkspaceResourceTreeNode && !((OffWorkspaceResourceTreeNode) e).hasChildren()) {
                filesToBeGenerated.add(((OffWorkspaceResourceTreeNode) e).getAbsolutePathStr());
            }
        }
        return filesToBeGenerated;
    }

    /**
     * @return a {@link Set} containing the {@link Template}s, that are included in the selected
     *         {@link ComparableIncrement}s
     */
    public List<TemplateTo> getTemplatesToBeGenerated() {

        List<TemplateTo> templatesToBeGenerated = Lists.newArrayList();
        for (String pathToBeGenerated : getFilePathsToBeGenerated()) {
            templatesToBeGenerated.addAll(displayedfilePathToTemplateMapping.get(pathToBeGenerated));
        }

        return templatesToBeGenerated;
    }

    /**
     * @return all selected {@link IncrementTo}s of the {@link TreeViewer} for increment selection
     */
    public List<IncrementTo> getSelectedIncrements() {
        List<IncrementTo> increments = Lists.newArrayList();
        for (Object o : Arrays.asList(incrementSelector.getCheckedElements())) {
            if (o instanceof IncrementTo) {
                increments.add((IncrementTo) o);
            }
        }
        return increments;
    }

    /**
     * @return all selected {@link IResource}s and {@link IJavaElement}s of the {@link TreeViewer} for file
     *         selection
     */
    public List<Object> getSelectedResources() {
        return Arrays.asList(resourcesTree.getCheckedElements());
    }

    /**
     * Loads the last package selection
     */
    private void loadSelection() {

        IEclipsePreferences preferences = ConfigurationScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        Preferences selection = preferences.node("selection");

        TreeItem[] items = incrementSelector.getTree().getItems();
        for (int i = 0; i < items.length; i++) {
            ComparableIncrement element = (ComparableIncrement) items[i].getData();
            if (element.getTriggerId() != null) {
                String value =
                    selection.node(element.getTriggerId()).get(element.getId(), CHECK_STATE.UNCHECKED.name());
                if (value.equals(CHECK_STATE.CHECKED.name())) {
                    incrementSelector.setChecked(element, true);
                }
            } else if (element.getId().equals(CobiGenWrapper.ALL_INCREMENT_ID)) {
                String value = selection.node(CobiGenWrapper.ALL_INCREMENT_NAME).get(element.getId(),
                    CHECK_STATE.UNCHECKED.name());
                if (value.equals(CHECK_STATE.CHECKED.name())) {
                    incrementSelector.setChecked(element, true);
                }
            }
        }
    }

    /**
     * Saves the current package selection
     */
    public void saveSelection() {

        if (!rememberSelection.getSelection()) {
            return;
        }
        IEclipsePreferences preferences = ConfigurationScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        Preferences selection = preferences.node("selection");

        TreeItem[] items = incrementSelector.getTree().getItems();
        for (int i = 0; i < items.length; i++) {
            ComparableIncrement element = (ComparableIncrement) items[i].getData();
            if (element.getTriggerId() != null) {
                if (items[i].getChecked()) {
                    selection.node(element.getTriggerId()).put(element.getId(), CHECK_STATE.CHECKED.name());
                } else {
                    selection.node(element.getTriggerId()).put(element.getId(), CHECK_STATE.UNCHECKED.name());
                }
            } else if (element.getId().equals(CobiGenWrapper.ALL_INCREMENT_ID)) {
                if (items[i].getChecked()) {
                    selection.node(CobiGenWrapper.ALL_INCREMENT_NAME).put(element.getId(), CHECK_STATE.CHECKED.name());
                } else {
                    selection.node(CobiGenWrapper.ALL_INCREMENT_NAME).put(element.getId(),
                        CHECK_STATE.UNCHECKED.name());
                }
            }
        }

        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            LOG.error("Error while flushing last selection into preferences.", e);
        }
    }

    /**
     * Checks whether the "rememberSelection" box is checked
     * @return <code>true</code> if "rememberSelection" is enabled<br>
     *         <code>false</code> otherwise
     */
    public boolean isSetRememberSelection() {
        if (rememberSelection != null) {
            return rememberSelection.getSelection();
        }
        return false;
    }

    /**
     * Sets the cache for the mapping between currently displayed file paths and its {@link TemplateTo}
     * representation
     * @param cachedMapping
     *            cached mapping
     */
    public void setDisplayedfilePathToTemplateMapping(Map<String, Set<TemplateTo>> cachedMapping) {
        displayedfilePathToTemplateMapping = cachedMapping;
    }

}
