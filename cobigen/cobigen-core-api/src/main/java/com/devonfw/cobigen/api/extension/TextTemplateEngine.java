package com.devonfw.cobigen.api.extension;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

import com.devonfw.cobigen.api.annotation.ExceptionFacade;

/**
 * Plug-ins providing a new template engine should implement this interface. The implementation has to be registered in
 * {@link GeneratorPluginActivator}.
 */
@ExceptionFacade
public interface TextTemplateEngine {

  /**
   * The return value is considered for automatically retrieving file names from templates within a template-scan. The
   * template file ending will be eliminated (if exists) from a template's file name to recover the target file name.
   *
   * @return the file ending of templates in the format ".ftl". Might be {@code null} if the templates do not specify
   *         any file ending.
   */
  public String getTemplateFileEnding();

  /**
   * Processes the passed template with the passed model as input and writes the generated result to the output writer
   * with the given output encoding.
   *
   * @param template to be processed
   * @param model input for template processing
   * @param out output writer
   * @param outputEncoding output encoding
   */
  public void process(TextTemplate template, Map<String, Object> model, Writer out, String outputEncoding);

  /**
   * Sets the root folder of all templates to resolve relative template paths on.
   *
   * @param templateFolderPath the root folder of all templates.
   */
  public void setTemplateFolder(Path templateFolderPath);
}
