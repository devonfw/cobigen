package com.devonfw.cobigen.cli.commands;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
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
        if (pomFile.exists()) {
            model = reader.read(new FileReader(pomFile));
            List<Dependency> localPomDepedency = model.getDependencies();
            int count = 0;
            logger.info("(0) " + "All");
            for (Dependency lclDependency : localPomDepedency) {
                String[] localVersion = lclDependency.getVersion().split("\\.");
                if (!lclDependency.getArtifactId().equals("freemarker")
                    && !lclDependency.getArtifactId().equals("mmm-code-java-parser")
                    && !lclDependency.getArtifactId().equals("mmm-code-base")
                    && !lclDependency.getArtifactId().equals("mmm-code-java-maven")
                    && !lclDependency.getArtifactId().equals("mmm-util-core")) {
                    // Read pom to check which dependencies can be updated.
                    String centralMavenversion =
                        PluginUpdateUtil.checkLatestMavenVersion(lclDependency.getArtifactId());

                    String[] centralversionValue = centralMavenversion.split("\\.");
                    for (int ver = 0; ver < localVersion.length; ver++) {

                        if (Integer.parseInt(localVersion[ver]) < Integer.parseInt(centralversionValue[ver])) {
                            count++;
                            // Print the dependecy need to update
                            String requireupdate = lclDependency.getArtifactId();
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
            for (int j = 0; j < userInputPluginForUpdate.size(); j++) {
                String currentSelectedArtifact = userInputPluginForUpdate.get(j);

                String digitMatch = "\\d+";
                // If given updatable artifact is Integer
                if (currentSelectedArtifact.matches(digitMatch)) {
                    int selectedArtifactNumber = Integer.parseInt(currentSelectedArtifact);
                    // We need to update all
                    if (selectedArtifactNumber == 0) {
                        logger.info("(0) All");
                    }
                    logger.info("Updating the following components:");

                    File cpFile = rootCLIPath.resolve(MavenConstants.CLASSPATH_OUTPUT_FILE).toFile();
                    cpFile.deleteOnExit();
                    if (pomFile.exists()) {
                        model = reader.read(new FileReader(pomFile));
                    }
                    // updating artificial pom
                    for (Dependency lclDependency : localPomDepedency) {
                        String[] localVersion = lclDependency.getVersion().split("\\.");
                        if (!lclDependency.getArtifactId().equals("freemarker")
                            && !lclDependency.getArtifactId().equals("mmm-code-java-parser")
                            && !lclDependency.getArtifactId().equals("mmm-code-base")
                            && !lclDependency.getArtifactId().equals("mmm-code-java-maven")
                            && !lclDependency.getArtifactId().equals("mmm-util-core")) {
                            String centralMavenversion =
                                PluginUpdateUtil.checkLatestMavenVersion(lclDependency.getArtifactId());

                            String[] centralversionValue = centralMavenversion.split("\\.");
                            for (int ver = 0; ver < localVersion.length; ver++) {
                                model.setModelVersion(centralMavenversion);
                                if (Integer.parseInt(localVersion[ver]) < Integer.parseInt(centralversionValue[ver])) {
                                    try (FileWriter w = new FileWriter(pomFile)) {
                                        writer.write(w, model);
                                    }
                                }
                            }
                        }
                    }

                }
                logger.info("Updated successfully");
            }
        }

        return 1;
    }

}
