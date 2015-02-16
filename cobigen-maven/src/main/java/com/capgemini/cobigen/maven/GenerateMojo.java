package com.capgemini.cobigen.maven;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.javaplugin.JavaPluginActivator;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.maven.validation.InputPreProcessor;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.capgemini.cobigen.propertyplugin.PropertyMergerPluginActivator;
import com.capgemini.cobigen.textmerger.TextMergerPluginActivator;
import com.capgemini.cobigen.xmlplugin.XmlPluginActivator;
import com.google.common.collect.Lists;

import freemarker.template.TemplateException;

/**
 * CobiGen generation Mojo, which handles generation using a configuration folder/archive
 * @author mbrunnli (08.02.2015)
 */
@Mojo(name = "generate", requiresDependencyResolution = ResolutionScope.RUNTIME, requiresProject = true,
    defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyCollection = ResolutionScope.RUNTIME)
public class GenerateMojo extends AbstractMojo {

    static {
        PluginRegistry.loadPlugin(JavaPluginActivator.class);
        PluginRegistry.loadPlugin(XmlPluginActivator.class);
        PluginRegistry.loadPlugin(PropertyMergerPluginActivator.class);
        PluginRegistry.loadPlugin(TextMergerPluginActivator.class);
    }

    /**
     * Maven Project, which is currently built
     */
    @Component
    private MavenProject project;

    /**
     * {@link MojoExecution} to retrieve the pom-declared plugin dependencies.
     */
    @Component
    private MojoExecution execution;

    /**
     * Configuration folder to be used
     */
    @Parameter
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
    public void execute() throws MojoFailureException, MojoFailureException {

        List<Object> inputs = collectInputs();
        if (inputs.isEmpty()) {
            getLog().info("Nothing to be generated!");
            getLog().info("");
            return;
        }

        try {
            CobiGen cobiGen;
            if (configurationFolder != null) {
                cobiGen = new CobiGen(configurationFolder.toURI());
            } else {
                List<Dependency> dependencies =
                    execution.getMojoDescriptor().getPluginDescriptor().getPlugin().getDependencies();
                if (dependencies != null && !dependencies.isEmpty()) {
                    Dependency dependency = dependencies.iterator().next();
                    Artifact templatesArtifact =
                        execution.getMojoDescriptor().getPluginDescriptor().getArtifactMap()
                            .get(dependency.getGroupId() + ":" + dependency.getArtifactId());
                    cobiGen = new CobiGen(templatesArtifact.getFile().toURI());
                } else {
                    throw new MojoFailureException(
                        "No configuration injected. Please inject a 'configurationFolder' to a local folder"
                            + " or inject an archive as plugin dependency.");
                }
            }

            generateFromIncrements(cobiGen, inputs);
            generateFromTemplates(cobiGen, inputs);
        } catch (InvalidConfigurationException | IOException e) {
            getLog().error("An error occured while executing CobiGen", e);
            throw new MojoFailureException("An error occured while executing CobiGen: " + e.getMessage());
        }
    }

    /**
     * Collects/Converts all inputs from {@link #inputPackageFolders} and {@link #inputFiles} into CobiGen
     * compatible formats
     * @return the list of CobiGen compatible inputs
     * @throws MojoFailureException
     *             if the project {@link ClassLoader} could not be retrieved
     * @author mbrunnli (16.02.2015)
     */
    private List<Object> collectInputs() throws MojoFailureException {
        List<Object> inputs = Lists.newLinkedList();

        ClassLoader cl = getProjectClassLoader();
        if (inputPackageFolders != null && !inputPackageFolders.isEmpty()) {
            for (Map.Entry<String, File> inputPackage : inputPackageFolders.entrySet()) {
                PackageFolder packageFolder =
                    new PackageFolder(inputPackage.getValue().toURI(), inputPackage.getKey(), cl);
                inputs.add(packageFolder);
            }
        }

        if (inputFiles != null && !inputFiles.isEmpty()) {
            for (File file : inputFiles) {
                Object input = InputPreProcessor.process(file, cl);
                inputs.add(input);
            }
        }
        return inputs;
    }

