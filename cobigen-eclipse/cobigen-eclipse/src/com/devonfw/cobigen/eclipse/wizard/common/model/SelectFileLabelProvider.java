package com.devonfw.cobigen.eclipse.wizard.common.model;

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

import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.exceptions.CobiGenEclipseRuntimeException;
import com.devonfw.cobigen.eclipse.generator.CobiGenWrapper;
import com.devonfw.cobigen.eclipse.wizard.common.control.CheckStateListener;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.IJavaElementStub;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.IResourceStub;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.OffWorkspaceResourceTreeNode;

/**
 * Label Provider for the Export TreeViewer
 */
@SuppressWarnings("restriction")
public class SelectFileLabelProvider extends LabelProvider implements IColorProvider, ICheckStateListener {

  /** Item label suffix */
  private static final String LABEL_SUFFIX_NEW = " (new)";

  /** Item label suffix */
  private static final String LABEL_SUFFIX_OVERRIDE = " (override)";

  /** Item label suffix */
  private static final String LABEL_SUFFIX_CREATE_OVERRIDE = " (create/override)";

  /** Item label suffix */
  private static final String LABEL_SUFFIX_MERGE = " (merge)";

  /** Item label suffix */
  private static final String LABEL_SUFFIX_CREATE_MERGE = " (create/merge)";

  /** Item label for an unknown item */
  private static final String LABEL_UNDEFINED = "UNDEFINED";

  /** Item label if an error occurred */
  private static final String LABEL_ERROR = "ERROR";

  /** Item label for the default package */
  private static final String LABEL_DEFAULT_PACKAGE = "(default package)";

  /** Red color for override marking */
  private static final Color OVERRIDE_COLOR = new Color(Display.getDefault(), 255, 69, 0);

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
   * Creates a new {@link SelectFileContentProvider} which displays the texts and decorations according to the simulated
   * resources
   *
   * @param cobigenWrapper the currently used {@link CobiGenWrapper} instance
   * @param batch states whether the generation process is running in batch mode
   */
  public SelectFileLabelProvider(CobiGenWrapper cobigenWrapper, boolean batch) {

    this.cobigenWrapper = cobigenWrapper;
    this.batch = batch;

  }

  @Override
  public String getText(Object element) {

    MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
    String result = getTextInternal(element, true);
    MDC.remove(InfrastructureConstants.CORRELATION_ID);
    return result;
  }

