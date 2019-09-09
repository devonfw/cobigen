package com.devonfw.cobigen.tsplugin.inputreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.to.InputFileTo;
import com.devonfw.cobigen.impl.exceptions.ConnectionExceptionHandler;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;
import com.devonfw.cobigen.tsplugin.merger.constants.Constants;

/**
 * TypeScript input reader that uses a server to read TypeScript code
 */
public class TypeScriptInputReader implements InputReader {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TypeScriptInputReader.class);

    /**
     * Instance that handles all the operations performed to the external server, like initializing the
     * connection and sending new requests
     */
    private ExternalProcessHandler request = ExternalProcessHandler
        .getExternalProcessHandler(ExternalProcessConstants.HOST_NAME, ExternalProcessConstants.PORT);

    /**
     * Exception handler related to connectivity to the server
     */
    private ConnectionExceptionHandler connectionExc = new ConnectionExceptionHandler();

    /**
     * Creates a new {@link TypeScriptInputReader}
     */
    public TypeScriptInputReader() {

        try {
            // We first check if the server is already running
            request.startConnection();
            if (request.isNotConnected()) {
                startServerConnection();
            }
        } catch (IOException e) {
            // If it is not currently running, we need to execute it
            LOG.info("Server is not currently running. Let's initialize it");
            startServerConnection();
        }

    }

    /**
     * Deploys the server and tries to initialize a new connection between CobiGen and the server
     */
    private void startServerConnection() {
        request.executingExe(Constants.EXE_NAME, this.getClass());
        request.initializeConnection();
    }

    @Override
    public boolean isValidInput(Object input) {
        String fileContents = null;
        String inputCharset = "UTF-8";

        Path path;

        if (input instanceof Path) {
            path = (Path) input;
        } else if (input instanceof File) {
            path = ((File) input).toPath();
        }

        else {
            try {
                // Input corresponds to the parsed file
                Map<String, Object> mapModel = createModel(input);
                mapModel = (Map<String, Object>) mapModel.get("model");
                path = Paths.get(mapModel.get("path").toString());
                return isValidInput(path);
            } catch (NullPointerException e) {
                return false;
            }

        }

        if (request.isNotConnected()) {
            startServerConnection();
        }

        // File content is not needed, as only the file extension is checked
        fileContents = new String("");

        String fileName = path.toString();
        InputFileTo inputFile = new InputFileTo(fileName, fileContents, inputCharset);

        HttpURLConnection conn = request.getConnection("POST", "Content-Type", "application/json", "isValidInput");

        if (request.sendRequest(inputFile, conn, "UTF-8")) {

            String response = new String();
            try (InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(isr);) {

                LOG.info("Receiving response from Server....");
                response = br.readLine();

                return Boolean.parseBoolean(response);
            } catch (NullPointerException e) {
                return false;

            } catch (IOException e) {
                connectionExc.handle(e);
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> createModel(Object input) {
        Map<String, Object> pojoModel = new HashMap<>();

        try {

            ObjectMapper mapper = new ObjectMapper();
            String json = input.toString();

            // convert JSON string to Map
            pojoModel.put("model", mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            }));

            return pojoModel;

        } catch (JsonGenerationException e) {
            LOG.error("Exception during JSON writing. This is most probably a bug", e);
        } catch (JsonMappingException e) {
            LOG.error(
                "Exception during JSON mapping. This error occured while converting the templates model from JSON string to map",
                e);
        } catch (IOException e) {
            LOG.error("IO exception while converting the templates model from JSON string to map", e);
        }

        return null;

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

        LOG.debug("Retrieve input object for input {} {}", input, recursively ? "recursively" : "");
        List<Object> tsInputObjects = new LinkedList<>();

        LOG.debug("DEBUG getInputObjects: " + input.toString());

        try {
            if (isValidInput(input)) {
                String inputModel = (String) read(new File(input.toString()).toPath(), inputCharset);
                Map<String, Object> mapModel = (Map<String, Object>) createModel(inputModel).get("model");

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

        } finally {
            request.terminateProcessConnection();
        }

        LOG.error("The given input does neither contain classes nor interfaces");
        return tsInputObjects;
    }

    @Override
    public Map<String, Object> getTemplateMethods(Object input) {
        Map<String, Object> methodMap = new HashMap<>();
        return methodMap;
    }

    @Override
    public Object read(Path path, Charset inputCharset, Object... additionalArguments) throws InputReaderException {

        if (request.isNotConnected()) {
            startServerConnection();
        }

        String fileContents;
        String fileName = path.toString();
        try {

            fileContents = String.join("", Files.readAllLines(path, inputCharset));
        } catch (IOException e) {
            throw new InputReaderException("Could not read input file!" + fileName, e);
        }

        InputFileTo inputFile = new InputFileTo(fileName, fileContents, inputCharset.name());

        HttpURLConnection conn =
            request.getConnection("POST", "Content-Type", "application/json", "tsplugin/getInputModel");

        if (request.sendRequest(inputFile, conn, "UTF-8")) {

            StringBuffer inputModel = new StringBuffer();

            try (InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(isr);) {

                LOG.info("Receiving output from Server....");
                Stream<String> s = br.lines();
                s.parallel().forEachOrdered((String line) -> {
                    inputModel.append(line);
                });
                return inputModel.toString();

            } catch (Exception e) {

                connectionExc.handle(e);
            }
        }

        return null;
    }

    @Override
    public boolean isMostLikelyReadable(Path path) {
        return isValidInput(path);
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