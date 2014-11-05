package com.capgemini.cobigen.eclipse.generator.java;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.Charsets;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.eclipse.common.constants.ConfigResources;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.NotYetSupportedException;
import com.capgemini.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.capgemini.cobigen.eclipse.common.tools.PathUtil;
import com.capgemini.cobigen.eclipse.generator.java.entity.ComparableIncrement;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import freemarker.template.TemplateException;

/**
 * The generator interface for the external generator library
 *
 * @author mbrunnli (13.02.2013)
 */
public class JavaGeneratorWrapper {

    /**
     * Register Factory Generator instance
     */
    private CobiGen cobiGen;

    /**
     * Current input types
     */
    private Map<IType, Class<?>> inputTypes;

    /**
     * Package folder to be generated
     */
    private PackageFolder packageFolder;

    /**
     * Target Project for the generation
     */
    private IProject targetProject;

    /**
     * A set of removed fields for the generation.
     */
    private Set<String> ignoreFields = new HashSet<>();

    /**
     * All matching templates for the currently configured {@link #inputTypes input types}
     */
    private List<TemplateTo> matchingTemplates = Lists.newLinkedList();

    /**
     * Creates a new generator instance
     *
     * @throws InvalidConfigurationException
     *             if the given configuration does not match the templates.xsd
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws CoreException
     *             if the generator configuration project could not be opened
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @author mbrunnli (21.03.2014)
     */
    public JavaGeneratorWrapper() throws UnknownExpressionException, UnknownContextVariableException,
        GeneratorProjectNotExistentException, CoreException, InvalidConfigurationException {

        cobiGen = initializeGenerator();
    }

    /**
     * Creates a new generator instance
     *
     * @param type
     *            of the input POJO
     * @throws InvalidConfigurationException
     *             if the given configuration does not match the templates.xsd
     * @throws IOException
     *             if the generator project "RF-Generation" could not be accessed
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws CoreException
     *             if the generator configuration project could not be opened
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws ClassNotFoundException
     *             if the given type could not be found by the project {@link ClassLoader}
     * @author mbrunnli (13.02.2013)
     */
    public JavaGeneratorWrapper(IType type) throws IOException, InvalidConfigurationException,
        GeneratorProjectNotExistentException, CoreException, UnknownExpressionException,
        UnknownContextVariableException, ClassNotFoundException {

        this();
        setInputType(type);
    }

    /**
     * Initializes the {@link CobiGen} with the correct configuration
     *
     * @return the configured{@link CobiGen}
     * @throws InvalidConfigurationException
     *             if the given configuration does not match the templates.xsd
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws CoreException
     *             if an internal eclipse exception occurs
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration folder does not exist
     * @author mbrunnli (05.02.2013)
     */
    private CobiGen initializeGenerator() throws GeneratorProjectNotExistentException, CoreException,
        UnknownExpressionException, UnknownContextVariableException, InvalidConfigurationException {

        IProject generatorProj = ConfigResources.getGeneratorConfigurationProject();
        return new CobiGen(generatorProj.getLocation().toFile());
    }

    /**
     * Sets a {@link IPackageFragment} as input for the generator
     *
     * @param packageFragment
     *            generator input
     * @author mbrunnli (03.06.2014)
     */
    public void setInputPackage(IPackageFragment packageFragment) {

        inputTypes = null;
        packageFolder =
            new PackageFolder(packageFragment.getResource().getLocationURI(),
                packageFragment.getElementName());
        matchingTemplates = cobiGen.getMatchingTemplates(packageFolder);
    }

    /**
     * Changes the {@link JavaGeneratorWrapper}'s type an by this its pojo, model and template configuration.
     * Useful for batch generation.
     *
     * @param type
     *            of the input POJO
     * @throws CoreException
     *             if the Java runtime class path could not be determined
     * @throws ClassNotFoundException
     *             if the given type could not be found by the project {@link ClassLoader}
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws IOException
     *             if one of the template configurations could not be accessed
     * @author trippl (22.04.2013)
     */
    public void setInputType(IType type) throws CoreException, ClassNotFoundException, IOException {

        packageFolder = null;
        ClassLoader projectClassLoader = ClassLoaderUtil.getProjectClassLoader(type.getJavaProject());
        inputTypes = Maps.newHashMapWithExpectedSize(1);
        Class<?> loadedClass = projectClassLoader.loadClass(type.getFullyQualifiedName());
        inputTypes.put(type, loadedClass);
        matchingTemplates = cobiGen.getMatchingTemplates(loadedClass);
    }

