package com.capgemini.cobigen.maven;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.io.Charsets;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.InputInterpreter;
import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.exception.InputReaderException;
import com.capgemini.cobigen.api.extension.GeneratorPluginActivator;
import com.capgemini.cobigen.api.to.GenerableArtifact;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.IncrementTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.impl.PluginRegistry;
import com.capgemini.cobigen.impl.TemplateEngineRegistry;
import com.capgemini.cobigen.maven.utils.MojoUtils;
import com.capgemini.cobigen.maven.validation.InputPreProcessor;
import com.capgemini.cobigen.tempeng.freemarker.FreeMarkerTemplateEngine;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * CobiGen generation Mojo, which handles generation using a configuration folder/archive
 */
@Mojo(name = "generate", requiresDependencyResolution = ResolutionScope.TEST, requiresProject = true,
    defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyCollection = ResolutionScope.TEST)
public class GenerateMojo extends AbstractMojo {

    static {
        TemplateEngineRegistry.register(FreeMarkerTemplateEngine.class);
    }

    /** Maven Project, which is currently built */
    @Component
    private MavenProject project;

    /** {@link MojoExecution} to retrieve the pom-declared plugin dependencies. */
    @Component
    private MojoExecution execution;

    /** {@link PluginDescriptor} to retrieve the ClassRealm for this Plug-in */
    @Component
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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Iterator<GeneratorPluginActivator> pluginIterator =
            ServiceLoader.load(GeneratorPluginActivator.class).iterator();
        if (pluginIterator.hasNext()) {
            getLog().info("Loading Plugins");
        } else {
            getLog().error("No Plugins Found!");
        }
        while (pluginIterator.hasNext()) {
            GeneratorPluginActivator loadedPlugin = pluginIterator.next();
            getLog().debug(" * " + loadedPlugin.getClass().getName() + " found");
            PluginRegistry.loadPlugin(loadedPlugin.getClass());
        }

        List<Object> inputs = collectInputs(CobiGenFactory.getInputInterpreter());
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
        CobiGen cobiGen;
        if (configurationFolder != null) {
            try {
                cobiGen = CobiGenFactory.create(configurationFolder.toURI());
            } catch (IOException e) {
                throw new MojoExecutionException("The configured configuration folder could not be read.", e);
            }
        } else {
            List<Dependency> dependencies =
                execution.getMojoDescriptor().getPluginDescriptor().getPlugin().getDependencies();

            if (dependencies != null && !dependencies.isEmpty()) {
                Dependency dependency = dependencies.iterator().next();
                Artifact templatesArtifact = execution.getMojoDescriptor().getPluginDescriptor().getArtifactMap()
                    .get(dependency.getGroupId() + ":" + dependency.getArtifactId());
                try {
                    cobiGen = CobiGenFactory.create(templatesArtifact.getFile().toURI());
                } catch (IOException e) {
                    throw new MojoExecutionException("The templates artifact could not be read in location '"
                        + templatesArtifact.getFile().toURI() + "'.", e);
                }
            } else {
                throw new MojoFailureException(
                    "No configuration injected. Please inject a 'configurationFolder' to a local folder"
                        + " or inject an archive as plugin dependency.");
            }
        }
        List<GenerableArtifact> generableArtifacts = collectIncrements(cobiGen, inputs);
        generableArtifacts.addAll(collectTemplates(cobiGen, inputs));

