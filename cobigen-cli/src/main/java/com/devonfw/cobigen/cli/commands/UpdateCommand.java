package com.devonfw.cobigen.cli.commands;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;

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

/**
 * This class handle update command
 */
@Command(description = MessagesConstants.UPDATE_OPTION_DESCRIPTION, name = "update", aliases = { "u" },
    mixinStandardHelpOptions = true)
public class UpdateCommand implements Callable<Integer> {
    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    @SuppressWarnings("javadoc")
    @Override
    public Integer call() throws Exception {
        Model model = null;
        MavenXpp3Reader reader = new MavenXpp3Reader();
        MavenXpp3Writer writer = new MavenXpp3Writer();
        File locationCLI = new File(GenerateCommand.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path rootCLIPath = locationCLI.getParentFile().toPath();
        File pomFile = new CobiGenUtils().extractArtificialPom(rootCLIPath);
        HashMap<String, String> updatePluginVersions = new HashMap<String, String>();
        HashMap<Integer, String> listOfArtifacts = new HashMap<Integer, String>();
        List<String> centralMavenVersionList = new ArrayList<String>();
        ArrayList<String> userInputPluginForUpdate = new ArrayList<>();
        if (pomFile.exists()) {
            model = reader.read(new FileReader(pomFile));
            List<Dependency> localPomDependencies = model.getDependencies();
            logger.info("(0) " + "All");
            printOutdatedPlugin(localPomDependencies, centralMavenVersionList, updatePluginVersions, listOfArtifacts);
            logger.info(
                "Here are the components that can be updated, which ones do you want to  update? Please list the number of artifact(s) to update separated by comma:");
            if (pomFile.exists()) {
                model = reader.read(new FileReader(pomFile));
            }
            // User selects which dependencies to update
            userInputPluginSelection(userInputPluginForUpdate);
            logger.info("Updating the following components:");

            updateOutdatedPlugins(localPomDependencies, listOfArtifacts, centralMavenVersionList, model,
                userInputPluginForUpdate);

            writer.write(new FileWriter(pomFile), model);
            File cpFile = rootCLIPath.resolve(MavenConstants.CLASSPATH_OUTPUT_FILE).toFile();
            cpFile.deleteOnExit();
            logger.info("Updated successfully");
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
     * @throws IOException
     */
    @SuppressWarnings("javadoc")
    public void printOutdatedPlugin(List<Dependency> localPomDependencies, List<String> centralMavenVersionList,
        HashMap<String, String> updatePluginVersions, HashMap<Integer, String> listOfArtifacts)
        throws MalformedURLException, IOException, ParserConfigurationException {
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
                        count++;
                        centralMavenVersionList.add(centralMavenVersion);
                        requiresUpdate = lclDependencies.getArtifactId();
                        updatePluginVersions.put(lclDependencies.getArtifactId(), centralVersionValues[ver]);
                        listOfArtifacts.put(count, requiresUpdate);
                        // Print the dependecy need to update
                        logger.info("(" + (count) + ")" + requiresUpdate + " , " + lclDependencies.getVersion());
                        break;
                    }
                }

            }

        }
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
                            logger.info(lclDependencies.getArtifactId());
                            lclDependencies.setVersion(centralMavenVersionList.get(all));
                            Dependency pluginDependencies;
                            pluginDependencies = localPomDependencies.get(all);
                            pluginDependencies = lclDependencies;
                            all++;
                        }

                    }
                    model.setDependencies(localPomDependencies);

                } else {
                    // Updating selected plugin
                    String plugin = listOfArtifacts.get(selectedArtifactNumber);
                    logger.info("(" + selectedArtifactNumber + ")" + plugin);
                    Dependency pluginDependencies;
                    for (Dependency selectedDependencies : localPomDependencies) {
                        if ((plugin).equals(selectedDependencies.getArtifactId())) {
                            selectedDependencies.setVersion(centralMavenVersionList.get(index));
                            pluginDependencies = localPomDependencies.get(j);
                            pluginDependencies = selectedDependencies;

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
