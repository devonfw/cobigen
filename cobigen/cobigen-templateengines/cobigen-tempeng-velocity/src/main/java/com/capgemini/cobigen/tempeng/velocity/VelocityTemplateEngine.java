package com.capgemini.cobigen.tempeng.velocity;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.extension.TextTemplate;
import com.capgemini.cobigen.api.extension.TextTemplateEngine;
import com.capgemini.cobigen.tempeng.velocity.log.LogChuteDelegate;

/**
 *
 */
public class VelocityTemplateEngine implements TextTemplateEngine {

    /** Template Engine name */
    private static final String ENGINE_NAME = "Velocity";

    /** The file extension of the template files. */
    private static final String TEMPLATE_EXTENSION = ".vm";

    /**
     * The used template engine
     */
    private VelocityEngine engine;

    /**
     * Constructor that sets the most common properties for the Velocity engine<br/>
     * <list><li>A slf4j based logger</li> <li>Default encoding of UTF-8</li> <li>No Cache</li> <li>
     * FileResourceLoader class by default</li></list>
     */
    public VelocityTemplateEngine() {
        engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM,
            new LogChuteDelegate(LoggerFactory.getLogger(VelocityEngine.class)));
        engine.setProperty(RuntimeConstants.ENCODING_DEFAULT, "UTF-8");
        engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, new Boolean(false));

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
        engine.setProperty(RuntimeConstants.OUTPUT_ENCODING, outputEncoding);
        engine.init();
        Context context = new VelocityContext(model);
        Template vmTemplate = null;
        try {
            vmTemplate = engine.getTemplate(template.getRelativeTemplatePath());

        } catch (Throwable e) {
            throw new CobiGenRuntimeException("An error occured while retrieving the Velocity template "
                + template.getAbsoluteTemplatePath() + " from the Velocity configuration.", e);
        }

        if (vmTemplate != null) {
            try {
                vmTemplate.merge(context, out);
            } catch (VelocityException e) {
                throw new CobiGenRuntimeException("An error occurred while generating the template "
                    + template.getAbsoluteTemplatePath() + "\n" + e.getMessage(), e);
            } catch (Throwable e) {
                throw new CobiGenRuntimeException("An unkonwn error occurred while generating the template "
                    + template.getAbsoluteTemplatePath(), e);
            }
        }

    }

    @Override
    public void setTemplateFolder(Path templateFolderPath) {
        engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templateFolderPath.toString());
    }

}
