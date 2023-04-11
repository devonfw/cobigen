package com.devonfw.cobigen.templates.devon4j.test.templates;

import com.devonfw.cobigen.api.extension.TextTemplate;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.javaplugin.inputreader.JavaInputReader;
import com.devonfw.cobigen.tempeng.freemarker.FreeMarkerTemplateEngine;
import org.junit.Before;

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJavaTemplateTest {
  /**
   * Model used by freemarker
    */
  public Map<String, Object> model;
  /**
   * Engine that will generate output from model and template
   */
  public FreeMarkerTemplateEngine engine;
  /**
   * Implementation of TextTemplate for template processing
   */
  public TextTemplate template;

  /**
   * Creates an anonymous TextTemplate object that works on the given template files
   * @param relativePath Relative Path to the template from the /templates folder, most likely coincides with template
   *        filename
   * @param relativeAbsolutePath Relative path from source root to the template
   * @return anonymous instance of TextTemplate that holds the data in Overridden interface methods
   */
  public TextTemplate createTemplate(String relativePath, Path relativeAbsolutePath) {

    return new TextTemplate() {
      @Override
      public String getRelativeTemplatePath() {

        return relativePath;
      }

      @Override
      public Path getAbsoluteTemplatePath() {

        return relativeAbsolutePath;
      }
    };
  }

  /**
   * Creates a template engine for the given template path
   * 
   * @param templateFolderPath Relative path to template folder from project source root
   * @return {@link TextTemplateEngine} implementation for Apache FreeMarker.
   */
  public FreeMarkerTemplateEngine createEngine(String templateFolderPath) {

    FreeMarkerTemplateEngine engine = new FreeMarkerTemplateEngine();
    engine.setTemplateFolder(Paths.get(templateFolderPath));
    return engine;
  }

  /**
   * Instanciates a class of the given Type and adds it to the model with it's simplename.
   * (Just like all Utils following the naming convention)
   * @param clazz Class with a public NoArgsConstructor to instanciate it
   */
  public void addUtil(Class<?> clazz) {
    try {
      String name = clazz.getSimpleName();
      Object instance = clazz.getConstructor().newInstance();
      model.put(name, instance);
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new RuntimeException("Failed adding the Util to the Model, please check the error stacktrace and fix it or add the util manually!");
    }

  }

  /**
   * Consumes the given class to produce the template
   * @param modelClass Class to auto-generate reflective pojo model
   * @return Produced file
   */
  public String process(Class<?> modelClass) {
    Path tp = Paths.get(getTemplatePath());
    String filename = tp.getFileName().toString();
    Path templateFolder = tp.getParent();
    model = new JavaInputReader().createModel(modelClass);
    template = createTemplate(filename, templateFolder);
    engine = new FreeMarkerTemplateEngine();
    engine.setTemplateFolder(templateFolder);
    for (Class<?> utilClass : getUtils()) {
      addUtil(utilClass);
    }
    StringWriter out = new StringWriter();
    engine.process(template, model, out, "UTF-8");
    return out.toString();
  }

  /**
   * @return utils Classes to auto-instanciate and inject into freemarker for the template
   */
  abstract public Class<?>[] getUtils();

  /**
   * @return templatePath Path to the template relative to subproject source root
   */
  abstract public String getTemplatePath();
}
