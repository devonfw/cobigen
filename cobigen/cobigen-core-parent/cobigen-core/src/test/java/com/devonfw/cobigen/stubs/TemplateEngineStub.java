package com.capgemini.cobigen.stubs;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

import com.capgemini.cobigen.api.extension.TextTemplate;
import com.capgemini.cobigen.api.extension.TextTemplateEngine;

/**
 * Fake template engine matching the default template engine FreeMarker.
 */
public class TemplateEngineStub implements TextTemplateEngine {

    @Override
    public String getName() {
        return "FreeMarker";
    }

    @Override
    public String getTemplateFileEnding() {
        return ".ftl";
    }

    @Override
    public void process(TextTemplate template, Map<String, Object> model, Writer out, String outputEncoding) {

    }

    @Override
    public void setTemplateFolder(Path templateFolderPath) {

    }

}
