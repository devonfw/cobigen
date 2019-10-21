package com.devonfw.cobigen.cli.commands;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
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
        String centralMavenVersion = "";
        String requiresUpdate = "";
        if (pomFile.exists()) {
            model = reader.read(new FileReader(pomFile));
            List<Dependency> localPomDependencies = model.getDependencies();
            int count = 0;
            logger.info("(0) " + "All");
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
            logger.info(
                "Here are the components that can be updated, which ones do you want to  update? Please list the number of artifact(s) to update separated by comma:");
            ArrayList<String> userInputPluginForUpdate = new ArrayList<>();
            // User selects which dependencies to update
            for (String userArtifact : GenerateCommand.getUserInput().split(",")) {
                userInputPluginForUpdate.add(userArtifact);
            }
            logger.info("Updating the following components:");
            File cpFile = rootCLIPath.resolve(MavenConstants.CLASSPATH_OUTPUT_FILE).toFile();
            cpFile.deleteOnExit();
            for (int j = 0; j < userInputPluginForUpdate.size(); j++) {
                String currentSelectedArtifact = userInputPluginForUpdate.get(j);
                String digitMatch = "\\d+";
                // If given updatable artifact is Integer
                if (currentSelectedArtifact.matches(digitMatch)) {
                    int selectedArtifactNumber = Integer.parseInt(currentSelectedArtifact);
                    int index = selectedArtifactNumber - 1;
                    // We need to update all
                    if (pomFile.exists()) {
                        model = reader.read(new FileReader(pomFile));
                    }
                    int all = 0;
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

                        for (Dependency selectedDependencies : localPomDependencies) {
                            if ((plugin).equals(selectedDependencies.getArtifactId())) {
                                selectedDependencies.setVersion(centralMavenVersionList.get(index));
                                Dependency pluginDependencies;
                                pluginDependencies = localPomDependencies.get(j);
                                pluginDependencies = selectedDependencies;

                            }

                        }
                        model.setDependencies(localPomDependencies);
                    }

                    writer.write(new FileWriter(pomFile), model);

                }

            }
            logger.info("Updated successfully");
        }

        return 1;
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
