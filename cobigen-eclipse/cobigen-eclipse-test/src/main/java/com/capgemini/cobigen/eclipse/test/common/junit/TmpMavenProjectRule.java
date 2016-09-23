package com.capgemini.cobigen.eclipse.test.common.junit;

import java.io.ByteArrayInputStream;
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

/**
 * JUnit Rule for a temporary {@link IJavaProject}. Should be created in each test method by createProject
 * when it should be used.
 */
@SuppressWarnings("restriction")
public class TmpMavenProjectRule extends ExternalResource {

    /**
     * Project reference
     */
    private IJavaProject javaProject;

    @Override
    protected void after() {
        try {
            if (javaProject != null && javaProject.getProject() != null) {
                javaProject.getProject().delete(true, new NullProgressMonitor());
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        super.after();
    }

    /**
     * Creates the project in the current test workspace
     * @param name
     *            the project should be named with
     * @return Java
     * @throws CoreException
     *             if there occurs an eclipse internal problem while creating the project
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
     */
    public void updateProject() {
        // update maven project to implicitly set classpath and so on.
        new UpdateMavenProjectJob(new IProject[] { javaProject.getProject() })
            .runInWorkspace(new NullProgressMonitor());
    }

    /**
     * Creates a default pom file.
     * @param dependencies
     *            you can pass the <dependencies></dependencies> section as a parameter if you like
     * @throws CoreException
     *             if writing the file failed
     */
    public void createPom(String dependencies) throws CoreException {
        IFile pom = javaProject.getProject().getFile("pom.xml");

        // @formatter:off
        byte[] pomBytes = ("<?xml version='1.0' encoding='UTF-8'?>"
            + "<project xsi:schemaLocation='http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd'"
            + "xmlns='http://maven.apache.org/POM/4.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>"
            + "<modelVersion>4.0.0</modelVersion>" + "<groupId>generated</groupId>" + "<artifactId>generated."
            + new Random().nextInt() + "</artifactId>" + "<version>1.0.0</version>"
            + "<packaging>jar</packaging>" + ((dependencies != null) ? dependencies : "") + "</project>")
                .getBytes();
        // @formatter:on

        if (pom.exists()) {
            pom.setContents(new ByteArrayInputStream(pomBytes), IResource.FORCE, new NullProgressMonitor());
        } else {
            pom.create(new ByteArrayInputStream(pomBytes), true, new NullProgressMonitor());
        }
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