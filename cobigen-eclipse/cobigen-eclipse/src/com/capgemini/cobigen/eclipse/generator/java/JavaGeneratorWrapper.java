package com.capgemini.cobigen.eclipse.generator.java;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.capgemini.cobigen.eclipse.generator.CobiGenInputHolder;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.extension.to.TemplateTo;
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
public class JavaGeneratorWrapper extends CobiGenInputHolder {

    /**
     * Current input types
     */
    private Map<IType, Class<?>> inputTypes;

    /**
     * Package folder to be generated
     */
    private PackageFolder packageFolder;

    /**
     * A set of removed fields for the generation.
     */
    private Set<String> ignoreFields = new HashSet<>();

    /**
     * Creates a new generator instance
     *
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws CoreException
     *             if the generator configuration project could not be opened
     * @author mbrunnli (21.03.2014)
     */
    public JavaGeneratorWrapper() throws GeneratorProjectNotExistentException, CoreException {
        super();
    }

    /**
     * Creates a new generator instance
     *
     * @param type
     *            of the input POJO
     * @throws IOException
     *             if the generator project "RF-Generation" could not be accessed
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws CoreException
     *             if the generator configuration project could not be opened
     * @throws ClassNotFoundException
     *             if the given type could not be found by the project {@link ClassLoader}
     * @author mbrunnli (13.02.2013)
     */
    public JavaGeneratorWrapper(IType type) throws IOException, GeneratorProjectNotExistentException,
        CoreException, ClassNotFoundException {
        this();
        setInputType(type);
    }

    /**
     * Sets a {@link IPackageFragment} as input for the generator
     *
     * @param packageFragment
     *            generator input
     * @throws CoreException
     *             if an eclipse internal exception occurs
     * @throws MalformedURLException
     *             if an eclipse internal exception occurs while retrieving the class loader of the
     *             corresponding java project
     * @author mbrunnli (03.06.2014)
     */
    public void setInputPackage(IPackageFragment packageFragment) throws MalformedURLException, CoreException {
        inputTypes = null;
        packageFolder =
            new PackageFolder(packageFragment.getResource().getLocationURI(),
                packageFragment.getElementName());
        packageFolder.setClassLoader(ClassLoaderUtil.getProjectClassLoader(packageFragment.getJavaProject()));
        setInputs(Lists.<Object> newArrayList(packageFolder));
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
        setInputs(Lists.<Object> newArrayList(inputTypes.entrySet().iterator().next().getValue()));
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
            // Take first input type as precondition for the input is that all input types are part of the
            // same project
            ClassLoader projectClassLoader =
                ClassLoaderUtil.getProjectClassLoader(inputTypes.get(0).getJavaProject());
            for (IType type : inputTypes) {
                Class<?> loadedClass = projectClassLoader.loadClass(type.getFullyQualifiedName());
                this.inputTypes.put(type, loadedClass);
            }
            setInputs(Lists.<Object> newLinkedList(this.inputTypes.values()));
        }
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (03.12.2014)
     */
    @Override
    public void generate(TemplateTo template, boolean forceOverride) throws IOException, TemplateException,
        MergeException, CoreException {

        if (packageFolder != null) {
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

}
