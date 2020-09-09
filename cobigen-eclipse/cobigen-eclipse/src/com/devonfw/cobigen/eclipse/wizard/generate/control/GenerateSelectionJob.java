package com.devonfw.cobigen.eclipse.wizard.generate.control;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.actions.FormatAllAction;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.eclipse.common.AbstractCobiGenJob;
import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.CobiGenDialogConstants;
import com.devonfw.cobigen.eclipse.common.tools.ExceptionHandler;
import com.devonfw.cobigen.eclipse.common.tools.PathUtil;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.eclipse.generator.CobiGenWrapper;
import com.google.common.collect.Sets;

/**
 * Abstract implementation for processing generation
 */
public class GenerateSelectionJob extends AbstractCobiGenJob {

    /** Logger instance */
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /** Generator instance with which to generate the contents */
    protected CobiGenWrapper cobigenWrapper;

    /** The {@link Set} of paths to be generated */
    protected List<TemplateTo> templatesToBeGenerated;

    /**
     * Sets the given properties and make them accessible for sub types
     *
     * @param cobigenWrapper
     *            with which to generate the contents
     * @param templatesToBeGenerated
     *            {@link Set} of template ids to be generated
     */
    public GenerateSelectionJob(CobiGenWrapper cobigenWrapper, List<TemplateTo> templatesToBeGenerated) {

        this.cobigenWrapper = cobigenWrapper;
        this.templatesToBeGenerated = templatesToBeGenerated;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
        LOG.info("Start generation process...");

        if (templatesToBeGenerated.size() == 0) {
            LOG.warn("No templates determined to be generated... This might be a bug.");
            return;
        }

        try {
            final GenerationReportTo generationReport = performGeneration(monitor);

            if (generationReport.isSuccessful()) {
                Set<String> generatedFiles =
                    cobigenWrapper.getWorkspaceDependentTemplateDestinationPath(generationReport.getGeneratedFiles());
                Set<IProject> projects = Sets.newHashSet();
                for (String filePath : generatedFiles) {

                    IProject project =
                        ResourcesPlugin.getWorkspace().getRoot().getProject(PathUtil.getProject(filePath));
                    project = PathUtil.getRelativeProjectIfNeeded(filePath, project);

                    if (project.exists()) {
                        projects.add(project);
                    }
                }
                for (IProject proj : projects) {
                    proj.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                }

                final ICompilationUnit[] cus = getGeneratedCompilationUnits(generatedFiles);

                monitor.setTaskName("Organize Imports...");
                organizeImports(cus);

                monitor.setTaskName("Format Source Code...");
                formatSourceCode(cus);

                if (generationReport.hasWarnings()) {
                    PlatformUIUtil.getWorkbench().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder strBuilder = new StringBuilder();
                            int counter = 0;
                            for (String warning : generationReport.getWarnings()) {
                                strBuilder.append(++counter);
                                strBuilder.append(". ");
                                strBuilder.append(warning);
                                strBuilder.append("\n");
                            }

                            MessageDialog.openWarning(PlatformUIUtil.getWorkbench().getDisplay().getActiveShell(),
                                CobiGenDialogConstants.DIALOG_TITLE_GEN_SUCCEEDED_W_WARNINGS,
                                "Contents from " + templatesToBeGenerated.size()
                                    + " templates have been generated.\n\nWarnings:\n" + strBuilder.toString());
                        }
                    });
                } else {
                    String reportMessage = generationReport.isCancelled() ? "generation got Cancelled"
                        : "Contents from " + templatesToBeGenerated.size() + " templates have been generated.";
                    PlatformUIUtil.getWorkbench().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            MessageDialog.openInformation(PlatformUIUtil.getWorkbench().getDisplay().getActiveShell(),
                                CobiGenDialogConstants.DIALOG_TITLE_GEN_SUCCEEDED, reportMessage);
                        }
                    });
                }
            } else {
                PlatformUIUtil.getWorkbench().getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        Throwable firstError = generationReport.getErrors().get(0);
                        String tempGenMessage = "The merge of generated contents to the "
                            + "target code base has been aborted. Please find the errorneous generation "
                            + "results in the following temporary folder for further investigation: "
                            + generationReport.getTemporaryWorkingDirectory();
                        PlatformUIUtil.openErrorDialog(generationReport.getErrors().size() > 1
                            ? "Multiple errors occurred during generation. There are "
                                + generationReport.getErrors().size()
                                + " errors in total. See the stack trace only of the first error below."
                                + " Please investigate the Log file to view all errors if needed. " + tempGenMessage
                            : "An error occurred during generation. " + tempGenMessage, firstError);
                    }
                });
                for (Throwable e : generationReport.getErrors()) {
                    LOG.error("An error occurred during generation:", e);
                }
            }
        } catch (Throwable e) {
            ExceptionHandler.handle(e, null);
        } finally {
            LOG.info("Finished processing generation.");
            monitor.done();
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }

    /**
     * Performs the individual generation logic. The boolean return type should indicate whether the
     * generation causes any results, such that post processing like project refresh / organize imports /
     * format source code can be applied.
     *
     * @param monitor
     *            {@link IProgressMonitor} for tracking current work. The monitor should NOT be set to
     *            {@link IProgressMonitor#done()}, because post processing will do that!
     * @return {@link GenerationReportTo generation report} of CobiGen
     * @throws Exception
     *             if the generation results in any exceptional case
     */
    protected GenerationReportTo performGeneration(IProgressMonitor monitor) throws Exception {
        return cobigenWrapper.generate(templatesToBeGenerated, monitor);
    };

    /**
     * Organizes the imports by calling the {@link OrganizeImportsAction}
     * @param cus
     *            {@link CompilationUnit}s to be organized
     */
    private void organizeImports(final ICompilationUnit[] cus) {
        if (cus.length < 1) {
            return;
        }

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
     * @param cus
     *            {@link CompilationUnit}s to be formatted
     */
    private void formatSourceCode(final ICompilationUnit[] cus) {
        if (cus.length < 1) {
            return;
        }

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
     * @param allTargetPathsInWorkspace
     *            all target paths in workspace
     * @return an array of {@link ICompilationUnit}s, which are targeted by the generated paths
     */
    private ICompilationUnit[] getGeneratedCompilationUnits(Set<String> allTargetPathsInWorkspace) {

        IProject proj = cobigenWrapper.getGenerationTargetProject();
        if (proj != null) {
            List<ICompilationUnit> cus = new LinkedList<>();
            for (String path : allTargetPathsInWorkspace) {
                IResource file = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
                if (file != null && file instanceof IFile) {
                    IJavaElement elem = JavaCore.create(file);
                    if (elem != null && elem instanceof ICompilationUnit) {
                        cus.add((ICompilationUnit) elem);
                    }
                }
            }

            return cus.toArray(new ICompilationUnit[0]);
        } else {
            return new ICompilationUnit[0];
        }
    }

}
