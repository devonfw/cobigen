package com.capgemini.cobigen.api.extension;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.impl.annotation.ExceptionFacade;

/**
 * This is an extension point to enable further generator input support. Implementations should inherit this
 * interface and should be registered via an implemented {@link TriggerInterpreter} to be integrated into the
 * CobiGen generation process.
 */
@ExceptionFacade
public interface InputReader {

    /**
     * This function will be called if matching triggers or matching templates should be retrieved for a given
     * input object
     * @param input
     *            object to be checked
     * @return <code>true</code> if the given input can be processed by the implementing {@link InputReader},
     *         <code>false</code> otherwise
     */
    public boolean isValidInput(Object input);

    /**
     * This function should create the FreeMarker object model from the given input
     * @param input
     *            object the model should be build of (not null)
     * @return a key to object {@link Map} representing an object model for the generation
     */
    public Map<String, Object> createModel(Object input);

    /**
     * States whether the given input object combines multiple input objects to be used for generation
     * @param input
     *            to be checked
     * @return <code>true</code> if the given input combines multiple input objects for generation<br>
     *         <code>false</code>, otherwise
     */
    public boolean combinesMultipleInputObjects(Object input);

    /**
     * Will return the set of combined input objects if the given input combines multiple input objects (resp.
     * {@link #combinesMultipleInputObjects(Object)} returns <code>true</code>).
     * @param input
     *            the combined input object
     * @param inputCharset
     *            to be used for reading new inputs
     * @return a list of input objects, the generation should be triggered for each.
     */
    public List<Object> getInputObjects(Object input, Charset inputCharset);

    /**
     * This method returns available template methods from the plugins as {@link Map}. If the plugin which
     * corresponds to the input does not provide any template methods an empty {@link Map} will be returned. *
     * @param input
     *            the object the methods should be derived from
     * @return a key to object {@link Map}. The keys are the method names and the objects are the
     *         corresponding instances of the class where the method is implemented.
     */
    public Map<String, Object> getTemplateMethods(Object input);

    /**
     * Will return the set of combined input objects if the given input combines multiple input objects (resp.
     * {@link #combinesMultipleInputObjects(Object)} returns <code>true</code>).
     * @param input
     *            the combined input object
     * @param inputCharset
     *            to be used for reading new inputs
     * @return a list of input objects, the generation should be triggered for each.
     */
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset);

}
