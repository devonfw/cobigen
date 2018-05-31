package com.capgemini.cobigen.tempeng.freemarker;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.extension.TextTemplate;
import com.capgemini.cobigen.api.extension.TextTemplateEngine;

import freemarker.cache.NullCacheStorage;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * {@link TextTemplateEngine} implementation for Apache FreeMarker.
 */
public class FreeMarkerTemplateEngine implements TextTemplateEngine {

    /** Template Engine name */
    private static final String ENGINE_NAME = "FreeMarker";

    /** The file extension of the template files. */
    private static final String TEMPLATE_EXTENSION = ".ftl";

    /** The commonly used FreeMarker engine configuration */
    private Configuration freeMarkerConfig;

    /**
     * Constructor, which initializes the commonly used FreeMarker configuration.
     */
    public FreeMarkerTemplateEngine() {
        freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_23);
        freeMarkerConfig.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23).build());
        freeMarkerConfig.clearEncodingMap();
        freeMarkerConfig.setDefaultEncoding("UTF-8");
        freeMarkerConfig.setLocalizedLookup(false);
        freeMarkerConfig.setTemplateLoader(new NioFileSystemTemplateLoader());
        freeMarkerConfig.setCacheStorage(new NullCacheStorage());
    }

    @Override
    public String getName() {
        return ENGINE_NAME;
    }

    @Override
    public String getTemplateFileEnding() {
        return TEMPLATE_EXTENSION;
    }

    @Override
    public void process(TextTemplate template, Map<String, Object> model, Writer out, String outputEncoding) {
        Template fmTemplate = null;
        try {
            fmTemplate = freeMarkerConfig.getTemplate(template.getRelativeTemplatePath());
        } catch (Throwable e) {
            throw new CobiGenRuntimeException("An error occured while retrieving the FreeMarker template "
                + template.getAbsoluteTemplatePath() + " from the FreeMarker configuration.", e);
        }

        if (fmTemplate != null) {
            try {
                Environment env = fmTemplate.createProcessingEnvironment(model, out);
                env.setOutputEncoding(outputEncoding);
                env.process();
            } catch (TemplateException e) {
                throw new CobiGenRuntimeException("An error occurred while generating the template "
                    + template.getAbsoluteTemplatePath() + "\n" + e.getMessage(), e);
            } catch (Throwable e) {
                throw new CobiGenRuntimeException(
                    "An unkonwn error occurred while generating the template " + template.getAbsoluteTemplatePath(), e);
            }
        }
    }

    @Override
    public void setTemplateFolder(Path templateFolderPath) {
        ((NioFileSystemTemplateLoader) freeMarkerConfig.getTemplateLoader()).setTemplateRoot(templateFolderPath);
    }

}
