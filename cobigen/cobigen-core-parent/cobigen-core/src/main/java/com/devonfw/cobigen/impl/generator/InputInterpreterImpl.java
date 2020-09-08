package com.devonfw.cobigen.impl.generator;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.InputInterpreter;
import com.devonfw.cobigen.api.annotation.Cached;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.exception.PluginNotAvailableException;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.generator.api.InputResolver;
import com.devonfw.cobigen.impl.generator.api.TriggerMatchingEvaluator;

/**
 * Implementation of the CobiGen API for input processing
 */
public class InputInterpreterImpl implements InputInterpreter {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(InputInterpreterImpl.class);

    /** Configuration interpreter instance */
    @Inject
    private TriggerMatchingEvaluator configurationInterpreter;

    /** {@link InputResolver} instance */
    @Inject
    private InputResolver inputResolver;

    @Cached
    @Override
    public boolean combinesMultipleInputs(Object input) {
        List<Trigger> matchingTriggers = configurationInterpreter.getMatchingTriggers(input);
        return matchingTriggers.stream().anyMatch(e -> e.matchesByContainerMatcher());
    }

    @Cached
    @Override
    public List<Object> resolveContainers(Object input) {
        List<Trigger> matchingTriggers = configurationInterpreter.getMatchingTriggers(input);
        List<Object> inputs = new ArrayList<>();
        for (Trigger t : matchingTriggers) {
            inputs.addAll(inputResolver.resolveContainerElements(input, t));
        }
        return inputs;
    }

    // not cached by intention
    @Override
    public Object read(String type, Path path, Charset inputCharset, Object... additionalArguments)
        throws InputReaderException {
        return getInputReader(type).read(path, inputCharset, additionalArguments);
    }

    @Override
    public Object read(Path path, Charset inputCharset, Object... additionalArguments) throws InputReaderException {
        List<String> triggerInterpreterKeySet = PluginRegistry.getTriggerInterpreterKeySet();

        // We first try to find an input reader that is most likely readable
        Map<String, Boolean> readableCache = new HashMap<>();
        Object readable = null;

        // First find plug-ins which might come from outside of cobigen space (externally developed plugins)
        for (String triggerType : triggerInterpreterKeySet) {
            readable = readInput(path, inputCharset, readableCache, triggerType, true, additionalArguments);
            if (readable != null) {
                return readable;
            }
        }

        // If no external input reader was found, check internal input readers
        for (String triggerType : readableCache.keySet()) {
            readable = readInput(path, inputCharset, readableCache, triggerType, null, additionalArguments);
            if (readable != null) {
                return readable;
            }
        }

        throw new InputReaderException("Could not read input at path " + path + " with any installed plugin.");

    }

    /**
     * Checks and returns a valid input either directly or from a provided cache.
     *
     * @param path
     *            the Path to the object. Can also point to a folder
     * @param inputCharset
     *            of the input to be used
     * @param readableCache
     *            HashMap of TriggerInterpreter and Boolean
     * @param triggerType
     *            type of TriggerInterpreter
     * @param expectedResult
     *            Boolean expected result of isMostLikelyReadable check
     * @param additionalArguments
     *            depending on the InputReader implementation
     * @return Object that is a valid input or null if the file cannot be read by any InputReader
     */
    private Object readInput(Path path, Charset inputCharset, Map<String, Boolean> readableCache, String triggerType,
        Boolean expectedResult, Object... additionalArguments) {
        String readerType = "EXTERNAL";
        if (expectedResult == null) {
            readerType = "INTERNAL";
        }
        try {
            if (isMostLikelyReadable(triggerType, path, readableCache) == expectedResult) {
                LOG.info("Try reading input {} with {} inputreader '{}'...", path, readerType, triggerType);
                return getInputReader(triggerType).read(path, inputCharset, additionalArguments);
            }
        } catch (InputReaderException e) {
            LOG.debug(
                "Was not able to read input {} with {} inputreader '{}' although it was reported to be most likely readable. Trying next input reader...",
                path, readerType, triggerType, e);
        } catch (Throwable e) {
            LOG.debug(
                "While reading the input {} with the {} inputreader {}, an exception occured. Trying next input reader...",
                path, readerType, triggerType, e);
        }
        return null;
    }

    /**
     * Checks if the input is most likely readable and fills the provided cache
     *
     * @param type
     *            String of TriggerType
     * @param path
     *            the file Path
     * @param cache
     *            Map of TriggerType and isMostLikelyReadable check results
     * @return Boolean true if readable by external plugin, null for internal plugin and false if not readable
     */
    private Boolean isMostLikelyReadable(String type, Path path, Map<String, Boolean> cache) {
        if (!cache.containsKey(type)) {
            cache.put(type, isMostLikelyReadable(type, path));
        }
        return cache.get(type);
    }

    @Override
    public Boolean isMostLikelyReadable(String type, Path path) {
        return getInputReader(type).isMostLikelyReadable(path);
    }

    /**
     * @param type
     *            of the input
     * @return InputReader for the given type.
     * @throws CobiGenRuntimeException
     *             if no InputReadercould be found
     */
    private InputReader getInputReader(String type) {
        TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(type);
        if (triggerInterpreter == null) {
            throw new PluginNotAvailableException("TriggerInterpreter", type);
        }
        if (triggerInterpreter.getInputReader() == null) {
            throw new PluginNotAvailableException("InputReader", type);
        }

        return triggerInterpreter.getInputReader();
    }

}
