package com.devonfw.cobigen.cli.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.InputInterpreter;
import com.devonfw.cobigen.api.constants.MavenConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.extension.ClassServiceLoader;
import com.google.common.base.Charsets;

/**
 * Utilities class for CobiGen related operations. For instance, it creates a new CobiGen instance and registers all the
 * plug-ins
 */
public class CobiGenUtils {

  /**
   * CLI home folder for pom.xml configuration
   */
  public static final String CLI_HOME = "cli-config";

  /**
   * Logger instance for the CLI
   */
  private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

  /**
   * Registers CobiGen plug-ins and instantiates CobiGen
   *
   * @param templatesProject the templates project or jar
   * @param allowMonolithicConfiguration ignores deprecated monolithic template folder structure and if found does not
   *        throw a DeprecatedMonolithicConfigurationException
   * @return object of CobiGen
   */
  public static CobiGen initializeCobiGen(Path templatesProject, boolean allowMonolithicConfiguration) {

    registerPlugins();
    CobiGen cg;
    if (templatesProject != null) {
      return CobiGenFactory.create(templatesProject.toUri(), allowMonolithicConfiguration);
    } else {
      return CobiGenFactory.create(allowMonolithicConfiguration);
    }
    return cg;
  }

  /**
   * @return the home path of the CLI
   */
  public static Path getCliHomePath() {

    return CobiGenPaths.getCobiGenHomePath().resolve(CLI_HOME);
  }

  /**
   * Registers the given different CobiGen plug-ins by building an artificial POM extracted next to the CLI location and
   * then adding the needed URLs to the class loader.
   *
   * @return the classloader created for registering plugins
   */
  public static ClassLoader registerPlugins() {

    Path rootCLIPath = getCliHomePath();
    File pomFile = extractArtificialPom();

    String pomFileHash = MavenUtil.generatePomFileHash(pomFile.toPath(), MavenUtil.determineMavenRepositoryPath());

    Path cpFile = rootCLIPath.resolve(String.format(MavenConstants.CLASSPATH_CACHE_FILE, pomFileHash));

    URLClassLoader cobigenClassLoader = MavenUtil.addURLsFromCachedClassPathsFile(cpFile, pomFile.toPath(),
        Thread.currentThread().getContextClassLoader());

    ClassServiceLoader.lookupServices(cobigenClassLoader);
    return cobigenClassLoader;

  }

  /**
   * Extracts an artificial POM which defines all the CobiGen plug-ins that are needed
   *
   * @return the extracted POM file
   */
  public static File extractArtificialPom() {

    Path cliHome = getCliHomePath();
    File pomFile = cliHome.resolve(MavenConstants.POM).toFile();
    if (!pomFile.exists()) {
      try (InputStream resourcesIs1 = CobiGenUtils.class.getResourceAsStream("/" + MavenConstants.POM);
          InputStream resourcesIs2 = CobiGenUtils.class.getClass().getResourceAsStream("/" + MavenConstants.POM)) {
        if (resourcesIs1 != null) {
          LOG.debug("Taking pom.xml from classpath");
          Files.createDirectories(pomFile.toPath().getParent());
          Files.copy(resourcesIs1, pomFile.getAbsoluteFile().toPath());
        } else if (resourcesIs2 != null) {
          LOG.debug("Taking pom.xml from system classpath");
          Files.createDirectories(pomFile.toPath().getParent());
          Files.copy(resourcesIs1, pomFile.getAbsoluteFile().toPath());
        } else {
          if (CobiGenUtils.class.getClassLoader() instanceof URLClassLoader) {
            LOG.debug("Classloader URLs:");
            Arrays.stream(((URLClassLoader) CobiGenUtils.class.getClassLoader()).getURLs())
                .forEach(url -> LOG.debug("  - {}", url));
          }
          if (CobiGenUtils.class.getClass().getClassLoader() instanceof URLClassLoader) {
            LOG.debug("System Classloader URLs:");
            Arrays.stream(((URLClassLoader) CobiGenUtils.class.getClassLoader()).getURLs())
                .forEach(url -> LOG.debug("  - {}", url));
          }
          throw new CobiGenRuntimeException("Unable to locate pom.xml on classpath");
        }
      } catch (IOException e1) {
        throw new CobiGenRuntimeException("Failed to extract CobiGen plugins pom.", e1);
      }
    }
    return pomFile;
  }

  /**
   * For Increments Returns a list that retains only the elements in this list that are contained in the specified
   * collection (optional operation). In other words, the resultant list removes from this list all of its elements that
   * are not contained in the specified collection.
   *
   * @param currentList list containing elements to be retained in this list
   * @param listToIntersect second list to be used for the intersection
   * @return resultant list containing increments that are in both lists
   */
  public static List<IncrementTo> retainAllIncrements(List<IncrementTo> currentList,
      List<IncrementTo> listToIntersect) {

    List<IncrementTo> resultingList = new ArrayList<>();

    for (IncrementTo currentIncrement : currentList) {
      String currentIncrementDesc = currentIncrement.getId() + currentIncrement.getTriggerId();
      for (IncrementTo intersectIncrement : listToIntersect) {

        String intersectIncrementDesc = intersectIncrement.getId() + intersectIncrement.getTriggerId();

        if (currentIncrementDesc.equals(intersectIncrementDesc)) {
          resultingList.add(currentIncrement);
          break;
        }
      }
    }
    return resultingList;
  }

  /**
   * For Templates Returns a list that retains only the elements in this list that are contained in the specified
   * collection (optional operation). In other words, the resultant list removes from this list all of its elements that
   * are not contained in the specified collection.
   *
   * @param currentList list containing elements to be retained in this list
   * @param listToIntersect second list to be used for the intersection
   * @return resultant list containing increments that are in both lists
   */
  public static List<TemplateTo> retainAllTemplates(List<TemplateTo> currentList, List<TemplateTo> listToIntersect) {

    List<TemplateTo> resultantList = new ArrayList<>();

    for (TemplateTo currentTemplate : currentList) {
      String currentTemplateDesc = currentTemplate.getId() + currentTemplate.getTriggerId();
      for (TemplateTo intersectTemplate : listToIntersect) {

        String intersectTemplateDesc = intersectTemplate.getId() + intersectTemplate.getTriggerId();

        if (currentTemplateDesc.equals(intersectTemplateDesc)) {
          resultantList.add(currentTemplate);
          break;
        }
      }
    }
    return resultantList;
  }

  /**
   * Processes the given file to be converted into any CobiGen valid input format
   *
   * @param file {@link File} converted into any CobiGen valid input format
   * @param cl {@link ClassLoader} to be used, when considering Java-related inputs
   * @param inputInterpreter parse cobiGen compliant input from the file
   * @throws InputReaderException if the input retrieval did not result in a valid CobiGen input
   * @return a CobiGen valid input
   */
  public static Object process(InputInterpreter inputInterpreter, Path file, ClassLoader cl)
      throws InputReaderException {

    if (!Files.exists(file) || Files.isReadable(file)) {
      throw new InputReaderException("Could not read input file " + file);
    }
    Object input = null;
    try {
      input = inputInterpreter.read(Paths.get(file.toUri()), Charsets.UTF_8, cl);
    } catch (InputReaderException e) {
      LOG.debug("No input reader was able to read file {}", file.toUri(), e);
    }
    if (input != null) {
      return input;
    }
    throw new InputReaderException("The file " + file + " is not a valid input for CobiGen.");
  }

}
