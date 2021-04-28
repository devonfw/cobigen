package com.devonfw.cobigen.unittest.config.common;

import com.devonfw.cobigen.impl.extension.TemplateEngineRegistry;
import com.devonfw.cobigen.stubs.TemplateEngineStub;

/**
 * Abstract test super class to register the template engine stub.
 */
public abstract class AbstractUnitTest {

    static {
        TemplateEngineRegistry.register(TemplateEngineStub.class, "FreeMarker");
    }
}
