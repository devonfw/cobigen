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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
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

import com.capgemini.cobigen.eclipse.common.exceptions.NotYetSupportedException;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.exceptions.PluginProcessingException;
import com.capgemini.cobigen.extension.to.TemplateTo;

import freemarker.template.TemplateException;

/**
 * Abstract implementation for processing generation
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public abstract class AbstractGenerateSelectionProcess implements IRunnableWithProgress {

    /**
     * Logger instance
     */
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * {@link Shell} on which to display error messages
     */
    private Shell shell;

    /**
     * Generator instance with which to generate the contents
     */
    protected JavaGeneratorWrapper javaGeneratorWrapper;

    /**
     * The {@link Set} of paths to be generated
     */
    protected List<TemplateTo> templatesToBeGenerated;

    /**
     * Sets the given properties and make them accessible for sub types
     *
     * @param shell
     *            on which to display error messages
     * @param javaGeneratorWrapper
     *            with which to generate the contents
     * @param templatesToBeGenerated
     *            {@link Set} of template ids to be generated
     */
    public AbstractGenerateSelectionProcess(Shell shell, JavaGeneratorWrapper javaGeneratorWrapper,
        List<TemplateTo> templatesToBeGenerated) {

        this.shell = shell;
        this.javaGeneratorWrapper = javaGeneratorWrapper;
        this.templatesToBeGenerated = templatesToBeGenerated;
    }

    /**
     * {@inheritDoc}
     *
     * @author trippl (22.04.2013) / mbrunnli (06.08.2014)
     */
    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

        if (templatesToBeGenerated.size() == 0) {
            return;
        }

        try {
            boolean anyResults = performGeneration(monitor);

            if (anyResults) {
                IProject proj = javaGeneratorWrapper.getGenerationTargetProject();
                if (proj != null) {
                    proj.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                }

                final ICompilationUnit[] cus = getGeneratedCompilationUnits();

                monitor.setTaskName("Organize Imports...");
                organizeImports(cus);

                monitor.setTaskName("Format Source Code...");
                formatSourceCode(cus);
            }

            MessageDialog.openInformation(shell, "Success!", "Contents from " + templatesToBeGenerated.size()
                + " templates have been generated.");

        } catch (MalformedURLException e) {
            // should not occur --> programmatical fault
            MessageDialog.openError(shell, "Malformed URL Exception", e.getMessage());
            LOG.error("Malformed URL Exception", e);
        } catch (CoreException e) {
            MessageDialog.openError(shell, "Eclipse internal Exception",
                "An eclipse internal exception occurred during processing:\n" + e.getMessage()
                    + "\n If this problem persists please report it to the CobiGen developers.");
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
        } catch (PluginProcessingException e) {
            MessageDialog.openError(shell, "Plug-in Processing Exception",
                "A plug-in caused an unhandled exception:\n" + e.getMessage());
            LOG.error("A plug-in caused an unhandled exception:\n{}", e.getMessage(), e);
        } catch (NotYetSupportedException e) {
            MessageDialog.openInformation(shell, "Not yet supported operation!", e.getMessage());
            LOG.warn("An unsupported operation has been triggered:\n{}", e.getMessage(), e);
        } catch (Throwable e) {
            MessageDialog.openError(shell, "Unknown Exception", e.getMessage());
            LOG.error("Unknown Exception", e);
        }
        monitor.done();
    }

    /**
     * Performs the individual generation logic. The boolean return type should indicate whether the
     * generation causes any results, such that post processing like project refresh / organize imports /
     * format source code can be applied.
     *
     * @param monitor
     *            {@link IProgressMonitor} for tracking current work. The monitor should NOT be set to
     *            {@link IProgressMonitor#done()}, because post processing will do that!
     * @return <code>true</code>, if generation causes results, which should be post processed<br>
     *         <code>false</code> , otherwise
     * @throws Exception
     *             if the generation results in any exceptional case
     */
    protected abstract boolean performGeneration(IProgressMonitor monitor) throws Exception;

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
     * @throws JavaModelException
     *             if an interal eclipse exception occurred
     * @author mbrunnli (04.06.2014)
     */
    private ICompilationUnit[] getGeneratedCompilationUnits() throws JavaModelException {

        IProject proj = javaGeneratorWrapper.getGenerationTargetProject();
        if (proj != null) {
            List<ICompilationUnit> cus = new LinkedList<>();
            for (IFile file : javaGeneratorWrapper.getAllTargetFiles()) {
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
