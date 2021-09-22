package com.devonfw.cobigen.eclipse.generator.java;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.InputInterpreter;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.devonfw.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.google.common.collect.Lists;

/**
 * Converter to convert the IDE representation of IDE elements to valid input types for the {@link CobiGen generator}
 */
public class JavaInputConverter {

  /**
   * Converts a list of IDE objects to the supported CobiGen input types
   *
   * @param javaElements java IDE objects (mainly of type {@link IJavaElement}), which should be converted
   * @param inputInterpreter to interpret inputs
   * @return the corresponding {@link List} of inputs for the {@link CobiGen generator}
   * @throws GeneratorCreationException if any exception occurred during converting the inputs or creating the generator
   */
  public static List<Object> convertInput(List<Object> javaElements, InputInterpreter inputInterpreter)
      throws GeneratorCreationException {

    List<Object> convertedInputs = Lists.newLinkedList();

    /*
     * Precondition / Assumption: all elements of the list are of the same type
     */
    for (Object elem : javaElements) {
      if (elem instanceof IPackageFragment) {
        try {
          IPackageFragment frag = (IPackageFragment) elem;
          Object packageFolder = inputInterpreter.read(Paths.get(frag.getCorrespondingResource().getLocationURI()),
              StandardCharsets.UTF_8, frag.getElementName(),
              ClassLoaderUtil.getProjectClassLoader(frag.getJavaProject()));
          convertedInputs.add(packageFolder);
        } catch (MalformedURLException e) {
          throw new GeneratorCreationException(
              "An internal exception occurred while building the project class loader.", e);
        } catch (CoreException e) {
          throw new GeneratorCreationException("An eclipse internal exception occurred.", e);
        } catch (InputReaderException e) {
          throw new GeneratorCreationException("Could not read from resource " + elem.toString(), e);
        }
      } else if (elem instanceof ICompilationUnit) {
        // Take first input type as precondition for the input is that all input types are part of the
        // same project
        try {
          IType[] types = ((ICompilationUnit) elem).getTypes();
          if (types.length < 1) {
            throw new GeneratorCreationException("The input does not declare a class");
          }
          IType rootType = types[0];
          try {
            ClassLoader projectClassLoader = ClassLoaderUtil.getProjectClassLoader(rootType.getJavaProject());
            Object input = inputInterpreter.read(
                Paths.get(((ICompilationUnit) elem).getCorrespondingResource().getRawLocationURI()),
                StandardCharsets.UTF_8, projectClassLoader);
            convertedInputs.add(input);
          } catch (MalformedURLException e) {
            throw new GeneratorCreationException(
                "An internal exception occurred while loading Java class " + rootType.getFullyQualifiedName(), e);
          } catch (InputReaderException e) {
            throw new GeneratorCreationException("Could not read from resource " + elem.toString(), e);
          }
        } catch (MergeException e) {
          throw new GeneratorCreationException(
              "Could not parse Java base file: " + ((ICompilationUnit) elem).getElementName() + ":\n" + e.getMessage(),
              e);
        } catch (CoreException e) {
          throw new GeneratorCreationException("An eclipse internal exception occurred.", e);
        }
      }
    }

    return convertedInputs;
  }
}
