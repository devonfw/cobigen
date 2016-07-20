package com.capgemini.cobigen.maven;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

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
import com.capgemini.cobigen.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.javaplugin.JavaPluginActivator;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.maven.validation.InputPreProcessor;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.capgemini.cobigen.propertyplugin.PropertyMergerPluginActivator;
import com.capgemini.cobigen.textmerger.TextMergerPluginActivator;
import com.capgemini.cobigen.xmlplugin.XmlPluginActivator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import freemarker.template.TemplateException;

/**
 * CobiGen generation Mojo, which handles generation using a configuration folder/archive
 * @author mbrunnli (08.02.2015)
 */
@Mojo(name = "generate", requiresDependencyResolution = ResolutionScope.TEST, requiresProject = true,
    defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyCollection = ResolutionScope.TEST)
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
     * Input packages
     */
    @Parameter
    private List<String> inputPackages;

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
     * Destination root path the relative paths of templates will be resolved with.
     */
    @Parameter(defaultValue = "${basedir}")
    private File destinationRoot;

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
                    Artifact templatesArtifact = execution.getMojoDescriptor().getPluginDescriptor().getArtifactMap()
                        .get(dependency.getGroupId() + ":" + dependency.getArtifactId());
                    cobiGen = new CobiGen(templatesArtifact.getFile().toURI());
                } else {
                    throw new MojoFailureException(
                        "No configuration injected. Please inject a 'configurationFolder' to a local folder"
                            + " or inject an archive as plugin dependency.");
                }
            }
            cobiGen.setContextSetting(ContextSetting.GenerationTargetRootPath, destinationRoot.getAbsolutePath());

            generateFromIncrements(cobiGen, inputs);
            generateFromTemplates(cobiGen, inputs);
        } catch (InvalidConfigurationException | IOException e) {
            getLog().error("An error occured while executing CobiGen", e);
            throw new MojoFailureException("An error occured while executing CobiGen: " + e.getMessage());
        }
    }

    /**
     * Collects/Converts all inputs from {@link #inputPackages} and {@link #inputFiles} into CobiGen
     * compatible formats
     * @return the list of CobiGen compatible inputs
     * @throws MojoFailureException
     *             if the project {@link ClassLoader} could not be retrieved
     * @author mbrunnli (16.02.2015)
     */
    private List<Object> collectInputs() throws MojoFailureException {
        getLog().debug("Collect inputs...");
        List<Object> inputs = Lists.newLinkedList();

        ClassLoader cl = getProjectClassLoader();
        if (inputPackages != null && !inputPackages.isEmpty()) {
            for (String inputPackage : inputPackages) {
                getLog().debug("Resolve package '" + inputPackage + "'");

                // collect all source roots to resolve input paths
                List<String> sourceRoots = Lists.newLinkedList();
                sourceRoots.addAll(project.getCompileSourceRoots());
                sourceRoots.addAll(project.getTestCompileSourceRoots());

                boolean sourceFound = false;
                List<Path> sourcePathsObserved = Lists.newLinkedList();
                for (String sourceRoot : sourceRoots) {
                    String packagePath =
                        inputPackage.replaceAll("\\.", Matcher.quoteReplacement(System.getProperty("file.separator")));
                    Path sourcePath = Paths.get(sourceRoot, packagePath);
                    getLog().debug("Checking source path " + sourcePath);
                    if (exists(sourcePath) && isReadable(sourcePath) && isDirectory(sourcePath)) {
                        PackageFolder packageFolder = new PackageFolder(sourcePath.toUri(), inputPackage, cl);
                        inputs.add(packageFolder);
                        sourceFound = true;
                    } else {
                        sourcePathsObserved.add(sourcePath);
                    }
                }

                if (!sourceFound) {
                    throw new MojoFailureException("Currently, packages as inputs are only supported "
                        + "if defined as sources in the current project to be build. Having searched for sources at paths: "
                        + sourcePathsObserved);
                }
            }
        }

        if (inputFiles != null && !inputFiles.isEmpty()) {
            for (File file : inputFiles) {
                getLog().debug("Resolve file '" + file.toURI().toString() + "'");
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
                List<String> configuredIncrements = new LinkedList<>(increments);
                for (IncrementTo increment : matchingIncrements) {
                    if (increments.contains(increment.getId())) {
                        try {
                            cobiGen.generate(input, increment, forceOverride);
                        } catch (IOException | TemplateException | MergeException e) {
                            String errorMessage;
                            if (input instanceof PackageFolder) {
                                errorMessage =
                                    "An exception occured while generating increment with id '" + increment.getId()
                                        + "' for input package/folder '" + ((PackageFolder) input).getLocation() + "'";
                            } else {
                                errorMessage = "An exception occured while generating increment with id '"
                                    + increment.getId() + "' for input file '" + ((File) input).toURI() + "'";
                            }
                            getLog().error(errorMessage, e);
                            throw new MojoFailureException(errorMessage);
                        }
                        configuredIncrements.remove(increment.getId());
                    }
                }
                // error handling for increments not found
                if (!configuredIncrements.isEmpty()) {
                    if (input instanceof PackageFolder) {
                        throw new MojoFailureException(
                            "Increments with ids '" + configuredIncrements + "' not matched for input '"
                                + ((PackageFolder) input).getLocation() + "' by provided CobiGen configuration.");
                    } else {
                        throw new MojoFailureException("Increments with ids '" + configuredIncrements
                            + "' not matched for input '" + input + "' by provided CobiGen configuration.");
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
                List<TemplateTo> matchingTemplates = cobiGen.getMatchingTemplates(input);
                List<String> configuredTemplates = new LinkedList<>(templates);
                for (TemplateTo template : matchingTemplates) {
                    if (templates.contains(template.getId())) {
                        try {
                            cobiGen.generate(input, template, forceOverride);
                        } catch (IOException | TemplateException | MergeException e) {
                            String errorMessage;
                            if (input instanceof PackageFolder) {
                                errorMessage =
                                    "An exception occured while generating template with id '" + template.getId()
                                        + "' for input package/folder '" + ((PackageFolder) input).getLocation() + "'";
                            } else {
                                errorMessage = "An exception occured while generating template with id '"
                                    + template.getId() + "' for input file '" + ((File) input).toURI() + "'";
                            }
                            getLog().error(errorMessage, e);
                            throw new MojoFailureException(errorMessage);
                        }
                    } else {
                        throw new MojoFailureException("Increment with id '" + template.getId()
                            + "' not found in provided CobiGen configuration.");
                    }
                }
                // error handling for increments not found
                if (!configuredTemplates.isEmpty()) {
                    if (input instanceof PackageFolder) {
                        throw new MojoFailureException(
                            "Templates with ids '" + configuredTemplates + "' not matched for package folder '"
                                + ((PackageFolder) input).getLocation() + "' by provided CobiGen configuration.");
                    } else {
                        throw new MojoFailureException("Templates with ids '" + configuredTemplates
                            + "' not matched for input '" + input + "' by provided CobiGen configuration.");
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
        Set<String> classpathElements = Sets.newHashSet();
        try {
            classpathElements.addAll(project.getCompileClasspathElements());
            classpathElements.addAll(project.getTestClasspathElements());
            List<URL> projectClasspathList = new ArrayList<>();
            for (String element : classpathElements) {
                try {
                    URL url = new File(element).toURI().toURL();
                    getLog().debug("Add Classpath-URL: " + url);
                    projectClasspathList.add(url);
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
