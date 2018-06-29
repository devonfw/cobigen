package com.devonfw.cobigen.openapiplugin.inputreader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.openapiplugin.model.ComponentDef;
import com.devonfw.cobigen.openapiplugin.model.EntityDef;
import com.devonfw.cobigen.openapiplugin.model.HeaderDef;
import com.devonfw.cobigen.openapiplugin.model.InfoDef;
import com.devonfw.cobigen.openapiplugin.model.OpenAPIFile;
import com.devonfw.cobigen.openapiplugin.model.OperationDef;
import com.devonfw.cobigen.openapiplugin.model.ParameterDef;
import com.devonfw.cobigen.openapiplugin.model.PathDef;
import com.devonfw.cobigen.openapiplugin.model.PropertyDef;
import com.devonfw.cobigen.openapiplugin.model.ResponseDef;
import com.devonfw.cobigen.openapiplugin.model.ServerDef;
import com.devonfw.cobigen.openapiplugin.util.constants.Constants;
import com.jayway.jsonpath.Configuration;
import com.reprezen.jsonoverlay.JsonOverlay;
import com.reprezen.jsonoverlay.Overlay;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.OpenApi3Parser;
import com.reprezen.kaizen.oasparser.model3.Info;
import com.reprezen.kaizen.oasparser.model3.MediaType;
import com.reprezen.kaizen.oasparser.model3.OpenApi3;
import com.reprezen.kaizen.oasparser.model3.Parameter;
import com.reprezen.kaizen.oasparser.model3.Path;
import com.reprezen.kaizen.oasparser.model3.RequestBody;
import com.reprezen.kaizen.oasparser.model3.Response;
import com.reprezen.kaizen.oasparser.model3.Schema;
import com.reprezen.kaizen.oasparser.model3.Server;

