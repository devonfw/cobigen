package com.capgemini.cobigen.eclipse.generator.java;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.capgemini.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;
import com.google.common.collect.Lists;

/**
 * Converter to convert the IDE representation of IDE elements to valid input types for the {@link CobiGen
 * generator}
 * @author mbrunnli (04.12.2014)
 */
public class JavaInputConverter {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(JavaInputConverter.class);

    /**
     * Converts a list of IDE objects to the supported CobiGen input types
     * @param javaElements
     *            java IDE objects (mainly of type {@link IJavaElement}), which should be converted
     * @return the corresponding {@link List} of inputs for the {@link CobiGen generator}
     * @throws GeneratorCreationException
     *             if any exception occurred during converting the inputs or creating the generator
     * @author mbrunnli (04.12.2014)
     */
    public static List<Object> convertInput(List<Object> javaElements) throws GeneratorCreationException {
        List<Object> convertedInputs = Lists.newLinkedList();

        /*
         * Precondition / Assumption: all elements of the list are of the same type
         */
        for (Object elem : javaElements) {
            if (elem instanceof IPackageFragment) {
                try {
                    IPackageFragment frag = (IPackageFragment) elem;
                    PackageFolder packageFolder =
                        new PackageFolder(frag.getResource().getLocationURI(), frag.getElementName());
                    packageFolder.setClassLoader(ClassLoaderUtil.getProjectClassLoader(frag.getJavaProject()));
                    convertedInputs.add(packageFolder);
                } catch (MalformedURLException e) {
                    LOG.error("An internal exception occurred while building the project class loader.", e);
                    throw new GeneratorCreationException(
                        "An internal exception occurred while building the project class loader.", e);
                } catch (CoreException e) {
                    LOG.error("An eclipse internal exception occurred.", e);
                    throw new GeneratorCreationException("An eclipse internal exception occurred.", e);
                }
            } else if (elem instanceof ICompilationUnit) {
                // Take first input type as precondition for the input is that all input types are part of the
                // same project
                try {
                    IType rootType = ((ICompilationUnit) elem).getTypes()[0];
                    try {
                        ClassLoader projectClassLoader =
                            ClassLoaderUtil.getProjectClassLoader(rootType.getJavaProject());
                        Class<?> loadedClass = projectClassLoader.loadClass(rootType.getFullyQualifiedName());
                        Object[] inputSourceAndClass = new Object[] { loadedClass,
                            JavaParserUtil.getFirstJavaClass(
                                ClassLoaderUtil.getProjectClassLoader(rootType.getJavaProject()),
                                new StringReader(((ICompilationUnit) elem).getSource())) };
                        convertedInputs.add(inputSourceAndClass);
                    } catch (MalformedURLException e) {
                        LOG.error("An internal exception occurred while loading Java class {}",
                            rootType.getFullyQualifiedName(), e);
                        throw new GeneratorCreationException("An internal exception occurred while loading Java class "
                            + rootType.getFullyQualifiedName(), e);
                    } catch (ClassNotFoundException e) {
                        LOG.error("Could not instantiate Java class {}", rootType.getFullyQualifiedName(), e);
                        throw new GeneratorCreationException(
                            "Could not instantiate Java class " + rootType.getFullyQualifiedName(), e);
                    }
                } catch (MergeException e) {
                    throw new GeneratorCreationException("Could not parse Java base file: "
                        + ((ICompilationUnit) elem).getElementName() + ":\n" + e.getMessage(), e);
                } catch (JavaModelException e) {
                    LOG.error("An eclipse internal exception occurred while accessing the java model.", e);
                    throw new GeneratorCreationException("An eclipse internal exception occurred.", e);
                } catch (CoreException e) {
                    LOG.error("An eclipse internal exception occurred.", e);
                    throw new GeneratorCreationException("An eclipse internal exception occurred.", e);
                }
            }
        }

        return convertedInputs;
    }
}
