package com.devonfw.cobigen.maven;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.io.Charsets;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.to.GenerableArtifact;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.util.TemplatesClassloaderUtil;
import com.devonfw.cobigen.maven.validation.InputPreProcessor;
import com.google.inject.internal.util.Sets;

/**
 * CobiGen generation Mojo, which handles generation using a configuration folder/archive
 */
@Mojo(name = "generate", requiresDependencyResolution = ResolutionScope.TEST, requiresProject = true,
    defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyCollection = ResolutionScope.TEST)
public class GenerateMojo extends AbstractMojo {

    /** Keyword to generate all of a kind */
    public static final String ALL = "ALL";

    /** Maven Project, which is currently built */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /** {@link MojoExecution} to retrieve the pom-declared plugin dependencies. */
    @Parameter(defaultValue = "${mojoExecution}", readonly = true)
    private MojoExecution execution;

    /** {@link PluginDescriptor} to retrieve the ClassRealm for this Plug-in */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    public PluginDescriptor pluginDescriptor;

    /** Configuration folder to be used */
    @Parameter
    private File configurationFolder;

    /** Increments to be generated */
    @Parameter
    private List<String> increments;

    /** Templates to be generated */
    @Parameter
    private List<String> templates;

    /** Input packages */
    @Parameter
    private List<String> inputPackages;

    /** Input files */
    @Parameter
    private List<File> inputFiles;

    /** States, whether the generation force overriding files and contents */
    @Parameter(defaultValue = "false")
    private boolean forceOverride;

    /** Destination root path the relative paths of templates will be resolved with. */
    @Parameter(defaultValue = "${basedir}")
    private File destinationRoot;

    /** Determines whether the maven build should fail if nothing has been generated on execution */
    @Parameter(defaultValue = "false")
    private boolean failOnNothingGenerated;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        CobiGen cobiGen = null;
        try {
            cobiGen = createCobiGenInstance();
        } catch (MojoExecutionException e) {
            throw new MojoExecutionException("An error occured while creating the CobiGen instance", e);
        }

        List<Object> inputs = collectInputs(cobiGen);
        if (inputs.isEmpty()) {
            getLog().info("No inputs specified for generation!");
            getLog().info("");
            return;
        }
        if ((templates == null || templates.isEmpty()) && (increments == null || increments.isEmpty())) {
            getLog().info("No templates/increments specified for generation!");
            getLog().info("");
            return;
        }
        List<GenerableArtifact> generableArtifacts = collectIncrements(cobiGen, inputs);
        generableArtifacts.addAll(collectTemplates(cobiGen, inputs));

