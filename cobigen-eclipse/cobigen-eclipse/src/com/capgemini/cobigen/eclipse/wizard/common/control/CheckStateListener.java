package com.capgemini.cobigen.eclipse.wizard.common.control;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
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
import com.capgemini.cobigen.eclipse.common.tools.PathUtil;
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

    /** Lastly checked generation packages */
    private Set<Object> lastCheckedIncrements = new HashSet<>();

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
                .setCheckedResources(resourcesTree.getCheckedElements());
            refreshNodes(event);
        } else if (event.getSource().equals(incrementSelector)) {
            performCheckLogic(event, incrementSelector);
            Set<Object> checkedElements = new HashSet<>(Arrays.asList(incrementSelector.getCheckedElements()));
            performCheckLogicForALLPackage(incrementSelector, checkedElements);

            Set<String> paths = getSelectedGenerationPaths();
            ((SelectFileContentProvider) resourcesTree.getContentProvider()).filter(paths);
            resourcesTree.setCheckedElements(new Object[0]);
            resourcesTree.refresh();
            resourcesTree.expandAll();
            if (!batch) {
                setSimulatedResourcesChecked();
                setMergeableResourcesChecked();
            } else {
                setAllResourcesChecked();
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
     * Returns all generation paths of the currently selected generation packages
     * @return all generation paths of the currently selected generation packages
     */
    private Set<String> getSelectedGenerationPaths() {

        Set<String> paths = new HashSet<>();
        for (Object o : lastCheckedIncrements) {
            if (o instanceof ComparableIncrement) {
                ComparableIncrement pkg = ((ComparableIncrement) o);
                paths.addAll(getDestinationPaths(pkg));
                if (pkg.getId().equals("all")) {
                    break;
                }
            }
        }
        return paths;
    }

    /**
     * Returns the set of all workspace relative destination paths for the templates of the given
     * {@link ComparableIncrement} but only for the {@link CobiGenWrapper#getCurrentRepresentingInput()}.
     * @param pkg
     *            {@link ComparableIncrement} the template destination paths should be retrieved for
     * @return the {@link HashSet} of destination paths
     */
    private Set<String> getDestinationPaths(ComparableIncrement pkg) {

        Set<String> paths = new HashSet<>();
        for (TemplateTo template : pkg.getTemplates()) {
            Path targetAbsolutePath = cobigenWrapper.resolveTemplateDestinationPath(template);
            paths.add(PathUtil.getProjectDependentFile(cobigenWrapper.getGenerationTargetProject(), targetAbsolutePath)
                .getFullPath().toString());
        }
        return paths;
    }

    /**
     * Performs an intelligent check logic, e.g. check/uncheck all packages when selecting "all"
     * @param packageSelector
     *            the {@link CheckboxTableViewer} listing all generation packages
     * @param checkedElements
     *            the {@link Set} of all elements checked by the user
     */
    private void performCheckLogicForALLPackage(CheckboxTreeViewer packageSelector, Set<Object> checkedElements) {

        Set<Object> addedDiff = new HashSet<>(checkedElements);
        Set<Object> removedDiff = new HashSet<>(lastCheckedIncrements);
        addedDiff.removeAll(lastCheckedIncrements);
        removedDiff.removeAll(checkedElements);
        ComparableIncrement all = new ComparableIncrement("all", "All", null, Lists.<TemplateTo> newLinkedList(),
            Lists.<IncrementTo> newLinkedList());
        if (!lastCheckedIncrements.contains(all) && addedDiff.contains(all)) {
            selectAllPackages(packageSelector);
        } else if (lastCheckedIncrements.contains(all) && removedDiff.contains(all)) {
            setAllChecked(packageSelector, false);
            lastCheckedIncrements.clear();
        } else if (!removedDiff.isEmpty()) {
            lastCheckedIncrements = checkedElements;
            lastCheckedIncrements.remove(all);
            packageSelector.setChecked(all, false);
        } else {
            lastCheckedIncrements = checkedElements;
        }
    }

    /**
     * Sets all checkboxes of the package selector to be checked
     * @param packageSelector
     *            {@link CheckboxTreeViewer}
     * @param checked
     *            <code>true</code> for all check boxes being checked, <code>false</code> otherwise
     */
    private void setAllChecked(CheckboxTreeViewer packageSelector, boolean checked) {

        TreePath[] rootPaths =
            ((SelectIncrementContentProvider) packageSelector.getContentProvider()).getAllRootPaths();
        for (TreePath path : rootPaths) {
            packageSelector.setSubtreeChecked(path, checked);
        }
    }

    /**
     * Selects all packages in the package selector
     * @param incrementSelector
     *            package selector
     */
    private void selectAllPackages(CheckboxTreeViewer incrementSelector) {

        setAllChecked(incrementSelector, true);
        lastCheckedIncrements = new HashSet<>(Arrays.asList((Object[]) incrementSelector.getInput()));
    }

    /**
     * Sets all simulated resources to be initially checked
     */
    private void setSimulatedResourcesChecked() {

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
    private void setMergeableResourcesChecked() {

        CheckboxTreeViewer resourcesTree = page.getResourcesTree();
        for (IFile file : cobigenWrapper.getMergeableFiles()) {
            Object mergableTreeObject = ((SelectFileContentProvider) resourcesTree.getContentProvider())
                .getProvidedObject(file.getFullPath().toString());
            if (mergableTreeObject != null) {
                resourcesTree.setChecked(mergableTreeObject, true);
            }
        }
    }

    /**
     * Sets all resources to be checked or unchecked
     */
    private void setAllResourcesChecked() {

        CheckboxTreeViewer resourcesTree = page.getResourcesTree();
        for (IFile f : cobigenWrapper.getAllTargetFilesOfFirstInput()) {
            Object treeObject = ((SelectFileContentProvider) resourcesTree.getContentProvider())
                .getProvidedObject(f.getFullPath().toString());
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
