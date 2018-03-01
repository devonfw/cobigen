package com.capgemini.cobigen.eclipse.generator;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.constants.ConfigurationConstants;
import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.IncrementTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.eclipse.common.exceptions.CobiGenEclipseRuntimeException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.capgemini.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.capgemini.cobigen.eclipse.common.tools.EclipseJavaModelUtil;
import com.capgemini.cobigen.eclipse.common.tools.MapUtils;
import com.capgemini.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.capgemini.cobigen.eclipse.generator.entity.ComparableIncrement;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/** Wrapper for CobiGen providing an eclipse compliant API. */
public abstract class CobiGenWrapper extends AbstractCobiGenWrapper {

    /** Increment ID of the virtual increment representing all templates */
    public static final String ALL_INCREMENT_ID = "all";

    /** Increment Name of the virtual increment representing all templates */
    public static final String ALL_INCREMENT_NAME = "All";

    /** Assigning logger to CobiGenWrapper */
    private static final Logger LOG = LoggerFactory.getLogger(CobiGenWrapper.class);

    /** States, whether the input is unique and a container */
    private boolean singleNonContainerInput;

    /** Current registered input objects */
    private List<Object> inputs;

    /** All matching templates for the currently configured {@link #inputs input objects} */
    private List<TemplateTo> matchingTemplates = Lists.newLinkedList();

    /** Cache for destination path resolution for templates */
    private Map<TemplateTo, Path> resolvedDestPathsCache = Maps.newHashMap();

    /** Cache, storing all templates of any increment with the template's workspace related paths */
    private Map<IncrementTo, Map<String, Set<TemplateTo>>> incrementToTemplateWorkspacePathsCache = Maps.newHashMap();

    /** Cached projects in workspace */
    private Map<Path, IProject> projectsInWorkspace = Maps.newHashMap();

    /**
     * Cache of external workspace paths, which will be filled after calling
     * {@link #getTemplateDestinationPaths(Collection)}.
     */
    private Set<String> workspaceExternalPath = Sets.newHashSet();

    /**
     * Creates a new {@link CobiGenWrapper}
     * @param cobiGen
     *            initialized {@link CobiGen} instance
     * @param inputs
     *            list of inputs for generation
     * @param inputSourceProject
     *            project from which the inputs have been selected
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration folder does not exist
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     */
    public CobiGenWrapper(CobiGen cobiGen, IProject inputSourceProject, List<Object> inputs)
        throws GeneratorProjectNotExistentException, InvalidConfigurationException {
        super(cobiGen, inputSourceProject);
        setInputs(inputs);

        for (IProject proj : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            projectsInWorkspace.put(proj.getLocation().toFile().toPath(), proj);
        }
    }

    /**
     * Sets the given input objects for generation
     * @param inputs
     *            input objects for generation
     */
    private void setInputs(List<Object> inputs) {
        this.inputs = inputs;

        LOG.info("Set new generator inputs: {}", inputs);
        matchingTemplates = Lists.newLinkedList();

        LOG.debug("Calculating matching templates...");
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
        AnalyzeInputJob job = new AnalyzeInputJob(cobiGen, inputs);
        try {
            dialog.run(true, false, job);
        } catch (InvocationTargetException e) {
            LOG.error("An internal error occured while invoking input analyzer job.", e);
            throw new CobiGenEclipseRuntimeException("An internal error occured while invoking input analyzer job", e);
        } catch (InterruptedException e) {
            LOG.warn("The working thread doing the input analyzer job has been interrupted.", e);
            throw new CobiGenEclipseRuntimeException(
                "The working thread doing the input analyzer job has been interrupted", e);
        }

        // forward exception thrown in the processing thread if an exception occurred
        if (job.isExceptionOccurred()) {
            throw job.getOccurredException();
        }

        matchingTemplates = job.getResultMatchingTemplates();
        LOG.debug("Matching templates: {}", matchingTemplates);
        singleNonContainerInput = job.isResultSingleNonContainerInput();
        LOG.debug("SingleNonContainerInput: {}", singleNonContainerInput);
        LOG.debug("Finished analyzing generation input.");
    }

    /**
     * @return whether the input represents a single non container input
     */
    public boolean isSingleNonContainerInput() {
        return singleNonContainerInput;
    }