    /**
     * Generates all increments for each input.
     * @param cobiGen
     *            generator instance to be used for generation
     * @param inputs
     *            to be used for generation
     * @throws MojoFailureException
     *             if any problem occurred while generation
     * @author mbrunnli (09.02.2015)
     */
    private void generateFromIncrements(CobiGen cobiGen, List<Object> inputs) throws MojoFailureException {
        if (increments != null && !increments.isEmpty()) {
            for (Object input : inputs) {
                List<IncrementTo> matchingIncrements = cobiGen.getMatchingIncrements(input);
                for (IncrementTo increment : matchingIncrements) {
                    if (increments.contains(increment.getId())) {
                        try {
                            cobiGen.generate(input, increment, forceOverride);
                        } catch (IOException | TemplateException | MergeException e) {
                            String errorMessage;
                            if (input instanceof PackageFolder) {
                                errorMessage =
                                    "An exception occured while generating increment with id '"
                                        + increment.getId() + "' for input package/folder '"
                                        + ((PackageFolder) input).getLocation() + "'";
                            } else {
                                errorMessage =
                                    "An exception occured while generating increment with id '"
                                        + increment.getId() + "' for input file '" + ((File) input).toURI()
                                        + "'";
                            }
                            getLog().error(errorMessage, e);
                            throw new MojoFailureException(errorMessage);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates all templates for each input.
     * @param cobiGen
     *            generator instance to be used for generation
     * @param inputs
     *            to be used for generation
     * @throws MojoFailureException
     *             if any problem occurred while generation
     * @author mbrunnli (09.02.2015)
     */
    private void generateFromTemplates(CobiGen cobiGen, List<Object> inputs) throws MojoFailureException {
        if (inputFiles != null && !inputFiles.isEmpty()) {
            for (Object input : inputs) {
                List<IncrementTo> matchingIncrements = cobiGen.getMatchingIncrements(input);
                for (IncrementTo template : matchingIncrements) {
                    if (increments.contains(template.getId())) {
                        try {
                            cobiGen.generate(input, template, forceOverride);
                        } catch (IOException | TemplateException | MergeException e) {
                            String errorMessage;
                            if (input instanceof PackageFolder) {
                                errorMessage =
                                    "An exception occured while generating template with id '"
                                        + template.getId() + "' for input package/folder '"
                                        + ((PackageFolder) input).getLocation() + "'";
                            } else {
                                errorMessage =
                                    "An exception occured while generating template with id '"
                                        + template.getId() + "' for input file '" + ((File) input).toURI()
                                        + "'";
                            }
                            getLog().error(errorMessage, e);
                            throw new MojoFailureException(errorMessage);
                        }
                    }
                }
            }
        }
    }

    /**
     * Builds the {@link ClassLoader} for the current maven project
     * @return the project {@link ClassLoader}
     * @throws MojoFailureException
     *             if the maven project dependencies could not be resolved
     * @author mbrunnli (11.02.2015)
     */
    private ClassLoader getProjectClassLoader() throws MojoFailureException {
        List<String> classpathElements = null;
        try {
            classpathElements = project.getCompileClasspathElements();
            List<URL> projectClasspathList = new ArrayList<>();
            for (String element : classpathElements) {
                try {
                    projectClasspathList.add(new File(element).toURI().toURL());
                } catch (MalformedURLException e) {
                    getLog().error(element + " is an invalid classpath element", e);
                    throw new MojoFailureException(element + " is an invalid classpath element");
                }
            }
            return new URLClassLoader(projectClasspathList.toArray(new URL[0]));
        } catch (DependencyResolutionRequiredException e) {
            getLog().error("Dependency resolution failed", e);
            throw new MojoFailureException("Dependency resolution failed");
        }
    }

}
