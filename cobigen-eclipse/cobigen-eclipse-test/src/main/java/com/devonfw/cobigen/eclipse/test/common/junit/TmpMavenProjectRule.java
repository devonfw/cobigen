package com.devonfw.cobigen.eclipse.test.common.junit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.ui.internal.UpdateMavenProjectJob;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Rule for a temporary {@link IJavaProject}. Should be created in each test method by createProject
 * when it should be used.
 */
@SuppressWarnings("restriction")
public class TmpMavenProjectRule extends ExternalResource {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TmpMavenProjectRule.class);

    /**
     * Project reference
     */
    private IJavaProject javaProject;

    /** Maven Project specification containing group id, artifact id, and version as a pom xml description */
    private String mvnProjectSpecification;

    @Override
    protected void after() {
        try {
            if (javaProject != null && javaProject.getProject() != null) {
                javaProject.getProject().delete(true, new NullProgressMonitor());
            }
        } catch (CoreException e) {
            LOG.warn("Was not able to delete project by workbench API. Try to delete it directly on file system.");
            boolean deleted = new File(javaProject.getProject().getLocationURI()).delete();
            if (!deleted) {
                LOG.warn("Was not able to delete project by File IO API.");
            } else {
                try {
                    ResourcesPlugin.getWorkspace().getRoot().refreshLocal(1, new NullProgressMonitor());
                } catch (CoreException e1) {
                    LOG.warn("Was not able to refresh workspace after deleting Project on disk.");
                }
            }
        } finally {
            javaProject = null;
            mvnProjectSpecification = null;
        }
        super.after();
    }

    /**
     * Creates the project in the current test workspace
     * @param name
     *            the project should be named with
     * @return Java
     * @throws Exception
     *             if there occurs an eclipse internal problem while creating the project
     */
    public IJavaProject createProject(String name) throws Exception {
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
        description.setNatureIds(new String[] { JavaCore.NATURE_ID, IMavenConstants.NATURE_ID });
        project.setDescription(description, new NullProgressMonitor());

        javaProject = JavaCore.create(project);
        createSourceFolders();
        createPom(null);

        updateProject();
        return javaProject;
    }

    /**
     * Performs a maven project update for the underlying project
     * @throws CoreException
     *             refresh did not work
     */
    public void updateProject() throws CoreException {
        // make sure the contents are synchronized
        javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        // update maven project to implicitly set classpath and so on.
        new UpdateMavenProjectJob(new IProject[] { javaProject.getProject() })
            .runInWorkspace(new NullProgressMonitor());

    }

    /**
     * Creates a default pom file.
     * @param dependencies
     *            you can pass the <dependencies></dependencies> section as a parameter if you like
     * @throws Exception
     *             if writing the file failed
     */
    public void createPom(String dependencies) throws Exception {
        IFile pom = javaProject.getProject().getFile("pom.xml");

        mvnProjectSpecification = "<groupId>generated</groupId>" + "<artifactId>generated." + new Random().nextInt()
            + "</artifactId>" + "<version>1.0.0</version>";

        // @formatter:off
        byte[] pomBytes = ("<?xml version='1.0' encoding='UTF-8'?>"
            + "<project xsi:schemaLocation='http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd'"
            + "xmlns='http://maven.apache.org/POM/4.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>"
            + "<modelVersion>4.0.0</modelVersion>" + mvnProjectSpecification + "<packaging>jar</packaging>"
            + ((dependencies != null) ? dependencies : "") + "</project>").getBytes();
        // @formatter:on

        try (ByteArrayInputStream source = new ByteArrayInputStream(pomBytes)) {
            if (pom.exists()) {
                pom.setContents(source, IResource.FORCE, new NullProgressMonitor());
            } else {
                pom.create(source, true, new NullProgressMonitor());
            }
            pom.touch(new NullProgressMonitor());
        }

        // make sure the contents are synchronized
        javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    }

    /**
     * Returns the current maven project specification containing group id, artifact id, and version as a pom
     * xml description. May be directly passed to {@link #createPom(String)} to create a dependency between
     * two test projects.
     * @return the dependency xml for this project.
     */
    public String getMavenProjectSpecification() {
        return mvnProjectSpecification;
    }

    /**
     * Creates the essential maven folder structure of source folders
     * @throws CoreException
     *             if an internal exception occurs while creating the bin folder
     */
    private void createSourceFolders() throws CoreException {
        IFolder src = javaProject.getProject().getFolder("src");
        src.create(true, true, new NullProgressMonitor());
        IFolder main = src.getFolder("main");
        main.create(true, true, new NullProgressMonitor());
        main.getFolder("java").create(true, true, new NullProgressMonitor());
        main.getFolder("test").create(true, true, new NullProgressMonitor());
    }

}