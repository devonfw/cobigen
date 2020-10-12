package com.devonfw.cobigen.cli.commands;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MavenConstants;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;
import com.devonfw.cobigen.cli.utils.PluginUpdateUtil;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * This class handle update command
 */
@Command(description = MessagesConstants.UPDATE_OPTION_DESCRIPTION, name = "update", aliases = { "u" },
    mixinStandardHelpOptions = true)
public class UpdateCommand implements Callable<Integer> {
    /**
     * Logger to output useful information to the user
     */
    private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * If this options is enabled, all plugins get updated.
     */
    @Option(names = { "--all" }, negatable = true, description = MessagesConstants.UPDATE_ALL_DESCRIPTION)
    boolean updateAll;

    @Override
    public Integer call() throws Exception {

        File locationCLI = new File(GenerateCommand.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path rootCLIPath = locationCLI.getParentFile().toPath();
        File pomFile = new CobiGenUtils().extractArtificialPom(rootCLIPath);
        HashMap<String, String> updatePluginVersions = new HashMap<>();
        HashMap<Integer, String> listOfArtifacts = new HashMap<>();
        List<String> centralMavenVersionList = new ArrayList<>();
        ArrayList<String> userInputPluginForUpdate = new ArrayList<>();

        if (pomFile.exists()) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader(pomFile));
            List<Dependency> localPomDependencies = model.getDependencies();
            boolean isAllUpdated = printOutdatedPlugin(localPomDependencies, centralMavenVersionList,
                updatePluginVersions, listOfArtifacts);

            if (isAllUpdated) {
                return 0;
            }
            if (updateAll) {
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

            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(new FileWriter(pomFile), model);
            File cpFile = rootCLIPath.resolve(MavenConstants.CLASSPATH_OUTPUT_FILE).toFile();
            cpFile.deleteOnExit();
            LOG.info("Updated successfully. Next time you generate, the plug-ins will be downloaded.");
        }

        return 1;
    }

    /**
     * This method take user input for update template
     * @param userInputPluginForUpdate
     *            This parameter hold list of User Input
     */
    private void userInputPluginSelection(ArrayList<String> userInputPluginForUpdate) {
        for (String userArtifact : GenerateCommand.getUserInput().split(",")) {
            userInputPluginForUpdate.add(userArtifact);
        }
    }

    /**
     * This method printing the outdated plug-ins
     * @param localPomDependencies
     *            This parameter hold the artificial dependencies
     * @param centralMavenVersionList
     *            This parameter hold version list
     * @param updatePluginVersions
     *            This parameter contain the key ,value pair of version
     * @param listOfArtifacts
     *            this hold list of artifact id which is need to update
     */
    public boolean printOutdatedPlugin(List<Dependency> localPomDependencies, List<String> centralMavenVersionList,
        HashMap<String, String> updatePluginVersions, HashMap<Integer, String> listOfArtifacts) {
        int count = 0;
        String centralMavenVersion = "";
        String requiresUpdate = "";
        for (Dependency lclDependencies : localPomDependencies) {
            String[] localVersion = lclDependencies.getVersion().split("\\.");
            if (dependencyShouldBeUpdated(lclDependencies.getGroupId())) {
                // Read pom to check which dependencies can be updated.
                centralMavenVersion = PluginUpdateUtil.latestPluginVersion(lclDependencies.getArtifactId());
                String[] centralVersionValues = centralMavenVersion.split("\\.");

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
                        LOG.info("({}) {}, {}", count, requiresUpdate, lclDependencies.getVersion());
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
     * This method updating the outdated plug-ins
     * @param localPomDependencies
     *            This is hold the dependencies of artificial pom
     * @param listOfArtifacts
     *            This parameter hold the value of updated plug-ins version
     * @param centralMavenVersionList
     *            This parameter hold version list
     * @param model
     *            This parameter hold the after pom file reader dependencies
     * @param userInputPluginForUpdate
     *            This is hold user input for update plug-ins
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
                            LOG.info(lclDependencies.getArtifactId());
                            lclDependencies.setVersion(centralMavenVersionList.get(all));
                            all++;
                        }

                    }
                    model.setDependencies(localPomDependencies);

                } else {
                    // Updating selected plugin
                    String plugin = listOfArtifacts.get(selectedArtifactNumber);
                    LOG.info("({}) {}", selectedArtifactNumber, plugin);
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
     * This method checks which artifacts are related to CobiGen. If so, returns true. This is useful for just
     * Updating CobiGen related plug-ins to user
     * @param groupId
     *            group id to check whether it is CobiGen related
     * @return true if group id is related to CobiGen
     *
     */
    private Boolean dependencyShouldBeUpdated(String groupId) {

        if (MavenConstants.COBIGEN_GROUPID.equals(groupId)) {
            return true;
        }
        return false;
    }

}
