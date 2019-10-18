package com.devonfw.cobigen.cli.utils;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import com.devonfw.cobigen.cli.commands.GenerateCommand;
import com.devonfw.cobigen.cli.constants.MavenConstants;

import picocli.CommandLine.IVersionProvider;

/**
 * This class implement getVersion() and this method returns the version of plug-ins
 */
public class CobiGenVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        Model model = null;
        List<String> versionProvider = new ArrayList<>();
        MavenXpp3Reader reader = new MavenXpp3Reader();

        File locationCLI = new File(GenerateCommand.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path rootCLIPath = locationCLI.getParentFile().toPath();

        File pomFile = new CobiGenUtils().extractArtificialPom(rootCLIPath);

        if (pomFile.exists()) {
            model = reader.read(new FileReader(pomFile));
            versionProvider.add("CobiGen CLI " + model.getVersion());
            versionProvider.add("");
        } else {
            versionProvider.add("Error while retrieving CLI version. Pom.xml was not found on your PC. This is a bug");
            return versionProvider.toArray(new String[versionProvider.size()]);
        }

        List<Dependency> modelDependencies = model.getDependencies();

        for (int i = 0; i < modelDependencies.size(); i++) {
            String artifactId = modelDependencies.get(i).getArtifactId();
            String version = modelDependencies.get(i).getVersion();

            if (dependencyShouldBePrinted(modelDependencies.get(i).getGroupId())) {
                versionProvider.add("Plugin: " + artifactId + " " + version);
            }
        }
        return versionProvider.toArray(new String[versionProvider.size()]);
    }

    /**
     * This method checks which artifacts are related to CobiGen. If so, returns true. This is useful for just
     * printing CobiGen related plug-ins to user
     * @param groupId
     *            group id to check whether it is CobiGen related
     * @return true if group id is related to CobiGen
     *
     */
    private Boolean dependencyShouldBePrinted(String groupId) {

        if (MavenConstants.COBIGEN_GROUPID.equals(groupId)) {
            return true;
        }
        return false;
    }
}
