/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.generate.control;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.actions.FormatAllAction;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.extension.to.TemplateTo;

import freemarker.template.TemplateException;

/**
 * Running this this process as issued in {@link IRunnableWithProgress} performs the generation tasks of the
 * generation wizard
 * @author mbrunnli (12.03.2013)
 */
public class GenerateSelectionProcess implements IRunnableWithProgress {

    /**
     * {@link Shell} on which to display error messages
     */
    private Shell shell;

    /**
     * Generator instance with which to generate the contents
     */
    private JavaGeneratorWrapper javaGeneratorWrapper;

    /**
     * The {@link Set} of paths to be generated
     */
    private Set<String> pathsToBeGenerated;

    /**
     * Assigning logger to GenerateSelectionProcess
     */
    private static final Logger LOG = LoggerFactory.getLogger(GenerateSelectionProcess.class);

    /**
     * Creates a new process ({@link IRunnableWithProgress}) for performing the generation tasks
     * @param shell
     *            on which to display error messages
     * @param javaGeneratorWrapper
     *            with which to generate the contents
     * @param pathsToBeGenerated
     *            {@link Set} of paths to be generated
     * @author mbrunnli (12.03.2013)
     */
    public GenerateSelectionProcess(Shell shell, JavaGeneratorWrapper javaGeneratorWrapper,
        Set<String> pathsToBeGenerated) {
        this.shell = shell;
        this.javaGeneratorWrapper = javaGeneratorWrapper;
        this.pathsToBeGenerated = pathsToBeGenerated;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (12.03.2013)
     */
    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        if (pathsToBeGenerated.size() == 0)
            return;

        try {
            final IProject proj = javaGeneratorWrapper.getGenerationTargetProject();
            if (proj != null) {
                monitor.beginTask("Generate files...", pathsToBeGenerated.size());
                for (String path : pathsToBeGenerated) {
                    List<TemplateTo> templates = javaGeneratorWrapper.getTemplatesForFilePath(path);
                    for (TemplateTo t : templates) {
                        if (t != null) {
                            if (t.getMergeStrategy() == null) {
                                javaGeneratorWrapper.generate(t, true);
                            } else {
                                javaGeneratorWrapper.generate(t, false);
                            }
                        }
                    }
                    monitor.worked(1);
                }
                proj.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            }

            monitor.setTaskName("Organize Imports...");
            organizeImports(pathsToBeGenerated);

            monitor.setTaskName("Format Source Code...");
            formatSourceCode(pathsToBeGenerated);

            MessageDialog.openInformation(shell, "Success!", pathsToBeGenerated.size()
                + " files and folders have been generated.");

        } catch (MalformedURLException e) {
            // should not occur --> programmatical fault
            MessageDialog.openError(shell, "Malformed URL Exception", e.getMessage());
            LOG.error("Malformed URL Exception", e);
        } catch (CoreException e) {
            MessageDialog.openError(shell, "Eclipse internal Exception", e.getMessage());
            LOG.error("Eclipse internal Exception", e);
        } catch (TemplateException e) {
            MessageDialog.openError(shell, "Template Exception",
                e.getMessage() + "\n" + e.getFTLInstructionStack());
            LOG.error("Template Exception", e);
        } catch (IOException e) {
            MessageDialog.openError(shell, "IO Exception", e.getMessage());
            LOG.error("IO Exception", e);
        } catch (TransformerException e) {
            MessageDialog.openError(shell, "Transformer Exception", e.getMessage());
            LOG.error("Transforer Exception", e);
        } catch (SAXException e) {
            MessageDialog.openError(shell, "SAX Exception", e.getMessage());
            LOG.error("SAX Exception", e);
        } catch (Throwable e) {
            MessageDialog.openError(shell, "Unknown Exception", e.getMessage());
            LOG.error("Unknown Exception", e);
        } finally {
            // TODO remove newly created (only!) files and refresh project
        }
        monitor.done();
    }

    /**
     * Organizes the imports by calling the {@link OrganizeImportsAction}
     * @param generatedPaths
     *            all generated paths the organize imports should be applied to
     * @author mbrunnli (12.03.2013)
     */
    private void organizeImports(Set<String> generatedPaths) {
        final ICompilationUnit[] cus = getGeneratedCompilationUnits(generatedPaths);
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                IWorkbenchPartSite site = PlatformUIUtil.getActiveWorkbenchPage().getActivePart().getSite();
                OrganizeImportsAction org = new OrganizeImportsAction(site);
                org.run(new StructuredSelection(cus));
            }
        });
    }

    /**
     * Formats source code of all java files which have been generated or merged
     * @param generatedPaths
     *            all generated paths the source code formatting should be applied to
     * @author mbrunnli (27.03.2013)
     */
    private void formatSourceCode(Set<String> generatedPaths) {
        final ICompilationUnit[] cus = getGeneratedCompilationUnits(generatedPaths);
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                IWorkbenchPartSite site = PlatformUIUtil.getActiveWorkbenchPage().getActivePart().getSite();
                FormatAllAction action = new FormatAllAction(site);
                action.runOnMultiple(cus);
            }
        });
    }

    /**
     * Retrieves all {@link ICompilationUnit}s targeted by the generated paths
     * @param generatedPaths
     *            all paths, where resources were generated to
     * @return an array of {@link ICompilationUnit}s, which are targeted by the generated paths
     * @author mbrunnli (04.06.2014)
     */
    private ICompilationUnit[] getGeneratedCompilationUnits(Set<String> generatedPaths) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        List<ICompilationUnit> cus = new LinkedList<ICompilationUnit>();
        for (String path : generatedPaths) {
            // check if it is a sub path and not the project itself, otherwise root.getFile will throw an
            // exception
            if (path.substring(1).indexOf("/") != -1) {
                IFile file = root.getFile(new Path(path));
                if (file.exists()) {
                    IJavaElement elem = JavaCore.create(file);
                    if (elem instanceof ICompilationUnit) {
                        cus.add((ICompilationUnit) elem);
                    }
                }
            }
        }
        return cus.toArray(new ICompilationUnit[0]);
    }

}
