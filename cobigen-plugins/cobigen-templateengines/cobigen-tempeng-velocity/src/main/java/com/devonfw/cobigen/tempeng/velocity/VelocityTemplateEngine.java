package com.devonfw.cobigen.tempeng.velocity;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.annotation.Name;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.extension.TextTemplate;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.tempeng.velocity.constant.VelocityMetadata;
import com.devonfw.cobigen.tempeng.velocity.log.LogChuteDelegate;
import com.devonfw.cobigen.tempeng.velocity.runtime.resources.NullResourceCache;
import com.devonfw.cobigen.tempeng.velocity.runtime.resources.ResourceManagerDelegate;

/** Template engine for Apache Velocity */
@Name("Velocity")
public class VelocityTemplateEngine implements TextTemplateEngine {

    /** The file extension of the template files. */
    private static final String TEMPLATE_EXTENSION = ".vm";

    /**
     * The used template engine
     */
    private VelocityEngine engine;

    /**
     * Constructor that sets the most common properties for the Velocity engine<br>
     *
     * <ul>
     * <li>A slf4j based logger</li>
     * <li>Default encoding of UTF-8</li>
     * <li>No Cache</li>
     * <li>FileResourceLoader class by default</li>
     * </ul>
     */
    public VelocityTemplateEngine() {
        engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM,
            new LogChuteDelegate(LoggerFactory.getLogger(VelocityEngine.class)));
        engine.setProperty(RuntimeConstants.ENCODING_DEFAULT, "UTF-8");
        engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, new Boolean(false));
        engine.setProperty(RuntimeConstants.RESOURCE_MANAGER_CLASS, ResourceManagerDelegate.class.getName());
        engine.setProperty(RuntimeConstants.RESOURCE_MANAGER_LOGWHENFOUND, new Boolean(true));
        engine.setProperty(RuntimeConstants.RESOURCE_MANAGER_CACHE_CLASS, NullResourceCache.class.getName());
    }

    @Override
    public String getTemplateFileEnding() {
        return TEMPLATE_EXTENSION;
    }

    @Override
    public void process(TextTemplate template, Map<String, Object> model, Writer out, String outputEncoding) {
        engine.setProperty(RuntimeConstants.OUTPUT_ENCODING, outputEncoding);
        executeInThisClassloader(null, (p) -> {
            engine.init();
            return null;
        });

        Context context = new VelocityContext(model);
        Template vmTemplate = null;
        try {
            vmTemplate =
                executeInThisClassloader(template.getRelativeTemplatePath(), (path) -> engine.getTemplate(path));
        } catch (Throwable e) {
            throw new CobiGenRuntimeException(
                "An error occured while retrieving the Velocity template " + template.getAbsoluteTemplatePath()
                    + " from the Velocity configuration. (Velocity v" + VelocityMetadata.VERSION + ")",
                e);
        }

        if (vmTemplate != null) {
            try {
                vmTemplate.merge(context, out);
            } catch (VelocityException e) {
                throw new CobiGenRuntimeException(
                    "An error occurred while generating the template." + template.getAbsoluteTemplatePath()
                        + "(Velocity v" + VelocityMetadata.VERSION + ")",
                    e);
            } catch (Throwable e) {
                throw new CobiGenRuntimeException("An unkonwn error occurred while generating the template."
                    + template.getAbsoluteTemplatePath() + "(Velocity v" + VelocityMetadata.VERSION + ")", e);
            }
        }
    }

    @Override
    public void setTemplateFolder(Path templateFolderPath) {
        engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templateFolderPath.toString());
    }

    /**
     * Execute a function within the classloader loading THIS class to circumvent from velocity classpath
     * conflicts in osgi environments
     * @param <T>
     *            parameter type of the function
     * @param <R>
     *            return type of the function
     * @param param
     *            function parameter
     * @param exec
     *            function to be called
     * @return the return value of the function
     */
    private <T, R> R executeInThisClassloader(T param, Function<T, R> exec) {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        thread.setContextClassLoader(this.getClass().getClassLoader());
        try {
            return exec.apply(param);
        } finally {
            thread.setContextClassLoader(loader);
        }
    }
}
