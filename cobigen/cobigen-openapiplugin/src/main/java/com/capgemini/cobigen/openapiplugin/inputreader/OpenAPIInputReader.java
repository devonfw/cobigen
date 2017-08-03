package com.capgemini.cobigen.openapiplugin.inputreader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.exception.InputReaderException;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.openapiplugin.model.ComponentDef;
import com.capgemini.cobigen.openapiplugin.model.OpenAPIFile;
import com.capgemini.cobigen.openapiplugin.model.PathDef;
import com.capgemini.cobigen.openapiplugin.utils.constants.Constants;

import io.swagger.models.HttpMethod;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BaseIntegerProperty;
import io.swagger.models.properties.BinaryProperty;
import io.swagger.models.properties.ByteArrayProperty;
import io.swagger.models.properties.DecimalProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.EmailProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.PasswordProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import io.swagger.parser.Swagger20Parser;

/**
 * Extension for the {@link InputReader} Interface of the CobiGen, to be able to read Swagger definition files
 * into FreeMarker models
 */
public class OpenAPIInputReader implements InputReader {

    @Override
    public boolean isValidInput(Object input) {
        if (input != null && input.getClass().getPackage() != null
            && input.getClass().getPackage().toString().equals(OpenAPIFile.class.getPackage().toString())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Object> createModel(Object input) {
        Map<String, Object> pojoModel = new HashMap<>();
        pojoModel.put("model", input);

        return pojoModel;
    }

    /**
     * @param paths
     * @return
     */
    private List<Map<String, Object>> getPaths(List<PathDef> pathsDefs) {
        List<Map<String, Object>> paths = new LinkedList<>();
        Map<String, Object> pathValues;
        for (PathDef pathDef : pathsDefs) {
            pathValues = new HashMap<>();
            pathValues.put(ModelConstant.PATH_URL, pathDef.getPathURI());
            pathValues.put(ModelConstant.OPERATIONS, getOperations(pathDef.getPath().getOperationMap()));
            paths.add(pathValues);
        }
        return paths;
    }

    /**
     * @param operationMap
     * @return
     */
    private List<Map<String, Object>> getOperations(Map<HttpMethod, Operation> operationMap) {
        List<Map<String, Object>> operations = new LinkedList<>();
        Map<String, Object> opValues;
        for (HttpMethod op : operationMap.keySet()) {
            opValues = new HashMap<>();
            opValues.put(ModelConstant.HTTP_OPERATION, op.name());
            if (operationMap.get(op).getSummary() != null) {
                opValues.put(ModelConstant.SUMMARY, operationMap.get(op).getSummary());
            }
            if (operationMap.get(op).getDescription() != null) {
                opValues.put(ModelConstant.DESCRIPTION, operationMap.get(op).getDescription());
            }
            if (operationMap.get(op).getProduces() != null) {
                opValues.put(ModelConstant.PRODUCES, operationMap.get(op).getProduces());
            }
            if (operationMap.get(op).getParameters() != null) {
                opValues.put(ModelConstant.PARAMETERS,
                    getParameters(operationMap.get(op).getParameters(), operationMap.get(op).getConsumes()));
            }
            if (operationMap.get(op).getConsumes() != null) {

                opValues.put(ModelConstant.CONSUMES, operationMap.get(op).getConsumes());
            }

            if (operationMap.get(op).getResponses() != null) {
                opValues.put(ModelConstant.RESPONSES, getResponses(operationMap.get(op).getResponses()));
            } else {
                opValues.put(ModelConstant.RESPONSES, new LinkedList<>());
            }
            operations.add(opValues);
        }
        return operations;
    }

    /**
     * @param responses
     * @return
     */
    private Map<String, Object> getResponses(Map<String, Response> responses) {
        Map<String, Object> response = new HashMap<>();

        for (String resp : responses.keySet()) {
            if (resp.equals("200")) {
                if (responses.get(resp).getDescription() != null) {
                    response.put(ModelConstant.DESCRIPTION, responses.get(resp).getDescription());
                }
                if (responses.get(resp).getSchema() != null) {
                    if (responses.get(resp).getSchema() instanceof ArrayProperty) {
                        response.put(ModelConstant.IS_COLLECTION, true);
                        response.put(ModelConstant.TYPE,
                            ((RefProperty) ((ArrayProperty) responses.get(resp).getSchema()).getItems())
                                .getSimpleRef());
                    } else if (responses.get(resp).getSchema() instanceof RefProperty) {
                        response.put(ModelConstant.TYPE,
                            ((RefProperty) responses.get(resp).getSchema()).getSimpleRef());
                    }
                }
            }
        }
        return response;
    }

    /**
     * @param parameters
     * @param list
     * @param consumes
     * @return
     */
    private List<Map<String, Object>> getParameters(List<Parameter> parameters, List<String> consumes) {
        List<Map<String, Object>> params = new LinkedList<>();
        Map<String, Object> paramValues;
        for (Parameter param : parameters) {
            paramValues = new HashMap<>();
            paramValues.put(ModelConstant.NAME, param.getName());
            if (param.getDescription() != null) {
                paramValues.put(ModelConstant.DESCRIPTION, param.getDescription());
            }
            paramValues.put(ModelConstant.CONSTRAINTS, getConstraints(param));
            if (param instanceof PathParameter) {
                paramValues.put(ModelConstant.TYPE,
                    buildType(((PathParameter) param).getType(), ((PathParameter) param).getFormat(), null, null));
            }
            if (param instanceof QueryParameter) {
                paramValues.put(ModelConstant.TYPE,
                    buildType(((QueryParameter) param).getType(), ((QueryParameter) param).getFormat(), null, null));
            }
            if (param instanceof BodyParameter) {
                if (((BodyParameter) param).getSchema() != null) {
                    paramValues.put(ModelConstant.TYPE,
                        ((RefModel) ((BodyParameter) param).getSchema()).getSimpleRef());
                }
            }
            if (param instanceof FormParameter) {
                paramValues.put(ModelConstant.MULTIPART, true);
            }

            params.add(paramValues);

        }
        return params;
    }

    /**
     * @param param
     * @return
     */
    private Map<String, Object> getConstraints(Parameter param) {
        Map<String, Object> constraints = new HashMap<>();
        if (param.getRequired()) {
            constraints.put(ModelConstant.REQUIRED, true);
        }
        if (param instanceof PathParameter) {
            constraints.put(ModelConstant.MAXIMUM, ((PathParameter) param).getMaximum());
            constraints.put(ModelConstant.MINIMUM, ((PathParameter) param).getMaximum());
            constraints.put(ModelConstant.MAX_LENGTH, ((PathParameter) param).getMaxLength());
            constraints.put(ModelConstant.MIN_LENGTH, ((PathParameter) param).getMinLength());
        } else if (param instanceof QueryParameter) {
            constraints.put(ModelConstant.MAXIMUM, ((QueryParameter) param).getMaximum());
            constraints.put(ModelConstant.MINIMUM, ((QueryParameter) param).getMaximum());
            constraints.put(ModelConstant.MAX_LENGTH, ((QueryParameter) param).getMaxLength());
            constraints.put(ModelConstant.MIN_LENGTH, ((QueryParameter) param).getMinLength());
        } else {
            return constraints;
        }
        return constraints;
    }

    /**
     * @param entities
     * @return
     */
    private Object getDefinitions(List<ModelImpl> entities) {
        List<Map<String, Object>> definitions = new LinkedList<>();
        Map<String, Object> defValues;
        for (ModelImpl model : entities) {
            defValues = new HashMap<>();
            defValues.put(ModelConstant.NAME, model.getName());
            if (model.getDescription() != null) {
                defValues.put(ModelConstant.DESCRIPTION, model.getDescription());
            }
            if (model.getRequired() != null) {
                defValues.put(ModelConstant.FIELDS, getFields(model.getProperties(), model.getRequired()));
            } else {
                defValues.put(ModelConstant.FIELDS, getFields(model.getProperties(), new LinkedList<String>()));
            }
            definitions.add(defValues);
        }
        return definitions;
    }

    /**
     * @param properties
     * @param required
     * @return
     */
    private List<Map<String, Object>> getFields(Map<String, Property> properties, List<String> required) {

        List<Map<String, Object>> fields = new LinkedList<>();
        Map<String, Object> fieldValues;
        if (properties != null) {
            for (String key : properties.keySet()) {
                fieldValues = new HashMap<>();

                fieldValues.put(ModelConstant.NAME, key);
                if (properties.get(key) instanceof RefProperty) {
                    fieldValues.put(ModelConstant.TYPE, ((RefProperty) properties.get(key)).getSimpleRef());
                    fieldValues.put(ModelConstant.IS_REFERENCE, true);
                } else if (properties.get(key) instanceof ArrayProperty) {
                    ArrayProperty array = (ArrayProperty) properties.get(key);
                    if (array.getItems() instanceof RefProperty) {
                        fieldValues.put(ModelConstant.TYPE, ((RefProperty) array.getItems()).getSimpleRef());
                        fieldValues.put(ModelConstant.IS_COLLECTION, true);
                        fieldValues.put(ModelConstant.IS_REFERENCE, true);
                    }
                } else {
                    fieldValues.put(ModelConstant.TYPE, buildType(properties.get(key).getType(),
                        properties.get(key).getFormat(), properties.get(key), key));
                }
                fieldValues.put(ModelConstant.CONSTRAINTS, getConstraints(properties.get(key), required, key));
                if (properties.get(key).getDescription() != null) {
                    fieldValues.put(ModelConstant.DESCRIPTION, properties.get(key).getDescription());
                }
                fields.add(fieldValues);
            }
        }
        return fields;
    }

    /**
     * @param property
     * @param required
     * @param key
     * @return
     */
    private Map<String, Object> getConstraints(Property property, List<String> required, String key) {
        Map<String, Object> constraints = new HashMap<>();
        if (required.contains(key)) {
            constraints.put(ModelConstant.REQUIRED, true);
        }
        if (property instanceof IntegerProperty || property instanceof LongProperty) {
            BaseIntegerProperty prop = (BaseIntegerProperty) property;
            if (prop.getMaximum() != null) {
                constraints.put(ModelConstant.MAXIMUM, prop.getMaximum());
            }
            if (prop.getMinimum() != null) {
                constraints.put(ModelConstant.MINIMUM, prop.getMinimum());
            }
        } else if (property instanceof FloatProperty || property instanceof DoubleProperty) {
            DecimalProperty prop = (DecimalProperty) property;
            if (prop.getMaximum() != null) {
                constraints.put(ModelConstant.MAXIMUM, prop.getMaximum());
            }
            if (prop.getMinimum() != null) {
                constraints.put(ModelConstant.MINIMUM, prop.getMinimum());
            }
        } else if (property instanceof ArrayProperty) {

        } else if (property instanceof StringProperty || property instanceof EmailProperty
            || property instanceof ByteArrayProperty) {

            StringProperty str = (StringProperty) property;

            if (str.getMaxLength() != null) {
                constraints.put(ModelConstant.MAX_LENGTH, str.getMaxLength());
            }
            if (str.getMinLength() != null) {
                constraints.put(ModelConstant.MIN_LENGTH, str.getMinLength());
            }
        } else if (property instanceof PasswordProperty) {

            PasswordProperty pass = (PasswordProperty) property;
            if (pass.getMaxLength() != null) {
                constraints.put(ModelConstant.MAX_LENGTH, pass.getMaxLength());
            }
            if (pass.getMinLength() != null) {
                constraints.put(ModelConstant.MIN_LENGTH, pass.getMinLength());
            }
        } else if (property instanceof BinaryProperty) {
            BinaryProperty bin = (BinaryProperty) property;
            if (bin.getMaxLength() != null) {
                constraints.put(ModelConstant.MAX_LENGTH, bin.getMaxLength());
            }
            if (bin.getMinLength() != null) {
                constraints.put(ModelConstant.MIN_LENGTH, bin.getMinLength());
            }
        } else {
            return constraints;
        }
        return constraints;
    }

    /**
     * @param type
     * @param format
     * @param property
     * @param key
     * @return
     */
    private String buildType(String type, String format, Property property, String key) {
        if (type.equals("integer") && format.equals("int32")) {
            return Constants.INTEGER;
        } else if (type.equals("number") && format.equals("double")) {
            return Constants.DOUBLE;
        } else if (type.equals("integer") && format.equals("int64")) {
            return Constants.LONG;
        } else if (type.equals("boolean")) {
            return Constants.BOOLEAN;
        } else if (type.equals("string") && format == null) {
            return Constants.STRING;
        } else if (type.equals("object")) {
            return key;
        } else if (property instanceof RefProperty) {
            return ((RefProperty) property).getSimpleRef();
        } else if (property instanceof ByteArrayProperty) {
            return Constants.BYTE;
        } else if (type.equals("string") && format.equals("date")) {
            return Constants.DATE;
        } else if (type.equals("string") && format.equals("date-time")) {
            return Constants.TIMESTAMP;
        } else if (type.equals("string") && format.equals("binary")) {
            return Constants.FLOAT;
        } else if (type.equals("string") && format.equals("email")) {
            return Constants.STRING;
        } else if (type.equals("string") && format.equals("password")) {
            return Constants.STRING;
        } else {
            return Constants.STRING;
        }

    }

    @Override
    public boolean combinesMultipleInputObjects(Object input) {
        if (input instanceof OpenAPIFile) {
            return true;
        }
        return false;
    }

    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        List<Object> inputs = new LinkedList<>();
        if (input instanceof OpenAPIFile) {
            inputs.addAll(getComponents(((OpenAPIFile) input).getAST()));
        }
        return inputs;
    }

    @Override
    public Map<String, Object> getTemplateMethods(Object input) {
        return new HashMap<>();
    }

    @Override
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {
        return getInputObjects(input, inputCharset);
    }

    /**
     * @param input
     * @return
     */
    private List<ComponentDef> getComponents(Swagger input) {
        List<ComponentDef> objects = new LinkedList<>();
        List<String> added = new LinkedList<>();
        for (String key : input.getPaths().keySet()) {
            String[] mp = key.split("/");
            if (added.indexOf(mp[1]) < 0) {
                ComponentDef componentDef = new ComponentDef();
                componentDef.setComponent(mp[1]);
                componentDef.setVersion(mp[2]);
                componentDef.getPaths().addAll(getPaths(input.getPaths(), mp[1]));
                componentDef.getEntities().addAll(getObjectDefinitions(input, mp[1]));

                objects.add(componentDef);
                added.add(mp[1]);
            }
        }
        return objects;
    }

    /**
     * @param paths
     * @param component
     * @return
     */
    private List<PathDef> getPaths(Map<String, Path> paths, String component) {
        List<PathDef> pathDefs = new LinkedList<>();
        for (String pathKey : paths.keySet()) {
            if (pathKey.contains(component)) {
                String[] mp = pathKey.split("/");
                PathDef path = new PathDef();
                String pathUri = "/";
                for (int i = 3; i < mp.length; i++) {
                    pathUri = pathUri.concat(mp[i] + "/");
                }
                path.setPathURI(pathUri);
                if (pathKey.contains(mp[1])) {
                    path.setPath(paths.get(pathKey));
                }
                pathDefs.add(path);
            }
        }
        return pathDefs;
    }

    @Override
    public Object read(java.nio.file.Path path, Charset inputCharset, Object... additionalArguments)
        throws InputReaderException {
        if (!Files.isRegularFile(path)) {
            throw new InputReaderException("Path " + path.toAbsolutePath().toUri().toString() + " is not a file!");
        }

        try {
            String contents = new String(Files.readAllBytes(path), inputCharset);
            Swagger swagger = new Swagger20Parser().parse(contents);
            if (swagger == null) {
                throw new InputReaderException(path + " is not a valid OpenAPI file");
            }
            return new OpenAPIFile(path, swagger);
        } catch (IOException e) {
            throw new InputReaderException(
                "An error occurred on reading the file " + path.toAbsolutePath().toUri().toString());
        }
    }

    /**
     * @param input
     * @param map
     * @param keySet
     * @param component
     * @return
     */
    private List<ModelImpl> getObjectDefinitions(Swagger input, String component) {
        List<ModelImpl> objects = new LinkedList<>();
        List<String> added = new LinkedList<>();
        for (String key : input.getPaths().keySet()) {
            if (key.contains(component)) {
                for (Operation op : input.getPaths().get(key).getOperations()) {
                    if (op.getResponses() != null) {
                        for (String responseKey : op.getResponses().keySet()) {
                            if (op.getResponses().get(responseKey).getSchema() != null) {
                                if (op.getResponses().get(responseKey).getSchema() instanceof RefProperty
                                    && input.getDefinitions()
                                        .get(((RefProperty) op.getResponses().get(responseKey).getSchema())
                                            .getSimpleRef()) instanceof ModelImpl
                                    && !added.contains(((RefProperty) op.getResponses().get(responseKey).getSchema())
                                        .getSimpleRef())) {
                                    ModelImpl inputObject = (ModelImpl) input.getDefinitions().get(
                                        ((RefProperty) op.getResponses().get(responseKey).getSchema()).getSimpleRef());
                                    inputObject.setName(
                                        ((RefProperty) op.getResponses().get(responseKey).getSchema()).getSimpleRef());
                                    objects.add(inputObject);
                                    added.add(
                                        ((RefProperty) op.getResponses().get(responseKey).getSchema()).getSimpleRef());
                                }
                            }
                        }
                    }
                    if (op.getParameters() != null) {
                        for (Parameter param : op.getParameters()) {
                            if (param instanceof BodyParameter) {
                                if (((BodyParameter) param).getSchema() instanceof RefProperty
                                    && input.getDefinitions()
                                        .get(((RefProperty) ((BodyParameter) param).getSchema())
                                            .getSimpleRef()) instanceof ModelImpl
                                    && !added
                                        .contains(((RefProperty) ((BodyParameter) param).getSchema()).getSimpleRef())) {
                                    ModelImpl inputObject = (ModelImpl) input.getDefinitions()
                                        .get(((RefProperty) ((BodyParameter) param).getSchema()).getSimpleRef());
                                    inputObject
                                        .setName(((RefProperty) ((BodyParameter) param).getSchema()).getSimpleRef());
                                    objects.add(inputObject);
                                    added.add(((RefProperty) ((BodyParameter) param).getSchema()).getSimpleRef());
                                }
                            }
                        }
                    }
                }
            }
        }
        return objects;
    }

}
