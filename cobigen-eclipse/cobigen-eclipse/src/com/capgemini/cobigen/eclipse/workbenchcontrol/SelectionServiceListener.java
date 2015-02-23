package com.capgemini.cobigen.eclipse.workbenchcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.services.ISourceProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.eclipse.common.constants.ConfigResources;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.capgemini.cobigen.eclipse.common.tools.JavaModelUtil;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.xmlplugin.util.XmlUtil;
import com.google.common.collect.Lists;

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

    /**
     * Creates a new instance of the {@link SelectionServiceListener}
     *
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
    public SelectionServiceListener() throws GeneratorProjectNotExistentException, CoreException,
        InvalidConfigurationException, GeneratorCreationException {

        ISourceProviderService isps =
            (ISourceProviderService) PlatformUIUtil.getActiveWorkbenchWindow().getService(
                ISourceProviderService.class);
        sp = (SourceProvider) isps.getSourceProvider(SourceProvider.VALID_INPUT);

        IProject generatorConfProj = ConfigResources.getGeneratorConfigurationProject();
        try {
            cobiGen = new CobiGen(generatorConfProj.getLocationURI());
        } catch (IOException e) {
            LOG.error("Configuration source could not be read", e);
            throw new GeneratorCreationException("Configuration source could not be read", e);
        }
        // TODO check if needed as every time there will be a new instance of the generator
        ResourcesPlugin.getWorkspace().addResourceChangeListener(
            new ConfigurationRCL(generatorConfProj, cobiGen), IResourceChangeEvent.POST_CHANGE);
    }

    /**
     * {@inheritDoc} The method get's called on every click of the input file/folder. Checks whether the Code
     * generation on that file/folder is permitted or not. If not the 'Generation' is greyed. It checks for
     * both ProjectExplorer and PackageExplorer.
     * @author mbrunnli (15.02.2013), adapted by trippl (22.04.2013), adapted by sbasnet(30.10.2014)
     */
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (part instanceof PackageExplorerPart || part instanceof ProjectExplorer
            && selection instanceof IStructuredSelection) {
            if (isValidInput((IStructuredSelection) selection)) {
                sp.setVariable(SourceProvider.VALID_INPUT, true);
            } else {
                sp.setVariable(SourceProvider.VALID_INPUT, false);
            }
        }
    }

    /**
     * Checks if the selected items are supported by one or more {@link Trigger}s, and if they are supported
     * by the same {@link Trigger}s
     *
     * @param selection
     *            the selection made
     * @return true, if all items are supported by the same trigger(s)<br>
     *         false, if they are not supported by any trigger at all, or the triggers are not the same
     * @author trippl (22.04.2013)
     */
    private boolean isValidInput(IStructuredSelection selection) {

        Iterator<?> it = selection.iterator();
        List<String> firstTriggers = null;

        boolean uniqueSourceSelected = false;

        while (it.hasNext()) {
            Object tmp = it.next();
            if (uniqueSourceSelected) {
                // Currently it is only possible to select one IPackageFragment or IFile
                return false;
            } else if (tmp instanceof ICompilationUnit) {
                if (firstTriggers == null) {
                    firstTriggers = findMatchingTriggers((ICompilationUnit) tmp);
                } else {
                    if (!firstTriggers.equals(findMatchingTriggers((ICompilationUnit) tmp))) {
                        return false;
                    }
                }
            } else if (tmp instanceof IPackageFragment) {
                uniqueSourceSelected = true;
                if (firstTriggers == null) {
                    firstTriggers =
                        cobiGen.getMatchingTriggerIds(new PackageFolder(((IPackageFragment) tmp)
                            .getResource().getLocationURI(), ((IPackageFragment) tmp).getElementName()));
                } else {
                    // It is only possible to select one IPackageFragment
                    return false;
                }
            } else if (tmp instanceof IFile) {
                uniqueSourceSelected = true;
                if (firstTriggers == null) {
                    InputStream stream;
                    try {
                        stream = ((IFile) tmp).getContents();
                        Document domDocument = XmlUtil.parseXmlStreamToDom(stream);
                        firstTriggers = cobiGen.getMatchingTriggerIds(domDocument);
                    } catch (CoreException e) {
                        LOG.error("An eclipse internal exception occured", e);
                    } catch (IOException e) {
                        LOG.error("The file {} could not be read.", ((IFile) tmp).getName(), e);
                    } catch (ParserConfigurationException e) {
                        LOG.error(
                            "The file {} could not be parsed, because of an internal configuration error.",
                            ((IFile) tmp).getName(), e);
                    } catch (SAXException e) {
                        LOG.warn(
                            "Checking for valid input: The file {} could not be parsed, because it is not a valid xml document",
                            ((IFile) tmp).getName());
                    }
                } else {
                    // It is only possible to select one file
                    return false;
                }
            } else {
                return false;
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
     * @author trippl (22.04.2013)
     */
    private List<String> findMatchingTriggers(ICompilationUnit cu) {

        ClassLoader classLoader;
        IType type = null;
        try {
            classLoader = ClassLoaderUtil.getProjectClassLoader(cu.getJavaProject());
            type = JavaModelUtil.getJavaClassType(cu);
            return cobiGen.getMatchingTriggerIds(classLoader.loadClass(type.getFullyQualifiedName()));
        } catch (MalformedURLException e) {
            LOG.error("Error while retrieving the project's ('{}') classloader", cu.getJavaProject()
                .getElementName(), e);
        } catch (CoreException e) {
            LOG.error("An eclipse internal exception occured", e);
        } catch (ClassNotFoundException e) {
            LOG.error("The class '{}' could not be found.", type.getFullyQualifiedName(), e);
        } catch (UnsupportedClassVersionError e) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {

                    MessageDialog
                        .openError(
                            Display.getDefault().getActiveShell(),
                            "Incompatible Java version",
                            "You have selected a java class, which Java version is higher than your current Java runtime, you are running eclipse with.\n"
                                + "Please update your PATH variable to hold the latest Java runtime you are developing for and restart eclipse.");
                }
            });
            LOG.error("Incompatible java version. Current runtime: {}", System.getProperty("java.version"), e);
        }
        return Lists.newLinkedList();
    }
}
