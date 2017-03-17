package com.capgemini.cobigen.eclipse.wizard.common.model;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.capgemini.cobigen.api.to.IncrementTo;
import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.exceptions.CobiGenEclipseRuntimeException;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IJavaElementStub;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IResourceStub;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.OffWorkspaceResourceTreeNode;

/**
 * Label Provider for the Export TreeViewer
 */
@SuppressWarnings("restriction")
public class SelectFileLabelProvider extends LabelProvider implements IColorProvider, ICheckStateListener {

    /** Logger instance */
    private static final Logger LOG = LoggerFactory.getLogger(SelectFileContentProvider.class);

    /** The currently selected resources */
    private Set<Object> selectedResources = new HashSet<>();

    /** Currently selected increments */
    private Set<IncrementTo> selectedIncrements = new HashSet<>();

    /** The current {@link CobiGenWrapper} instance */
    private CobiGenWrapper cobigenWrapper;

    /** Defines whether the {@link CobiGenWrapper} is in batch mode. */
    private boolean batch;

    /**
     * Creates a new {@link SelectFileContentProvider} which displays the texts and decorations according to
     * the simulated resources
     *
     * @param cobigenWrapper
     *            the currently used {@link CobiGenWrapper} instance
     * @param batch
     *            states whether the generation process is running in batch mode
     */
    public SelectFileLabelProvider(CobiGenWrapper cobigenWrapper, boolean batch) {

        this.cobigenWrapper = cobigenWrapper;
        this.batch = batch;
    }

    @Override
    public String getText(Object element) {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

        String result = "";

        if (element instanceof IResource) {
            result = ((IResource) element).getName();
        } else if (element instanceof IPackageFragmentRoot) {
            result = getFullName((IPackageFragmentRoot) element);
        } else if (element instanceof IPackageFragment) {
            try {
                result = HierarchicalTreeOperator.getChildName((IPackageFragment) element);
            } catch (JavaModelException e) {
                LOG.error(
                    "Could not retrieve package name of package with element name '{}'. An internal eclipse exception occured.",
                    ((IPackageFragment) element).getElementName(), e);
                result = "ERROR";
            }
            if (result.isEmpty()) {
                result = "(default package)";
            }
        } else if (element instanceof IJavaElement) {
            result = ((IJavaElement) element).getElementName();
        } else if (element instanceof OffWorkspaceResourceTreeNode) {
            result = ((OffWorkspaceResourceTreeNode) element).getPathStr();
        }

        result = addMetaInformation(element, result);

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return result.isEmpty() ? "UNDEFINED" : result;
    }

    @Override
    public Image getImage(Object element) {

        ImageDescriptor defaultImageDescriptor =
            PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(getText(element));
        Image result = defaultImageDescriptor.createImage();
        if (element instanceof IProject) {
            result = PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
        } else if (element instanceof IFolder) {
            result = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
        } else if (element instanceof IJavaElement) {
            JavaElementImageProvider p = new JavaElementImageProvider();
            result = p.getImageLabel(element, JavaElementImageProvider.SMALL_ICONS);
        }
        return result;
    }

    @Override
    public Color getForeground(Object element) {

        return null;
    }

    @Override
    public Color getBackground(Object element) {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

        if (selectedResources.contains(element)) {
            if ((element instanceof IJavaElementStub || element instanceof IResourceStub
                || (element instanceof OffWorkspaceResourceTreeNode
                    && !Files.exists(((OffWorkspaceResourceTreeNode) element).getAbsolutePath())))
                && !batch) {
                return Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
            } else if (element instanceof IFile || element instanceof ICompilationUnit
                || element instanceof OffWorkspaceResourceTreeNode) {
                if (isMergableFile(element)) {
                    return Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
                } else {
                    return new Color(Display.getDefault(), 255, 69, 0);
                }
            }
        }
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return null;
    }

    /**
     * Checks whether the given object is marked as mergable
     *
     * @param element
     *            to be checked
     * @return <code>true</code> if the given object can be merged<br>
     *         <code>false</code> otherwise
     */
    private boolean isMergableFile(Object element) {

        String path = "";
        if (element instanceof IResource) {
            path = ((IResource) element).getFullPath().toString();
        } else if (element instanceof IJavaElement) {
            path = ((IJavaElement) element).getPath().toString();
        } else if (element instanceof OffWorkspaceResourceTreeNode) {
            path = (((OffWorkspaceResourceTreeNode) element).getAbsolutePathStr());
        }
        return cobigenWrapper.isMergableFile(path, selectedIncrements);
    }

    /**
     * Sets the currently selected resources
     * @param selectedResources
     *            the currently selected resources
     */
    public void setSelectedResources(Object[] selectedResources) {
        this.selectedResources = new HashSet<>(Arrays.asList(selectedResources));
    }

    /**
     * Adds meta information to the elements name, such as new or merge or override
     * @param element
     *            to be enriched with information
     * @param source
     *            string to be enriched
     * @return the enriched result string
     */
    private String addMetaInformation(Object element, String source) {

        String result = new String(source);
        if (selectedResources.contains(element)) {
            if (element instanceof IJavaElementStub || element instanceof IResourceStub
                || (element instanceof OffWorkspaceResourceTreeNode
                    && !Files.exists(((OffWorkspaceResourceTreeNode) element).getAbsolutePath()))) {
                result += " (new)";
            } else if (element instanceof IFile || element instanceof ICompilationUnit) {
                if (isMergableFile(element)) {
                    result += batch ? " (create/merge)" : " (merge)";
                } else {
                    result += batch ? " (create/override)" : " (override)";
                }
            }
        }
        return result;
    }

    /**
     * Returns the full name of an {@link IPackageFragmentRoot} as by default only the last segment is
     * returned by {@link IJavaElement#getElementName()}
     *
     * @param root
     *            {@link IPackageFragmentRoot} for which the whole name should be determined
     * @return the full name of an {@link IPackageFragmentRoot}
     */
    private String getFullName(IPackageFragmentRoot root) {

        String path = root.getPath().toString();
        IJavaProject proj = root.getJavaProject();
        if (proj != null) {
            path = path.replaceFirst("/" + proj.getElementName() + "/", "");
        }
        return path;
    }

    @Override
    public void checkStateChanged(CheckStateChangedEvent event) {
        synchronized (selectedIncrements) {
            // Increments selection has been changed
            if (event.getSource() instanceof CheckboxTreeViewer) {
                Set<Object> selectedElements =
                    new HashSet<>(Arrays.asList(((CheckboxTreeViewer) event.getSource()).getCheckedElements()));

                selectedIncrements.clear();
                for (Object o : selectedElements) {
                    if (o instanceof IncrementTo) {
                        selectedIncrements.add((IncrementTo) o);
                    } else {
                        throw new CobiGenEclipseRuntimeException(
                            "Unexpected increment type '" + o.getClass().getCanonicalName() + "' !");
                    }
                }
            }
        }
    }
}
