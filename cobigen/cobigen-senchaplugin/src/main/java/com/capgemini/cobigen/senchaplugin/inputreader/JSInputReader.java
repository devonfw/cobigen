package com.capgemini.cobigen.senchaplugin.inputreader;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.extension.IInputReader;

/**
 *
 * @author rudiazma (4 de ago. de 2016)
 */
public class JSInputReader implements IInputReader {

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

}
