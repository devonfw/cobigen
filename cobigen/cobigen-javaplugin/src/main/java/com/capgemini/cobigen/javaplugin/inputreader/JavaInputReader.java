/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.javaplugin.inputreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.javaplugin.merger.libextension.ModifyableClassLibraryBuilder;
import com.capgemini.cobigen.javaplugin.merger.libextension.ModifyableJavaClass;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Extension for the {@link IInputReader} Interface of the CobiGen, to be able to read Java classes
 * into FreeMarker models
 * @author mbrunnli (15.10.2013)
 */
public class JavaInputReader implements IInputReader {

    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(JavaInputReader.class);

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public boolean isValidInput(Object input) {
        if (input instanceof Class<?> || input instanceof JavaClass || input instanceof PackageFolder)
            return true;
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (15.10.2013)
     */
    @Override
    public Map<String, Object> createModel(Object o) {
        if (o instanceof Class<?>)
            return new JavaModelBuilder().createModel((Class<?>) o);
        if (o instanceof JavaClass)
            return new ParsedJavaModelBuilder().createModel((JavaClass) o);
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (03.06.2014)
     */
    public boolean combinesMultipleInputObjects(Object input) {
        if (input instanceof PackageFolder)
            return true;
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (03.06.2014)
     */
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        List<Object> javaClasses = new LinkedList<>();
        if (input instanceof PackageFolder) {
            File packageFolder = new File(((PackageFolder) input).getLocation());
            // TODO construct an option to declare recursive and non recursive input retrieval
            List<File> files = retrieveAllJavaSourceFilesRecursively(packageFolder);
            for (File f : files) {

                ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
                classLibraryBuilder.appendDefaultClassLoaders();
                try {
                    classLibraryBuilder
                        .addSource(new InputStreamReader(new FileInputStream(f), inputCharset));
                    JavaSource source = null;
                    for (JavaSource s : classLibraryBuilder.getClassLibrary().getJavaSources()) {
                        source = s;
                        // only consider one class per file
                        break;
                    }
                    if (source != null) {
                        // save cast as given by the customized builder
                        if (source.getClasses().size() > 0) {
                            JavaClass javaClass = (ModifyableJavaClass) source.getClasses().get(0);
                            javaClasses.add(javaClass);
                        }
                    }
                } catch (IOException e) {
                    LOG.error("The file {} could not be parsed as a java class", f.getAbsolutePath()
                        .toString(), e);
                }

            }
        }
        return javaClasses;
    }

    /**
     * Retrieves all java source files (with ending *.java) under the package's folder recursively
     * @param packageFolder
     *            the package's folder
     * @return the list of files contained in the package's folder
     * @author mbrunnli (03.06.2014)
     */
    private List<File> retrieveAllJavaSourceFilesRecursively(File packageFolder) {
        List<File> files = new LinkedList<>();
        if (packageFolder.isDirectory()) {
            for (File f : packageFolder.listFiles()) {
                if (f.isDirectory()) {
                    files.addAll(retrieveAllJavaSourceFilesRecursively(f));
                } else if (f.getName().endsWith(".java")) {
                    files.add(f);
                }
            }
        }
        return files;
    }
}
