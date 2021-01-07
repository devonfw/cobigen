package com.devonfw.cobigen.api;

import java.nio.file.Path;
import java.util.List;

import com.devonfw.cobigen.api.annotation.Cached;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;

/**
 * The configuration interpreter is responsible to interpret the underlying CobiGen configuration and e.g.
 * retrieve templates and increment for a specific input.
 */
public interface ConfigurationInterpreter {

    /**
     * Returns all matching trigger ids for a given input object
     *
     * @param matcherInput
     *            object
     * @return the {@link List} of matching trigger ids
     */
    public List<String> getMatchingTriggerIds(Object matcherInput);

    /**
     * Returns all matching increments for a given input object
     *
     * @param matcherInput
     *            object
     * @return this {@link List} of matching increments
     * @throws InvalidConfigurationException
     *             if the configuration of CobiGen is not valid
     */
    public List<IncrementTo> getMatchingIncrements(Object matcherInput) throws InvalidConfigurationException;

    /**
     * Returns the {@link List} of matching templates for the given input object
     *
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @return the {@link List} of matching templates
     * @throws InvalidConfigurationException
     *             if the configuration is not valid
     */
    @Cached
    public List<TemplateTo> getMatchingTemplates(Object matcherInput) throws InvalidConfigurationException;

    /**
     * Resolves the destination path of a template for a given root path to generate to as well as a given
     * input to be used for variable resolution.
     * @param targetRootPath
     *            root path to generate to
     * @param template
     *            {@link TemplateTo} to resolve the path for
     * @param input
     *            the generator input to be used.
     * @return the full {@link Path} of the targeted file by the template
     */
    public Path resolveTemplateDestinationPath(Path targetRootPath, TemplateTo template, Object input);

}