        try {
            for (Object input : inputs) {
                getLog().debug("Invoke CobiGen for input " + input);
                List<Class<?>> utilClasses = resolveUtilClasses();
                GenerationReportTo report = cobiGen.generate(input, generableArtifacts,
                    Paths.get(destinationRoot.toURI()), forceOverride, utilClasses);
                if (!report.isSuccessful()) {
                    for (Throwable e : report.getErrors()) {
                        getLog().error(e.getMessage(), e);
                    }
                    throw new MojoFailureException("Generation not successfull", report.getErrors().get(0));
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
     * Walks the class path in search of an 'context.xml' resource to identify the enclosing folder or jar
     * file. That location is then searched for class files and a list with those loaded classes is returned
     * @return a List of Classes for template generation.
     */
    private List<Class<?>> resolveUtilClasses() {
        final List<Class<?>> result = new LinkedList<>();
        final ClassRealm classRealm = pluginDescriptor.getClassRealm();
        if (configurationFolder != null) {
            try {
                pluginDescriptor.getClassRealm().addURL(configurationFolder.toURI().toURL());
                getLog().debug("Added " + configurationFolder.toURI().toURL().toString() + " to class path");
            } catch (MalformedURLException e) {
                getLog().error("Could not add configuration folder " + configurationFolder.toString(), e);
            }
        }
        URL contextXmlUrl = classRealm.getResource("context.xml");
        if (contextXmlUrl == null) {
            getLog().error("No context.xml could be found in the classpath!");
            return null;
        }
        getLog().debug("Found context.xml @ " + contextXmlUrl.toString());
        final List<String> foundClasses = new LinkedList<>();
        if (contextXmlUrl.toString().startsWith("jar")) {
            try {
                // Get the URI of the jar from the URL of the contained context.xml
                URI jarUri = URI.create(contextXmlUrl.toString().split("!")[0]);
                FileSystem jarfs = FileSystems.getFileSystem(jarUri);

                // walk the jar file
                getLog().debug("Searching for classes in " + jarUri.toString());
                Files.walkFileTree(jarfs.getPath("/"), new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.toString().endsWith(".class")) {
                            getLog().debug("    * Found clas file " + file.toString());
                            // remove the leading '/' and the trailing '.class'
                            String fileName = file.toString().substring(1, file.toString().length() - 6);
                            // replace the path separator '/' with package separator '.' and add it to the
                            // list of found files
                            foundClasses.add(fileName.replace("/", "."));
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        // Log errors but do not throw an exception
                        getLog().warn(exc);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                getLog().error(e);
            }
            for (String className : foundClasses) {
                try {
                    result.add(classRealm.loadClass(className));
                } catch (ClassNotFoundException e) {
                    getLog().warn("Could not load " + className + " from classpath", e);
                }
            }
        } else {
            final Path configFolder = Paths.get(URI.create(contextXmlUrl.toString())).getParent();
            getLog().debug("Searching for classes in " + configFolder.toString());
            final List<Path> foundPaths = new LinkedList<>();
            try {
                Files.walkFileTree(configFolder, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.toString().endsWith(".class")) {
                            getLog().debug("    * Found class file " + file.toString());
                            foundPaths.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        // Log errors but do not throw an exception
                        getLog().warn(exc);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                getLog().error(e);
            }

            Path commonParent = new MojoUtils().getCommonParent(foundPaths);
            while (!commonParent.equals(configFolder)) {
                try {
                    pluginDescriptor.getClassRealm().addURL(commonParent.toUri().toURL());
                    getLog().debug("Added " + commonParent.toUri().toURL().toString() + " to class path");
                } catch (MalformedURLException e) {
                    getLog().error("Could not add folder " + commonParent.toString(), e);
                }
                commonParent = commonParent.getParent();
            }

            for (Path path : foundPaths) {
                try {
                    result.add(loadClassByPath(path, classRealm));
                } catch (ClassNotFoundException e) {
                    getLog().error(e);
                }
            }
        }

        return result;

    }

    /**
     * Collects/Converts all inputs from {@link #inputPackages} and {@link #inputFiles} into CobiGen
     * compatible formats
     * @param inputInterpreter
     *            to interpret input objects
     * @return the list of CobiGen compatible inputs
     * @throws MojoFailureException
     *             if the project {@link ClassLoader} could not be retrieved
     */
    private List<Object> collectInputs(InputInterpreter inputInterpreter) throws MojoFailureException {
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
                        Object packageFolder;
                        try {
                            packageFolder = inputInterpreter.read("java", Paths.get(sourcePath.toUri()), Charsets.UTF_8,
                                inputPackage, cl);
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
                Object input = InputPreProcessor.process(inputInterpreter, file, cl);
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
                URL contextURL = loader.getResource("context.xml");
                if (contextURL != null) {
                    getLog().debug("Found content.xml @ " + contextURL.toString());
                } else {
                    getLog().warn("No context.xml found in classpath");
                }

            }
            return loader;
        } catch (DependencyResolutionRequiredException e) {
            getLog().error("Dependency resolution failed", e);
            throw new MojoFailureException("Dependency resolution failed", e);
        }
    }

    /**
     * Tries to load a class over it's file path. If the path is /a/b/c/Some.class this method tries to load
     * the following classes in this order: <list>
     * <li>Some</li>
     * <li>c.Some</li>
     * <li>b.c.Some</li>
     * <li>a.b.c.Some</> </list>
     * @param classPath
     *            the {@link Path} of the Class file
     * @param cl
     *            the used ClassLoader
     * @return Class<?> of the class file
     * @throws ClassNotFoundException
     *             if no class could be found all the way up to the path root
     */
    private Class<?> loadClassByPath(Path classPath, ClassLoader cl) throws ClassNotFoundException {
        // Get a list with all path segments, starting with the class name
        Queue<String> pathSegments = new LinkedList<>();
        // Split the path by the systems file separator and without the .class suffix
        String[] pathSegmentsArray = classPath.toString().substring(0, classPath.toString().length() - 6)
            .split("\\".equals(File.separator) ? "\\\\" : File.separator);
        for (int i = pathSegmentsArray.length - 1; i > -1; i--) {
            pathSegments.add(pathSegmentsArray[i]);
        }

        if (!pathSegments.isEmpty()) {
            String className = "";
            while (!pathSegments.isEmpty()) {
                if (className == "") {
                    className = pathSegments.poll();
                } else {
                    className = pathSegments.poll() + "." + className;
                }
                try {
                    getLog().debug("Try to load " + className);
                    return cl.loadClass(className);
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    continue;
                }
            }
        }
        throw new ClassNotFoundException("Could not find class on path " + classPath.toString());

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
