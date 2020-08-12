package com.devonfw.cobigen.impl.generator;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
        Set<String> keySet = PluginRegistry.getTriggerInterpreterKeySet();
        // We first try to find an input reader that is most likely readable
        HashMap<String, Boolean> readableCache = new HashMap<String, Boolean>();

        // Create cache for readable states
        for (String s : keySet) {
            readableCache.put(s, isMostLikelyReadable(s, path));
        }

        // Check external input readers first
        for (String s : readableCache.keySet()) {
            try {
                if (readableCache.get(s) != null && readableCache.get(s)) {
                    LOG.info("Try reading input {} with EXTERNAL inputreader '{}'...", path, s);
                    return getInputReader(s).read(path, inputCharset, additionalArguments);
                }
            } catch (InputReaderException e) {
                LOG.debug(
                    "Was not able to read input {} with EXTERNAL inputreader '{}' although it was reported to be most likely readable. Trying next input reader...",
                    path, s, e);
            } catch (Throwable e) {
                LOG.debug(
                    "While reading the input {} with the EXTERNAL inputreader {}, an Exception occured. Trying next input reader...",
                    path, s, e);
            }
        }

        // If no external input reader was found, check internal input readers
        for (String s : readableCache.keySet()) {
            try {
                if (readableCache.get(s) == null) {
                    LOG.info("Try reading input {} with DEFAULT inputreader '{}'...", path, s);
                    return getInputReader(s).read(path, inputCharset, additionalArguments);
                }
            } catch (InputReaderException e) {
                LOG.debug(
                    "Was not able to read input {} with DEFAULT inputreader '{}' although it was reported to be most likely readable. Trying next input reader...",
                    path, s, e);
            } catch (Throwable e) {
                LOG.debug(
                    "While reading the input {} with the DEFAULT inputreader {}, an Exception occured. Trying next input reader...",
                    path, s, e);
            }
        }
        throw new InputReaderException("Could not read input at path " + path + " with any installed plugin.");
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
