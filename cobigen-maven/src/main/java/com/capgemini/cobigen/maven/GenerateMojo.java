package com.capgemini.cobigen.maven;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;

import freemarker.template.TemplateException;

/**
 * @author mbrunnli (08.02.2015)
 */
@Mojo(name = "generate", requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateMojo extends AbstractMojo {

    /**
     * Maven Project, which is currently built
     */
    @Component
    private MavenProject project;

    /**
     * Configuration folder to be used
     */
    @Parameter(required = true)
    private File configurationFolder;

    /**
     * Increments to be generated
     */
    @Parameter
    private List<String> increments;

    /**
     * Templates to be generated
     */
    @Parameter
    private List<String> templates;

    /**
     * Input package
     */
    @Parameter
    private Map<String, File> inputPackageFolders;

    /**
     * Input files
     */
    @Parameter
    private List<File> inputFiles;

    /**
     * States, whether the generation force overriding files and contents
     */
    @Parameter(defaultValue = "false")
    private boolean forceOverride;

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.02.2015)
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        CobiGen cobiGen = new CobiGen(configurationFolder);
        generateFromIncrements(cobiGen);
        generateFromTemplates(cobiGen);
    }

    /**
     * @param cobiGen
     * @throws MojoExecutionException
     * @author mbrunnli (09.02.2015)
     */
    private void generateFromIncrements(CobiGen cobiGen) throws MojoExecutionException {
        if (inputPackageFolders != null && !inputPackageFolders.isEmpty()) {
            ClassLoader cl = getClassLoader();
            for (Map.Entry<String, File> inputPackage : inputPackageFolders.entrySet()) {
                PackageFolder packageFolder =
                    new PackageFolder(inputPackage.getValue().toURI(), inputPackage.getKey(), cl);
                if (increments != null && !increments.isEmpty()) {
                    List<IncrementTo> matchingIncrements = cobiGen.getMatchingIncrements(packageFolder);
                    for (IncrementTo increment : matchingIncrements) {
                        if (increments.contains(increment.getId())) {
                            try {
                                cobiGen.generate(packageFolder, increment, forceOverride);
                            } catch (IOException | TemplateException | MergeException e) {
                                String errorMessage =
                                    "An exception occured while generating increment with id '"
                                        + increment.getId() + "' for input package/folder '"
                                        + packageFolder.getLocation() + "'";
                                getLog().error(errorMessage, e);
                                throw new MojoExecutionException(errorMessage, e);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param cobiGen
     * @throws MojoExecutionException
     * @author mbrunnli (09.02.2015)
     */
    private void generateFromTemplates(CobiGen cobiGen) throws MojoExecutionException {
        if (inputFiles != null && !inputFiles.isEmpty()) {
            ClassLoader cl = getClassLoader();
            for (File file : inputFiles) {
                List<TemplateTo> matchingTemplates = cobiGen.getMatchingTemplates(file);
                for (TemplateTo template : matchingTemplates) {
                    if (increments.contains(template.getId())) {
                        try {
                            cobiGen.generate(file, template, forceOverride);
                        } catch (IOException | TemplateException | MergeException e) {
                            String errorMessage =
                                "An exception occured while generating template with id '" + template.getId()
                                    + "' for input file '" + file.getAbsolutePath().toString() + "'";
                            getLog().error(errorMessage, e);
                            throw new MojoExecutionException(errorMessage, e);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @return
     * @throws MojoExecutionException
     * @author mbrunnli (11.02.2015)
     */
    private ClassLoader getClassLoader() throws MojoExecutionException {
        List<String> classpathElements = null;
        try {
            classpathElements = project.getCompileClasspathElements();
            List<URL> projectClasspathList = new ArrayList<>();
            for (String element : classpathElements) {
                try {
                    projectClasspathList.add(new File(element).toURI().toURL());
                } catch (MalformedURLException e) {
                    getLog().error(element + " is an invalid classpath element", e);
                    throw new MojoExecutionException(element + " is an invalid classpath element", e);
                }
            }
            return new URLClassLoader(projectClasspathList.toArray(new URL[0]));
        } catch (DependencyResolutionRequiredException e) {
            getLog().error("Dependency resolution failed", e);
            throw new MojoExecutionException("Dependency resolution failed", e);
        }
    }

    /**
     * Sets the field 'configurationFolder'.
     * @param configurationFolder
     *            new value of configurationFolder
     * @author mbrunnli (08.02.2015)
     */
    public void setConfigurationFolder(File configurationFolder) {
        this.configurationFolder = configurationFolder;
    }

    /**
     * Sets the field 'increments'.
     * @param increments
     *            new value of increments
     * @author mbrunnli (08.02.2015)
     */
    public void setIncrements(List<String> increments) {
        this.increments = increments;
    }

    /**
     * Sets the field 'templates'.
     * @param templates
     *            new value of templates
     * @author mbrunnli (08.02.2015)
     */
    public void setTemplates(List<String> templates) {
        this.templates = templates;
    }

}