    /**
     * Changes the {@link JavaGeneratorWrapper}'s input to the list of types. Useful for batch generation.
     *
     * @param inputTypes
     *            to be generated
     * @throws CoreException
     *             if the Java runtime class path could not be determined
     * @throws ClassNotFoundException
     *             if the given type could not be found by the project {@link ClassLoader}
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws MalformedURLException
     *             while resolving an input type's project class loader
     * @author mbrunnli (12.10.2014)
     */
    public void setInputTypes(List<IType> inputTypes) throws CoreException, ClassNotFoundException,
        MalformedURLException {
        if (inputTypes != null && inputTypes.size() > 0) {
            packageFolder = null;
            this.inputTypes = Maps.newHashMapWithExpectedSize(inputTypes.size());
            matchingTemplates = Lists.newLinkedList();
            // Take first input type as precondition for the input is that all input types are part of the
            // same project
            ClassLoader projectClassLoader =
                ClassLoaderUtil.getProjectClassLoader(inputTypes.get(0).getJavaProject());
            for (IType type : inputTypes) {
                Class<?> loadedClass = projectClassLoader.loadClass(type.getFullyQualifiedName());
                this.inputTypes.put(type, loadedClass);
                matchingTemplates.addAll(cobiGen.getMatchingTemplates(loadedClass));
            }
        }
    }

    /**
     * Builds an adapted model for the generation process containing javadoc
     *
     * @param inputType
     *            input {@link IType}
     * @param origModel
     *            the original model
     * @return the adapted model
     * @throws JavaModelException
     *             if the given type does not exist or if an exception occurs while accessing its
     *             corresponding resource
     * @author mbrunnli (05.04.2013)
     */
    private Map<String, Object> adaptModel(Map<String, Object> origModel, IType inputType)
        throws JavaModelException {

        Map<String, Object> newModel = new HashMap<>(origModel);
        JavaModelAdaptor javaModelAdaptor = new JavaModelAdaptor(newModel);
        javaModelAdaptor.addAttributesDescription(inputType);
        javaModelAdaptor.addMethods(inputType);
        return newModel;
    }

    /**
     * Sets the generation root target for all templates
     *
     * @param proj
     *            {@link IProject} which represents the target root
     * @author mbrunnli (13.02.2013)
     */
    public void setGenerationTargetProject(IProject proj) {

        targetProject = proj;
        cobiGen.setContextSetting(ContextSetting.GenerationTargetRootPath, proj.getProject().getLocation()
            .toString());
    }

    /**
     * Returns the generation target project
     *
     * @return the generation target project
     * @author mbrunnli (13.02.2013)
     */
    public IProject getGenerationTargetProject() {

        return targetProject;
    }

    /**
     * Generates the given template
     *
     * @param template
     *            {@link TemplateTo} to be generated
     * @param forceOverride
     *            forces the generator to override the maybe existing target file of the template
     * @throws TemplateException
     *             any exception of the FreeMarker engine
     * @throws IOException
     *             if the specified template could not be found
     * @throws MergeException
     *             if there are some problems while merging
     * @throws CoreException
     *             if an internal eclipse exception occurs
     * @throws NotYetSupportedException
     *             if any action has been triggered, which is currently not supported
     * @author mbrunnli (14.02.2013)
     */
    public void generate(TemplateTo template, boolean forceOverride) throws IOException, TemplateException,
        MergeException, CoreException, NotYetSupportedException {

        if (packageFolder != null) {
            IProject proj = getGenerationTargetProject();
            IJavaProject javaProject = JavaCore.create(proj);
            if (javaProject == null) {
                throw new NotYetSupportedException(
                    "The target project, where you selected the input from, should be a java project. "
                        + "Currently this is the only type of projects to be supported.");
            }
            packageFolder.setClassLoader(ClassLoaderUtil.getProjectClassLoader(javaProject));
            cobiGen.generate(packageFolder, template, forceOverride);
        } else {
            for (Entry<IType, Class<?>> entry : inputTypes.entrySet()) {
                Object[] inputSourceAndClass =
                    new Object[] {
                        entry.getValue(),
                        JavaParserUtil.getFirstJavaClass(
                            ClassLoaderUtil.getProjectClassLoader(entry.getKey().getJavaProject()),
                            new StringReader(entry.getKey().getCompilationUnit().getSource())) };
                Map<String, Object> model =
                    cobiGen.getModelBuilder(inputSourceAndClass, template.getTriggerId()).createModel();
                adaptModel(model, entry.getKey());
                removeIgnoredFieldsFromModel(model);
                cobiGen.generate(inputSourceAndClass, template, model, forceOverride);
            }
        }
    }

