package com.capgemini.cobigen.swaggerplugin.inputreader;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.swaggerplugin.inputreader.to.SwaggerFile;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

/**
 *
 */
public class SwaggerInputReader implements InputReader {

    @Override
    public boolean isValidInput(Object input) {
        if (input instanceof SwaggerFile) {
            Swagger swagger = new SwaggerParser().read(((File) input).getAbsolutePath());
            if (swagger == null) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Object> createModel(Object input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean combinesMultipleInputObjects(Object input) {
        if (input instanceof SwaggerFile) {
            return true;
        }
        return false;
    }

    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        return getInputObjectsRecursively(input, inputCharset);
    }

    @Override
    public Map<String, Object> getTemplateMethods(Object input) {
        return new HashMap<>();
    }

    @Override
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {
        Swagger swagger = new SwaggerParser().read(((File) input).getAbsolutePath());
        return null;
    }

}
