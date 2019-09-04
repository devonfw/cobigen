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

	static class PropertiesVersionProvider implements IVersionProvider {
		@Override
		public String[] getVersion() throws Exception {

			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model;
			if ((new File("pom.xml")).exists())
				model = reader.read(new FileReader("pom.xml"));
			else
				model = reader.read(
						new InputStreamReader(CobiGenCommand.class.getResourceAsStream("/src/main/resources/pom.xml")));
			List<Model> versionname = new ArrayList<Model>();
			versionname.add(model);
			for (int i = 0; i < versionname.size(); i++)
				for (int j = 0; j < versionname.get(i).getDependencies().size(); j++) {
					System.out.println("name:= "+versionname.get(i).getName()+"  version"+versionname.get(i).getVersion());
					System.out.println("name:= " + versionname.get(i).getDependencies().get(j).getArtifactId()
							+ "  version= " + versionname.get(i).getDependencies().get(j).getVersion());
				}
			String s[] = new String[10];

			return s;
		}

	}

}