        try {
            for (Object input : inputs) {
                getLog().debug("Invoke CobiGen for input " + input);

                final ClassRealm classRealm = pluginDescriptor.getClassRealm();

                GenerationReportTo report;
                if (configurationFolder != null) {
                    classRealm.addURL(configurationFolder.toURI().toURL());
                    report = cobiGen.generate(input, generableArtifacts, Paths.get(destinationRoot.toURI()),
                        forceOverride, classRealm, null, configurationFolder.toPath());
                } else {
                    report = cobiGen.generate(input, generableArtifacts, Paths.get(destinationRoot.toURI()),
                        forceOverride, classRealm, null, null);
                }

                if (!report.isSuccessful()) {
                    for (Throwable e : report.getErrors()) {
                        getLog().error(e.getMessage(), e);
                    }
                    throw new MojoFailureException("Generation not successfull", report.getErrors().get(0));
                }

                if (report.getGeneratedFiles().isEmpty() && failOnNothingGenerated) {
                    throw new MojoFailureException("The execution '" + execution.getExecutionId()
                        + "' of cobigen-maven-plugin resulted in no file to be generated!");
                }
            }
        } catch (CobiGenRuntimeException e) {
            getLog().error(e.getMessage(), e);
            throw new MojoFailureException(e.getMessage(), e);
        } catch (MojoFailureException e) {
            throw e;
        } catch (Throwable e) {
            getLog().error("An error occured while executing CobiGen: " + e.getMessage(), e);
            throw new MojoFailureException("An error occured while executing CobiGen: " + e.getMessage(), e);
        }
    }

    /**
     * Creates an instance of {@link CobiGen} based on a given configuration project or configuration jar.
     * @return the initialized {@link CobiGen} instance
     * @throws MojoExecutionException
     *             if the configuration could not be read
     */
    private CobiGen createCobiGenInstance() throws MojoExecutionException {
        CobiGen cobiGen;

        if (configurationFolder != null) {

            getLog().debug("configurationFolder found in:" + configurationFolder.toURI().toString());
            try {
                cobiGen = CobiGenFactory.create(configurationFolder.toURI());
            } catch (IOException e) {
                throw new MojoExecutionException("The configured configuration folder could not be read.", e);
            }
        } else {

            final ClassRealm classRealm = pluginDescriptor.getClassRealm();
            URL contextConfigurationLocation = TemplatesClassloaderUtil.getContextConfiguration(classRealm);

            URI configFile = URI.create(contextConfigurationLocation.getFile().toString().split("!")[0]);

            getLog().debug("reading configuration from file:" + configFile.toString());

            try {
                cobiGen = CobiGenFactory.create(configFile);
            } catch (IOException e) {
                throw new MojoExecutionException("The templates artifact could not be read in location '"
                    + contextConfigurationLocation.getFile() + "'.", e);
            }
        }
        return cobiGen;
    }

    /**
     * Collects/Converts all inputs from {@link #inputPackages} and {@link #inputFiles} into CobiGen
     * compatible formats
     * @param cobigen
     *            to interpret input objects
     * @return the list of CobiGen compatible inputs
     * @throws MojoFailureException
     *             if the project {@link ClassLoader} could not be retrieved
     */
    private List<Object> collectInputs(CobiGen cobigen) throws MojoFailureException {
        getLog().debug("Collect inputs...");
        List<Object> inputs = new LinkedList<>();

        ClassLoader cl = getProjectClassLoader();
        if (inputPackages != null && !inputPackages.isEmpty()) {
            for (String inputPackage : inputPackages) {
                getLog().debug("Resolve package '" + inputPackage + "'");

                // collect all source roots to resolve input paths
                List<String> sourceRoots = new LinkedList<>();
                sourceRoots.addAll(project.getCompileSourceRoots());
                sourceRoots.addAll(project.getTestCompileSourceRoots());

                boolean sourceFound = false;
                List<Path> sourcePathsObserved = new LinkedList<>();
                for (String sourceRoot : sourceRoots) {
                    String packagePath =
                        inputPackage.replaceAll("\\.", Matcher.quoteReplacement(System.getProperty("file.separator")));
                    Path sourcePath = Paths.get(sourceRoot, packagePath);
                    getLog().debug("Checking source path " + sourcePath);
                    if (exists(sourcePath) && isReadable(sourcePath) && isDirectory(sourcePath)) {
                        Object packageFolder;
                        try {
                            packageFolder =
                                cobigen.read("java", Paths.get(sourcePath.toUri()), Charsets.UTF_8, inputPackage, cl);
                            inputs.add(packageFolder);
                            sourceFound = true;
                        } catch (InputReaderException e) {
                            throw new MojoFailureException("Could not read input package " + sourcePath.toString(), e);
                        }

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
                Object input = InputPreProcessor.process(cobigen, file, cl);
                inputs.add(input);
            }
        }
        getLog().debug(inputs.size() + " inputs collected.");
        return inputs;
    }

    /**
     * Generates all increments for each input.
     * @param cobiGen
     *            generator instance to be used for generation
     * @param inputs
     *            to be used for generation
     * @return the collected increments to be generated
     * @throws MojoFailureException
     *             if the maven configuration does not match cobigen configuration (context.xml)
     */
    private List<GenerableArtifact> collectIncrements(CobiGen cobiGen, List<Object> inputs)
        throws MojoFailureException {
        List<GenerableArtifact> generableArtifacts = new ArrayList<>();
        if (increments != null && !increments.isEmpty()) {
            if (increments.contains(ALL)) {
                if (increments.size() > 1) {
                    throw new MojoFailureException(
                        "You specified the 'ALL' increment to generate all available increments next to another increment, which was most probably not intended.");
                }

                for (Object input : inputs) {
                    generableArtifacts.addAll(cobiGen.getMatchingIncrements(input));
                }
            } else {
                for (Object input : inputs) {
                    List<IncrementTo> matchingIncrements = cobiGen.getMatchingIncrements(input);
                    List<String> configuredIncrements = new LinkedList<>(increments);
                    for (IncrementTo increment : matchingIncrements) {
                        if (increments.contains(increment.getId())) {
                            generableArtifacts.add(increment);
                            configuredIncrements.remove(increment.getId());
                        }
                    }
                    // error handling for increments not found
                    if (!configuredIncrements.isEmpty()) {
                        throw new MojoFailureException(
                            "Increments with ids '" + configuredIncrements + "' not matched for input '"
                                + getStringRepresentation(input) + "' by provided CobiGen configuration.");
                    }
                }
            }
        }
        return generableArtifacts;
    }

    /**
     * Generates all templates for each input.
     * @param cobiGen
     *            generator instance to be used for generation
     * @param inputs
     *            to be used for generation
     * @return the collected templates to be generated
     * @throws MojoFailureException
     *             if any problem occurred while generation
     */
    private List<GenerableArtifact> collectTemplates(CobiGen cobiGen, List<Object> inputs) throws MojoFailureException {
        List<GenerableArtifact> generableArtifacts = new ArrayList<>();
        if (templates != null && !templates.isEmpty()) {
            if (templates.contains(ALL)) {
                if (templates.size() > 1) {
                    throw new MojoFailureException(
                        "You specified the 'ALL' template to generate all available templates next to another template, which was most probably not intended.");
                }

                for (Object input : inputs) {
                    generableArtifacts.addAll(cobiGen.getMatchingTemplates(input));
                }
            } else {
                for (Object input : inputs) {
                    List<TemplateTo> matchingTemplates = cobiGen.getMatchingTemplates(input);
                    List<String> configuredTemplates = new LinkedList<>(templates);
                    for (TemplateTo template : matchingTemplates) {
                        if (templates.contains(template.getId())) {
                            generableArtifacts.add(template);
                            configuredTemplates.remove(template.getId());
                        }
                    }
                    // error handling for increments not found
                    if (!configuredTemplates.isEmpty()) {
                        throw new MojoFailureException("Templates with ids '" + configuredTemplates
                            + "' did not match package in folder '" + getStringRepresentation(input) + "'.");

                    }
                }
            }
        }
        return generableArtifacts;
    }

    /**
     * Builds the {@link ClassLoader} for the current maven project based on the plugins class loader
     * @return the project {@link ClassLoader}
     * @throws MojoFailureException
     *             if the maven project dependencies could not be resolved
     */
    private ClassLoader getProjectClassLoader() throws MojoFailureException {
        Set<String> classpathElements = Sets.newHashSet();
        try {
            classpathElements.addAll(project.getCompileClasspathElements());
            classpathElements.addAll(project.getTestClasspathElements());
            ClassRealm loader = pluginDescriptor.getClassRealm();
            getLog().debug("Fetched ClassRealm for Plug-In");
            for (String element : classpathElements) {
                try {
                    URL url = new File(element).toURI().toURL();
                    getLog().debug("Add Classpath-URL: " + url);
                    loader.addURL(url);
                } catch (MalformedURLException e) {
                    getLog().error(element + " is an invalid classpath element", e);
                    throw new MojoFailureException(element + " is an invalid classpath element");
                }
            }
            if (getLog().isDebugEnabled()) {
                getLog().debug("ClassLoader knows:");
                for (URL url : loader.getURLs()) {
                    getLog().debug("    * " + url.toString());
                }
            }
            return loader;
        } catch (DependencyResolutionRequiredException e) {
            getLog().error("Dependency resolution failed", e);
            throw new MojoFailureException("Dependency resolution failed", e);
        }
    }

    /**
     * Returns a String representation of an object
     * @param object
     *            to be represented
     * @return A String representing the object. Uses Arrays.toString() for arrays and toString() otherwise
     */
    private String getStringRepresentation(Object object) {
        if (object instanceof Object[]) {
            return Arrays.toString((Object[]) object);
        } else {
            return object.toString();
        }
    }
}
