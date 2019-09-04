package com.devonfw.cobigen.cli.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.xml.pull.XmlPullParserException;

import com.devonfw.cobigen.cli.constants.MavenConstants;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;

import net.sf.mmm.util.io.api.IoMode;
import net.sf.mmm.util.io.api.RuntimeIoException;
import net.sf.mmm.util.xml.base.XmlInvalidException;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;


/**
 * This class defines the main CobiGen command
 */


@Command(description = MessagesConstants.WELCOME_MESSAGE, name = "cobigen", aliases = { "cg" },
    mixinStandardHelpOptions = true,  subcommands = { GenerateCommand.class },versionProvider = CobiGenCommand.PropertiesVersionProvider.class)
public class CobiGenCommand implements Runnable {

	@Override
	public void run() {

		// Nothing to do here, this is the master command
	}

	/**
	 * This class implement getVersion() and this method return the version of
	 * plug-in
	 */
	static class PropertiesVersionProvider implements IVersionProvider {
		@Override
		public String[] getVersion() throws Exception {
			List<String> versionProvider = new ArrayList<String>();
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model = null;
			File locationCLI = new File(CobiGenUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			Path rootCLIPath = locationCLI.getParentFile().toPath();

			File pomFile = extractArtificialPom(rootCLIPath);

			if (pomFile.exists()) {
				model = reader.read(new FileReader(pomFile));
			}

			List<Model> versionList = new ArrayList<Model>();
			versionList.add(model);

			List<Dependency> modelDependencies = model.getDependencies();

			for (int i = 0; i < modelDependencies.size(); i++) {

				versionProvider.add(" name:= " + modelDependencies.get(i).getArtifactId() + " version=  "
						+ modelDependencies.get(i).getVersion());

			}
			return versionProvider.toArray(new String[versionProvider.size()]);
		}

	}

	/**
	 * Extracts an artificial POM which defines all the CobiGen plug-ins that are
	 * needed
	 * 
	 * @param rootCLIPath path where the artificial POM will be extracted to
	 * @return the extracted POM file
	 */
	private static File extractArtificialPom(Path rootCLIPath) {
		File pomFile = rootCLIPath.resolve(MavenConstants.POM).toFile();
		if (!pomFile.exists()) {
			try (InputStream resourcesIS = (CobiGenCommand.class.getResourceAsStream("/" + MavenConstants.POM));) {
				Files.copy(resourcesIS, pomFile.getAbsoluteFile().toPath());
			} catch (IOException e1) {
				System.out.println(
						"Failed to extract CobiGen plugins pom into your computer. Maybe you need to use admin permissions.");
			}
		}
		return pomFile;
	}

}
