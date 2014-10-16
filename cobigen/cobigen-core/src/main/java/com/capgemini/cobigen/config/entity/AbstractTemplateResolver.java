package com.capgemini.cobigen.config.entity;

import java.util.Map;

import com.capgemini.cobigen.config.resolver.PathExpressionResolver;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.model.ContextVariableResolver;

/**
 * Implements the common functionality of template destination path resolving
 * @author mbrunnli (16.10.2014)
 */
public abstract class AbstractTemplateResolver {

    /**
     * Relative path for the result.
     */
    private String unresolvedDestinationPath;

    /**
     * The trigger's id the template is assigned to
     */
    private Trigger trigger;

    /**
     * Used interpreter for the given trigger
     */
    private ITriggerInterpreter triggerInterpreter;

    /**
     * Initializes all fields
     * @param unresolvedDestinationPath
     *            unresolved (raw) destination path from the templates configuration
     * @param trigger
     *            trigger the template has been retrieved from
     * @param triggerInterpreter
     *            the trigger has been interpreted with
     * @author mbrunnli (16.10.2014)
     */
    public AbstractTemplateResolver(String unresolvedDestinationPath, Trigger trigger,
        ITriggerInterpreter triggerInterpreter) {
        this.unresolvedDestinationPath = unresolvedDestinationPath;
        this.trigger = trigger;
        this.triggerInterpreter = triggerInterpreter;
    }

    /**
     * Returns the unresolved destination path defined in the templates configuration
     * @return the unresolved destination path
     * @author mbrunnli (16.10.2014)
     */
    public String getUnresolvedDestinationPath() {
        return unresolvedDestinationPath;
    }

    /**
     * Returns the destination path the generated resources should be generated to
     * @return the destination path
     * @param input
     *            the destination path should be resolved for
     * @author mbrunnli (09.04.2014)
     */
    public String resolveDestinationPath(Object input) {
        Map<String, String> variables =
            new ContextVariableResolver(input, trigger).resolveVariables(triggerInterpreter);
        return new PathExpressionResolver(variables).evaluateExpressions(unresolvedDestinationPath);
    }

    /**
     * Returns the field 'trigger'
     * @return value of trigger
     * @author mbrunnli (16.10.2014)
     */
    protected Trigger getTrigger() {
        return trigger;
    }
}
