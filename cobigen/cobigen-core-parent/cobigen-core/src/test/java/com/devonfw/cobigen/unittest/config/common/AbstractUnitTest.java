package com.capgemini.cobigen.unittest.config.common;

import com.capgemini.cobigen.impl.extension.TemplateEngineRegistry;
import com.capgemini.cobigen.stubs.TemplateEngineStub;

/**
 * Abstract test super class to register the template engine stub.
 */
public abstract class AbstractUnitTest {

    static {
        TemplateEngineRegistry.register(TemplateEngineStub.class);
    }
}
