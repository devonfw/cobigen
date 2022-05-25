package com.devonfw.cobigen.cli.commands;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.TemplateAdapter;
import com.devonfw.cobigen.api.exception.TemplateSelectionForAdaptionException;
import com.devonfw.cobigen.api.exception.UpgradeTemplatesNotificationException;
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
   * Logger to output useful information to the user
   */
  private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

  /**
   * If this options is enabled, all templates are extracted.
   */
  @Option(names = { "--all" }, negatable = true, description = MessagesConstants.ADAPT_ALL_DESCRIPTION)
  boolean adaptAll;

  @Override
  public Integer doAction() throws Exception {

    TemplateAdapter templateAdapter;
    if (this.templatesProject == null) {
      templateAdapter = new TemplateAdapterImpl();
    } else {
      templateAdapter = new TemplateAdapterImpl(this.templatesProject);
    }

    try {
      templateAdapter.adaptTemplates();
    } catch (UpgradeTemplatesNotificationException e) {
      if (askUserToContinueWithUpgrade(e)) {
        templateAdapter.upgradeMonolithicTemplates();
      }
    } catch (TemplateSelectionForAdaptionException e) {
      List<Path> templateJars = e.getTemplateSets();
      if (templateJars != null && !templateJars.isEmpty()) {
        List<Path> templateJarsToAdapt = getJarsToAdapt(templateAdapter, templateJars);
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
   * Gives the user a selection of available template set jars to adapt.
   *
   * @param templateJars A {@link List} with all available template set jars.
   * @return A {@link List} with the template set jars selected by the user to adapt.
   */
  private List<Path> getJarsToAdapt(TemplateAdapter templateAdapter, List<Path> templateJars) {

    List<Path> jarsToAdapt = new ArrayList<>();
    if (templateJars != null && templateJars.size() > 0) {
      printJarsForSelection(templateAdapter, templateJars);

      List<String> userSelection = new ArrayList<>();

      if (this.adaptAll) {
        userSelection.add("0");
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
  private void printJarsForSelection(TemplateAdapter templateAdapter, List<Path> templateSetJarPaths) {

    LOG.info("(0) " + "All");
    for (Path templateSetJarPath : templateSetJarPaths) {
      LOG.info("(" + (templateSetJarPaths.indexOf(templateSetJarPath) + 1) + ") "
          + templateSetJarPath.getFileName().toString().replace(".jar", "")
          + (templateAdapter.isTemplateSetAlreadyAdapted(templateSetJarPath) ? " (already adapted)" : ""));
    }
    LOG.info("Please enter the number(s) of jar(s) that you want to adapt separated by comma.");
  }

  /**
   * Ask the user to continue with the upgrade of the templates.
   *
   * @return Returns {@code true} if the user want to continue with the uprade of the templates.
   */
  private boolean askUserToContinueWithUpgrade(UpgradeTemplatesNotificationException e) {

    LOG.info(e.getMessage());
    LOG.info("Type 'y' or 'yes' to upgrade the configuration?");
    String userInput = ValidationUtils.getUserInput();
    if (userInput != null && (userInput.toLowerCase().equals("y") || userInput.toLowerCase().equals("yes"))) {
      return true;
    }
    return false;
  }
}
