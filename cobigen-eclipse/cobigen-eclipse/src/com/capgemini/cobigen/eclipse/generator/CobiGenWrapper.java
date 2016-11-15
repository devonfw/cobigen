package com.capgemini.cobigen.eclipse.generator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.Charsets;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.CobiGen;
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
import com.capgemini.cobigen.eclipse.common.tools.PathUtil;
import com.capgemini.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.capgemini.cobigen.eclipse.generator.entity.ComparableIncrement;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.google.common.collect.Lists;

/**
 * Wrapper for CobiGen providing an eclipse compliant API.
 */
public abstract class CobiGenWrapper extends AbstractCobiGenWrapper {

    /**
     * States whether at least one input object has been set
     */
    private boolean initialized;

    /**
     * Assigning logger to CobiGenWrapper
     */
    private static final Logger LOG = LoggerFactory.getLogger(CobiGenWrapper.class);

    /**
     * States, whether the input is unique and a container
     */
    private boolean singleNonContainerInput;

    /**
     * Current registered input objects
     */
    private List<Object> inputs;

    /**
     * All matching templates for the currently configured {@link #inputs input objects}
     */
    private List<TemplateTo> matchingTemplates = Lists.newLinkedList();

    /**
     * Creates a new {@link CobiGenWrapper}
     * @throws CoreException
     *             if an internal eclipse exception occurs
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration folder does not exist
     * @throws IOException
     *             if the generator project could not be found or read
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     */
    public CobiGenWrapper() throws GeneratorProjectNotExistentException, CoreException,
        InvalidConfigurationException, IOException {
        super();
    }

    /**
     * Creates a new {@link CobiGenWrapper} and sets the given input objects
     * @param inputs
     *            for generation
     * @throws CoreException
     *             if an internal eclipse exception occurs
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration folder does not exist
     * @throws IOException
     *             if the generator project could not be found or read
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     * @author mbrunnli (03.12.2014)
     */
    public CobiGenWrapper(List<Object> inputs) throws GeneratorProjectNotExistentException, CoreException,
        InvalidConfigurationException, IOException {
        super();
        setInputs(inputs);
    }

    /**
     * Sets the given input object for generation
     * @param input
     *            input object for generation
     */
    public void setInput(Object input) {
        if (input != null) {
            LOG.info("Set new generator input. Calculating matching templates...");
            initialized = true;
            inputs = Lists.newArrayList(input);
            matchingTemplates = cobiGen.getMatchingTemplates(input);
            singleNonContainerInput = !cobiGen.combinesMultipleInputs(input);
            LOG.info("Finished calculating matching templates.");
        } else {
            initialized = false;
            inputs = null;
            matchingTemplates = null;
            singleNonContainerInput = false;
        }
    }