/**
 * Extension for the {@link InputReader} Interface of the CobiGen, to be able to read OpenApi3 definition
 * files into FreeMarker models
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
     * Get constraints configurations for {@link PropertyDef} and {@link ParameterDef}
     * @param schema
     *            schema to extract constraints from. Might be component property schema or parameter schema
     * @return map of different constraints
     */
    private Map<String, Object> extractConstraints(Schema schema) {
        Map<String, Object> constraints = new HashMap<>();
        if (schema.getMaximum() != null) {
            constraints.put(ModelConstant.MAXIMUM, schema.getMaximum());
        }
        if (schema.getMinimum() != null) {
            constraints.put(ModelConstant.MINIMUM, schema.getMinimum());
        }
        if (schema.getType().equals(Constants.ARRAY)) {
            if (schema.getMaxItems() != null) {
                constraints.put(ModelConstant.MAX_LENGTH, schema.getMaxItems());
            }
            if (schema.getMinItems() != null) {
                constraints.put(ModelConstant.MIN_LENGTH, schema.getMinItems());
            }
        } else if (schema.getType().equals("string")) {
            if (schema.getMaxLength() != null) {
                constraints.put(ModelConstant.MAX_LENGTH, schema.getMaxLength());
            }
            if (schema.getMinLength() != null) {
                constraints.put(ModelConstant.MIN_LENGTH, schema.getMinLength());
            }
        }
        if (schema.isUniqueItems()) {
            constraints.put(ModelConstant.UNIQUE, schema.isUniqueItems());
        } else {
            constraints.put(ModelConstant.UNIQUE, false);
        }
        return constraints;
    }

    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        List<Object> inputs = new LinkedList<>();
        if (input instanceof OpenAPIFile) {
            inputs.addAll(extractComponents(((OpenAPIFile) input).getAST()));
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
     * Get a list of entities defined at an OpenaApi3 file returning a list of {@link EntityDef}'s
     *
     * @param openApi
     *            the model for an OpenApi3 file
     * @return list of entities
     */
    private List<EntityDef> extractComponents(OpenApi3 openApi) {
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(Overlay.toJson(openApi).toString());
        HeaderDef header = new HeaderDef();
        header.setServers(extractServers(openApi));
        header.setInfo(extractInfo(openApi));
        List<EntityDef> objects = new LinkedList<>();
        for (String key : openApi.getSchemas().keySet()) {
            EntityDef entityDef = new EntityDef();
            entityDef.setName(key);
            entityDef.setDescription(openApi.getSchema(key).getDescription());
            ComponentDef componentDef = new ComponentDef();
            entityDef.setProperties(extractProperties(openApi, document, key));

            // If no x-component tag was found on the input file, throw invalid configuration
            if (openApi.getSchema(key).getExtensions().get(Constants.COMPONENT_EXT) == null) {
                throw new InvalidConfigurationException(
                    "Your Swagger file is not correctly formatted, it lacks of x-component tags.\n\n"
                        + "Go to the documentation "
                        + "(https://github.com/devonfw/tools-cobigen/wiki/cobigen-openapiplugin#full-example) "
                        + "to check how to correctly format it."
                        + " If it is still not working, check your file indentation!");
            }
            entityDef.setComponentName(openApi.getSchema(key).getExtensions().get(Constants.COMPONENT_EXT).toString());

            // If the path's tag was not found on the input file, throw invalid configuration
            if (openApi.getPaths().size() == 0) {
                throw new InvalidConfigurationException(
                    "Your Swagger file is not correctly formatted, it lacks of the correct path syntax.\n\n"
                        + "Go to the documentation (https://github.com/devonfw/tools-cobigen"
                        + "/wiki/cobigen-openapiplugin#paths) to check how to correctly format it."
                        + " If it is still not working, check your file indentation!");
            }

            // Sets a Map containing all the extensions of the info part of the OpenAPI file
            if (Overlay.isPresent((JsonOverlay<?>) openApi.getInfo())) {
                entityDef.setUserPropertiesMap(openApi.getInfo().getExtensions());
            }
            // Traverse the extensions of the entity for setting those attributes to the Map
            Iterator<String> it = openApi.getSchema(key).getExtensions().keySet().iterator();
            while (it.hasNext()) {
                String keyMap = it.next();
                entityDef.setUserProperty(keyMap, openApi.getSchema(key).getExtensions().get(keyMap).toString());
            }
            componentDef.setPaths(extractPaths(openApi.getPaths(),
                openApi.getSchema(key).getExtensions().get(Constants.COMPONENT_EXT).toString()));
            entityDef.setComponent(componentDef);
            entityDef.setHeader(header);
            objects.add(entityDef);
        }
        return objects;
    }

    /**
     * @param openApi
     *            document root
     * @return an object of {@link InfoDef}
     */
    private InfoDef extractInfo(OpenApi3 openApi) {
        InfoDef info = new InfoDef();
        Info inf = openApi.getInfo();
        info.setDescription(inf.getDescription());
        info.setTitle(inf.getTitle());
        return info;
    }

    /**
     * @param openApi
     *            document root
     * @return list of {@link ServerDef}'s
     */
    private List<ServerDef> extractServers(OpenApi3 openApi) {
        List<ServerDef> servers = new LinkedList<>();
        ServerDef serv;
        for (Server server : openApi.getServers()) {
            serv = new ServerDef();
            serv.setDescription(server.getDescription());
            serv.setURI(server.getUrl());
            servers.add(serv);
        }
        return servers;
    }

    /**
     * @param openApi
     *            document root
     * @param componentSchema
     *            component schema respectively object or entity
     * @param property
     *            property name to schema mapping
     * @param targetComponent
     *            name of the target component
     * @return model of a property
     */
    private PropertyDef extractReferenceProperty(OpenApi3 openApi, Schema componentSchema,
        Entry<String, ? extends Schema> property, String targetComponent) {
        Schema propertySchema = property.getValue();
        if (Overlay.getJsonReference(propertySchema).sameFile(Overlay.getJsonReference(openApi))) {
            String refTypeName = componentSchema.getName();
            if (propertySchema.getType().equals(Constants.ARRAY)) {
                refTypeName = propertySchema.getItemsSchema().getName();
            } else {
                refTypeName = propertySchema.getName();
            }
            String thisComponent = componentSchema.getExtension(Constants.COMPONENT_EXT).toString();

            boolean sameComponent;
            if (thisComponent.equals(targetComponent)) {
                sameComponent = true;
            } else {
                sameComponent = false;
            }
            PropertyDef propertyDef = new PropertyDef();
            propertyDef.setIsEntity(true);
            propertyDef.setRequired(componentSchema.getRequiredFields().contains(property.getKey()));
            propertyDef.setSameComponent(sameComponent);
            propertyDef.setType(refTypeName);
            // TODO unidirectional?
            return propertyDef;
        } else {
            throw new NotYetSupportedException(
                "References across files are not yet supported. Please create an issue on GitHub if you are interested in this feature.");
        }
    }

    /**
     * Get the fields of an entity returning a list of {@link PropertyDef}'s
     *
     * @param openApi
     *            the OpenApi3 model
     * @param jsonDocument
     *            parsed JSON document
     * @param componentName
     *            entity name
     * @return List of {@link PropertyDef}'s
     */
    private List<PropertyDef> extractProperties(OpenApi3 openApi, Object jsonDocument, String componentName) {
        Schema componentSchema = openApi.getSchema(componentName);
        Map<String, ? extends Schema> properties = componentSchema.getProperties();
        List<PropertyDef> objects = new LinkedList<>();
        for (Entry<String, ? extends Schema> prop : properties.entrySet()) {
            String propertyName = prop.getKey();
            Schema propertySchema = prop.getValue();
            PropertyDef propModel;
            if (propertySchema.getType().equals(Constants.ARRAY)) {
                if (propertySchema.getItemsSchema().getType().equals(Constants.OBJECT)
                    && propertySchema.getItemsSchema() != null
                    && propertySchema.getItemsSchema().getExtension(Constants.COMPONENT_EXT) != null) {
                    String targetComponent =
                        propertySchema.getItemsSchema().getExtension(Constants.COMPONENT_EXT).toString();
                    propModel = extractReferenceProperty(openApi, componentSchema, prop, targetComponent);
                } else {
                    propModel = new PropertyDef();
                    propModel.setType(propertySchema.getItemsSchema().getType());
                }

                if (propertySchema.getItemsSchema().getFormat() != null) {
                    propModel.setFormat(propertySchema.getItemsSchema().getFormat());
                }
                propModel.setIsCollection(true);
            } else {
                if (propertySchema.getType().equals(Constants.OBJECT)
                    && propertySchema.getExtension(Constants.COMPONENT_EXT) != null) {
                    String targetComponent = propertySchema.getExtension(Constants.COMPONENT_EXT).toString();
                    propModel = extractReferenceProperty(openApi, componentSchema, prop, targetComponent);
                } else {
                    propModel = new PropertyDef();
                    propModel.setType(propertySchema.getType());
                }

                if (propertySchema.getFormat() != null) {
                    propModel.setFormat(propertySchema.getFormat());
                }
            }
            propModel.setName(propertyName);
            propModel.setDescription(propertySchema.getDescription());

            Map<String, Object> constraints = extractConstraints(propertySchema);
            if (componentSchema.getRequiredFields().contains(propertyName)) {
                constraints.put(ModelConstant.NOTNULL, true);
            } else {
                constraints.put(ModelConstant.NOTNULL, false);
            }
            propModel.setConstraints(constraints);
            objects.add(propModel);
        }

        return objects;
    }

    /**
     * Get a list of {@link PathDef} from a list of OpenApi paths definitions
     *
     * @param paths
     *            the list of OpenApi paths definitions
     * @param componentName
     *            the component where the paths belong to
     * @return list of {@link PathDef}'s
     */
    private List<PathDef> extractPaths(Map<String, ? extends Path> paths, String componentName) {
        List<PathDef> pathDefs = new LinkedList<>();
        for (String pathKey : paths.keySet()) {
            if (pathKey.toLowerCase().contains(componentName.toLowerCase())) {
                String[] mp = pathKey.split("/");
                String pathUri = "/";
                for (int i = 3; i < mp.length; i++) {
                    pathUri = pathUri.concat(mp[i] + "/");
                }
                PathDef path = new PathDef(pathUri, mp[2]);
                if (pathKey.toLowerCase().contains(mp[1].toLowerCase())) {
                    for (String opKey : paths.get(pathKey).getOperations().keySet()) {
                        OperationDef operation = new OperationDef(opKey);
                        operation.setDescription(paths.get(pathKey).getOperation(opKey).getDescription());
                        operation.setSummary(paths.get(pathKey).getOperation(opKey).getSummary());
                        operation.setOperationId((paths.get(pathKey).getOperation(opKey).getOperationId()));
                        operation.setResponses(extractResponses(paths.get(pathKey).getOperation(opKey).getResponses(),
                            paths.get(pathKey).getOperation(opKey).getTags()));
                        operation.setTags(paths.get(pathKey).getOperation(opKey).getTags());
                        if (path.getOperations() == null) {
                            path.setOperations(new ArrayList<OperationDef>());
                        }
                        operation.getParameters()
                            .addAll(extractParameters(paths.get(pathKey).getOperation(opKey).getParameters(),
                                paths.get(pathKey).getOperation(opKey).getTags(),
                                paths.get(pathKey).getOperation(opKey).getRequestBody()));
                        path.getOperations().add(operation);
                    }
                }
                pathDefs.add(path);
            }
        }

        return pathDefs;
    }

    /**
     * Return a list of {@link ParameterDef} from a collection of parameters of an operation
     *
     * @param parameters
     *            list of OpenApi parameter definition
     * @param tags
     *            list of tags
     * @param requestBody
     *            in case of body parameter
     * @return List of {@link ParameterDef}'s
     */
    private List<ParameterDef> extractParameters(Collection<? extends Parameter> parameters, Collection<String> tags,
        RequestBody requestBody) {
        List<ParameterDef> parametersList = new LinkedList<>();
        ParameterDef parameter;
        for (Parameter param : parameters) {
            parameter = new ParameterDef();
            switch (param.getIn()) {
            case "path":
                parameter.setInPath(true);
                break;
            case "query":
                parameter.setInQuery(true);
                break;
            case "header":
                parameter.setInHeader(true);
                break;
            }

            parameter.setName(param.getName());
            Schema schema = param.getSchema();
            Map<String, Object> constraints = extractConstraints(schema);
            if (param.isRequired()) {
                constraints.put(ModelConstant.NOTNULL, true);
            } else {
                constraints.put(ModelConstant.NOTNULL, false);
            }
            parameter.setConstraints(constraints);
            parameter.setDescription(param.getDescription());
            if (schema.getType().equals(Constants.ARRAY)) {
                parameter.setIsCollection(true);
                if (schema.getItemsSchema() != null) {
                    parameter.setIsEntity(true);
                }
            }
            try {
                if (Overlay.isReference(param, Constants.SCHEMA)) {
                    parameter.setIsEntity(true);
                    parameter.setType(schema.getName());
                } else {
                    parameter.setType(schema.getType());
                    parameter.setFormat(schema.getFormat());
                }
            } catch (NullPointerException e) {
                throw new CobiGenRuntimeException("Error at parameter " + param.getName()
                    + ". Invalid OpenAPI file, path parameters need to have a schema defined.");
            }
            parametersList.add(parameter);
        }

        if (requestBody != null) {
            Schema mediaSchema;
            for (String media : requestBody.getContentMediaTypes().keySet()) {
                parameter = new ParameterDef();
                parameter.setMediaType(media);
                if (tags.contains(Constants.SEARCH_CRITERIA)
                    || tags.contains(Constants.SEARCH_CRITERIA.toLowerCase())) {
                    parameter.setIsSearchCriteria(true);
                    parameter.setName("criteria");
                }
                if (requestBody.getContentMediaTypes().get(media).getSchema() != null) {
                    mediaSchema = requestBody.getContentMediaTypes().get(media).getSchema();
                    parameter.setIsEntity(true);
                    parameter.setType(mediaSchema.getName());
                    if (!parameter.getIsSearchCriteria()) {
                        char c[] = mediaSchema.getName().toCharArray();
                        c[0] = Character.toLowerCase(c[0]);
                        parameter.setName(new String(c));
                    }
                }
                if (parameter.getType() != null) {
                    Map<String, Object> constraints = new HashMap<>();
                    constraints.put(ModelConstant.NOTNULL, true);
                    parameter.setConstraints(constraints);
                    parametersList.add(parameter);
                }
            }
        }
        return parametersList;
    }

    /**
     * Returns a {@link ResponseDef} from a operation '200' response definition depending on the tags
     *
     * @param responses
     *            list of OpenApu responses definition
     * @param tags
     *            list of oasp4j relative tags
     * @return List of {@link ResponseDef}'s
     */
    private List<ResponseDef> extractResponses(Map<String, ? extends Response> responses, Collection<String> tags) {
        ResponseDef response;
        List<String> mediaTypes = new LinkedList<>();
        List<ResponseDef> resps = new LinkedList<>();
        for (String resp : responses.keySet()) {
            response = new ResponseDef();
            response.setCode(resp);
            Map<String, MediaType> contentMediaTypes = responses.get(resp).getContentMediaTypes();
            response.setDescription(responses.get(resp).getDescription());
            if (contentMediaTypes != null) {
                if (contentMediaTypes.isEmpty()) {
                    response.setIsVoid(true);
                }
                for (String media : contentMediaTypes.keySet()) {
                    mediaTypes.add(media);
                    Reference schemaReference = Overlay.getReference(contentMediaTypes.get(media), Constants.SCHEMA);
                    Schema schema = contentMediaTypes.get(media).getSchema();
                    if (schema != null) {
                        if (schemaReference != null) {
                            response.setType(schema.getName());
                            response.setIsEntity(true);
                            EntityDef eDef = new EntityDef();
                            List<PropertyDef> propDefs = new LinkedList<>();
                            for (Schema propertySchema : schema.getProperties().values()) {
                                PropertyDef prop = new PropertyDef();
                                prop.setDescription(propertySchema.getDescription());
                                prop.setFormat(propertySchema.getFormat());
                                prop.setName(propertySchema.getName());
                                prop.setType(propertySchema.getType());
                                propDefs.add(prop);
                            }
                            eDef.setName(schema.getName());
                            eDef.setProperties(propDefs);
                            response.setEntityRef(eDef);
                        } else if (schema.getType().equals(Constants.ARRAY)) {
                            if (schema.getItemsSchema() != null) {
                                response.setType(schema.getItemsSchema().getName());
                                response.setIsEntity(true);
                            } else {
                                response.setType(schema.getItemsSchema().getType());
                            }
                            if (tags.contains(Constants.PAGINATED)) {
                                response.setIsPaginated(true);
                            } else {
                                response.setIsArray(true);
                            }

                        } else if (schema.getType() != null) {
                            response.setType(schema.getType());
                        } else {
                            response.setIsVoid(true);
                        }
                    } else {
                        String refString = schemaReference.getRefString();
                        throw new InvalidConfigurationException(
                            "Referenced entity " + refString.substring(refString.lastIndexOf('/'))
                                + " not found. The reference " + refString + " schould be fixed before generation.");
                    }
                }
            } else {
                response.setIsVoid(true);
            }
            response.setMediaTypes(mediaTypes);
            resps.add(response);
        }
        return resps;

    }

    @Override
    public Object read(java.nio.file.Path path, Charset inputCharset, Object... additionalArguments)
        throws InputReaderException {
        if (!Files.isRegularFile(path)) {
            throw new InputReaderException("Path " + path.toAbsolutePath().toUri().toString() + " is not a file!");
        }
        OpenApi3 openApi = new OpenApi3Parser().parse(path.toUri());
        if (openApi == null) {
            throw new InputReaderException(path + " is not a valid OpenAPI file");
        }
        return new OpenAPIFile(path, openApi);
    }

}
