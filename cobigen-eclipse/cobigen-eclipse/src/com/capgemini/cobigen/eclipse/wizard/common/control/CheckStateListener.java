package com.capgemini.cobigen.eclipse.wizard.common.control;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.capgemini.cobigen.api.to.IncrementTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.eclipse.generator.entity.ComparableIncrement;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.wizard.common.SelectFilesPage;
import com.capgemini.cobigen.eclipse.wizard.common.model.SelectFileContentProvider;
import com.capgemini.cobigen.eclipse.wizard.common.model.SelectFileLabelProvider;
import com.capgemini.cobigen.eclipse.wizard.common.model.SelectIncrementContentProvider;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IJavaElementStub;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IResourceStub;
import com.google.common.collect.Lists;

/**
 * This {@link CheckStateListener} provides the check / uncheck of the increments list and the generation
 * resource tree and synchronizes both in order to get a consistent view
 */
public class CheckStateListener implements ICheckStateListener, SelectionListener {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(CheckStateListener.class);

    /** Currently used {@link CobiGenWrapper} instance */
    private CobiGenWrapper cobigenWrapper;

    /** The {@link SelectFilesPage} of the wizard providing the different viewer */
    private SelectFilesPage page;

    /** Lastly selected increments */
    private Set<IncrementTo> selectedIncrements = new HashSet<>();

    /** Defines whether the {@link CobiGenWrapper} is in batch mode. */
    private boolean batch;

    /**
     * Creates a new {@link CheckStateListener} instance
     *
     * @param cobigenWrapper
     *            currently used {@link JavaGeneratorWrapper} instance
     * @param page
     *            current {@link SelectFilesPage} reference
     * @param batch
     *            states whether the check state listener should run in batch mode
     */
    public CheckStateListener(CobiGenWrapper cobigenWrapper, SelectFilesPage page, boolean batch) {
        this.cobigenWrapper = cobigenWrapper;
        this.page = page;
        this.batch = batch;
    }

    @Override
    public void checkStateChanged(CheckStateChangedEvent event) {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
        LOG.info("Increment selection changed. Calculating generation preview file tree...");

        CheckboxTreeViewer resourcesTree = page.getResourcesTree();
        CheckboxTreeViewer incrementSelector = page.getPackageSelector();
        if (event.getSource().equals(resourcesTree)) {
            resourcesTree.setSubtreeChecked(event.getElement(), event.getChecked());
            ((SelectFileLabelProvider) resourcesTree.getLabelProvider())
                .setSelectedResources(resourcesTree.getCheckedElements());
            refreshNodes(event);
        } else if (event.getSource().equals(incrementSelector)) {
            performCheckLogic(event, incrementSelector);
            Set<Object> checkedElements = new HashSet<>(Arrays.asList(incrementSelector.getCheckedElements()));
            performCheckLogicForALLPackage(incrementSelector, checkedElements);

            Map<String, Set<TemplateTo>> paths = cobigenWrapper.getTemplateDestinationPaths(selectedIncrements);
            ((SelectFileContentProvider) resourcesTree.getContentProvider()).filter(paths.keySet());
            page.setDisplayedfilePathToTemplateMapping(paths);

            resourcesTree.setCheckedElements(new Object[0]);
            resourcesTree.refresh();
            resourcesTree.expandAll();
            if (!batch) {
                selectSimulatedResources();
                selectMergeableResources();
            } else {
                selectAllResources(paths.keySet());
            }
        }

        checkPageComplete();

        LOG.info("Calculating of changed preview file tree finished.");
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }

    /**
     * Checks whether there are resources selected for generation and sets the {@link WizardPage} to be
     * completed if there is at least one selected resource
     */
    private void checkPageComplete() {

        CheckboxTreeViewer resourcesTree = page.getResourcesTree();
        if (page.isSetRememberSelection() || resourcesTree.getCheckedElements().length > 0) {
            page.setPageComplete(true);
        } else {
            page.setPageComplete(false);
        }
    }

    /**
     * Performs an intelligent check logic such that the same element in different paths will be checked
     * simultaneously, parents will be unselected if at least one child is not selected, and parents will be
     * automatically selected if all children of the parent are selected
     * @param event
     *            triggering {@link CheckStateChangedEvent}
     * @param packageSelector
     *            current {@link CheckboxTreeViewer} for the package selection
     */
    private void performCheckLogic(CheckStateChangedEvent event, CheckboxTreeViewer packageSelector) {

        SelectIncrementContentProvider cp = (SelectIncrementContentProvider) packageSelector.getContentProvider();
        TreePath[] paths = cp.getAllPaths(event.getElement());
        for (TreePath path : paths) {
            packageSelector.setSubtreeChecked(path, event.getChecked());
        }

        TreePath[] parents = cp.getParents(event.getElement());
        if (event.getChecked()) {
            for (TreePath parent : parents) {
                boolean allChecked = true;
                for (Object child : cp.getChildren(parent)) {
                    if (!packageSelector.getChecked(parent.createChildPath(child))) {
                        allChecked = false;
                        break;
                    }
                }
                if (allChecked) {
                    packageSelector.setChecked(parent, true);
                }
            }
        } else {
            for (TreePath parent : parents) {
                if (parent.getSegmentCount() > 0) {
                    packageSelector.setChecked(parent, false);
                }
            }
        }
    }