  /**
   * Implementation of {@link LabelProvider#getText(Object)}
   */
  @SuppressWarnings("javadoc")
  private String getTextInternal(Object element, boolean addMetadata) {

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
        result = LABEL_ERROR;
      }
      if (result.isEmpty()) {
        result = LABEL_DEFAULT_PACKAGE;
      }
    } else if (element instanceof IJavaElement) {
      result = ((IJavaElement) element).getElementName();
    } else if (element instanceof OffWorkspaceResourceTreeNode) {
      result = ((OffWorkspaceResourceTreeNode) element).getPathStr();
    }

    if (addMetadata) {
      result = addMetaInformation(element, result);
    }
    result = result.isEmpty() ? LABEL_UNDEFINED : result;
    return result;
  }

  @Override
  public Image getImage(Object element) {

    String labelTextWithoutSuffix = getTextInternal(element, false);
    ImageDescriptor defaultImageDescriptor = PlatformUI.getWorkbench().getEditorRegistry()
        .getImageDescriptor(labelTextWithoutSuffix);
    Image result = defaultImageDescriptor.createImage();
    if (element instanceof IProject) {
      result = PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
    } else if (element instanceof IFolder) {
      result = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
    } else if (element instanceof IJavaElement) {
      JavaElementImageProvider p = new JavaElementImageProvider();
      result = p.getImageLabel(element, JavaElementImageProvider.SMALL_ICONS);
    } else if (element instanceof OffWorkspaceResourceTreeNode
        && ((OffWorkspaceResourceTreeNode) element).hasChildren()) {
      result = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
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

    if (this.selectedResources.contains(element)) {
      if ((element instanceof IJavaElementStub || element instanceof IResourceStub
          || (element instanceof OffWorkspaceResourceTreeNode
              && !Files.exists(((OffWorkspaceResourceTreeNode) element).getAbsolutePath())))
          && !this.batch) {
        return Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
      } else if (element instanceof IFile || element instanceof ICompilationUnit
          || element instanceof OffWorkspaceResourceTreeNode) {
        if (isMergableFile(element) && !isOverridableFile(element)) {
          return Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
        } else {
          return OVERRIDE_COLOR;
        }
      }
    }
    MDC.remove(InfrastructureConstants.CORRELATION_ID);
    return null;
  }

  /**
   * Checks whether the given object is marked as mergable
   *
   * @param element to be checked
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
    return this.cobigenWrapper.isMergableFile(path, this.selectedIncrements);
  }

  /**
   * Checks whether the given object is marked as mergable
   *
   * @param element to be checked
   * @return <code>true</code> if the given object can be merged<br>
   *         <code>false</code> otherwise
   */
  private boolean isOverridableFile(Object element) {

    String path = "";
    if (element instanceof IResource) {
      path = ((IResource) element).getFullPath().toString();
    } else if (element instanceof IJavaElement) {
      path = ((IJavaElement) element).getPath().toString();
    } else if (element instanceof OffWorkspaceResourceTreeNode) {
      path = (((OffWorkspaceResourceTreeNode) element).getAbsolutePathStr());
    }
    return this.cobigenWrapper.isOverridableFile(path, this.selectedIncrements);
  }

  /**
   * Sets the currently selected resources
   *
   * @param selectedResources the currently selected resources
   */
  public void setSelectedResources(Object[] selectedResources) {

    this.selectedResources = new HashSet<>(Arrays.asList(selectedResources));
  }

  /**
   * Adds meta information to the elements name, such as new or merge or override
   *
   * @param element to be enriched with information
   * @param source string to be enriched
   * @return the enriched result string
   */
  private String addMetaInformation(Object element, String source) {

    String result = new String(source);
    if (this.selectedResources.contains(element)) {
      if (!this.batch && (element instanceof IJavaElementStub || element instanceof IResourceStub
          || (element instanceof OffWorkspaceResourceTreeNode && !((OffWorkspaceResourceTreeNode) element).hasChildren()
              && !Files.exists(((OffWorkspaceResourceTreeNode) element).getAbsolutePath())))) {
        result += LABEL_SUFFIX_NEW;
      } else if (element instanceof IFile || element instanceof ICompilationUnit
          || (element instanceof OffWorkspaceResourceTreeNode
              && !((OffWorkspaceResourceTreeNode) element).hasChildren())) {
        if (isOverridableFile(element)) {
          result += LABEL_SUFFIX_OVERRIDE;
        } else if (isMergableFile(element)) {
          result += this.batch ? LABEL_SUFFIX_CREATE_MERGE : LABEL_SUFFIX_MERGE;
        } else {
          result += this.batch ? LABEL_SUFFIX_CREATE_OVERRIDE : LABEL_SUFFIX_OVERRIDE;
        }
      }
    }
    return result;
  }

  /**
   * Returns the full name of an {@link IPackageFragmentRoot} as by default only the last segment is returned by
   * {@link IJavaElement#getElementName()}
   *
   * @param root {@link IPackageFragmentRoot} for which the whole name should be determined
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

    synchronized (this.selectedIncrements) {
      // Increments selection has been changed
      if (event.getSource() instanceof CheckboxTreeViewer) {

        CheckboxTreeViewer incrementSelector = (CheckboxTreeViewer) event.getSource();

        CheckStateListener listener = new CheckStateListener(this.cobigenWrapper, null, this.batch);
        listener.performCheckLogic(event, incrementSelector);

        Set<Object> selectedElements = new HashSet<>(
            Arrays.asList(((CheckboxTreeViewer) event.getSource()).getCheckedElements()));

        this.selectedIncrements.clear();
        for (Object o : selectedElements) {
          if (o instanceof IncrementTo) {
            this.selectedIncrements.add((IncrementTo) o);
          } else {
            throw new CobiGenEclipseRuntimeException(
                "Unexpected increment type '" + o.getClass().getCanonicalName() + "' !");
          }
        }
      }
    }
  }
}
