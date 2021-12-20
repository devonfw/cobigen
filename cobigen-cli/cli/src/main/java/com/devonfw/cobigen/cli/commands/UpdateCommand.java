package com.devonfw.cobigen.cli.commands;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;
import com.devonfw.cobigen.cli.utils.PluginUpdateUtil;
import com.devonfw.cobigen.cli.utils.ValidationUtils;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * This class handles the update command
 */
@Command(description = MessagesConstants.UPDATE_OPTION_DESCRIPTION, name = "update", aliases = {
"u" }, mixinStandardHelpOptions = true)
public class UpdateCommand extends CommandCommons {
  /**
   * Logger to output useful information to the user
   */
  private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

  /** Pattern to match versions for updating maven dependencies */
  private static final Pattern VERSION_PATTERN = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+)(-[a-zA-Z]+)?");

  /**
   * If this options is enabled, all plugins get updated.
   */
  @Option(names = { "--all" }, negatable = true, description = MessagesConstants.UPDATE_ALL_DESCRIPTION)
  boolean updateAll;

  @Override
  public Integer doAction() throws Exception {

    File pomFile = CobiGenUtils.extractArtificialPom();
    HashMap<String, String> updatePluginVersions = new HashMap<>();
    HashMap<Integer, String> listOfArtifacts = new HashMap<>();
    List<String> centralMavenVersionList = new ArrayList<>();
    ArrayList<String> userInputPluginForUpdate = new ArrayList<>();

    if (pomFile.exists()) {
      MavenXpp3Reader reader = new MavenXpp3Reader();
      try (FileReader fr = new FileReader(pomFile)) {
        Model model = reader.read(fr);
        List<Dependency> localPomDependencies = model.getDependencies();
        boolean isAllUpdated = printOutdatedPlugin(localPomDependencies, centralMavenVersionList, updatePluginVersions,
            listOfArtifacts);

        if (isAllUpdated) {
          return 0;
        }
        if (this.updateAll) {
          userInputPluginForUpdate.add("0");
          LOG.info("(0) ALL is selected!");
        } else {
          // User selects which dependencies to update
          userInputPluginSelection(userInputPluginForUpdate);
          if (userInputPluginForUpdate.size() == 1) {
            if (!StringUtils.isNumeric(userInputPluginForUpdate.get(0))) {
              LOG.info("Nothing selected to update...");
              return 0;
            }
          }
        }
        LOG.info("Updating the following components:");

        updateOutdatedPlugins(localPomDependencies, listOfArtifacts, centralMavenVersionList, model,
            userInputPluginForUpdate);

        try (FileWriter fw = new FileWriter(pomFile)) {
          MavenXpp3Writer writer = new MavenXpp3Writer();
          writer.write(fw, model);
        }
      }
      LOG.info("Updated successfully. Next time you generate, the plug-ins will be downloaded.");
      return 0;
    }
    return 1;
  }

  /**
   * This method takes the user input for updating templates
   *
   * @param userInputPluginForUpdate This parameter holds the list of User Input
   */
  private void userInputPluginSelection(ArrayList<String> userInputPluginForUpdate) {

    for (String userArtifact : ValidationUtils.getUserInput().split(",")) {
      userInputPluginForUpdate.add(userArtifact);
    }
  }

  /**
   * This method is printing the outdated plug-ins
   *
   * @param localPomDependencies This parameter holds the artificial dependencies
   * @param centralMavenVersionList This parameter holds version list
   * @param updatePluginVersions This parameter contains the key, value pair of versions
   * @param listOfArtifacts This holds a list of artifact ids which need to be updated
   * @return <code>true</code> if all plugins are up-to-date and <code>false</code> if update is needed
   */
  public boolean printOutdatedPlugin(List<Dependency> localPomDependencies, List<String> centralMavenVersionList,
      HashMap<String, String> updatePluginVersions, HashMap<Integer, String> listOfArtifacts) {

    int count = 0;
    String centralMavenVersion = "";
    String requiresUpdate = "";

    for (Dependency lclDependencies : localPomDependencies) {
      if (dependencyShouldBeUpdated(lclDependencies.getGroupId())) {
        // Read pom to check which dependencies can be updated.
        centralMavenVersion = PluginUpdateUtil.latestPluginVersion(lclDependencies.getArtifactId());
        String[] centralVersionValues = extractVersionFragments(centralMavenVersion);
        String[] localVersion = extractVersionFragments(lclDependencies.getVersion());

        for (int ver = 0; ver < localVersion.length; ver++) {
          if (Integer.parseInt(localVersion[ver]) < Integer.parseInt(centralVersionValues[ver])) {
            if (count == 0) {
              LOG.info("(0) " + "All");
            }
            count++;
            centralMavenVersionList.add(centralMavenVersion);
            requiresUpdate = lclDependencies.getArtifactId();
            updatePluginVersions.put(lclDependencies.getArtifactId(), centralVersionValues[ver]);
            listOfArtifacts.put(count, requiresUpdate);
            // Print the dependecy need to update
            LOG.info("({}) {}, {} -> {}", count, requiresUpdate, lclDependencies.getVersion(), centralMavenVersion);
            break;
          }
        }
      }
    }
    // Finished checking all the plug-ins
    if (count == 0) {
      LOG.info("All plug-ins are up to date...");
      return true;
    }
    LOG.info("Here are the components that can be updated, which ones do you want to  update? "
        + "Please list the number of artifact(s) to update separated by comma:");
    return false;
  }

  /**
   * Match and extract the version fragments separated by dot
   *
   * @param version string
   * @return the version fragments split by dot and ignoring any additional value after dash
   */
  private String[] extractVersionFragments(String version) {

    Matcher lclMatcher = VERSION_PATTERN.matcher(version);
    if (!lclMatcher.find()) {
      throw new CobiGenRuntimeException(
          "unable to match version " + version + " against version pattern " + VERSION_PATTERN.pattern());
    }
    String[] localVersion = lclMatcher.group(1).split("\\.");
    return localVersion;
  }

  /**
   * This method is updating the outdated plug-ins
   *
   * @param localPomDependencies This is holding the dependencies of artificial pom
   * @param listOfArtifacts This parameter holds the value of updated plug-ins version
   * @param centralMavenVersionList This parameter holds the version list
   * @param model This parameter holds the after pom file reader dependencies
   * @param userInputPluginForUpdate This is holding user input for update plug-ins
   */
  public void updateOutdatedPlugins(List<Dependency> localPomDependencies, HashMap<Integer, String> listOfArtifacts,
      List<String> centralMavenVersionList, Model model, ArrayList<String> userInputPluginForUpdate) {

    int all = 0;
    for (int j = 0; j < userInputPluginForUpdate.size(); j++) {
      String currentSelectedArtifact = userInputPluginForUpdate.get(j);
      String digitMatch = "\\d+";
      // If given updatable artifact is Integer
      if (currentSelectedArtifact.matches(digitMatch)) {
        int selectedArtifactNumber = Integer.parseInt(currentSelectedArtifact);
        int index = selectedArtifactNumber - 1;
        // We need to update all

        if (selectedArtifactNumber == 0) {
          // Updating all plugin
          for (Dependency lclDependencies : localPomDependencies) {
            if ((listOfArtifacts).containsValue(lclDependencies.getArtifactId())) {
              LOG.info("{} -> {}", lclDependencies.getArtifactId(), centralMavenVersionList.get(all));
              lclDependencies.setVersion(centralMavenVersionList.get(all));
              all++;
            }
          }
          model.setDependencies(localPomDependencies);

        } else {
          // Updating selected plugin
          String plugin = listOfArtifacts.get(selectedArtifactNumber);
          LOG.info("({}) {} -> {}", selectedArtifactNumber, plugin, centralMavenVersionList.get(index));
          for (Dependency selectedDependencies : localPomDependencies) {
            if ((plugin).equals(selectedDependencies.getArtifactId())) {
              selectedDependencies.setVersion(centralMavenVersionList.get(index));
            }
          }
          model.setDependencies(localPomDependencies);
        }
      }
    }
  }

  /**
   * This method checks which artifacts are related to CobiGen. If so, returns true. This is useful for just Updating
   * CobiGen related plug-ins to user
   *
   * @param groupId group id to check whether it is CobiGen related
   * @return true if group id is related to CobiGen
   *
   */
  private boolean dependencyShouldBeUpdated(String groupId) {

    return MavenMetadata.GROUPID.equals(groupId);
  }

}
