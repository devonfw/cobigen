package com.devonfw.cobigen.cli.commands;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.to.GenerableArtifact;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;
import com.devonfw.cobigen.cli.utils.PluginUpdateUtil;
import com.devonfw.cobigen.impl.generator.InputInterpreterImpl;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * This class handle update command
 */
@Command(description = MessagesConstants.GENERATE_DESCRIPTION, name = "update", aliases = { "u" },
    mixinStandardHelpOptions = true)
public class UpdateCommand implements Callable<Integer> {
    InputInterpreterImpl inputInterpreter = new InputInterpreterImpl();

    public UpdateCommand() {
        super();
    }

    /**
     * This option provide specified list of template
     */
    @Option(names = { "--templates", "-t" }, split = ",", description = MessagesConstants.TEMPLATES_OPTION_DESCRIPTION)
    /**
     * Initialize templates variable
     */
    ArrayList<String> templates = null;

    /**
     * If this options is enabled, we will print also debug messages
     */
    @Option(names = { "--verbose", "-v" }, negatable = true, description = MessagesConstants.VERBOSE_OPTION_DESCRIPTION)
    boolean verbose;

    /**
     * This option provides the use of multiple available increments
     */
    @Option(names = { "update", "u" }, split = ",", description = MessagesConstants.UPDATE_OPTION_DESCRIPTION)
    /**
     * Utils class for CobiGen related operations
     */
    private CobiGenUtils cobigenUtils = new CobiGenUtils();

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    @Override
    public Integer call() throws Exception {
        Model model = null;
        List<String> versionProvider = new ArrayList<>();
        List<String> needversionupdate = new ArrayList<>();
        List<GenerableArtifact> userSelection = new ArrayList<>();
        MavenXpp3Reader reader = new MavenXpp3Reader();
        File locationCLI = new File(GenerateCommand.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path rootCLIPath = locationCLI.getParentFile().toPath();
        File pomFile = new CobiGenUtils().extractArtificialPom(rootCLIPath);
        if (pomFile.exists()) {
            model = reader.read(new FileReader(pomFile));
            List<Dependency> localPomDepedency = model.getDependencies();
            int count = 0;
            logger.info("(0) " + "All");
            for (Dependency lclDependency : localPomDepedency) {

                if (!lclDependency.getArtifactId().equals("freemarker")
                    && !lclDependency.getArtifactId().equals("mmm-code-java-parser")
                    && !lclDependency.getArtifactId().equals("mmm-code-base")
                    && !lclDependency.getArtifactId().equals("mmm-code-java-maven")
                    && !lclDependency.getArtifactId().equals("mmm-util-core")) {
                    String centralMavenversion =
                        PluginUpdateUtil.checkLatestMavenVersion(lclDependency.getArtifactId());
                    String[] localVersion = lclDependency.getVersion().split("\\.");

                    String[] centralversionValue = centralMavenversion.split("\\.");
                    for (int ver = 0; ver < localVersion.length; ver++) {

                        if (Integer.parseInt(localVersion[ver]) < Integer.parseInt(centralversionValue[ver])) {
                            count++;
                            String requireupdate = lclDependency.getArtifactId();
                            logger.info("(" + (count) + ")" + requireupdate + " , " + lclDependency.getVersion());
                        }
                    }

                }

            }

        }

        logger.info("Here are the components that can be updated, which ones do you want to  update? Please list the");
        ArrayList<String> userInputPluginForUpdate = new ArrayList<>();
        for (String userArtifact : GenerateCommand.getUserInput().split(",")) {
            userInputPluginForUpdate.add(userArtifact);
        }
        for (int j = 0; j < userInputPluginForUpdate.size(); j++) {
            String currentSelectedArtifact = userInputPluginForUpdate.get(j);

            String digitMatch = "\\d+";
            // If given generable artifact is Integer
            if (currentSelectedArtifact.matches(digitMatch)) {
                int selectedArtifactNumber = Integer.parseInt(currentSelectedArtifact);
                int index = selectedArtifactNumber - 1;
                // We need to generate all
                if (selectedArtifactNumber == 0) {
                    logger.info("(0) All");
                }
                logger.info("updated succesfully");
            }

        }

        return 1;
    }

    @SuppressWarnings("unchecked")
    private List<TemplateTo> toTemplateTo(List<? extends GenerableArtifact> matching) {
        return (List<TemplateTo>) matching;
    }

}
