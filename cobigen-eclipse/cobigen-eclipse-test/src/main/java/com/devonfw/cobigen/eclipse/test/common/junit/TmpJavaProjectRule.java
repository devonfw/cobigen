package com.devonfw.cobigen.eclipse.test.common.junit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * JUnit Rule for a temporary {@link IJavaProject}. Should be created in each test method by createProject when it
 * should be used.
 */
public class TmpJavaProjectRule extends ExternalResource {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(TmpJavaProjectRule.class);

  /**
   * Project reference
   */
  private IJavaProject javaProject;

  @Override
  protected void after() throws CobiGenRuntimeException {

    try {
      if (this.javaProject != null && this.javaProject.getProject() != null) {
        this.javaProject.getProject().delete(true, new NullProgressMonitor());
      }
    } catch (CoreException e) {
      LOG.warn("Was not able to delete project by workbench API. Try to delete it directly on file system.");
      boolean deleted = new File(this.javaProject.getProject().getLocationURI()).delete();
      if (!deleted) {
        LOG.warn("Was not able to delete project by File IO API.");
      }
    }
    super.after();
  }

  /**
   * Creates the project in the current test workspace
   *
   * @param name the project should be named with
   * @return Java
   * @throws CoreException if there occurs an eclipse internal problem while creating the project
   */
  public IJavaProject createProject(String name) throws CoreException {

    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);

    if (!project.exists()) {
      project.create(new NullProgressMonitor());
    } else {
      throw new IllegalStateException(
          "Could not create new temporary JavaProject. There is already a project with name " + name
              + " registered in workspace.");
    }
    while (!project.exists()) {
    }

    if (!project.isOpen()) {
      project.open(new NullProgressMonitor());
    }
    while (!project.isOpen()) {
    }

    IProjectDescription description = project.getDescription();
    description.setNatureIds(new String[] { JavaCore.NATURE_ID });
    project.setDescription(description, new NullProgressMonitor());

    this.javaProject = JavaCore.create(project);
    createBinFolder();
    defineClassPathEntries();

    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
    return this.javaProject;
  }

  /**
   * Creates the bin folder for a valid java project instance
   *
   * @throws CoreException if an internal exception occurs while creating the bin folder
   * @throws JavaModelException if build folder could not be set
   */
  private void createBinFolder() throws CoreException, JavaModelException {

    IFolder binFolder = this.javaProject.getProject().getFolder("bin");
    binFolder.create(true, true, new NullProgressMonitor());
    this.javaProject.setOutputLocation(binFolder.getFullPath(), new NullProgressMonitor());
  }

  /**
   * Defines the class path entries for a valid java project
   *
   * @throws CoreException if the source folder could not be created
   */
  private void defineClassPathEntries() throws CoreException {

    List<IClasspathEntry> entries = new ArrayList<>();
    IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
    LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
    for (LibraryLocation element : locations) {
      entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
    }

    // create source folder and add it to the class path
    IFolder sourceFolder = this.javaProject.getProject().getFolder("src");
    sourceFolder.create(true, true, new NullProgressMonitor());
    entries.add(JavaCore.newSourceEntry(sourceFolder.getFullPath()));

    // add libs to project class path
    this.javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), new NullProgressMonitor());
  }

}