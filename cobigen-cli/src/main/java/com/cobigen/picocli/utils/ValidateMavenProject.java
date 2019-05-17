package com.cobigen.picocli.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateMavenProject {
	 private static Logger logger = LoggerFactory.getLogger(ValidateMavenProject.class);
	
	private static final String POM_EXTENSION = "xml";

	public File findPom(File source) {

		String filename = source.getName();
		if (source.isFile()) {
			String basename;
			File pomFile;
			int lastDot = filename.lastIndexOf('.');
			if (lastDot > 0) {
				basename = filename.substring(0, lastDot);
				pomFile = new File(source.getParent(), basename + POM_EXTENSION);
				if (pomFile.exists()) {
					logger.info("User is in valid maven project project ");
					return pomFile;
					
				}
			}
			int lastSlash = filename.indexOf('-');
			if (lastSlash > 0) {
				basename = filename.substring(0, lastSlash);
				pomFile = new File(source.getParent(), basename + POM_EXTENSION);
				if (pomFile.exists()) {
					return pomFile;
				}
			}
		} else if (source.isDirectory()) {
			return findPomFromFolder(source, 0);
		}
		return null;
	}
	
	private File findPomFromFolder(File folder, int recursion) {

	    if (folder == null) {
	      return null;
	    }
	    String POM_XML = "pom.xml";
		File pomFile = new File(folder, POM_XML);
	    if (pomFile.exists()) {
	      return pomFile;
	    }
	    if (recursion > 4) {
	      return null;
	    }
	    return findPomFromFolder(folder.getParentFile(), recursion + 1);
	  }

}
