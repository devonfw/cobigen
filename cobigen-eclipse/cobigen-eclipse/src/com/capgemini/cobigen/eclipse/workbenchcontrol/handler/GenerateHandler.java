package com.capgemini.cobigen.eclipse.workbenchcontrol.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.MDC;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.constants.ResourceConstants;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.capgemini.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.capgemini.cobigen.eclipse.common.tools.JavaModelUtil;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.eclipse.generator.GeneratorWrapperFactory;
import com.capgemini.cobigen.eclipse.wizard.generate.GenerateBatchWizard;
import com.capgemini.cobigen.eclipse.wizard.generate.GenerateWizard;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.exceptions.UnknownTemplateException;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.xmlplugin.util.XmlUtil;

/**
 * Handler for the Package-Explorer Event
 * @author mbrunnli (13.02.2013)
 */
public class GenerateHandler extends AbstractHandler {

    /**
     * Assigning logger to GenerateHandler
     */
    private static final Logger LOG = LoggerFactory.getLogger(GenerateHandler.class);

    /**
     * {@inheritDoc}
     * @author mbrunnli (13.02.2013), updated by sholzer (22.09.2015)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID());
        LOG.debug("on click Event? " + event.getClass().getName());

        ISelection sel = HandlerUtil.getCurrentSelection(event);

        if (sel instanceof ITreeSelection) {

            // when this handler is executed, we should we should be sure, that the selection is currently
            // supported by the following implementation

            try {
                checkValidSelection(sel);
                CobiGenWrapper generator =
                    GeneratorWrapperFactory.createGenerator((IStructuredSelection) sel);
                if (generator == null) {
                    MessageDialog.openError(HandlerUtil.getActiveShell(event), "Not yet supported!",
                        "The selection is currently not supported as valid input.");
                    return null;
                }

                if (((IStructuredSelection) sel).size() > 1 || (((IStructuredSelection) sel).size() == 1)
                    && ((IStructuredSelection) sel).getFirstElement() instanceof IPackageFragment) {
                    WizardDialog wiz =
                        new WizardDialog(HandlerUtil.getActiveShell(event),
                            new GenerateBatchWizard(generator));
                    wiz.setPageSize(new Point(800, 500));
                    wiz.open();
                } else if (((IStructuredSelection) sel).size() == 1) {
                    WizardDialog wiz =
                        new WizardDialog(HandlerUtil.getActiveShell(event), new GenerateWizard(generator));
                    wiz.setPageSize(new Point(800, 500));
                    wiz.open();
                }

            } catch (UnknownContextVariableException e) {
                PlatformUIUtil.openErrorDialog("Error", "Unknown Context Variable: " + e.getMessage(), e);
                LOG.error("Unknown Context Variable", e);
            } catch (UnknownTemplateException e) {
                PlatformUIUtil.openErrorDialog("Error", "Unknown Template: " + e.getMessage(), e);
                LOG.error("Unknown Template", e);
            } catch (UnknownExpressionException e) {
                PlatformUIUtil.openErrorDialog("Error", "Unknown Expression: " + e.getMessage(), e);
                LOG.error("Unknown Expression", e);
            } catch (InvalidConfigurationException e) {
                PlatformUIUtil.openErrorDialog("Error", "Invalid Configuration: " + e.getMessage(), e);
                LOG.error("Invalid Configuration", e);
            } catch (GeneratorProjectNotExistentException e) {
                MessageDialog
                    .openError(
                        HandlerUtil.getActiveShell(event),
                        "Generator configuration project not found!",
                        "The project '"
                            + ResourceConstants.CONFIG_PROJECT_NAME
                            + "' containing the configuration and templates is currently not existent. Please create one or check it out from SVN as stated in the user documentation.");
                LOG.error(
                    "The project '{}' containing the configuration and templates is currently not existent. Please create one or check it out from SVN as stated in the user documentation.",
                    ResourceConstants.CONFIG_PROJECT_NAME, e);
            } catch (GeneratorCreationException e) {
                PlatformUIUtil.openErrorDialog("Error", "Could not create an instance of the generator!", e);
                LOG.error("Could not create an instance of the generator.", e);
            } catch (InvalidInputException e) {
                PlatformUIUtil.openErrorDialog("Error", "Invalid selection: " + e.getMessage(), e);
                LOG.error("Invalid Configuration", e);
            } catch (Throwable e) {
                PlatformUIUtil.openErrorDialog("Error", "An unexpected exception occurred!", e);
                LOG.error("An unexpected exception occurred!", e);
            }
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return null;
    }

    /**
     * Checks if a selection is a valid selection. Adapted from
     * {@link com.capgemini.cobigen.eclipse.workbenchcontrol.SelectionServiceListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, ISelection)}
     * @param sel
     *            the selection to be validated
     * @author sholzer (Sep 23, 2015)
     * @throws InvalidInputException
     *             if the selection can't be validated
     * @throws GeneratorCreationException
     *             if the used CobigenWrapper couldn't be instantiated
     * @throws GeneratorProjectNotExistentException
     *             if the used CobigenWrapper couldn't be instantiated
     */
    public void checkValidSelection(ISelection sel) throws InvalidInputException,
        GeneratorProjectNotExistentException, GeneratorCreationException {
        if (sel instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) sel;
            CobiGenWrapper cobiGen = GeneratorWrapperFactory.createGenerator(selection);
            Iterator<?> it = selection.iterator();
            List<String> firstTriggers = null;

            boolean uniqueSourceSelected = false;

            while (it.hasNext()) {
                Object tmp = it.next();
                if (tmp instanceof ICompilationUnit) {
                    if (firstTriggers == null) {
                        firstTriggers = findMatchingTriggers((ICompilationUnit) tmp, cobiGen);
                    } else {
                        if (!firstTriggers.equals(findMatchingTriggers((ICompilationUnit) tmp, cobiGen))) {
                            throw new InvalidInputException(
                                "You selected at least two inputs, which are not matching the same triggers. "
                                    + "For batch processing all inputs have to match the same triggers.");
                        }
                    }
                } else if (tmp instanceof IPackageFragment) {
                    uniqueSourceSelected = true;
                    firstTriggers =
                        cobiGen.getMatchingTriggerIds(new PackageFolder(((IPackageFragment) tmp)
                            .getResource().getLocationURI(), ((IPackageFragment) tmp).getElementName()));
                } else if (tmp instanceof IFile) {
                    uniqueSourceSelected = true;
                    try (InputStream stream = ((IFile) tmp).getContents()) {
                        LOG.debug("Try parsing file {} as xml...", ((IFile) tmp).getName());
                        Document domDocument = XmlUtil.parseXmlStreamToDom(stream);
                        firstTriggers = cobiGen.getMatchingTriggerIds(domDocument);
                    } catch (CoreException e) {
                        throw new InvalidInputException("An eclipse internal exception occured! ", e);
                    } catch (IOException e) {
                        throw new InvalidInputException("The file " + ((IFile) tmp).getName()
                            + " could not be read!", e);
                    } catch (ParserConfigurationException e) {
                        throw new InvalidInputException("The file " + ((IFile) tmp).getName()
                            + " could not be parsed, because of an internal configuration error!", e);
                    } catch (SAXException e) {
                        throw new InvalidInputException(
                            "The contents of the file "
                                + ((IFile) tmp).getName()
                                + " could not be detected as an instance of any CobiGen supported input language.");
                    }
                } else {
                    throw new InvalidInputException(
                        "You selected at least one input, which type is currently not supported as input for generation. "
                            + "Please choose a different one or read the CobiGen documentation for more details.");
                }

                if (uniqueSourceSelected && selection.size() > 1) {
                    throw new InvalidInputException(
                        "You selected at least one input in a mass-selection,"
                            + " which type is currently not supported for batch processing. "
                            + "Please just select multiple inputs only if batch processing is supported for all inputs.");
                }
            }
            if (firstTriggers == null || firstTriggers.isEmpty()) {
                throw new InvalidInputException("Could not find matching triggers for the current selection");
            }
        } else {
            throw new InvalidInputException(
                "The current selection is not an instance of IStructuredSelection");
        }
    }

    /**
     * adapted from {@link com.capgemini.cobigen.eclipse.workbenchcontrol.SelectionServiceListener}
     * @param cu
     *            {@link ICompilationUnit} to be checked
     * @param cobiGen
     *            the CobigenWrapper to be used for test generation
     * @return the {@link Set} of {@link Trigger}s
     * @throws InvalidInputException
     *             if the input could not be read as expected
     * @author trippl (22.04.2013), adapted by sholzer (28.09.2015)
     */
    private List<String> findMatchingTriggers(ICompilationUnit cu, CobiGenWrapper cobiGen)
        throws InvalidInputException {

        ClassLoader classLoader;
        IType type = null;
        try {
            classLoader = ClassLoaderUtil.getProjectClassLoader(cu.getJavaProject());
            type = JavaModelUtil.getJavaClassType(cu);
            return cobiGen.getMatchingTriggerIds(classLoader.loadClass(type.getFullyQualifiedName()));
        } catch (MalformedURLException e) {
            throw new InvalidInputException("Error while retrieving the project's ('"
                + cu.getJavaProject().getElementName() + "') classloader.", e);
        } catch (CoreException e) {
            throw new InvalidInputException("An eclipse internal exception occured!", e);
        } catch (ClassNotFoundException e) {
            throw new InvalidInputException("The class '" + type.getFullyQualifiedName()
                + "' could not be found. "
                + "This may be cause of a non-compiling host project of the selected input.", e);
        } catch (UnsupportedClassVersionError e) {
            throw new InvalidInputException(
                "Incompatible java version: "
                    + "You have selected a java class, which Java version is higher than the Java runtime your eclipse is running with. "
                    + "Please update your PATH variable to reference the latest Java runtime you are developing for and restart eclipse.\n"
                    + "Current runtime: " + System.getProperty("java.version"), e);
        }
    }

}
