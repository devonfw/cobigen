package com.devonfw.cobigen.cli.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.TemplateAdapter;
import com.devonfw.cobigen.api.exception.TemplateSelectionForAdaptionException;
import com.devonfw.cobigen.api.exception.UpgradeTemplatesNotificationException;
import com.devonfw.cobigen.api.util.mavencoordinate.MavenCoordinateState;
import com.devonfw.cobigen.api.util.mavencoordinate.MavenCoordinateStatePair;
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

  private final int HUMAN_READABLE = 1;

  /**
   * Logger to output useful information to the user
   */
  private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

  /**
   * If this options is enabled, all templates are extracted.
   */
  @Option(names = { "--all" }, description = MessagesConstants.ADAPT_ALL_DESCRIPTION)
  boolean adaptAll;

  /**
   * Allows usage of the old monolithic template structure instead of the new template sets structure.
   */
  @Option(names = { "--force-monolithic-configuration",
  "--force-mc" }, description = MessagesConstants.FORCE_MONOLITHIC_CONFIGURATION)
  boolean forceMonolithicConfiguration;

  @Override
  public Integer doAction() throws Exception {

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(this.templatesProject);

    try {
      templateAdapter.adaptTemplates();
    } catch (UpgradeTemplatesNotificationException e) {
      if (!this.forceMonolithicConfiguration && askUserToContinueWithUpgrade(e)) {
        templateAdapter.upgradeMonolithicTemplates(this.templatesProject);
      }
    } catch (TemplateSelectionForAdaptionException e) {
      List<MavenCoordinateStatePair> templateSetMavenCoordinatePairs = e.getTemplateSetMavenCoordinateStatePairs();
      if (templateSetMavenCoordinatePairs != null && !templateSetMavenCoordinatePairs.isEmpty()) {
        getJarsToAdapt(templateAdapter, templateSetMavenCoordinatePairs);
        if (!templateSetMavenCoordinatePairs.isEmpty()) {
          templateAdapter.adaptTemplateSets(templateSetMavenCoordinatePairs, false);
        }
      } else {
        LOG.info("No template set jars found to extract.");
      }
    }

    return 0;
  }

  /**
   * Gives the user a selection of available template set jars to adapt. The users selection will reflect in the parsed
   * {@link java.util.List List} of {@link MavenCoordinateStatePair MavenCoordinateStatePairs} by a side affect of this
   * method.
   *
   * @param templateAdapter the scope of the current adapt process
   * @param templateSetMavenCoordinateStatePairs A {@link java.util.List List} of {@link MavenCoordinateStatePair
   *        MavenCoordinateStatePairs} with all available template set jars.
   */
  private void getJarsToAdapt(TemplateAdapter templateAdapter,
      List<MavenCoordinateStatePair> templateSetMavenCoordinateStatePairs) {

    if (templateSetMavenCoordinateStatePairs != null && templateSetMavenCoordinateStatePairs.size() > 0) {
      List<MavenCoordinateState> listViewOfPairs = templateSetMavenCoordinateStatePairs.stream()
          .flatMap(pair -> Stream.of(pair.getValue0(), pair.getValue1())).collect(Collectors.toList());
      printJarsForSelection(templateAdapter, templateSetMavenCoordinateStatePairs, listViewOfPairs);

      List<String> userSelection = new ArrayList<>();

      if (this.adaptAll) {
        userSelection.add("0");
      } else {
        for (String templateSelection : ValidationUtils.getUserInput().split(",")) {
          userSelection.add(templateSelection);
        }
      }

      if (userSelection.contains("0")) {
        listViewOfPairs.forEach(mvnCoordState -> {
          mvnCoordState.setToBeAdapted(true);
        });
      } else {
        for (String jarSelected : userSelection) {
          listViewOfPairs.get(Integer.parseInt(jarSelected) - this.HUMAN_READABLE).setToBeAdapted(true);
        }
      }
    }

  }

  /**
   * Prints the available template set jars
   *
   * @param templateAdapter the scope of the current adapt process
   * @param templateSetMavenCoordinatePairs {@link java.util.List List} of {@link MavenCoordinateStatePair
   *        MavenCoordinateStatePairs}
   * @param listViewOfPairs A {@link java.util.List List} view of {@code templateSetMavenCoordinatePairs}
   */
  private void printJarsForSelection(TemplateAdapter templateAdapter,
      List<MavenCoordinateStatePair> templateSetMavenCoordinatePairs, List<MavenCoordinateState> listViewOfPairs) {

    LOG.info("(0) " + "All");

    templateSetMavenCoordinatePairs.forEach(pair -> {

      MavenCoordinateState nonSourcesMember = pair.getValue0();
      MavenCoordinateState sourcesMember = pair.getValue1();
      int nonSourcesMemberIdx = listViewOfPairs.indexOf(nonSourcesMember) + this.HUMAN_READABLE;
      int sourcesMemberIdx = listViewOfPairs.indexOf(sourcesMember) + this.HUMAN_READABLE;

      LOG.info("(" + nonSourcesMemberIdx + ") " + nonSourcesMember.getRealDirectoryName()
          + (templateAdapter.isTemplateSetAlreadyAdapted(nonSourcesMember) ? " (already adapted)" : ""));
      LOG.info("(" + sourcesMemberIdx + ") " + sourcesMember.getRealDirectoryName()
          + (templateAdapter.isTemplateSetAlreadyAdapted(sourcesMember) ? " (already adapted)" : ""));
    });
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
