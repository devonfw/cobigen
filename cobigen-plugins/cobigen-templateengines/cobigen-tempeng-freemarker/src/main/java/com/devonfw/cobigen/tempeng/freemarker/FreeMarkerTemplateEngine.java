package com.devonfw.cobigen.tempeng.freemarker;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

import com.devonfw.cobigen.api.annotation.Name;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.extension.TextTemplate;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.tempeng.freemarker.constant.FreemarkerMetadata;

import freemarker.cache.NullCacheStorage;
import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * {@link TextTemplateEngine} implementation for Apache FreeMarker.
 */
@Name("FreeMarker")
public class FreeMarkerTemplateEngine implements TextTemplateEngine {

  /** The file extension of the template files. */
  private static final String TEMPLATE_EXTENSION = ".ftl";

  /** The commonly used FreeMarker engine configuration */
  private Configuration freeMarkerConfig;

  /**
   * Constructor, which initializes the commonly used FreeMarker configuration.
   */
  public FreeMarkerTemplateEngine() {

    this.freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_23);
    this.freeMarkerConfig.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23).build());
    this.freeMarkerConfig.clearEncodingMap();
    this.freeMarkerConfig.setDefaultEncoding("UTF-8");
    this.freeMarkerConfig.setLocalizedLookup(false);
    this.freeMarkerConfig.setTemplateLoader(new NioFileSystemTemplateLoader());
    this.freeMarkerConfig.setCacheStorage(new NullCacheStorage());
  }

  @Override
  public String getTemplateFileEnding() {

    return TEMPLATE_EXTENSION;
  }

  @Override
  public void process(TextTemplate template, Map<String, Object> model, Writer out, String outputEncoding) {

    Template fmTemplate = null;
    try {
      fmTemplate = this.freeMarkerConfig.getTemplate(template.getRelativeTemplatePath());
    } catch (ParseException e) {
      throw new CobiGenRuntimeException("Could not parse FreeMarker template: " + template.getAbsoluteTemplatePath()
          + ". (FreeMarker v" + FreemarkerMetadata.VERSION + " )", e);
    } catch (Throwable e) {
      throw new CobiGenRuntimeException(
          "An error occured while retrieving the FreeMarker template: " + template.getAbsoluteTemplatePath()
              + " from the FreeMarker configuration. (FreeMarker v" + FreemarkerMetadata.VERSION + " )",
          e);
    }

    if (fmTemplate != null) {
      try {
        Environment env = fmTemplate.createProcessingEnvironment(model, out);
        env.setOutputEncoding(outputEncoding);
        env.setLogTemplateExceptions(false); // no duplicate logging
        env.process();
      } catch (TemplateException e) {
        String detailedCause = "";
        if (e.getCause().getClass().getCanonicalName().contains("java.lang")) {
          detailedCause = ". A problem with Reflection has likely occurred: "
                  + e.getCause().toString()
                  + ", please consider rebuilding your project as a possible fix.";
        }
        throw new CobiGenRuntimeException("An error occurred while generating the template: "
            + template.getAbsoluteTemplatePath() + " (FreeMarker v" + FreemarkerMetadata.VERSION + ")" + detailedCause, e);
      } catch (Throwable e) {
        throw new CobiGenRuntimeException("An unkonwn error occurred while generating the template: "
            + template.getAbsoluteTemplatePath() + " (FreeMarker v" + FreemarkerMetadata.VERSION + ")", e);
      }
    }
  }

  @Override
  public void setTemplateFolder(Path templateFolderPath) {

    ((NioFileSystemTemplateLoader) this.freeMarkerConfig.getTemplateLoader()).setTemplateRoot(templateFolderPath);
  }

}
