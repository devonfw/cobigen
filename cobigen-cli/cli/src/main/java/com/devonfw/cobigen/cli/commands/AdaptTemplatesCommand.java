package com.devonfw.cobigen.cli.commands;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.TemplateAdapter;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.utils.ValidationUtils;
import com.devonfw.cobigen.impl.adapter.TemplateAdapterImpl;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * This class handles the user defined template directory e.g. determining and obtaining the latest templates jar,
 * unpacking the sources and compiled classes and storing the custom location path in a configuration file
 */
@Command(description = MessagesConstants.ADAPT_TEMPLATES_DESCRIPTION, name = "adapt-templates", aliases = {
"a" }, mixinStandardHelpOptions = true)
public class AdaptTemplatesCommand extends CommandCommons {

  /**
   * List of template sets to adapt
   */
  @Option(names = { "--template-sets",
  "-ts" }, split = ",", description = MessagesConstants.TEMPLATE_SETS_OPTION_DESCRIPTION)
  List<String> templateSets = null;

  /**
   * Logger to output useful information to the user
   */
  private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

  @Override
  public Integer doAction() throws Exception {

    TemplateAdapter templateAdapter;
    if (this.templatesProject == null) {
      templateAdapter = new TemplateAdapterImpl();
    } else {
      templateAdapter = new TemplateAdapterImpl(this.templatesProject);
    }

    if (templateAdapter.isMonolithicTemplatesConfiguration()) {
      Path destinationPath = templateAdapter.getTemplatesLocation().resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
      templateAdapter.adaptMonolithicTemplates(destinationPath, false);
    } else {
      List<Path> templateJars = templateAdapter.getTemplateSetJarPaths();
      if (templateJars != null && !templateJars.isEmpty()) {
        List<Path> templateJarsToAdapt = getJarsToAdapt(templateJars);
        if (!templateJarsToAdapt.isEmpty()) {
          templateAdapter.adaptTemplateSets(templateJarsToAdapt, false);
        }
      } else {
        LOG.info("No template set jars found to extract.");
      }
    }

    return 0;
  }

  /**
   *
   * @param templateJars
   * @return
   */
  private List<Path> getJarsToAdapt(List<Path> templateJars) {

    List<Path> jarsToAdapt = new ArrayList<>();
    if (templateJars != null && templateJars.size() > 0) {
      printJarsForSelection(templateJars);

      List<String> userSelection = new ArrayList<>();
      if (this.templateSets != null) {
        userSelection = this.templateSets;
      } else {
        for (String templateSelection : ValidationUtils.getUserInput().split(",")) {
          userSelection.add(templateSelection);
        }
      }

      if (userSelection.contains("0")) {
        jarsToAdapt = templateJars;
      } else {
        for (String jarSelected : userSelection) {
          jarsToAdapt.add(templateJars.get(Integer.parseInt(jarSelected) - 1));
        }
      }
    }

    return jarsToAdapt;
  }

  /**
   * Prints the available template set jars
   *
   * @param templateSetJarPaths List of {@link Path} to available template jar files
   */
  private void printJarsForSelection(List<Path> templateSetJarPaths) {

    LOG.info("(0) " + "All");
    for (Path templateSetJarPath : templateSetJarPaths) {
      LOG.info("(" + (templateSetJarPaths.indexOf(templateSetJarPath) + 1) + ") "
          + templateSetJarPath.getFileName().toString().replace(".jar", ""));
    }
    LOG.info("Please enter the number(s) of jar(s) that you want to adapt separated by comma.");
  }
}
