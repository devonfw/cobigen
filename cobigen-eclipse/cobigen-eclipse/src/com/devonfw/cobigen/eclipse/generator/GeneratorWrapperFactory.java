package com.devonfw.cobigen.eclipse.generator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.CobiGenResourceRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.devonfw.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.devonfw.cobigen.eclipse.generator.generic.FileInputConverter;
import com.devonfw.cobigen.eclipse.generator.generic.FileInputGeneratorWrapper;
import com.devonfw.cobigen.eclipse.generator.java.JavaInputConverter;
import com.devonfw.cobigen.eclipse.generator.java.JavaInputGeneratorWrapper;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.google.common.collect.Lists;

/**
 * Generator creation factory, which creates a specific generator instance dependent on the current selection
 * within the eclipse IDE
 * @author mbrunnli (03.12.2014)
 */
public class GeneratorWrapperFactory {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorWrapperFactory.class);

    /**
     * Creates a generator dependent on the input of the selection
     * @param selection
     *            current {@link IStructuredSelection} treated as input for generation
     * @return a specific {@link CobiGenWrapper} instance
     * @throws GeneratorCreationException
     *             if any exception occurred during converting the inputs or creating the generator
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project does not exist
     */
    public static CobiGenWrapper createGenerator(ISelection selection)
        throws GeneratorCreationException, GeneratorProjectNotExistentException {

        List<Object> extractedInputs = extractValidEclipseInputs(selection);

        if (extractedInputs.size() > 0) {
            CobiGen cobigen = initializeGenerator();

            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {

                }
            });

            ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
            progressMonitor.open();
            progressMonitor.getProgressMonitor().beginTask("Reading inputs...", 0);
            Object firstElement = extractedInputs.get(0);

            try {
                if (firstElement instanceof IJavaElement) {
                    LOG.info("Create new CobiGen instance for java inputs...");
                    return new JavaInputGeneratorWrapper(cobigen,
                        ((IJavaElement) firstElement).getJavaProject().getProject(),
                        JavaInputConverter.convertInput(extractedInputs, cobigen));
                } else if (firstElement instanceof IFile) {
                    LOG.info("Create new CobiGen instance for file inputs...");
                    return new FileInputGeneratorWrapper(cobigen, ((IFile) firstElement).getProject(),
                        FileInputConverter.convertInput(cobigen, extractedInputs));
                }
            } finally {
                progressMonitor.close();
            }
        }
        return null;
    }

    /**
     * Extracts a list of valid eclipse inputs. Therefore this method will throw an
     * {@link InvalidInputException},whenever<br>
     * <ul>
     * <li>the selection contains different content types</li>
     * <li>the selection contains a content type, which is currently not supported</li>
     * </ul>
     * @param selection
     *            current {@link IStructuredSelection selection} of within the IDE
     * @return the {@link List} of selected objects, whereas all elements of the list are of the same content
     *         type
     */
    private static List<Object> extractValidEclipseInputs(ISelection selection) {
        LOG.info("Start extraction of valid inputs from selection...");
        List<Object> inputObjects = Lists.newLinkedList();
        IJavaElement iJavaElem = null;

        // When the user is selecting text from the text editor
        if (selection instanceof ITextSelection) {
            IFileEditorInput iEditorInput =
                (IFileEditorInput) PlatformUIUtil.getActiveWorkbenchPage().getActiveEditor().getEditorInput();

            IFile iFile = iEditorInput.getFile();
            iJavaElem = JavaCore.create(iFile);
            if (iJavaElem instanceof ICompilationUnit) {
                inputObjects.add(iJavaElem);
            } else {
                inputObjects.add(iFile);
            }
        }

        /*
         * Collect selected objects and cast them to an IResource if necessary
         */
        else if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            Iterator<?> it = structuredSelection.iterator();

            inputObjects = Stream.generate(it::next).limit(structuredSelection.size()).map(o -> {
                if (o instanceof ICompilationUnit) {
                    ICompilationUnit cu = (ICompilationUnit) o;
                    try {
                        return cu.getCorrespondingResource();
                    } catch (JavaModelException e) {
                        throw new CobiGenResourceRuntimeException(
                            "there is no corresponding Resource to the selected input: " + cu.getPath());
                    }
                }
                return o;
            }).map(o -> {
                if (o instanceof IResource) {
                    IResource resource = ((IResource) o);
                    if (resource.getLocation() != null) {
                        return o;
                    } else {
                        throw new CobiGenResourceRuntimeException(
                            "Files are missing on the Filesystem for the selected input Files: "
                                + resource.getFullPath());
                    }
                } else {
                    throw new CobiGenResourceRuntimeException("unknown selected input");
                }
            }).collect(Collectors.toList());
        }

        LOG.info("Finished extraction of inputs from selection successfully.");
        return inputObjects;
    }

    /**
     * Initializes the {@link CobiGen} with the correct configuration
     *
     * @return the configured{@link CobiGen}
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration folder does not exist
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     * @throws GeneratorCreationException
     *             if the generator configuration project does not exist
     */
    private static CobiGen initializeGenerator() throws InvalidConfigurationException, GeneratorCreationException {
        try {
            ResourcesPluginUtil.refreshConfigurationProject();
            IProject generatorProj = ResourcesPluginUtil.getGeneratorConfigurationProject();

            if (generatorProj == null) {
                throw new GeneratorCreationException(
                    "Configuration source could not be read. Have you downloaded the templates?");
            }

            // We need to check whether it is a valid Java Project
            IJavaProject configJavaProject = JavaCore.create(generatorProj);

            // If it is not valid, we should use the jar
            if (null == generatorProj.getLocationURI() || !configJavaProject.exists()) {
                String fileName = ResourcesPluginUtil.getJarPath(false);
                IPath ws = ResourcesPluginUtil.getWorkspaceLocation();
                File file =
                    new File(ws.append(ResourceConstants.DOWNLOADED_JAR_FOLDER + File.separator + fileName).toString());
                return CobiGenFactory.create(file.toURI());
            } else {
                return CobiGenFactory.create(generatorProj.getLocationURI());
            }
        } catch (CoreException e) {
            throw new GeneratorCreationException("An eclipse internal exception occurred", e);
        } catch (IOException e) {
            throw new GeneratorCreationException(
                "Configuration source could not be read.\nIf you were updating templates, it may mean"
                    + " that you have no internet connection.",
                e);
        }
    }
}