    /**
     * Returns all available generation packages
     *
     * @return all available generation packages
     * @author mbrunnli (25.02.2013)
     */
    public ComparableIncrement[] getAllIncrements() {

        LinkedList<ComparableIncrement> result = Lists.newLinkedList();
        List<IncrementTo> matchingIncrements;
        if (packageFolder != null) {
            matchingIncrements = cobiGen.getMatchingIncrements(packageFolder);
        } else { // inputTypes != null (invariant)
            // It is ok to only get the matching increments of the first input type as all input types should
            // retrieve the same increments as this is a precondition for generation
            matchingIncrements = cobiGen.getMatchingIncrements(inputTypes.values().iterator().next());
        }

        // convert to comparable increments
        for (IncrementTo increment : matchingIncrements) {
            result.add(new ComparableIncrement(increment.getId(), increment.getDescription(), increment
                .getTriggerId(), increment.getTemplates(), increment.getDependentIncrements()));
        }

        // add "all" increment, which should include all possible templates
        ComparableIncrement all =
            new ComparableIncrement("all", "All", null, Lists.<TemplateTo> newLinkedList(),
                Lists.<IncrementTo> newLinkedList());
        for (TemplateTo t : matchingTemplates) {
            all.addTemplate(t);
        }
        result.push(all);
        ComparableIncrement[] array = result.toArray(new ComparableIncrement[0]);
        Arrays.sort(array);
        return array;
    }

    /**
     * Returns all matching trigger ids for the currently stored input
     *
     * @return a list of matching trigger ids
     * @author mbrunnli (03.06.2014)
     */
    public List<String> getMatchingTriggerIds() {

        if (packageFolder != null) {
            return cobiGen.getMatchingTriggerIds(packageFolder);
        } else {
            return cobiGen.getMatchingTriggerIds(inputTypes.values().iterator().next());
        }
    }

    /**
     * Returns all available generation packages (sorted and element "all" added on top)
     *
     * @return all available generation packages
     * @author mbrunnli (25.02.2013)
     */
    public List<TemplateTo> getAllTemplates() {

        return matchingTemplates;
    }

    /**
     * Returns the attributes and its types from the current model
     *
     * @return a {@link Map} mapping attribute name to attribute type name
     * @throws InvalidConfigurationException
     *             if the generator's configuration is faulty
     * @author mbrunnli (12.03.2013)
     */
    public Map<String, String> getAttributesToTypeMapOfFirstInput() throws InvalidConfigurationException {

        Map<String, String> result = new HashMap<>();
        Class<?> firstInputClass = inputTypes.values().iterator().next();
        List<String> matchingTriggerIds = cobiGen.getMatchingTriggerIds(firstInputClass);
        Map<String, Object> model =
            cobiGen.getModelBuilder(firstInputClass, matchingTriggerIds.get(0)).createModel();
        @SuppressWarnings("unchecked")
        Map<String, Object> pojoModel = (Map<String, Object>) model.get("pojo");
        if (pojoModel != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> attributes = (List<Map<String, String>>) pojoModel.get("attributes");
            for (Map<String, String> attr : attributes) {
                result.put(attr.get("name"), attr.get("type"));
            }
        }
        return result;
    }

    /**
     * Removes a given attributes from the model
     *
     * @param name
     *            name of the attribute to be removed
     * @author mbrunnli (21.03.2013)
     */
    public void removeFieldFromModel(String name) {

        ignoreFields.add(name);
    }