    /**
     * Generates the the list of templates based on the given {@link #inputs}.
     * @param templates
     *            to be generated
     * @param monitor
     *            to track the progress
     * @return the {@link GenerationReportTo generation report of CobiGen}
     * @throws Exception
     *             if anything during generation fails.
     */
    public GenerationReportTo generate(List<TemplateTo> templates, IProgressMonitor monitor) throws Exception {

        final IProject proj = getGenerationTargetProject();
        if (proj != null) {
            monitor.beginTask("Generating files...", templates.size());
            List<Class<?>> utilClasses = resolveTemplateUtilClasses();

            // set override flags individually for every template
            for (TemplateTo template : templates) {
                // if template is resolved to be generated (it has been selected manually in the generate
                // wizard and does not declare any merge strategy), the complete file should be overwritten
                if (template.getMergeStrategy() == null) {
                    template.setForceOverride(true);
                }
            }

            URI generationTargetUri = getGenerationTargetProject().getLocationURI();
            if (generationTargetUri == null) {
                throw new CobiGenRuntimeException("The location URI of the generation target project "
                    + getGenerationTargetProject().getName() + " could not be resolved. This might be "
                    + "a temporary issue. If this problem persists, please state a bug report.");
            }

            GenerationReportTo report;
            if (singleNonContainerInput) {
                // if we only consider one input, we want to allow some customizations of the generation
                Map<String, Object> model = cobiGen.getModelBuilder(inputs.get(0)).createModel();
                adaptModel(model);
                report = cobiGen.generate(inputs.get(0), templates, Paths.get(generationTargetUri), false, utilClasses,
                    model);
            } else {
                report = new GenerationReportTo();
                for (Object input : inputs) {
                    report.aggregate(
                        cobiGen.generate(input, templates, Paths.get(generationTargetUri), false, utilClasses));
                }
            }

            proj.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            monitor.done();
            LOG.info("Generation finished successfully.");
            return report;
        } else {
            throw new CobiGenRuntimeException("No generation target project configured! This is a Bug!");
        }
    }

    /**
     * Resolves all classes, which have been defined in the template configuration folder.
     * @return the list of classes
     * @throws Exception
     *             if anything during classpath resolving and class loading fails.
     */
    private List<Class<?>> resolveTemplateUtilClasses() throws Exception {
        List<Class<?>> classes = Lists.newArrayList();

        IProject configProject = ResourcesPluginUtil.getGeneratorConfigurationProject();
        IJavaProject configJavaProject = JavaCore.create(configProject);

        // if it is not a Java project, do not try to load anything
        if (configJavaProject != null && configJavaProject.exists()) {
            ClassLoader inputClassLoader = getInputClassloader();
            // create classpath for the configuration project while keeping the input's classpath
            // as the parent classpath to prevent classpath shading.
            inputClassLoader = ClassLoaderUtil.getProjectClassLoader(configJavaProject, inputClassLoader);

            for (IPackageFragmentRoot roots : configJavaProject.getPackageFragmentRoots()) {
                for (IJavaElement e : roots.getChildren()) {
                    if (e instanceof IPackageFragment) {
                        for (ICompilationUnit cu : ((IPackageFragment) e).getCompilationUnits()) {
                            IType type = EclipseJavaModelUtil.getJavaClassType(cu);
                            classes.add(inputClassLoader.loadClass(type.getFullyQualifiedName()));
                        }
                    }
                }
            }
        }
        return classes;
    }

    /**
     * Retrieves the {@link ClassLoader} of the input.
     * @return the {@link ClassLoader} of the input or a newly created one of the input's project
     */
    protected ClassLoader getInputClassloader() {
        Object firstInput = inputs.get(0);
        if (firstInput instanceof Class<?>) {
            return ((Class<?>) firstInput).getClassLoader();
        } else if (firstInput instanceof Object[]) {
            Object[] arrInput = (Object[]) firstInput;
            if (arrInput[0] instanceof Class<?>) {
                return ((Class<?>) arrInput[0]).getClassLoader();
            } else if (arrInput[1] instanceof Class<?>) {
                return ((Class<?>) arrInput[1]).getClassLoader();
            }
        }
        return null;
    }

