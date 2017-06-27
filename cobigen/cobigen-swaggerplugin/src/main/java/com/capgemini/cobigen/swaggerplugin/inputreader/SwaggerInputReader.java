package com.capgemini.cobigen.swaggerplugin.inputreader;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.extension.InputReader;

/**
 * 
 */
public class SwaggerInputReader implements InputReader {

    @Override
    public boolean isValidInput(Object input) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<String, Object> createModel(Object input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean combinesMultipleInputObjects(Object input) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getTemplateMethods(Object input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {
        // TODO Auto-generated method stub
        return null;
    }

}
