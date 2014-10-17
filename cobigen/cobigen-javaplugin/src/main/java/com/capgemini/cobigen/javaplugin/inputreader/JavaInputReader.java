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
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Extension for the {@link IInputReader} Interface of the CobiGen, to be able to read Java classes into
 * FreeMarker models
 *
 * @author mbrunnli (15.10.2013)
 */
public class JavaInputReader implements IInputReader {

    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(JavaInputReader.class);

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public boolean isValidInput(Object input) {

        if (input instanceof Class<?> || input instanceof JavaClass || input instanceof PackageFolder) {
            return true;
        } else if (input instanceof Object[]) {
            // check whether the same Java class has been provided as parser as well as reflection object
            Object[] inputArr = (Object[]) input;
            if (inputArr.length == 2) {
                if (inputArr[0] instanceof JavaClass && inputArr[1] instanceof Class<?>) {
                    if (((JavaClass) inputArr[0]).getFullyQualifiedName().equals(
                        ((Class<?>) inputArr[1]).getCanonicalName())) {
                        return true;
                    }
                } else if (inputArr[0] instanceof Class<?> && inputArr[1] instanceof JavaClass) {
                    if (((Class<?>) inputArr[0]).getCanonicalName().equals(
                        ((JavaClass) inputArr[1]).getFullyQualifiedName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (15.10.2013)
     */
    @Override
    public Map<String, Object> createModel(Object o) {

        if (o instanceof Class<?>) {
            return new ReflectedJavaModelBuilder().createModel((Class<?>) o);
        }
        if (o instanceof JavaClass) {
            return new ParsedJavaModelBuilder().createModel((JavaClass) o);
        }
        if (o instanceof Object[] && isValidInput(o)) {
            Map<String, Object> model;
            Object[] inputArr = (Object[]) o;
            if (inputArr[0] instanceof JavaClass) {
                model = new ParsedJavaModelBuilder().createModel((JavaClass) inputArr[0]);
                ReflectedJavaModelBuilder.enrichModelByUtils(model, (Class<?>) inputArr[1]);
            } else {
                model = new ParsedJavaModelBuilder().createModel((JavaClass) inputArr[1]);
                ReflectedJavaModelBuilder.enrichModelByUtils(model, (Class<?>) inputArr[0]);
            }
            return model;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (03.06.2014)
     */
    @Override
    public boolean combinesMultipleInputObjects(Object input) {

        if (input instanceof PackageFolder) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (03.06.2014)
     */
    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {

        List<Object> javaClasses = new LinkedList<>();
        if (input instanceof PackageFolder) {
            File packageFolder = new File(((PackageFolder) input).getLocation());
            List<File> files = retrieveAllJavaSourceFiles(packageFolder);
            for (File f : files) {

                ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
                classLibraryBuilder.appendDefaultClassLoaders();
                if (((PackageFolder) input).getClassLoader() != null) {
                    classLibraryBuilder.appendClassLoader(((PackageFolder) input).getClassLoader());
                }
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
                            JavaClass javaClass = source.getClasses().get(0);
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
     * Retrieves all java source files (with ending *.java) under the package's folder non-recursively
     *
     * @param packageFolder
     *            the package's folder
     * @return the list of files contained in the package's folder
     * @author mbrunnli (03.06.2014)
     */
    private List<File> retrieveAllJavaSourceFiles(File packageFolder) {

        List<File> files = new LinkedList<>();
        if (packageFolder.isDirectory()) {
            for (File f : packageFolder.listFiles()) {
                if (!f.isDirectory() && f.getName().endsWith(".java")) {
                    files.add(f);
                }
            }
        }
        return files;
    }
}