    /**
     * Sets the given input objects for generation
     * @param inputs
     *            input objects for generation
     */
    public void setInputs(List<Object> inputs) {
        this.inputs = inputs;
        initialized = this.inputs != null && this.inputs.size() > 0;

        if (initialized) {
            LOG.info("Set new generator inputs. Calculating matching templates...");
            matchingTemplates = Lists.newLinkedList();

            ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
            AnalyzeInputJob job = new AnalyzeInputJob(cobiGen, inputs);
            try {
                dialog.run(true, false, job);
            } catch (InvocationTargetException e) {
                LOG.error("An internal error occured while invoking input analyzer job.", e);
                throw new CobiGenEclipseRuntimeException(
                    "An internal error occured while invoking input analyzer job", e);
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
            singleNonContainerInput = job.isResultSingleNonContainerInput();
            LOG.info("Finished analyzing generation input.");
        } else {
            inputs = null;
            matchingTemplates = null;
            singleNonContainerInput = false;
        }
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
    public GenerationReportTo generate(List<TemplateTo> templates, IProgressMonitor monitor)
        throws Exception {

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

            GenerationReportTo report;
            if (singleNonContainerInput) {
                // if we only consider one input, we want to allow some customizations of the generation
                Map<String, Object> model = cobiGen.getModelBuilder(inputs.get(0)).createModel();
                adaptModel(model);
                report = cobiGen.generate(inputs.get(0), templates,
                    Paths.get(getGenerationTargetProject().getLocationURI()), false, utilClasses, model);
            } else {
                report = cobiGen.generate(inputs, templates,
                    Paths.get(getGenerationTargetProject().getLocationURI()), false, utilClasses);
            }

            proj.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            monitor.done();
            LOG.info("Generation finished successfully.");
            return report;
            // return reportSummary;
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
    private ClassLoader getInputClassloader() {
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
        if (initialized) {
            return cobiGen.getMatchingTriggerIds(inputs.get(0));
        } else {
            return Lists.newLinkedList();
        }
    }

    /**
     * Returns all available generation packages
     * @return all available generation packages
     */
    public ComparableIncrement[] getAllIncrements() {

        LinkedList<ComparableIncrement> result = Lists.newLinkedList();
        List<IncrementTo> matchingIncrements;
        if (initialized) {
            matchingIncrements = cobiGen.getMatchingIncrements(inputs.get(0));

            // convert to comparable increments
            for (IncrementTo increment : matchingIncrements) {
                result.add(new ComparableIncrement(increment.getId(), increment.getDescription(),
                    increment.getTriggerId(), increment.getTemplates(), increment.getDependentIncrements()));
            }
        }

        // add "all" increment, which should include all possible templates
        ComparableIncrement all = new ComparableIncrement("all", "All", null,
            Lists.<TemplateTo> newLinkedList(), Lists.<IncrementTo> newLinkedList());
        for (TemplateTo t : matchingTemplates) {
            all.addTemplate(t);
        }
        result.push(all);
        ComparableIncrement[] array = result.toArray(new ComparableIncrement[0]);
        Arrays.sort(array);
        return array;
    }

    /**
     * Returns all available generation packages (sorted and element "all" added on top)
     * @return all available generation packages
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
     * Returns the {@link List} of templates, which target to the given path. (just of
     * {@link #getCurrentRepresentingInput()})
     *
     * @param filePath
     *            for which templates should be retrieved
     * @param consideredIncrements
     *            increments which should be considered for fetching templates
     * @return the {@link List} of templates, which generates the given file
     */
    public List<TemplateTo> getTemplatesForFilePath(String filePath, Set<IncrementTo> consideredIncrements) {
        List<TemplateTo> templates = Lists.newLinkedList();
        if (consideredIncrements != null) {
            for (IncrementTo increment : getAllIncrements()) {
                if (consideredIncrements.contains(increment)) {
                    for (TemplateTo tmp : increment.getTemplates()) {
                        Path targetAbsolutePath = cobiGen.resolveTemplateDestinationPath(
                            getGenerationTargetProjectPath(), tmp, getCurrentRepresentingInput());
                        String projectDependentPath = PathUtil
                            .getProjectDependentFilePath(getGenerationTargetProject(), targetAbsolutePath);
                        if (projectDependentPath.equals(PathUtil.getProjectDependendFilePath(filePath))) {
                            templates.add(tmp);
                        }
                    }
                }
            }
        } else {
            for (TemplateTo tmp : getAllTemplates()) {
                Path targetAbsolutePath = cobiGen.resolveTemplateDestinationPath(
                    getGenerationTargetProjectPath(), tmp, getCurrentRepresentingInput());
                String projectDependentPath =
                    PathUtil.getProjectDependentFilePath(getGenerationTargetProject(), targetAbsolutePath);
                if (projectDependentPath.equals(PathUtil.getProjectDependendFilePath(filePath))) {
                    templates.add(tmp);
                }
            }
        }
        return templates;
    }

    /**
     * Returns project dependent paths of all resources which are marked to be mergeable (just of
     * {@link #getCurrentRepresentingInput()})
     * @return The set of all mergeable project dependent file paths
     */
    public Set<IFile> getMergeableFiles() {

        Set<IFile> mergeableFiles = new HashSet<>();
        IProject targetProjet = getGenerationTargetProject();
        for (TemplateTo t : getAllTemplates()) {
            if (t.getMergeStrategy() != null) {
                Path targetAbsolutePath = cobiGen.resolveTemplateDestinationPath(
                    getGenerationTargetProjectPath(), t, getCurrentRepresentingInput());
                mergeableFiles.add(PathUtil.getProjectDependentFile(targetProjet, targetAbsolutePath));
            }
        }
        return mergeableFiles;
    }

    /**
     * Get all templates, which id's are contained in the list of template ids
     * @param templateIds
     *            a {@link List} of template, the list of all templates should be filtered with
     * @return all templates, which id's match one of the given template ids
     */
    public List<TemplateTo> getTemplates(List<String> templateIds) {
        List<TemplateTo> templates = Lists.newLinkedList();
        for (TemplateTo template : getAllTemplates()) {
            if (templateIds.contains(template.getId())) {
                templates.add(template);
            }
        }
        return templates;
    }

    /**
     * Returns project dependent paths of all possible generated resources for the first input in case of
     * batch generation
     * @return project dependent paths of all possible generated resources
     */
    public Set<IFile> getAllTargetFilesOfFirstInput() {

        Set<IFile> files = new HashSet<>();
        IProject targetProjet = getGenerationTargetProject();
        for (TemplateTo t : getAllTemplates()) {
            Path targetAbsolutePath = cobiGen.resolveTemplateDestinationPath(getGenerationTargetProjectPath(),
                t, getCurrentRepresentingInput());
            files.add(PathUtil.getProjectDependentFile(targetProjet, targetAbsolutePath));
        }
        return files;
    }

    /**
     * Returns project dependent paths of all possible generated resources
     * @return project dependent paths of all possible generated resources
     */
    public Set<IFile> getAllTargetFiles() {
        if (!initialized) {
            return new HashSet<>(0);
        }

        Set<IFile> files = new HashSet<>();
        boolean combinesMultipleInputs = cobiGen.combinesMultipleInputs(inputs.get(0));
        for (TemplateTo t : getAllTemplates()) {
            if (combinesMultipleInputs) {
                List<Object> cInputs = new JavaInputReader().getInputObjects(inputs.get(0), Charsets.UTF_8);
                for (Object input : cInputs) {
                    Path targetAbsolutePath =
                        cobiGen.resolveTemplateDestinationPath(getGenerationTargetProjectPath(), t, input);
                    files.add(
                        PathUtil.getProjectDependentFile(getGenerationTargetProject(), targetAbsolutePath));
                }
            } else {
                for (Object input : inputs) {
                    Path targetAbsolutePath =
                        cobiGen.resolveTemplateDestinationPath(getGenerationTargetProjectPath(), t, input);
                    files.add(
                        PathUtil.getProjectDependentFile(getGenerationTargetProject(), targetAbsolutePath));
                }
            }
        }
        return files;
    }

    /**
     * Returns the currently set input to be generated with
     * @return the currently set input to be generated with
     */
    public Object getCurrentRepresentingInput() {
        if (inputs == null || inputs.size() == 0) {
            return null;
        }

        // we currently only supporting one container at a time as valid selection
        if (cobiGen.combinesMultipleInputs(inputs.get(0))) {
            List<Object> children =
                new JavaInputReader().getInputObjectsRecursively(inputs.get(0), Charsets.UTF_8);
            // we have to return one of the children do enable correct variable solution in the user interface
            return children.get(0);
        } else {
            return inputs.get(0);
        }
    }

    /**
     * delegate of {@link CobiGen#getMatchingTriggerIds(Object)}
     * @param loadClass
     *            the object to be loaded
     * @return the list of matching trigger id's
     */
    public List<String> getMatchingTriggerIds(Object loadClass) {
        if (initialized) {
            return cobiGen.getMatchingTriggerIds(loadClass);
        } else {
            LOG.debug("Generator is not initialized. Could not get matching triggers for {}.",
                loadClass.toString());
            return null;
        }
    }

    /**
     * Checks if the selected items are supported by one or more {@link Trigger}s, and if they are supported
     * by the same {@link Trigger}s. Returns a boolean value, if all objects of the selection could be
     * processed. If there are objects, which are not yet supported as inputs for generation, or the selection
     * in composed of valid objects in an not yet supported way, an {@link InvalidInputException} will be
     * thrown. Thus, getting a boolean value can be interpreted as
     * "selection supported, but currently not matching trigger".
     *
     * @param selection
     *            the selection made
     * @return true, if all items are supported by the same trigger(s)<br>
     *         false, if they are not supported by any trigger at all
     * @throws InvalidInputException
     *             if the input could not be read as expected
     */
    public abstract boolean isValidInput(IStructuredSelection selection) throws InvalidInputException;

    /**
     * Resolves the template's destination paths by while using the {@link #getCurrentRepresentingInput()} as
     * input
     * @param template
     *            the destination path should be resolved for
     * @return the destination {@link Path}
     */
    public Path resolveTemplateDestinationPath(TemplateTo template) {
        return cobiGen.resolveTemplateDestinationPath(getGenerationTargetProjectPath(), template,
            getCurrentRepresentingInput());
    }

}
