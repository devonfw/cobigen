package com.capgemini.cobigen.eclipse.wizard.generate.control;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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

import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.eclipse.common.AbstractCobiGenJob;
import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.impl.exceptions.CobiGenRuntimeException;

/**
 * Abstract implementation for processing generation
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public abstract class AbstractGenerateSelectionJob extends AbstractCobiGenJob {

    /**
     * Logger instance
     */
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Generator instance with which to generate the contents
     */
    protected CobiGenWrapper cobigenWrapper;

    /**
     * The {@link Set} of paths to be generated
     */
    protected List<TemplateTo> templatesToBeGenerated;

    /**
     * Sets the given properties and make them accessible for sub types
     *
     * @param cobigenWrapper
     *            with which to generate the contents
     * @param templatesToBeGenerated
     *            {@link Set} of template ids to be generated
     */
    public AbstractGenerateSelectionJob(CobiGenWrapper cobigenWrapper,
        List<TemplateTo> templatesToBeGenerated) {

        this.cobigenWrapper = cobigenWrapper;
        this.templatesToBeGenerated = templatesToBeGenerated;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
        LOG.info("Start generation process...");

        if (templatesToBeGenerated.size() == 0) {
            return;
        }

        try {
            final GenerationReportTo generationReport = performGeneration(monitor);

            if (generationReport.isSuccessful()) {
                IProject proj = cobigenWrapper.getGenerationTargetProject();
                if (proj != null) {
                    proj.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                }

                final ICompilationUnit[] cus = getGeneratedCompilationUnits();

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

                            MessageDialog.openWarning(
                                PlatformUIUtil.getWorkbench().getDisplay().getActiveShell(),
                                "Success with warnings!",
                                "Contents from " + templatesToBeGenerated.size()
                                    + " templates have been generated.\n\nWarnings:\n"
                                    + strBuilder.toString());
                        }
                    });
                } else {
                    PlatformUIUtil.getWorkbench().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            MessageDialog.openInformation(
                                PlatformUIUtil.getWorkbench().getDisplay().getActiveShell(), "Success!",
                                "Contents from " + templatesToBeGenerated.size()
                                    + " templates have been generated.");
                        }
                    });
                }
            } else {
                PlatformUIUtil.getWorkbench().getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        Throwable firstError = generationReport.getErrors().get(0);
                        PlatformUIUtil.openErrorDialog("Generation exited with errors.",
                            "There are " + generationReport.getErrors().size()
                                + " errors in total. Below, there is just the stack trace of the first error."
                                + " Please see the Log File to view all errors.",
                            firstError);
                    }
                });
            }
        } catch (CoreException e) {
            PlatformUIUtil.openErrorDialog("Eclipse internal Exception",
                "An eclipse internal exception occurred during processing:\n" + e.getMessage()
                    + "\n If this problem persists please report it to the CobiGen developers.",
                e);
            LOG.error("Eclipse internal Exception", e);
        } catch (CobiGenRuntimeException e) {
            PlatformUIUtil.openErrorDialog("CobiGen Error", e.getMessage(), e);
            LOG.error("CobiGen Exception:\n{}", e.getMessage(), e);
        } catch (Throwable e) {
            PlatformUIUtil.openErrorDialog("Error", "An unexpected exception occurred!", e);
            LOG.error("An unexpected exception occurred!", e);
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
    protected abstract GenerationReportTo performGeneration(IProgressMonitor monitor) throws Exception;

    /**
     * Organizes the imports by calling the {@link OrganizeImportsAction}
     *
     * @param cus
     *            {@link CompilationUnit}s to be organized
     * @author mbrunnli (12.03.2013)
     */
    private void organizeImports(final ICompilationUnit[] cus) {

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
     *
     * @param cus
     *            {@link CompilationUnit}s to be formatted
     * @author mbrunnli (27.03.2013)
     */
    private void formatSourceCode(final ICompilationUnit[] cus) {

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
     *
     * @return an array of {@link ICompilationUnit}s, which are targeted by the generated paths
     * @author mbrunnli (04.06.2014)
     */
    private ICompilationUnit[] getGeneratedCompilationUnits() {

        IProject proj = cobigenWrapper.getGenerationTargetProject();
        if (proj != null) {
            List<ICompilationUnit> cus = new LinkedList<>();
            for (IFile file : cobigenWrapper.getAllTargetFiles()) {
                if (file.exists()) {
                    IJavaElement elem = JavaCore.create(file);
                    if (elem instanceof ICompilationUnit) {
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