    /**
     * Removes all fields from the model which have been flagged to be ignored
     *
     * @param model
     *            in which the ignored fields should be removed
     * @author mbrunnli (15.10.2013)
     */
    private void removeIgnoredFieldsFromModel(Map<String, Object> model) {

        @SuppressWarnings("unchecked")
        Map<String, Object> pojoModel = (Map<String, Object>) model.get("pojo");
        if (pojoModel != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> fields = (List<Map<String, String>>) pojoModel.get("attributes");
            for (Iterator<Map<String, String>> it = fields.iterator(); it.hasNext();) {
                Map<String, String> next = it.next();
                for (String ignoredField : ignoreFields) {
                    if (next.get("name").equals(ignoredField)) {
                        it.remove();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Returns the {@link List} of templates, which target to the given path.
     *
     * @param filePath
     *            for which templates should be retrieved
     * @param consideredIncrements
     *            increments which should be considered for fetching templates
     * @return the {@link List} of templates, which generates the given file
     * @author mbrunnli (14.02.2013)
     */
    public List<TemplateTo> getTemplatesForFilePath(String filePath, Set<IncrementTo> consideredIncrements) {
        // TODO DRINGEND!!! BUG, da die selektion sonst nicht mehr funktioniert??? testen!
        List<TemplateTo> templates = Lists.newLinkedList();
        if (consideredIncrements != null) {
            for (IncrementTo increment : getAllIncrements()) {
                if (consideredIncrements.contains(increment)) {
                    for (TemplateTo tmp : increment.getTemplates()) {
                        if (tmp.resolveDestinationPath(getCurrentRepresentingInput()).equals(
                            PathUtil.getProjectDependendFilePath(filePath))) {
                            templates.add(tmp);
                        }
                    }
                }
            }
        } else {
            for (TemplateTo tmp : getAllTemplates()) {
                if (tmp.resolveDestinationPath(getCurrentRepresentingInput()).equals(
                    PathUtil.getProjectDependendFilePath(filePath))) {
                    templates.add(tmp);
                }
            }
        }

        return templates;
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
     * @author trippl (22.04.2013)
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
     * Returns project dependent paths of all resources which are marked to be mergeable
     *
     * @return The set of all mergeable project dependent file paths
     * @author mbrunnli (15.03.2013)
     */
    public Set<IFile> getMergeableFiles() {

        Set<IFile> mergeableFiles = new HashSet<>();
        IProject targetProjet = getGenerationTargetProject();
        for (TemplateTo t : getAllTemplates()) {
            if (t.getMergeStrategy() != null) {
                mergeableFiles.add(targetProjet.getFile(t
                    .resolveDestinationPath(getCurrentRepresentingInput())));
            }
        }
        return mergeableFiles;
    }

    /**
     * Returns project dependent paths of all possible generated resources for the first input in case of
     * batch generation
     *
     * @return project dependent paths of all possible generated resources
     * @author mbrunnli (26.04.2013)
     */
    public Set<IFile> getAllTargetFilesForOneInput() {

        Set<IFile> files = new HashSet<>();
        IProject targetProjet = getGenerationTargetProject();
        for (TemplateTo t : getAllTemplates()) {
            files.add(targetProjet.getFile(t.resolveDestinationPath(getCurrentRepresentingInput())));
        }
        return files;
    }

    /**
     * Returns project dependent paths of all possible generated resources
     *
     * @return project dependent paths of all possible generated resources
     * @author mbrunnli (26.04.2013)
     */
    public Set<IFile> getAllTargetFiles() {

        Set<IFile> files = new HashSet<>();
        for (TemplateTo t : getAllTemplates()) {
            if (packageFolder != null) {
                List<Object> children = new JavaInputReader().getInputObjects(packageFolder, Charsets.UTF_8);
                for (Object child : children) {
                    files.add(targetProject.getFile(t.resolveDestinationPath(child)));
                }
            } else {
                for (Class<?> inputClass : inputTypes.values()) {
                    files.add(targetProject.getFile(t.resolveDestinationPath(inputClass)));
                }
            }
        }
        return files;
    }

    /**
     * Get all templates, which id's are contained in the list of template ids
     * @param templateIds
     *            a {@link List} of template, the list of all templates should be filtered with
     * @return all templates, which id's match one of the given template ids
     * @author mbrunnli (12.10.2014)
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
     * Returns the currently set input to be generated with
     * @return the currently set input to be generated with
     * @author mbrunnli (16.10.2014)
     */
    public Object getCurrentRepresentingInput() {
        if (packageFolder != null) {
            // not nice but necessary
            List<Object> packageChildren =
                new JavaInputReader().getInputObjects(packageFolder, Charsets.UTF_8);
            return packageChildren.get(0);
        } else {
            return inputTypes.values().iterator().next();
        }
    }

}
