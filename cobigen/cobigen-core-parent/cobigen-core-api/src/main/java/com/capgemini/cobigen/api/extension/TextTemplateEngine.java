package com.capgemini.cobigen.api.extension;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

import com.capgemini.cobigen.api.annotation.ExceptionFacade;

/**
 * Plug-ins providing a new template engine should implement this interface. The implementation has to be
 * registered in {@link GeneratorPluginActivator}.
 */
@ExceptionFacade
public interface TextTemplateEngine {

    /**
     * Returns the identifying name of the template engine.
     * @return name of the template engine
     */
    public String getName();

    /**
     * Processes the passed template with the passed model as input and writes the generated result to the
     * output writer with the given output encoding.
     * @param template
     *            to be processed
     * @param model
     *            input for template processing
     * @param out
     *            output writer
     * @param outputEncoding
     *            output encoding
     */
    public void process(TextTemplate template, Map<String, Object> model, Writer out, String outputEncoding);

    /**
     * Sets the root folder of all templates to resolve relative template paths on.
     * @param templateFolderPath
     *            the root folder of all templates.
     */
    public void setTemplateFolder(Path templateFolderPath);
}
