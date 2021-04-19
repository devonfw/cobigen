package com.devonfw.cobigen.tsplugin.inputreader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.externalprocess.ExternalProcess;
import com.devonfw.cobigen.api.externalprocess.ExternalServerInputReaderProxy;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TypeScript input reader that uses a server to read TypeScript code
 */
public class TypeScriptInputReader extends ExternalServerInputReaderProxy {

    /** Valid file extension */
    public static final String VALID_EXTENSION = "ts";

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TypeScriptInputReader.class);

    /**
     * Creates a new instance of TypeScriptInputReader
     * @param externalProcess
     *            the external process instance to communicate with.
     */
    public TypeScriptInputReader(ExternalProcess externalProcess) {
        super(externalProcess);
    }

    @Override
    public boolean isValidInput(Object input) {

        if (input instanceof Path) {
            return true;
        } else if (input instanceof File) {
            return true;
        } else if (input instanceof Map) {
            try {
                // Input corresponds to the parsed file
                Map<String, Object> mapModel = createModel(input);
                mapModel = (Map<String, Object>) mapModel.get("model");
                if (Paths.get(mapModel.get("path").toString()) == null) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                LOG.error("An exception occured while parsing the input", e);
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public Object read(Path path, Charset inputCharset, Object... additionalArguments) throws InputReaderException {
        String json = (String) super.read(path, inputCharset, additionalArguments);

        Map<String, Object> pojoModel = new HashMap<>();
        try {

            ObjectMapper mapper = new ObjectMapper();
            // convert JSON string to Map
            pojoModel.put("model", mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            }));

            return pojoModel;
        } catch (JsonGenerationException e) {
            throw new CobiGenRuntimeException("Exception during JSON writing. This is most probably a bug", e);
        } catch (JsonMappingException e) {
            throw new CobiGenRuntimeException(
                "Exception during JSON mapping. This error occured while converting the templates model from JSON string to map",
                e);
        } catch (IOException e) {
            throw new CobiGenRuntimeException(
                "IO exception while converting the templates model from JSON string to map", e);
        }
    }

    @Override
    public Map<String, Object> createModel(Object input) {
        return (Map<String, Object>) input; // nothing to do, did all at read
    }

    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        return getInputObjects(input, inputCharset, false);
    }

    @Override
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {
        return getInputObjects(input, inputCharset, true);
    }

    /**
     * Returns the first class or interface object.
     * @param input
     *            container input
     * @param inputCharset
     *            {@link Charset} to be used to read the children
     * @param recursively
     *            states, whether the children should be retrieved recursively
     * @return a list containing the first class or interface.
     */
    public List<Object> getInputObjects(Object input, Charset inputCharset, boolean recursively) {

        List<Object> tsInputObjects = new LinkedList<>();

        if (isValidInput(input)) {

            Map<String, Object> inputModel =
                (Map<String, Object>) read(new File(input.toString()).toPath(), inputCharset);
            Map<String, Object> mapModel = (Map<String, Object>) inputModel.get("model");

            if (mapModel.containsKey("classes")) {
                List<Object> classes = castToList(mapModel, "classes");
                for (Object classModel : classes) {
                    tsInputObjects.add(castToHashMap(classModel));
                }
            }

            if (mapModel.containsKey("interfaces")) {
                List<Object> interfaces = castToList(mapModel, "interfaces");
                for (Object interfaceModel : interfaces) {
                    tsInputObjects.add(castToHashMap(interfaceModel));
                }
            }
            return tsInputObjects;
        }

        LOG.error("The given input does neither contain classes nor interfaces");
        return tsInputObjects;
    }

    @Override
    public boolean isMostLikelyReadable(Path path) {

        List<String> validExtensions = Arrays.asList("ts", "js", "nest");
        String fileExtension = FilenameUtils.getExtension(path.toString()).toLowerCase();
        return validExtensions.contains(fileExtension);
    }

    /**
     * Cast to list an object
     * @param mapModel
     *            map where our object to cast is stored
     * @param key
     *            cast object with this key
     * @return our object casted to an array list
     */
    private ArrayList<Object> castToList(Map<String, Object> mapModel, String key) {
        return (ArrayList<Object>) mapModel.get(key);
    }

    /**
     * Cast to linked hash map an object
     * @param o
     *            object to cast to linked hash map
     * @return linked hash map
     */
    private LinkedHashMap<String, Object> castToHashMap(Object o) {
        return (LinkedHashMap<String, Object>) o;
    }

}