    /**
     * Refreshes the nodes affected by the given {@link CheckStateChangedEvent}
     * @param event
     *            {@link CheckStateChangedEvent} of {@link #checkStateChanged(CheckStateChangedEvent)}
     */
    private void refreshNodes(CheckStateChangedEvent event) {

        if (event.getElement() instanceof Object[]) {
            for (Object o : (Object[]) event.getElement()) {
                page.getResourcesTree().refresh(o);
            }
        } else {
            page.getResourcesTree().refresh(event.getElement());
        }
    }

    /**
     * Performs an intelligent check logic, e.g. check/uncheck all packages when selecting "all"
     * @param incrementSelector
     *            the {@link CheckboxTableViewer} listing all generation packages
     * @param selectedElements
     *            the {@link Set} of all elements checked by the user
     */
    private void performCheckLogicForALLPackage(CheckboxTreeViewer incrementSelector, Set<Object> selectedElements) {

        Set<Object> addedDiff = new HashSet<>(selectedElements);
        Set<? extends IncrementTo> removedDiff = new HashSet<>(selectedIncrements);
        addedDiff.removeAll(selectedIncrements);
        removedDiff.removeAll(selectedElements);
        ComparableIncrement all = new ComparableIncrement("all", "All", null, Lists.<TemplateTo> newLinkedList(),
            Lists.<IncrementTo> newLinkedList());
        if (!selectedIncrements.contains(all) && addedDiff.contains(all)) {
            setStateOfAllIncrements(incrementSelector, true);
            setSelectedIncrements(Arrays.asList((Object[]) incrementSelector.getInput()));
        } else if (selectedIncrements.contains(all) && removedDiff.contains(all)) {
            setStateOfAllIncrements(incrementSelector, false);
            selectedIncrements.clear();
        } else if (!removedDiff.isEmpty()) {
            setSelectedIncrements(selectedElements);
            selectedIncrements.remove(all);
            incrementSelector.setChecked(all, false);
        } else {
            setSelectedIncrements(selectedElements);
        }
    }

    /**
     * Casts the selected elements coming from the content provider to a list of {@link IncrementTo}s.
     * @param selectedElements
     *            {@link IncrementTo}s from the increment content provider
     */
    private void setSelectedIncrements(Collection<Object> selectedElements) {
        selectedIncrements.clear();
        for (Object o : selectedElements) {
            if (o instanceof IncrementTo) {
                selectedIncrements.add((IncrementTo) o);
            }
        }
    }

    /**
     * Sets all checkboxes of the package selector to be checked
     * @param incrementSelector
     *            {@link CheckboxTreeViewer}
     * @param checked
     *            <code>true</code> for all check boxes being checked, <code>false</code> otherwise
     */
    private void setStateOfAllIncrements(CheckboxTreeViewer incrementSelector, boolean checked) {

        TreePath[] rootPaths =
            ((SelectIncrementContentProvider) incrementSelector.getContentProvider()).getAllRootPaths();
        for (TreePath path : rootPaths) {
            incrementSelector.setSubtreeChecked(path, checked);
        }
    }

    /**
     * Sets all simulated resources to be initially checked
     */
    private void selectSimulatedResources() {

        CheckboxTreeViewer resourcesTree = page.getResourcesTree();
        LinkedList<Object> worklist = Lists.newLinkedList(Arrays.asList(
            ((SelectFileContentProvider) resourcesTree.getContentProvider()).getElements(resourcesTree.getInput())));

        while (worklist.peek() != null) {
            Object o = worklist.poll();
            if (o instanceof IJavaElementStub || o instanceof IResourceStub) {
                resourcesTree.setChecked(o, true);
            }
            worklist
                .addAll(Arrays.asList(((SelectFileContentProvider) resourcesTree.getContentProvider()).getChildren(o)));
        }
    }

    /**
     * Sets all mergeable files to be checked
     */
    private void selectMergeableResources() {

        CheckboxTreeViewer resourcesTree = page.getResourcesTree();
        for (String path : cobigenWrapper.getMergeableFiles(selectedIncrements)) {
            Object mergableTreeObject =
                ((SelectFileContentProvider) resourcesTree.getContentProvider()).getProvidedObject(path);
            if (mergableTreeObject != null) {
                resourcesTree.setChecked(mergableTreeObject, true);
            }
        }
    }

    /**
     * Sets all resources to be checked or unchecked
     * @param currentlyDisplayedPaths
     *            all currently displayed paths
     */
    private void selectAllResources(Set<String> currentlyDisplayedPaths) {

        CheckboxTreeViewer resourcesTree = page.getResourcesTree();
        for (String path : currentlyDisplayedPaths) {
            Object treeObject =
                ((SelectFileContentProvider) resourcesTree.getContentProvider()).getProvidedObject(path);
            if (treeObject != null) {
                resourcesTree.setChecked(treeObject, true);
            }
        }
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
        checkPageComplete();
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
        checkPageComplete();
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }
}
