package com.devonfw.cobigen.eclipse.generator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.DeprecatedMonolithicConfigurationException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
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
 * Generator creation factory, which creates a specific generator instance dependent on the current selection within the
 * eclipse IDE
 *
 * @author mbrunnli (03.12.2014)
 */
public class GeneratorWrapperFactory {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(GeneratorWrapperFactory.class);

  private static IProject generatorProj = null;

  /**
   * Creates a generator dependent on the input of the selection
   *
   * @param selection current {@link IStructuredSelection} treated as input for generation
   * @param monitor tracking progress
   * @param allowMonolithicConfiguration ignores deprecated monolithic template folder structure and if found does not
   *        throw a DeprecatedMonolithicConfigurationException
   * @return a specific {@link CobiGenWrapper} instance
   * @throws GeneratorCreationException if any exception occurred during converting the inputs or creating the generator
   * @throws GeneratorProjectNotExistentException if the generator configuration project does not exist
   * @throws InvalidInputException if the selection includes non supported input types or is composed in a non supported
   *         combination of inputs.
   */
  public static CobiGenWrapper createGenerator(ISelection selection, IProgressMonitor monitor,
      boolean allowMonolithicConfiguration)
      throws GeneratorCreationException, GeneratorProjectNotExistentException, InvalidInputException {

    List<Object> extractedInputs = extractValidEclipseInputs(selection);
    if (extractedInputs.size() > 0) {
      monitor.subTask("Initialize CobiGen instance");
      CobiGen cobigen = initializeGenerator(allowMonolithicConfiguration);

      monitor.subTask("Reading inputs...");
      monitor.worked(10);
      Object firstElement = extractedInputs.get(0);

      if (firstElement instanceof IJavaElement) {
        LOG.info("Create new CobiGen instance for java inputs...");
        return new JavaInputGeneratorWrapper(cobigen, ((IJavaElement) firstElement).getJavaProject().getProject(),
            JavaInputConverter.convertInput(extractedInputs, cobigen), monitor);
      } else if (firstElement instanceof IResource) {
        LOG.info("Create new CobiGen instance for file inputs...");
        return new FileInputGeneratorWrapper(cobigen, ((IResource) firstElement).getProject(),
            FileInputConverter.convertInput(cobigen, extractedInputs), monitor);
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
   *
   * @param selection current {@link IStructuredSelection selection} of within the IDE
   * @return the {@link List} of selected objects, whereas all elements of the list are of the same content type
   * @throws InvalidInputException if the selection includes non supported input types or is composed in a non supported
   *         combination of inputs.
   */
  private static List<Object> extractValidEclipseInputs(ISelection selection) throws InvalidInputException {

    LOG.info("Start extraction of valid inputs from selection...");
    int type = 0;
    boolean initialized = false;
    List<Object> inputObjects = Lists.newLinkedList();
    IJavaElement iJavaElem = null;

    // When the user is selecting text from the text editor
    if (selection instanceof ITextSelection) {

      IEditorInput iEditorInput = PlatformUIUtil.getActiveWorkbenchPage().getActiveEditor().getEditorInput();

      if (iEditorInput instanceof FileEditorInput) {

        IFile iFile = ((FileEditorInput) iEditorInput).getFile();
        iJavaElem = JavaCore.create(iFile);
        if (iJavaElem instanceof ICompilationUnit) {
          inputObjects.add(iJavaElem);
        } else {
          inputObjects.add(iFile);
        }
      }
    }

    /*
     * Collect selected objects and check whether all selected objects are of the same type
     */
    else if (selection instanceof IStructuredSelection) {
      IStructuredSelection structuredSelection = (IStructuredSelection) selection;
      Iterator<?> it = structuredSelection.iterator();
      while (it.hasNext()) {
        Object o = it.next();
        switch (type) {
          case 0:
            if (o instanceof ICompilationUnit) {
              inputObjects.add(o);
              initialized = true;
            } else if (initialized) {
              throw new InvalidInputException("Multiple different inputs have been selected of the following types: "
                  + ICompilationUnit.class + ", " + o.getClass());
            }
            if (initialized) {
              type = 0;
              break;
            }
            //$FALL-THROUGH$
          case 1:
            if (o instanceof IPackageFragment) {
              inputObjects.add(o);
              initialized = true;
            } else if (initialized) {
              throw new InvalidInputException("Multiple different inputs have been selected of the following types: "
                  + IPackageFragment.class + ", " + o.getClass());
            }
            if (initialized) {
              type = 1;
              break;
            }
            //$FALL-THROUGH$
          case 2:
            if (o instanceof IFile) {
              inputObjects.add(o);
              initialized = true;
            } else if (initialized) {
              throw new InvalidInputException("Multiple different inputs have been selected of the following types: "
                  + IFile.class + ", " + o.getClass());
            }
            if (initialized) {
              type = 2;
              break;
            }
            //$FALL-THROUGH$
          case 3:
            if (o instanceof IResource) {
              inputObjects.add(o);
              initialized = true;
            } else if (initialized) {
              throw new InvalidInputException("Multiple different inputs have been selected of the following types: "
                  + IFile.class + ", " + o.getClass());
            }
            if (initialized) {
              type = 3;
              break;
            }
            //$FALL-THROUGH$
          default:
            throw new InvalidInputException("Your selection contains an object of the type " + o.getClass().toString()
                + ", which is not yet supported to be treated as an input for generation.\n"
                + "Please adjust your selection to only contain supported objects like Java classes/packages or XML files.");
        }
      }
    }

    LOG.info("Finished extraction of inputs from selection successfully.");
    return inputObjects;
  }

  /**
   * Initializes the {@link CobiGen} with the correct configuration
   *
   * @return the configured{@link CobiGen}
   * @throws GeneratorProjectNotExistentException if the generator configuration folder does not exist
   * @throws InvalidConfigurationException if the context configuration is not valid
   * @throws GeneratorCreationException if the generator configuration project does not exist
   */
  private static CobiGen initializeGenerator(boolean allowMonolithicConfiguration)
      throws InvalidConfigurationException, GeneratorCreationException {

    try {
      ResourcesPluginUtil.refreshConfigurationProject();
      generatorProj = ResourcesPluginUtil.getGeneratorConfigurationProject();

      initializeCobiGen(generatorProj, allowMonolithicConfiguration);

      // we need to check if templates got upgraded then initialize cobigen again. Now with the new template-set
      // structure
      if (ResourcesPluginUtil.templatesUpgraded)
        return initializeCobiGen(null, true);

      return initializeCobiGen(generatorProj, true);
    } catch (CoreException e) {
      throw new GeneratorCreationException("An eclipse internal exception occurred", e);
    } catch (DeprecatedMonolithicConfigurationException e) {
      if (null == generatorProj.getLocationURI())
        throw e;
      else {
        throw new DeprecatedMonolithicConfigurationException(Paths.get(generatorProj.getLocationURI()));
      }
    } catch (Throwable e) {
      throw new GeneratorCreationException(
          "Configuration source could not be read.\nIf you were updating templates, it may mean"
              + " that you have no internet connection,",
          e);
    }
  }

  /**
   *
   * @param generatorProj the templates project
   * @param allowMonolithicConfiguration ignores deprecated monolithic template folder structure and if found does not
   *        throw a DeprecatedMonolithicConfigurationException
   * @return the configured{@link CobiGen}
   * @throws if the generator configuration project does not exist
   *
   */
  private static CobiGen initializeCobiGen(IProject generatorProj, boolean allowMonolithicConfiguration)
      throws GeneratorCreationException {

    Path templatesDirectoryPath = CobiGenPaths.getTemplateSetsFolderPath();
    Path templateSetsAdaptedFolderPath = templatesDirectoryPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
    Path templateSetsDownloadedFolderPath = templatesDirectoryPath.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);

    if (!ResourcesPluginUtil.generatorProjExists || ResourcesPluginUtil.templatesUpgraded
        || generatorProj.getLocationURI() == null) {

      /*
       * After the upgrade the new template-sets will be used. Only once! Then the old configuration has to be deleted
       * or the upgrader will start again. This time the upgrader cannot start again because template-sets already
       * exists.
       */
      ResourcesPluginUtil.templatesUpgraded = false;
      // check adapted and downloaded folder
      if (Files.exists(templateSetsAdaptedFolderPath) || Files.exists(templateSetsDownloadedFolderPath)) {
        return CobiGenFactory.create(templatesDirectoryPath.toUri(), allowMonolithicConfiguration);
      }
      templatesDirectoryPath = CobiGenPaths.getTemplatesFolderPath();
      if (Files.exists(templatesDirectoryPath)) {
        // If it is not valid, we should use the jar
        Path jarPath = TemplatesJarUtil.getJarFile(false, templatesDirectoryPath);
        boolean fileExists = (jarPath != null && Files.exists(jarPath));
        if (!fileExists) {
          MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
              "Not Downloaded the CobiGen Template Jar");
        }
        return CobiGenFactory.create(jarPath.toUri(), allowMonolithicConfiguration);
      }
    }
    if (generatorProj.getLocationURI() == null) {
      throw new GeneratorCreationException(
          "Configuration source could not be read. Have you downloaded the templates?");
    } else {
      return CobiGenFactory.create(generatorProj.getLocationURI(), allowMonolithicConfiguration);
    }
  }

}
