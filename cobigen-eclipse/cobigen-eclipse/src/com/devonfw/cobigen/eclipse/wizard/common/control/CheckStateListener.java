package com.devonfw.cobigen.eclipse.wizard.common.control;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.tools.ExceptionHandler;
import com.devonfw.cobigen.eclipse.common.tools.MapUtils;
import com.devonfw.cobigen.eclipse.generator.CobiGenWrapper;
import com.devonfw.cobigen.eclipse.generator.entity.ComparableIncrement;
import com.devonfw.cobigen.eclipse.generator.java.JavaInputGeneratorWrapper;
import com.devonfw.cobigen.eclipse.wizard.common.SelectFilesPage;
import com.devonfw.cobigen.eclipse.wizard.common.model.SelectFileContentProvider;
import com.devonfw.cobigen.eclipse.wizard.common.model.SelectFileLabelProvider;
import com.devonfw.cobigen.eclipse.wizard.common.model.SelectIncrementContentProvider;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.IJavaElementStub;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.IResourceStub;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.OffWorkspaceResourceTreeNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
     *            currently used {@link JavaInputGeneratorWrapper} instance
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

        try {
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
                performCheckLogicForALLIncrement(incrementSelector, checkedElements);

                Map<String, Set<TemplateTo>> paths = cobigenWrapper.getTemplateDestinationPaths(selectedIncrements);
                Set<String> workspaceExternalPaths = Sets.newHashSet();
                for (String path : paths.keySet()) {
                    if (cobigenWrapper.isWorkspaceExternalPath(path)) {
                        workspaceExternalPaths.add(path);
                    }
                }
                List<OffWorkspaceResourceTreeNode> offScopeResourceTree =
                    buildOffScopeResourceTree(workspaceExternalPaths);
                ((SelectFileContentProvider) resourcesTree.getContentProvider()).filter(paths.keySet(),
                    offScopeResourceTree);
                page.setDisplayedfilePathToTemplateMapping(paths);

                resourcesTree.setCheckedElements(new Object[0]);
                resourcesTree.refresh();
                resourcesTree.expandAll();
                if (!batch) {
                    selectNewResources();
                    selectMergeableResources();
                    selectOverridingResources();
                } else {
                    selectAllResources(paths.keySet());
                }
            }

            checkPageComplete();

        } catch (Throwable e) {
            ExceptionHandler.handle(e, null);
        }

        LOG.info("Calculating of changed preview file tree finished.");
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }

    /**
     * Builds the {@link OffWorkspaceResourceTreeNode} for workspace external files to be generated.
     * @param pathsStr
     *            paths as strings to be built as a tree
     * @return the list of root nodes.
     */
    private List<OffWorkspaceResourceTreeNode> buildOffScopeResourceTree(Set<String> pathsStr) {
        Set<Path> paths = Sets.newHashSet();
        for (String p : pathsStr) {
            paths.add(Paths.get(p));
        }

        List<OffWorkspaceResourceTreeNode> rootResources = Lists.newArrayList();

        Map<Path, Set<Path>> prefixToSuffixMap = Maps.newHashMap();
        for (Path path : paths) {
            MapUtils.deepMapAdd(prefixToSuffixMap, path.getRoot(), path.subpath(0, path.getNameCount()));
        }

        for (Path prefix : prefixToSuffixMap.keySet()) {
            OffWorkspaceResourceTreeNode curr = new OffWorkspaceResourceTreeNode(null, prefix);
            buildOffScopeResourceTree(curr, prefixToSuffixMap.get(prefix));
            rootResources.add(curr);
        }
        return rootResources;
    }

    /**
     * Builds the {@link OffWorkspaceResourceTreeNode} for workspace external files. This is the recursive
     * function to process a parent node and all its subsequent paths.
     * @param parent
     *            {@link OffWorkspaceResourceTreeNode} parent node
     * @param childPaths
     *            relative child paths of the parent node
     */
    private void buildOffScopeResourceTree(OffWorkspaceResourceTreeNode parent, Set<Path> childPaths) {

        Path emptyPath = Paths.get("");

        Map<Path, Set<Path>> prefixToSuffixMap = Maps.newHashMap();

        Path lonelyChildPath = emptyPath;
        if (childPaths.size() == 1) {
            lonelyChildPath = childPaths.iterator().next();
        }

        for (int i = 1;; i++) {
            prefixToSuffixMap.clear();
            Path pathPrefix = emptyPath;
            for (Path path : childPaths) {
                pathPrefix = path.subpath(i - 1, i);
                Path pathSuffix = null;
                if (i < path.getNameCount()) {
                    pathSuffix = path.subpath(i, path.getNameCount());
                }
                MapUtils.deepMapAdd(prefixToSuffixMap, pathPrefix, pathSuffix);
            }
            if (childPaths.size() != 1 && prefixToSuffixMap.size() != 1
                || childPaths.size() == 1 && i == lonelyChildPath.getNameCount() - 1) {
                break;
            } else {
                Path newRootPath = parent.getPath().resolve(pathPrefix);
                parent.setPath(newRootPath);
            }
        }

        for (Entry<Path, Set<Path>> entry : prefixToSuffixMap.entrySet()) {
            OffWorkspaceResourceTreeNode child;
            if (entry.getValue().size() == 1) {
                Path suffix = entry.getValue().iterator().next();
                Path path = entry.getKey();
                if (suffix != null) {
                    if (suffix.getNameCount() > 1) {
                        Path folderSuffix = suffix.subpath(0, suffix.getNameCount() - 1);
                        path = entry.getKey().resolve(folderSuffix);
                        child = new OffWorkspaceResourceTreeNode(parent, path);
                        child.addChild(new OffWorkspaceResourceTreeNode(child,
                            suffix.subpath(suffix.getNameCount() - 1, suffix.getNameCount())));
                    } else {
                        child = new OffWorkspaceResourceTreeNode(parent, path);
                        child.addChild(new OffWorkspaceResourceTreeNode(child, suffix));
                    }
                } else {
                    child = new OffWorkspaceResourceTreeNode(parent, path);
                }
            } else {
                child = new OffWorkspaceResourceTreeNode(parent, entry.getKey());
                if (entry.getValue() != null) {
                    buildOffScopeResourceTree(child, entry.getValue());
                }
            }
            parent.addChild(child);
        }
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
    public void performCheckLogic(CheckStateChangedEvent event, CheckboxTreeViewer packageSelector) {

        if (event.getSource().equals(packageSelector)) {
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

                if (event.getElement().toString().contains("All")) {
                    packageSelector.setAllChecked(true);
                }

                // checks if all child increments are checked and checks All-Checkbox
                boolean allChecked = true;
                for (TreeItem item : packageSelector.getTree().getItems()) {

                    if (!item.getChecked() && !item.getText().contains("All")) {
                        allChecked = false;
                        break;
                    }
                }
                if (allChecked) {
                    packageSelector.getTree().getItem(0).setChecked(true);
                }

            } else {
                for (TreePath parent : parents) {
                    if (parent.getSegmentCount() > 0) {
                        packageSelector.setChecked(parent, false);
                    }
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
     *            the {@link CheckboxTableViewer} listing all increments
     * @param selectedElements
     *            the {@link Set} of all elements checked by the user
     */
    public void performCheckLogicForALLIncrement(CheckboxTreeViewer incrementSelector, Set<Object> selectedElements) {

        Set<Object> addedDiff = new HashSet<>(selectedElements);
        Set<? extends IncrementTo> removedDiff = new HashSet<>(selectedIncrements);
        addedDiff.removeAll(selectedIncrements);
        removedDiff.removeAll(selectedElements);
        ComparableIncrement all =
            new ComparableIncrement(CobiGenWrapper.ALL_INCREMENT_ID, CobiGenWrapper.ALL_INCREMENT_NAME, null,
                Lists.<TemplateTo> newLinkedList(), Lists.<IncrementTo> newLinkedList());
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
     * Sets all resources which will be created to be initially selected
     */
    private void selectNewResources() {

        CheckboxTreeViewer resourcesTree = page.getResourcesTree();
        LinkedList<Object> worklist = Lists.newLinkedList(Arrays.asList(
            ((SelectFileContentProvider) resourcesTree.getContentProvider()).getElements(resourcesTree.getInput())));

        while (worklist.peek() != null) {
            Object o = worklist.poll();
            if (o instanceof IJavaElementStub || o instanceof IResourceStub
                || (o instanceof OffWorkspaceResourceTreeNode
                    && !Files.exists(((OffWorkspaceResourceTreeNode) o).getAbsolutePath()))) {
                resourcesTree.setChecked(o, true);
            }
            worklist
                .addAll(Arrays.asList(((SelectFileContentProvider) resourcesTree.getContentProvider()).getChildren(o)));
        }
    }

    /**
     * Sets all mergeable files to be selected
     */
    private void selectOverridingResources() {

        CheckboxTreeViewer resourcesTree = page.getResourcesTree();
        for (String path : cobigenWrapper.getOverridingFiles(selectedIncrements)) {
            Object mergableTreeObject =
                ((SelectFileContentProvider) resourcesTree.getContentProvider()).getProvidedObject(path);
            if (mergableTreeObject != null) {
                resourcesTree.setChecked(mergableTreeObject, true);
            }
        }
    }

    /**
     * Sets all mergeable files to be selected
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
