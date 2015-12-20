package com.capgemini.cobigen.eclipse.workbenchcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.services.ISourceProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.capgemini.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.capgemini.cobigen.eclipse.common.tools.JavaModelUtil;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.xmlplugin.util.XmlUtil;

/**
 * The {@link SelectionServiceListener} listens on the selections of the jdt {@link PackageExplorerPart} and
 * enables/disables the {@link SourceProvider#VALID_INPUT} system variable
 *
 * @author mbrunnli (15.02.2013)
 */
@SuppressWarnings({ "restriction" })
public class SelectionServiceListener implements ISelectionListener {

    /**
     * The {@link SourceProvider} for the system variables
     */
    private SourceProvider sp;

    /**
     * {@link CobiGen} instance
     */
    private CobiGen cobiGen;

    /**
     * Assigning logger to SelectionServiceListener
     */
    private static final Logger LOG = LoggerFactory.getLogger(SelectionServiceListener.class);

    /** {@link IResourceChangeListener} of the context configuration file */
    private ConfigurationRCL resourceChangeListener;

    /**
     * Creates a new instance of the {@link SelectionServiceListener}
     *
     * @param registerConfigurationChangedListener
     *            states if an {@link IResourceChangeListener} should be registered to track changes in the
     *            configuration files.
     * @throws CoreException
     *             if an internal eclipse exception occurred
     * @throws GeneratorProjectNotExistentException
     *             if the generation configuration folder does not exist
     * @throws InvalidConfigurationException
     *             if the configuration is invalid
     * @throws GeneratorCreationException
     *             if the generator could not be created
     * @author mbrunnli (15.02.2013)
     */
    public SelectionServiceListener(boolean registerConfigurationChangedListener)
        throws GeneratorProjectNotExistentException, CoreException, InvalidConfigurationException,
        GeneratorCreationException {

        ISourceProviderService isps =
            (ISourceProviderService) PlatformUIUtil.getActiveWorkbenchWindow().getService(
                ISourceProviderService.class);
        sp = (SourceProvider) isps.getSourceProvider(SourceProvider.VALID_INPUT);

        IProject generatorConfProj = ResourcesPluginUtil.getGeneratorConfigurationProject();
        try {
            cobiGen = new CobiGen(generatorConfProj.getLocationURI());
        } catch (IOException e) {
            throw new GeneratorCreationException("Configuration source could not be read!", e);
        }
        // TODO check if needed as every time there will be a new instance of the generator
        if (registerConfigurationChangedListener) {
            resourceChangeListener = new ConfigurationRCL(generatorConfProj, cobiGen);
            ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener,
                IResourceChangeEvent.POST_CHANGE);
            LOG.info("ResourceChangeListener for configuration files started.");
        }
    }

    /**
     * Stops the {@link IResourceChangeListener} for the configuration if
     * {@link #SelectionServiceListener(boolean)} was initialized with <code>true</code>.
     * @author mbrunnli (Jun 24, 2015)
     */
    public void stopConfigurationChangeListener() {
        if (resourceChangeListener != null) {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);
            LOG.info("ResourceChangeListener for configuration files stopped.");
        }
    }

    /**
     * {@inheritDoc} The method get's called on every click of the input file/folder. Checks whether the Code
     * generation on that file/folder is permitted or not. If not the 'Generation' is greyed. It checks for
     * both ProjectExplorer and PackageExplorer.
     * @author mbrunnli (15.02.2013), adapted by trippl (22.04.2013), adapted by sbasnet(30.10.2014)
     */
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

        if (part instanceof PackageExplorerPart || part instanceof ProjectExplorer
            && selection instanceof IStructuredSelection) {
            boolean validInput = false;
            try {
                validInput = isValidInput((IStructuredSelection) selection);
            } catch (InvalidInputException e) {
                if (e.hasRootCause()) {
                    LOG.error(e.getMessage(), e);
                } else {
                    LOG.error(e.getMessage());
                }
            } catch (Throwable e) {
                LOG.error("An unexpected exception occurred!", e);
            }
            sp.setVariable(SourceProvider.VALID_INPUT, validInput);
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }

    /**
     * Checks if the selected items are supported by one or more {@link Trigger}s, and if they are supported
     * by the same {@link Trigger}s
     *
     * @param selection
     *            the selection made
     * @return true, if all items are supported by the same trigger(s)<br>
     *         false, if they are not supported by any trigger at all
     * @throws InvalidInputException
     *             if the input could not be read as expected
     * @author trippl (22.04.2013)
     */
    public boolean isValidInput(IStructuredSelection selection) throws InvalidInputException {

        Iterator<?> it = selection.iterator();
        List<String> firstTriggers = null;

        boolean uniqueSourceSelected = false;

        while (it.hasNext()) {
            Object tmp = it.next();
            if (tmp instanceof ICompilationUnit) {
                if (firstTriggers == null) {
                    firstTriggers = findMatchingTriggers((ICompilationUnit) tmp);
                } else {
                    if (!firstTriggers.equals(findMatchingTriggers((ICompilationUnit) tmp))) {
                        throw new InvalidInputException(
                            "You selected at least two inputs, which are not matching the same triggers. "
                                + "For batch processing all inputs have to match the same triggers.");
                    }
                }
            } else if (tmp instanceof IPackageFragment) {
                uniqueSourceSelected = true;
                firstTriggers =
                    cobiGen.getMatchingTriggerIds(new PackageFolder(((IPackageFragment) tmp).getResource()
                        .getLocationURI(), ((IPackageFragment) tmp).getElementName()));
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
                    throw new InvalidInputException("The contents of the file " + ((IFile) tmp).getName()
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
        return firstTriggers != null && !firstTriggers.isEmpty();
    }

    /**
     * Returns a {@link Set} of {@link Trigger}s that support the give {@link ICompilationUnit}
     *
     * @param cu
     *            {@link ICompilationUnit} to be checked
     * @return the {@link Set} of {@link Trigger}s
     * @throws InvalidInputException
     *             if the input could not be read as expected
     * @author trippl (22.04.2013)
     */
    private List<String> findMatchingTriggers(ICompilationUnit cu) throws InvalidInputException {

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
