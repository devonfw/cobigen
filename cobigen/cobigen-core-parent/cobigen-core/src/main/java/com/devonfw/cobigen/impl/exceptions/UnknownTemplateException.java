package com.capgemini.cobigen.impl.exceptions;

import com.capgemini.cobigen.api.exception.InvalidConfigurationException;

/**
 * The {@link UnknownTemplateException} occurs if a template is requested which is not registered in the
 * template configuration
 * @author mbrunnli (19.02.2013)
 */
public class UnknownTemplateException extends InvalidConfigurationException {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 1;

    /**
     * Creates a new {@link UnknownTemplateException} with the given message
     * @param templateId
     *            Template ID
     */
    public UnknownTemplateException(String templateId) {
        super("Unknown template with id=" + templateId + ". Template could not be found in the configuration.");
    }
}
