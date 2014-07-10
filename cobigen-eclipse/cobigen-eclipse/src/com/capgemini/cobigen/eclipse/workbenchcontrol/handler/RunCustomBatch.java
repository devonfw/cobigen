/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.eclipse.workbenchcontrol.handler;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.wizard.generatecustombatch.GenerateCustomBatchWizard;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;

/**
 * 
 * @author mbrunnli (21.03.2014)
 */
public class RunCustomBatch extends AbstractHandler {

    /**
     * Assigning logger to RunCustomBatch
     */
    private static final Logger LOG = LoggerFactory.getLogger(RunCustomBatch.class);

    /**
     * {@inheritDoc}
     * @author mbrunnli (21.03.2014)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (sel instanceof IStructuredSelection) {
            IStructuredSelection sSel = (IStructuredSelection) sel;
            IPath elementPathOrPackage = null;
            IProject targetProject = null;
            Object firstElement = sSel.getFirstElement();
            if (firstElement instanceof IJavaElement) {
                try {
                    elementPathOrPackage =
                        ((IJavaElement) firstElement).getCorrespondingResource().getFullPath();
                    targetProject = ((IJavaElement) firstElement).getCorrespondingResource().getProject();
                } catch (JavaModelException e) {
                    MessageDialog
                        .openError(HandlerUtil.getActiveShell(event), "Internal - JavaModelException",
                            "An internal JDT exception occurs. This should not occur. Try again. If this problem persists, report it.");
                    LOG.error(
                        "An internal JDT exception occurs. This should not occur. Try again. If this problem persists, report it.",
                        e);
                }
            } else if (firstElement instanceof IResource) {
                elementPathOrPackage = ((IResource) firstElement).getFullPath();
                targetProject = ((IResource) firstElement).getProject();
            }
            if (elementPathOrPackage != null) {
                WizardDialog wiz;
                try {
                    wiz =
                        new WizardDialog(HandlerUtil.getActiveShell(event), new GenerateCustomBatchWizard(
                            targetProject, elementPathOrPackage));
                    wiz.setPageSize(new Point(800, 500));
                    wiz.open();
                } catch (UnknownExpressionException e) {
                    LOG.error("There is an unknown expression in the configuration", e);
                } catch (UnknownContextVariableException e) {
                    LOG.error("There is an unknown context variable", e);
                } catch (GeneratorProjectNotExistentException e) {
                    LOG.error("The generator configuration project does not exist", e);
                } catch (CoreException e) {
                    LOG.error("An Eclipse internal exception occurred", e);
                } catch (IOException e) {
                    LOG.error("An exception occured while accessing or writing files", e);
                } catch (InvalidConfigurationException e) {
                    LOG.error("The Generator's configuration is invalid", e);
                }
            } else {
                MessageDialog.openError(HandlerUtil.getActiveShell(event), "Unsupported Operation",
                    "The element which has bee selected for generation is currently not supported.");
            }
        }
        return null;
    }
}
