package com.capgemini.cobigen.senchaplugin;

import java.util.Random;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.senchaplugin.inputreader.JSInputReader;
import com.capgemini.cobigen.senchaplugin.matcher.JSMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Java Interpreter
 * @author rudiazma (28 de jul. de 2016)
 */
public class JSTriggerInterpreter implements TriggerInterpreter {

    /**
     * {@link TriggerInterpreter} type to be registered
     */
    public String type;

    private String modelId;

    private String controllerId;

    private String storeId;

    private String viewId;

    /**
     * Creates a new Java Interpreter
     * @param type
     *            to be registered
     * @author rudiazma (26 de jul. de 2016)
     */
    public JSTriggerInterpreter(String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * @author rudiazma (28 de jul. de 2016)
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * @author rudiazma (28 de jul. de 2016)
     */
    @Override
    public InputReader getInputReader() {
        return new JSInputReader(getModelId(), getControllerId(), getStoreId(), getViewId());
    }

    /**
     * {@inheritDoc}
     * @author rudiazma (28 de jul. de 2016)
     */
    @Override
    public MatcherInterpreter getMatcher() {
        return new JSMatcher();
    }

    /**
     * Generates random hexadecimal ID for Architect objects
     * @param length
     *            of the ID
     * @return id
     * @author rudiazma (Sep 19, 2016)
     */
    public static String createRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        String id = sb.toString();
        String resultId = id.substring(0, 8) + '-' + id.substring(8, 12) + '-' + id.substring(12, 16) + '-'
            + id.substring(16, 20) + '-' + id.substring(20, 32);
        return resultId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }
}
