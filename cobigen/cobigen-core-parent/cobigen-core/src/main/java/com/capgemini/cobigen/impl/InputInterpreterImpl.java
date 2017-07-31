package com.capgemini.cobigen.impl;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import com.capgemini.cobigen.api.InputInterpreter;
import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.exception.InputReaderException;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;

/**
 * Implementation of the CobiGen API for input processing
 */
public class InputInterpreterImpl implements InputInterpreter {

    @Override
    public List<Object> getInputObjectsRecursively(String type, Object input, Charset inputCharset) {
        return getInputReader(type).getInputObjectsRecursively(input, inputCharset);
    }

    @Override
    public List<Object> getInputObjects(String type, Object input, Charset inputCharset) {
        return getInputReader(type).getInputObjects(input, inputCharset);
    }

    @Override
    public Object read(String type, Path path, Charset inputCharset, Object... additionalArguments)
        throws InputReaderException {
        return getInputReader(type).read(path, inputCharset, additionalArguments);
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
            throw new CobiGenRuntimeException("No Plugin registered for type " + type);
        }
        if (triggerInterpreter.getInputReader() == null) {
            throw new CobiGenRuntimeException("No InputReader available for type " + type);
        }

        return triggerInterpreter.getInputReader();
    }

}
