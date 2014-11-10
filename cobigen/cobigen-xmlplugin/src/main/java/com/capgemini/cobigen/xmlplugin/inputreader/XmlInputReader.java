package com.capgemini.cobigen.xmlplugin.inputreader;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.extension.IInputReader;

/**
 *
 * @author fkreis (10.11.2014)
 */
public class XmlInputReader implements IInputReader {

    /**
     * {@inheritDoc}
     * @author fkreis (10.11.2014)
     */
    @Override
    public boolean isValidInput(Object input) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * @author fkreis (10.11.2014)
     */
    @Override
    public Map<String, Object> createModel(Object input) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * @author fkreis (10.11.2014)
     */
    @Override
    public boolean combinesMultipleInputObjects(Object input) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * @author fkreis (10.11.2014)
     */
    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc} <br>
     * <br>
     * Since the {@link XmlInputReader} does not provide any template methods it returns an empty {@link Map}.
     * @author fkreis (10.11.2014)
     */
    @Override
    public Map<String, Object> getTemplateMethods(Object input) {
        Map<String, Object> emptyMap = new HashMap<>();
        return emptyMap;
    }

}