    /**
     * This method should be implemented if you want to provide any model modifications before generation.
     * This method will only be called, if the generation has been triggered for exactly one input, which is
     * not a container.
     * @param model
     *            template model
     */
    protected abstract void adaptModel(Map<String, Object> model);

    /**
     * Returns all matching trigger ids for the currently stored input
     * @return a list of matching trigger ids
     */
    public List<String> getMatchingTriggerIds() {
        return cobiGen.getMatchingTriggerIds(inputs.get(0));
    }

    /**
     * @return all available increments
     */
    public ComparableIncrement[] getAllIncrements() {

        LinkedList<ComparableIncrement> result = Lists.newLinkedList();
        List<IncrementTo> matchingIncrements;
        matchingIncrements = cobiGen.getMatchingIncrements(getCurrentRepresentingInput());

        // convert to comparable increments
        for (IncrementTo increment : matchingIncrements) {
            result.add(new ComparableIncrement(increment.getId(), increment.getDescription(), increment.getTriggerId(),
                increment.getTemplates(), increment.getDependentIncrements()));
        }

        // add "all" increment, which should include all possible templates
        ComparableIncrement all = new ComparableIncrement(ALL_INCREMENT_ID, ALL_INCREMENT_NAME, null,
            Lists.<TemplateTo> newLinkedList(), Lists.<IncrementTo> newLinkedList());
        for (TemplateTo t : matchingTemplates) {
            all.addTemplate(t);
        }
        result.push(all);
        ComparableIncrement[] array = result.toArray(new ComparableIncrement[0]);
        Arrays.sort(array);
        LOG.debug("Available Increments: {}", result);
        return array;
    }

    /**
     * @return all available increments (sorted and element ALL_INCREMENT_ID added on top)
     */
    public List<TemplateTo> getAllTemplates() {
        return matchingTemplates;
    }

    /**
     * Returns the {@link TemplateTo}, which has the given templateId and belongs to the trigger with the
     * given triggerId or <code>null</code> if there is no template with the given id
     *
     * @param templateId
     *            the template id
     * @param triggerId
     *            the trigger id
     * @return the template, which has the given id<br>
     *         <code>null</code>, if there is no template with the given id
     */
    public TemplateTo getTemplateForId(String templateId, String triggerId) {

        List<TemplateTo> templates = getAllTemplates();
        for (TemplateTo tmp : templates) {
            if (tmp.getTriggerId().equals(triggerId)) {
                if (tmp.getId().equals(templateId)) {
                    return tmp;
                }
            }
        }
        return null;
    }

    /**
     * Returns project dependent paths of all resources which are marked to be overridable (just of
     * {@link #getCurrentRepresentingInput()})
     * @param increments
     *            to be considered
     * @return The set of all overridable project dependent file paths
     */
    public Set<String> getOverridingFiles(Collection<IncrementTo> increments) {
        if (increments.contains(new ComparableIncrement(ALL_INCREMENT_ID, ALL_INCREMENT_NAME, null,
            Lists.<TemplateTo> newLinkedList(), Lists.<IncrementTo> newLinkedList()))) {
            increments = cobiGen.getMatchingIncrements(getCurrentRepresentingInput());
        }
        Set<String> overridablePaths = Sets.newHashSet();
        Map<String, Set<TemplateTo>> templateDestinationPaths = getTemplateDestinationPaths(increments);
        for (Entry<String, Set<TemplateTo>> entry : templateDestinationPaths.entrySet()) {
            if (isOverridableFile(entry.getValue())) {
                overridablePaths.add(entry.getKey());
            }
        }
        return overridablePaths;
    }

    /**
     * Returns project dependent paths of all resources which are marked to be mergeable (just of
     * {@link #getCurrentRepresentingInput()})
     * @param increments
     *            to be considered
     * @return The set of all mergeable project dependent file paths
     */
    public Set<String> getMergeableFiles(Collection<IncrementTo> increments) {
        if (increments.contains(new ComparableIncrement(ALL_INCREMENT_ID, ALL_INCREMENT_NAME, null,
            Lists.<TemplateTo> newLinkedList(), Lists.<IncrementTo> newLinkedList()))) {
            increments = cobiGen.getMatchingIncrements(getCurrentRepresentingInput());
        }
        Set<String> mergeablePaths = Sets.newHashSet();
        Map<String, Set<TemplateTo>> templateDestinationPaths = getTemplateDestinationPaths(increments);
        for (Entry<String, Set<TemplateTo>> entry : templateDestinationPaths.entrySet()) {
            if (isMergableFile(entry.getValue())) {
                mergeablePaths.add(entry.getKey());
            }
        }
        return mergeablePaths;
    }

