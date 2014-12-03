package com.capgemini.cobigen.eclipse.generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.Charsets;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.tools.PathUtil;
import com.capgemini.cobigen.eclipse.generator.java.entity.ComparableIncrement;
import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 *
 * @author mbrunnli (02.12.2014)
 */
public abstract class CobiGenInputHolder extends CobiGenWrapper {

    private boolean initialized;

    private List<Object> inputs;

    /**
     * All matching templates for the currently configured {@link #inputTypes input types}
     */
    private List<TemplateTo> matchingTemplates = Lists.newLinkedList();

    public CobiGenInputHolder() throws GeneratorProjectNotExistentException, CoreException {
        super();
    }

    public CobiGenInputHolder(List<Object> inputs) throws GeneratorProjectNotExistentException, CoreException {
        super();
        setInputs(inputs);
    }

    public void setInputs(List<Object> inputs) {
        this.inputs = inputs;
        initialized = this.inputs != null && this.inputs.size() > 0;

        if (initialized) {
            matchingTemplates = Lists.newLinkedList();
            for (Object input : this.inputs) {
                matchingTemplates.addAll(cobiGen.getMatchingTemplates(input));
            }
        } else {
            matchingTemplates = null;
        }
    }

    /**
     * Returns all matching trigger ids for the currently stored input
     *
     * @return a list of matching trigger ids
     * @author mbrunnli (03.06.2014)
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
     *
     * @return all available generation packages
     * @author mbrunnli (25.02.2013)
     */
    public ComparableIncrement[] getAllIncrements() {

        LinkedList<ComparableIncrement> result = Lists.newLinkedList();
        List<IncrementTo> matchingIncrements;
        if (initialized) {
            matchingIncrements = cobiGen.getMatchingIncrements(inputs.get(0));

            // convert to comparable increments
            for (IncrementTo increment : matchingIncrements) {
                result.add(new ComparableIncrement(increment.getId(), increment.getDescription(), increment
                    .getTriggerId(), increment.getTemplates(), increment.getDependentIncrements()));
            }
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
     * Returns all available generation packages (sorted and element "all" added on top)
     *
     * @return all available generation packages
     * @author mbrunnli (25.02.2013)
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
     * Returns project dependent paths of all possible generated resources for the first input in case of
     * batch generation
     *
     * @return project dependent paths of all possible generated resources
     * @author mbrunnli (26.04.2013)
     */
    public Set<IFile> getAllTargetFilesOfFirstInput() {

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
        if (!initialized) {
            return Sets.newLinkedHashSetWithExpectedSize(0);
        }

        Set<IFile> files = new HashSet<>();
        for (TemplateTo t : getAllTemplates()) {
            if (cobiGen.combinesMultipleInputs(inputs.get(0))) {
                List<Object> children = new JavaInputReader().getInputObjects(inputs.get(0), Charsets.UTF_8);
                for (Object child : children) {
                    files.add(getGenerationTargetProject().getFile(t.resolveDestinationPath(child)));
                }
            } else {
                for (Object input : inputs) {
                    files.add(getGenerationTargetProject().getFile(t.resolveDestinationPath(input)));
                }
            }
        }
        return files;
    }

    /**
     * Returns the currently set input to be generated with
     * @return the currently set input to be generated with
     * @author mbrunnli (16.10.2014)
     */
    public Object getCurrentRepresentingInput() {
        if (inputs == null || inputs.size() == 0) {
            return null;
        }

        if (cobiGen.combinesMultipleInputs(inputs.get(0))) {
            List<Object> packageChildren = new JavaInputReader().getInputObjects(inputs, Charsets.UTF_8);
            // we have to return one of the children do enable correct variable solution in the user interface
            return packageChildren.get(0);
        } else {
            return inputs.get(0);
        }
    }
}
