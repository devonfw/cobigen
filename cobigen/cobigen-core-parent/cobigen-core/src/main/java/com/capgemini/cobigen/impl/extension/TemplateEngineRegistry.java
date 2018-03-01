package com.capgemini.cobigen.impl.extension;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.extension.TextTemplateEngine;
import com.capgemini.cobigen.impl.aop.ProxyFactory;
import com.google.common.collect.Maps;

/**
 * Registry for {@link TextTemplateEngine template engines}.
 */
public class TemplateEngineRegistry {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TemplateEngineRegistry.class);

    /**
     * Currently registered {@link TextTemplateEngine}s mapped by their type
     */
    private static Map<String, TextTemplateEngine> registeredEngines =
        Collections.synchronizedMap(Maps.<String, TextTemplateEngine> newHashMap());

    /**
     * Registers a new {@link TextTemplateEngine template engine}
     * @param <T>
     *            type of the {@link TextTemplateEngine template engine} to be registered
     * @param templateEngine
     *            {@link TextTemplateEngine template engine} to be registered
     */
    public static <T extends TextTemplateEngine> void register(Class<T> templateEngine) {

        try {
            TextTemplateEngine engine = templateEngine.newInstance();
            LOG.info("Register template engine '{}'.", templateEngine.getCanonicalName());

            if (StringUtils.isNotBlank(engine.getName())) {
                if (registeredEngines.containsKey(engine.getName())) {
                    throw new CobiGenRuntimeException(
                        "An template engine with type " + engine.getName() + " has already been registered.");
                }
                registeredEngines.put(engine.getName(), engine);
            } else {
                throw new CobiGenRuntimeException("Cannot register a template engine without a type.");
            }

        } catch (InstantiationException | IllegalAccessException e) {
            throw new CobiGenRuntimeException(
                "Could not intantiate TemplateEngine '" + templateEngine.getCanonicalName(), e);
        }
    }

    /**
     * Returns a {@link TextTemplateEngine template engine} based on its name.
     * @param name
     *            of the {@link TextTemplateEngine template engine}
     * @return the {@link TextTemplateEngine template engine} or {@code null} if no template engine has been
     *         registered with the name.
     */
    public static TextTemplateEngine getEngine(String name) {

        TextTemplateEngine templateEngine = registeredEngines.get(name);
        if (templateEngine == null) {
            throw new CobiGenRuntimeException("No template engine with name '" + name + "' registered.");
        }

        return ProxyFactory.getProxy(templateEngine);
    }
}