    /**
     * Checks whether the given object is marked as mergeable
     * @param path
     *            workspace relative path to be checked
     * @param consideredIncrements
     *            increments to be considered for the check
     * @return <code>true</code> if the given object can be merged<br>
     *         <code>false</code> otherwise
     */
    public boolean isMergableFile(String path, Collection<IncrementTo> consideredIncrements) {
        if (path != null) {
            if (consideredIncrements.contains(new ComparableIncrement(ALL_INCREMENT_ID, ALL_INCREMENT_NAME, null,
                Lists.<TemplateTo> newLinkedList(), Lists.<IncrementTo> newLinkedList()))) {
                consideredIncrements = cobiGen.getMatchingIncrements(getCurrentRepresentingInput());
            }
            Set<TemplateTo> templates = getTemplateDestinationPaths(consideredIncrements).get(path);
            if (templates != null) {
                return isMergableFile(templates);
            }
        }
        return false;
    }

    /**
     * Checks whether the given object is marked as mergable
     * @param templates
     *            templates to be checked
     * @return <code>true</code> if the given object can be merged<br>
     *         <code>false</code> otherwise
     */
    private boolean isMergableFile(Set<TemplateTo> templates) {
        for (TemplateTo template : templates) {
            if (template.getMergeStrategy() != null
                && !template.getMergeStrategy().equals(ConfigurationConstants.MERGE_STRATEGY_OVERRIDE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the given object is marked as overridable
     * @param path
     *            workspace relative path to be checked
     * @param consideredIncrements
     *            increments to be considered for the check
     * @return <code>true</code> if the given object will be overwritten<br>
     *         <code>false</code> otherwise
     */
    public boolean isOverridableFile(String path, Collection<IncrementTo> consideredIncrements) {
        if (path != null) {
            if (consideredIncrements.contains(new ComparableIncrement(ALL_INCREMENT_ID, ALL_INCREMENT_NAME, null,
                Lists.<TemplateTo> newLinkedList(), Lists.<IncrementTo> newLinkedList()))) {
                consideredIncrements = cobiGen.getMatchingIncrements(getCurrentRepresentingInput());
            }
            Set<TemplateTo> templates = getTemplateDestinationPaths(consideredIncrements).get(path);
            return isOverridableFile(templates);
        }
        return false;
    }

    /**
     * Checks whether the given object is marked as mergable
     * @param templates
     *            templates to be checked
     * @return <code>true</code> if the given object can be merged<br>
     *         <code>false</code> otherwise
     */
    private boolean isOverridableFile(Set<TemplateTo> templates) {
        for (TemplateTo template : templates) {
            if (template.getMergeStrategy() != null
                && template.getMergeStrategy().equals(ConfigurationConstants.MERGE_STRATEGY_OVERRIDE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resolves all target files for the given templates. Only resources which are located in the workspace
     * will be returned.
     * @param generatedTemplates
     *            templates for which the target files should be resolved
     * @return workspace dependent paths of all possible generated resources.
     */
    public Set<String> getAllTargetPathsInWorkspace(List<TemplateTo> generatedTemplates) {
        Set<String> paths = new HashSet<>();

        List<Object> inputs = cobiGen.resolveContainers(this.inputs.get(0));
        for (TemplateTo template : generatedTemplates) {
            for (Object input : inputs) {
                String path = resolveWorkspaceDependentTemplateDestinationPath(template, input);
                if (path != null) {
                    paths.add(path);
                }
            }
        }
        return paths;
    }

    /**
     * Returns the set of all workspace relative destination paths for the templates of the given
     * {@link ComparableIncrement} mapped to the correlated {@link TemplateTo}. This is done for the
     * {@link #getCurrentRepresentingInput() current representing input}.
     * @param <T>
     *            {@link IncrementTo} or any sub type
     * @param increments
     *            {@link IncrementTo Increments} the template destination paths should be retrieved for
     * @return the mapping of destination paths to its templates
     */
    public <T extends IncrementTo> Map<String, Set<TemplateTo>> getTemplateDestinationPaths(Collection<T> increments) {
        return getTemplateDestinationPaths(increments, getCurrentRepresentingInput());
    }

    /**
     * Returns the set of all workspace relative destination paths for the templates of the given
     * {@link ComparableIncrement} mapped to the correlated {@link TemplateTo}. This is done for the
     * {@link #getCurrentRepresentingInput() current representing input}.
     * @param <T>
     *            {@link IncrementTo} or any sub type
     * @param increments
     *            {@link IncrementTo Increments} the template destination paths should be retrieved for
     * @param input
     *            input for path resolution
     * @return the mapping of destination paths to its templates
     */
    private <T extends IncrementTo> Map<String, Set<TemplateTo>> getTemplateDestinationPaths(Collection<T> increments,
        Object input) {

        boolean cachingEnabled = input == getCurrentRepresentingInput();

        Map<String, Set<TemplateTo>> result = Maps.newHashMap();
        for (IncrementTo increment : increments) {
            if (increment.getId().equals(ALL_INCREMENT_ID)) {
                continue;
            }

            // check cache
            if (cachingEnabled) {
                Map<String, Set<TemplateTo>> cachedTemplatePaths =
                    incrementToTemplateWorkspacePathsCache.get(increment);
                if (cachedTemplatePaths != null) {
                    MapUtils.deepMapAddAll(result, cachedTemplatePaths);
                    continue;
                }
            }

            // process normal
            Map<String, Set<TemplateTo>> thisIncrementResult = Maps.newHashMap();
            for (TemplateTo template : increment.getTemplates()) {
                String path = resolveWorkspaceDependentTemplateDestinationPath(template, input);
                MapUtils.deepMapAdd(thisIncrementResult, path, template);
            }

            if (cachingEnabled) {
                incrementToTemplateWorkspacePathsCache.put(increment, thisIncrementResult);
            }
            MapUtils.deepMapAddAll(result, thisIncrementResult);
        }
        return result;
    }

    /**
     * Resolves the template destination path in a workspace relative style if possible. Otherwise, the
     * absolute path will be returned. {@link #resolveTemplateDestinationPath(TemplateTo, Object)}
     * @param input
     *            object to resolved the path for
     * @param template
     *            which path should be resolved
     * @return the workspace relative path as a string or {@code null} if the path is not in the workspace.
     */
    private String resolveWorkspaceDependentTemplateDestinationPath(TemplateTo template, Object input) {
        Path targetAbsolutePath = resolveTemplateDestinationPath(template, input);

        Path mostSpecificProject = null;
        for (Path projPath : projectsInWorkspace.keySet()) {
            if (targetAbsolutePath.startsWith(projPath)) {
                if (mostSpecificProject == null || projPath.getNameCount() > mostSpecificProject.getNameCount()) {
                    mostSpecificProject = projPath;
                }
            }
        }

        String path;
        if (mostSpecificProject != null) {
            Path relProjPath = mostSpecificProject.relativize(targetAbsolutePath);
            path = projectsInWorkspace.get(mostSpecificProject).getFullPath().toFile().toPath().resolve(relProjPath)
                .toString().replace("\\", "/");
        } else {
            path = targetAbsolutePath.toString().replace("\\", "/");
            workspaceExternalPath.add(path);
        }
        return path;
    }

    /**
     * Resolves the template destination path in a workspace relative style if possible. Otherwise, the
     * absolute path will be returned. {@link #resolveTemplateDestinationPath(TemplateTo, Object)}
     * @param generatedFiles
     *            paths to be calculated workspace dependent
     * @return the workspace relative path as a string or {@code null} if the path is not in the workspace.
     */
    public Set<String> getWorkspaceDependentTemplateDestinationPath(Collection<Path> generatedFiles) {
        Set<String> workspaceDependentPaths = new HashSet<>();
        for (Path targetAbsolutePath : generatedFiles) {
            Path mostSpecificProject = null;
            for (Path projPath : projectsInWorkspace.keySet()) {
                if (targetAbsolutePath.startsWith(projPath)) {
                    if (mostSpecificProject == null || projPath.getNameCount() > mostSpecificProject.getNameCount()) {
                        mostSpecificProject = projPath;
                    }
                }
            }

            String path;
            if (mostSpecificProject != null) {
                Path relProjPath = mostSpecificProject.relativize(targetAbsolutePath);
                path = projectsInWorkspace.get(mostSpecificProject).getFullPath().toFile().toPath().resolve(relProjPath)
                    .toString().replace("\\", "/");
            } else {
                path = targetAbsolutePath.toString().replace("\\", "/");
                workspaceExternalPath.add(path);
            }
            workspaceDependentPaths.add(path);
        }
        return workspaceDependentPaths;
    }

    /**
     * @return the currently set input to be generated with
     */
    public Object getCurrentRepresentingInput() {
        if (inputs == null || inputs.size() == 0) {
            return null;
        }

        // we currently only supporting one container at a time as valid selection
        List<Object> children = cobiGen.resolveContainers(inputs.get(0));
        // we have to return one of the children do enable correct variable solution in the user interface
        return children.get(0);
    }

    /**
     * Delegate of {@link CobiGen#getMatchingTriggerIds(Object)}
     * @param loadClass
     *            the object to be loaded
     * @return the list of matching trigger id's
     */
    public List<String> getMatchingTriggerIds(Object loadClass) {
        return cobiGen.getMatchingTriggerIds(loadClass);
    }

    /**
     * Checks if the selected items are supported by one or more {@link Trigger}s, and if they are supported
     * by the same {@link Trigger}s. Returns a boolean value, if all objects of the selection could be
     * processed. If there are objects, which are not yet supported as inputs for generation, or the selection
     * in composed of valid objects in an not yet supported way, an {@link InvalidInputException} will be
     * thrown. Thus, getting a boolean value can be interpreted as "selection supported, but currently not
     * matching trigger".
     *
     * @return true, if all items are supported by the same trigger(s)<br>
     *         false, if they are not supported by any trigger at all
     * @throws InvalidInputException
     *             if the input could not be read as expected
     */
    public boolean isValidInput() throws InvalidInputException {
        LOG.debug("Start checking selection validity for the use as Java input.");

        Iterator<?> it = inputs.iterator();
        List<String> firstTriggers = null;

        try {
            while (it.hasNext()) {
                Object tmp = it.next();
                List<String> matchingTriggerIds = cobiGen.getMatchingTriggerIds(tmp);

                if (firstTriggers == null) {
                    firstTriggers = matchingTriggerIds;
                } else if (!firstTriggers.equals(matchingTriggerIds)) {
                    throw new InvalidInputException(
                        "You selected at least two inputs, which are not matching the same triggers. "
                            + "For batch processing all inputs have to match the same triggers.");
                }
            }
        } finally {
            LOG.debug("Ended checking selection validity for the use as Java input.");
        }
        return firstTriggers != null && !firstTriggers.isEmpty();
    }

    /**
     * Resolves the template's destination paths by while using the {@link #getCurrentRepresentingInput()} as
     * input
     * @param template
     *            the destination path should be resolved for
     * @return the destination {@link Path}
     */
    public Path resolveTemplateDestinationPath(TemplateTo template) {
        Path resolvedPath = resolvedDestPathsCache.get(template);
        if (resolvedPath != null) {
            return resolvedPath;
        }
        return resolveTemplateDestinationPath(template, getCurrentRepresentingInput());
    }

    /**
     * Resolves the template's destination paths by while using the given input
     * @param template
     *            the destination path should be resolved for
     * @param input
     *            input for path resolution
     * @return the destination {@link Path}
     */
    private Path resolveTemplateDestinationPath(TemplateTo template, Object input) {
        return cobiGen.resolveTemplateDestinationPath(getGenerationTargetProjectPath(), template, input);
    }

    /**
     * @param path
     *            to be checked. Most probably the outcome of {@link #getTemplateDestinationPaths(Collection)}
     * @return {@code true} if this path has been registered before in
     *         {@link #getTemplateDestinationPaths(Collection)} as a workspace external path.
     */
    public boolean isWorkspaceExternalPath(String path) {
        return workspaceExternalPath.contains(path);
    }

}
