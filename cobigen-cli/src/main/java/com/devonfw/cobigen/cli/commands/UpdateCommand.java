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
        HashMap<String, String> updatepluginVersion = new HashMap<String, String>();
        HashMap<Integer, String> listOfArtifact = new HashMap<Integer, String>();
        List<String> centralMavenversionList = new ArrayList<String>();
        String centralMavenversion = "";
        String requireupdate = "";
        if (pomFile.exists()) {
            model = reader.read(new FileReader(pomFile));
            List<Dependency> localPomDepedency = model.getDependencies();
            int count = 0;
            logger.info("(0) " + "All");
            for (Dependency lclDependency : localPomDepedency) {
                String[] localVersion = lclDependency.getVersion().split("\\.");
                if (dependencyShouldBeUpdated(lclDependency.getGroupId())) {
                    // Read pom to check which dependencies can be updated.
                    centralMavenversion = PluginUpdateUtil.latestPluginVersion(lclDependency.getArtifactId());
                    String[] centralversionValue = centralMavenversion.split("\\.");
                    for (int ver = 0; ver < localVersion.length; ver++) {
                        if (Integer.parseInt(localVersion[ver]) < Integer.parseInt(centralversionValue[ver])) {
                            count++;
                            centralMavenversionList.add(centralMavenversion);
                            requireupdate = lclDependency.getArtifactId();
                            updatepluginVersion.put(lclDependency.getArtifactId(), centralversionValue[ver]);
                            listOfArtifact.put(count, requireupdate);
                            // Print the dependecy need to update
                            logger.info("(" + (count) + ")" + requireupdate + " , " + lclDependency.getVersion());
                        }
                    }

                }

            }
            logger.info(
                "Here are the components that can be updated, which ones do you want to  update? Please list the");
            ArrayList<String> userInputPluginForUpdate = new ArrayList<>();
            // User selects which dependencies to update
            for (String userArtifact : GenerateCommand.getUserInput().split(",")) {
                userInputPluginForUpdate.add(userArtifact);
            }
            logger.info("Updating the following components:");
            for (int j = 0; j < userInputPluginForUpdate.size(); j++) {
                String currentSelectedArtifact = userInputPluginForUpdate.get(j);
                String digitMatch = "\\d+";
                // If given updatable artifact is Integer
                if (currentSelectedArtifact.matches(digitMatch)) {
                    int selectedArtifactNumber = Integer.parseInt(currentSelectedArtifact);
                    int index = selectedArtifactNumber - 1;
                    // We need to update all
                    if (selectedArtifactNumber == 0) {
                        logger.info("(0) All");
                    }
                    String plugin = listOfArtifact.get(selectedArtifactNumber);
                    logger.info("(" + selectedArtifactNumber + ")" + plugin);
                    File cpFile = rootCLIPath.resolve(MavenConstants.CLASSPATH_OUTPUT_FILE).toFile();
                    cpFile.deleteOnExit();
                    if (pomFile.exists()) {
                        model = reader.read(new FileReader(pomFile));
                    }
                    for (Dependency lclDependency : localPomDepedency) {
                        if ((plugin).equals(lclDependency.getArtifactId())) {
                            lclDependency.setVersion(centralMavenversionList.get(j));
                            Dependency pluginDependency;
                            pluginDependency = localPomDepedency.get(j);
                            pluginDependency = lclDependency;

                        }

                    }
                    model.setDependencies(localPomDepedency);
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
