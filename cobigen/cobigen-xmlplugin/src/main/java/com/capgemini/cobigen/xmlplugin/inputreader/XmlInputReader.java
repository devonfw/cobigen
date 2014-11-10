package com.capgemini.cobigen.xmlplugin.inputreader;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.dom.DOMDocument;

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
        // TODO should DOMElement also be provieded?
        if (input instanceof DOMDocument) {
            return true;
        } else {
            return false;
        }

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
     * {@inheritDoc}<br>
     * <br>
     * Since the {@link XmlInputReader} does not support multiple input objects it always returns
     * <code>false</code>.
     * @author fkreis (10.11.2014)
     */
    @Override
    public boolean combinesMultipleInputObjects(Object input) {
        return false;
    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * Since the {@link XmlInputReader} does not support multiple input objects it always returns an empty
     * {@link List}.
     * @author fkreis (10.11.2014)
     */
    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        List<Object> emptyList = new LinkedList<>();
        return emptyList;
    }

    /**
     * {@inheritDoc} <br>
     * <br>
     * Since the {@link XmlInputReader} does not provide any template methods it always returns an empty
     * {@link Map}.
     * @author fkreis (10.11.2014)
     */
    @Override
    public Map<String, Object> getTemplateMethods(Object input) {
        Map<String, Object> emptyMap = new HashMap<>();
        return emptyMap;
    }

}
