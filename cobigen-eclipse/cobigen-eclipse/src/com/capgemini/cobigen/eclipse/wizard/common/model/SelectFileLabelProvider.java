/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.common.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IJavaElementStub;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IResourceStub;
import com.capgemini.cobigen.extension.to.TemplateTo;

/**
 * Label Provider for the Export TreeViewer
 */
@SuppressWarnings("restriction")
public class SelectFileLabelProvider extends LabelProvider implements IColorProvider {

    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(SelectFileContentProvider.class);

    /**
     * The currently selected resources
     */
    private Set<Object> checkedResources = new HashSet<Object>();

    /**
     * The current {@link JavaGeneratorWrapper} instance
     */
    private JavaGeneratorWrapper javaGeneratorWrapper;

    /**
     * Defines whether the {@link JavaGeneratorWrapper} is in batch mode.
     */
    private boolean batch;

    /**
     * Creates a new {@link SelectFileContentProvider} which displays the texts and decorations according to
     * the simulated resources
     * 
     * @param javaGeneratorWrapper
     *            the currently used {@link JavaGeneratorWrapper} instance
     * @param batch
     *            states whether the generation process is running in batch mode
     * @author mbrunnli (14.02.2013)
     */
    public SelectFileLabelProvider(JavaGeneratorWrapper javaGeneratorWrapper, boolean batch) {

        this.javaGeneratorWrapper = javaGeneratorWrapper;
        this.batch = batch;
    }

    /**
     * {@inheritDoc}
     * 
     * @author mbrunnli (14.02.2013)
     */
    @Override
    public String getText(Object element) {

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
        }

        result = addMetaInformation(element, result);

        return result.isEmpty() ? "UNDEFINED" : result;
    }

    /**
     * {@inheritDoc}
     * 
     * @author mbrunnli (14.02.2013)
     */
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

    /**
     * {@inheritDoc}
     * 
     * @author mbrunnli (14.02.2013)
     */
    @Override
    public Color getForeground(Object element) {

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @author mbrunnli (14.02.2013)
     */
    @Override
    public Color getBackground(Object element) {

        if (checkedResources.contains(element)) {
            if ((element instanceof IJavaElementStub || element instanceof IResourceStub) && !batch) {
                return Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
            } else if (element instanceof IFile || element instanceof ICompilationUnit) {
                if (isMergableFile(element)) {
                    return Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
                } else {
                    return new Color(Display.getDefault(), 255, 69, 0);
                }
            }
        }
        return null;
    }

    /**
     * Checks whether the given object is marked as mergable
     * 
     * @param element
     *            to be checked
     * @return <code>true</code> if the given object can be merged<br>
     *         <code>false</code> otherwise
     * @author mbrunnli (14.03.2013)
     */
    private boolean isMergableFile(Object element) {

        String path = null;
        if (element instanceof IResource) {
            path = ((IResource) element).getFullPath().toString();
        } else if (element instanceof IJavaElement) {
            path = ((IJavaElement) element).getPath().toString();
        }

        // boundary case: multiple templates target one path, which are not mergable... does not make sense
        List<TemplateTo> templates = javaGeneratorWrapper.getTemplatesForFilePath(path, null);
        for (TemplateTo template : templates) {
            if (path != null && template.getMergeStrategy() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the currently selected resources
     * 
     * @param checkedResources
     *            the currently selected resources
     * @author mbrunnli (14.03.2013)
     */
    public void setCheckedResources(Object[] checkedResources) {

        this.checkedResources = new HashSet<Object>(Arrays.asList(checkedResources));
    }

    /**
     * Adds meta information to the elements name, such as new or merge or override
     * 
     * @param element
     *            to be enriched with information
     * @param result
     *            enriched string
     * @return the enriched result string
     * @author mbrunnli (14.03.2013)
     */
    private String addMetaInformation(Object element, String result) {

        if (checkedResources.contains(element)) {
            if (element instanceof IJavaElementStub || element instanceof IResourceStub) {
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
     * @author mbrunnli (19.02.2013)
     */
    private String getFullName(IPackageFragmentRoot root) {

        String path = root.getPath().toString();
        IJavaProject proj = root.getJavaProject();
        if (proj != null) {
            path = path.replaceFirst("/" + proj.getElementName() + "/", "");
        }
        return path;
    }
}
