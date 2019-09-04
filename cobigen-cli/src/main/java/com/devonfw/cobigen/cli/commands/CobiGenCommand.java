package com.devonfw.cobigen.cli.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.xml.pull.XmlPullParserException;

import com.devonfw.cobigen.cli.constants.MessagesConstants;

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
	 * This class implement getVersion() and this method return the version of plug-in
	 */
	static class PropertiesVersionProvider implements IVersionProvider {
		@Override
		public String[] getVersion() throws Exception {
			String versionProvider[] = new String[50];
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model;
			if ((new File("pom.xml")).exists())
				model = reader.read(new FileReader("pom.xml"));
			else
				model = reader.read(
						new InputStreamReader(CobiGenCommand.class.getResourceAsStream("/src/main/resources/pom.xml")));
			List<Model> versionList = new ArrayList<Model>();
			versionList.add(model);

			for (int j = 0; j < versionList.get(0).getDependencies().size(); j++) {
				versionProvider[j] = " name:= " + model.getDependencies().get(j).getArtifactId() + " version=  "
						+ model.getDependencies().get(j).getVersion();
			}

			return versionProvider;
		}

	}

}
