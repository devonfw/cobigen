package com.capgemini.cobigen.openapiplugin.inputreader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.exception.InputReaderException;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.openapiplugin.model.ComponentDef;
import com.capgemini.cobigen.openapiplugin.model.EntityDef;
import com.capgemini.cobigen.openapiplugin.model.OpenAPIFile;
import com.capgemini.cobigen.openapiplugin.model.OperationDef;
import com.capgemini.cobigen.openapiplugin.model.ParameterDef;
import com.capgemini.cobigen.openapiplugin.model.PathDef;
import com.capgemini.cobigen.openapiplugin.model.PropertyDef;
import com.capgemini.cobigen.openapiplugin.model.RelationShip;
import com.capgemini.cobigen.openapiplugin.model.ResponseDef;
import com.capgemini.cobigen.openapiplugin.util.constants.Constants;
import com.reprezen.kaizen.oasparser.OpenApi3Parser;
import com.reprezen.kaizen.oasparser.model3.OpenApi3;
import com.reprezen.kaizen.oasparser.model3.Parameter;
import com.reprezen.kaizen.oasparser.model3.Path;
import com.reprezen.kaizen.oasparser.model3.RequestBody;
import com.reprezen.kaizen.oasparser.model3.Response;
import com.reprezen.kaizen.oasparser.model3.Schema;
import com.reprezen.kaizen.oasparser.ovl3.SchemaImpl;

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
     *
     * @param schema
     *            the schema with the constraint configuration
     * @return map of different constraints
     */
    private Map<String, Object> getConstraints(Schema schema) {
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
            inputs.addAll(getEntities(((OpenAPIFile) input).getAST()));
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
    private List<EntityDef> getEntities(OpenApi3 openApi) {
        List<EntityDef> objects = new LinkedList<>();
        for (String key : openApi.getSchemas().keySet()) {
            EntityDef entityDef = new EntityDef();
            entityDef.setName(key);
            entityDef.setDescription(openApi.getSchema(key).getDescription());
            ComponentDef componentDef = new ComponentDef();
            entityDef.setProperties(getFields(openApi.getSchema(key).getProperties(), openApi, key));
            entityDef.setComponentName(openApi.getSchema(key).getExtensions().get(Constants.COMPONENT_EXT).toString());
            componentDef.setPaths(getPaths(openApi.getPaths(),
                openApi.getSchema(key).getExtensions().get(Constants.COMPONENT_EXT).toString(), key));
            entityDef.setComponent(componentDef);
            entityDef.setRelationShips(getRealtionShips(openApi, key));
            objects.add(entityDef);
        }
        return objects;

    }

    /**
     * @param properties
     * @param openApi
     * @param key
     * @return
     */
    private List<RelationShip> getRealtionShips(OpenApi3 openApi, String key) {
        List<RelationShip> relationShips = new LinkedList<>();
        boolean sameComponent;
        for (String relationship : openApi.getSchema(key).getExtensions().keySet()) {
            sameComponent = false;
            if (relationship.equals(Constants.ONE_TO_ONE) || relationship.equals(Constants.MANY_TO_ONE)
                || relationship.equals(Constants.ONE_TO_MANY) || relationship.equals(Constants.MANY_TO_MANY)) {
                if (openApi.getSchema(key).getExtension(Constants.COMPONENT_EXT)
                    .equals(openApi.getSchema((String) openApi.getSchema(key).getExtension(relationship))
                        .getExtension(Constants.COMPONENT_EXT))) {
                    sameComponent = true;
                }
                if (relationship.equals(Constants.MANY_TO_ONE)) {
                    if (openApi.getSchema((String) openApi.getSchema(key).getExtension(relationship))
                        .getExtension(Constants.ONE_TO_MANY) != null
                        && openApi.getSchema((String) openApi.getSchema(key).getExtension(relationship))
                            .getExtension(Constants.ONE_TO_MANY).equals(key)) {
                        relationShips.add(new RelationShip(relationship.substring(2),
                            (String) openApi.getSchema(key).getExtension(relationship), sameComponent, false));
                    } else {
                        relationShips.add(new RelationShip(relationship.substring(2),
                            (String) openApi.getSchema(key).getExtension(relationship), sameComponent, true));
                    }
                } else if (relationship.equals(Constants.ONE_TO_MANY)) {
                    if (openApi.getSchema((String) openApi.getSchema(key).getExtension(relationship))
                        .getExtension(Constants.MANY_TO_ONE) != null
                        && openApi.getSchema((String) openApi.getSchema(key).getExtension(relationship))
                            .getExtension(Constants.MANY_TO_ONE).equals(key)) {
                        relationShips.add(new RelationShip(relationship.substring(2),
                            (String) openApi.getSchema(key).getExtension(relationship), sameComponent, false));
                    } else {
                        relationShips.add(new RelationShip(relationship.substring(2),
                            (String) openApi.getSchema(key).getExtension(relationship), sameComponent, true));
                    }
                } else if (relationship.equals(Constants.ONE_TO_ONE)) {
                    if (openApi.getSchema((String) openApi.getSchema(key).getExtension(relationship))
                        .getExtension(Constants.ONE_TO_ONE) != null
                        && openApi.getSchema((String) openApi.getSchema(key).getExtension(relationship))
                            .getExtension(Constants.ONE_TO_ONE).equals(key)) {
                        relationShips.add(new RelationShip(relationship.substring(2),
                            openApi.getSchema(key).getExtension(relationship).toString(), sameComponent, false));
                    } else {
                        relationShips.add(new RelationShip(relationship.substring(2),
                            openApi.getSchema(key).getExtension(relationship).toString(), sameComponent, true));
                    }
                } else {
                    if (openApi.getSchema((String) openApi.getSchema(key).getExtension(relationship))
                        .getExtension(Constants.MANY_TO_MANY) != null
                        && openApi.getSchema((String) openApi.getSchema(key).getExtension(relationship))
                            .getExtension(Constants.MANY_TO_MANY).equals(key)) {
                        relationShips.add(new RelationShip(relationship.substring(2),
                            openApi.getSchema(key).getExtension(relationship).toString(), sameComponent, false));
                    } else {
                        relationShips.add(new RelationShip(relationship.substring(2),
                            openApi.getSchema(key).getExtension(relationship).toString(), sameComponent, true));
                    }
                }
            }
        }
        return relationShips;
    }

    /**
     * Get the fields of an entity returning a list of {@link PropertyDef}'s
     *
     * @param properties
     *            map of properties of the OpenApi3 entity definition
     * @param openApi
     *            the OpenApi3 model
     * @param entity
     *            entity name
     * @return List of {@link PropertyDef}'s
     */
    private List<PropertyDef> getFields(Map<String, ? extends Schema> properties, OpenApi3 openApi, String entity) {
        List<PropertyDef> objects = new LinkedList<>();
        for (String key : properties.keySet()) {
            PropertyDef property = new PropertyDef();
            property.setName(key);
            property.setDescription(properties.get(key).getDescription());
            if (properties.get(key).getType().equals(Constants.ARRAY)) {
                property.setIsCollection(true);
                property.setType(((SchemaImpl) properties.get(key).getItemsSchema()).getType());
                if (((SchemaImpl) properties.get(key).getItemsSchema()).getFormat() != null) {
                    property.setFormat(((SchemaImpl) properties.get(key).getItemsSchema()).getFormat());
                }
            } else {
                property.setType(properties.get(key).getType());
                if (properties.get(key).getFormat() != null) {
                    property.setFormat(properties.get(key).getFormat());
                }
            }

            Map<String, Object> constraints = getConstraints(properties.get(key));
            if (openApi.getSchema(entity).getRequiredFields().contains(key)) {
                constraints.put(ModelConstant.NOTNULL, true);
            } else {
                constraints.put(ModelConstant.NOTNULL, false);
            }
            property.setConstraints(constraints);
            objects.add(property);
        }

        return objects;
    }

    /**
     * Get a list of {@link PathDef} from a list of OpenApi paths definitions
     *
     * @param paths
     *            the list of OpenApi paths definitions
     * @param component
     *            the component where the paths belong to
     * @param key
     *            the ley of the path to compare with the component
     * @return list of {@link PathDef}'s
     */
    private List<PathDef> getPaths(Map<String, ? extends Path> paths, String component, String key) {
        List<PathDef> pathDefs = new LinkedList<>();
        for (String pathKey : paths.keySet()) {
            if (pathKey.contains(component) && pathKey.contains("/" + key.toLowerCase() + "/")) {
                String[] mp = pathKey.split("/");
                String pathUri = "/";
                for (int i = 3; i < mp.length; i++) {
                    pathUri = pathUri.concat(mp[i] + "/");
                }
                PathDef path = new PathDef(pathUri, mp[2]);
                if (pathKey.contains(mp[1])) {
                    for (String opKey : paths.get(pathKey).getOperations().keySet()) {
                        OperationDef operation = new OperationDef(opKey);
                        operation.setDescription(paths.get(pathKey).getOperation(opKey).getDescription());
                        operation.setSummary(paths.get(pathKey).getOperation(opKey).getSummary());
                        operation.setOperationId((paths.get(pathKey).getOperation(opKey).getOperationId()));
                        operation.setResponse(getResponse(paths.get(pathKey).getOperation(opKey).getResponses(),
                            paths.get(pathKey).getOperation(opKey).getTags()));
                        operation.setTags(paths.get(pathKey).getOperation(opKey).getTags());
                        if (path.getOperations() == null) {
                            path.setOperations(new ArrayList<OperationDef>());
                        }
                        operation.getParameters()
                            .addAll(getParameters(paths.get(pathKey).getOperation(opKey).getParameters(),
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
    private List<ParameterDef> getParameters(Collection<? extends Parameter> parameters, Collection<String> tags,
        RequestBody requestBody) {
        List<ParameterDef> parametersList = new LinkedList<>();
        ParameterDef parameter = new ParameterDef();
        for (Parameter param : parameters) {

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
            Map<String, Object> constraints = getConstraints(param.getSchema());
            if (param.isRequired()) {
                constraints.put(ModelConstant.NOTNULL, true);
            } else {
                constraints.put(ModelConstant.NOTNULL, false);
            }
            parameter.setConstraints(constraints);
            parameter.setDescription(param.getDescription());
            if (param.getSchema().getType().equals(Constants.ARRAY)) {
                parameter.setIsCollection(true);
                if (((SchemaImpl) ((SchemaImpl) param.getSchema()).getItemsSchema()).getReference() != null) {
                    parameter.setIsEntity(true);
                    String[] mp = ((SchemaImpl) ((SchemaImpl) param.getSchema()).getItemsSchema()).getReference()
                        .getFragment().split("/");
                    parameter.setType(mp[mp.length - 1]);
                }
            }
            if (((SchemaImpl) param.getSchema()).getReference() != null) {
                String[] mp = ((SchemaImpl) param.getSchema()).getReference().getFragment().split("/");
                parameter.setIsEntity(true);
                parameter.setType(mp[mp.length - 1]);
            }
            parameter.setType(param.getSchema().getType());
            parameter.setFormat(param.getSchema().getFormat());
            parametersList.add(parameter);
        }
        if (requestBody != null) {
            for (String media : requestBody.getContentMediaTypes().keySet()) {
                parameter = new ParameterDef();
                parameter.setMediaType(media);
                if (tags.contains(Constants.SEARCH_CRITERIA)
                    || tags.contains(Constants.SEARCH_CRITERIA.toLowerCase())) {
                    parameter.setIsSearchCriteria(true);
                    parameter.setName("criteria");
                }
                if (((SchemaImpl) requestBody.getContentMediaTypes().get(media).getSchema()).getReference() != null) {
                    parameter.setIsEntity(true);
                    String[] mp = ((SchemaImpl) requestBody.getContentMediaTypes().get(media).getSchema())
                        .getReference().getFragment().split("/");
                    parameter.setType(mp[mp.length - 1]);
                    if (!parameter.getIsSearchCriteria()) {
                        char c[] = mp[mp.length - 1].toCharArray();
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
    private ResponseDef getResponse(Map<String, ? extends Response> responses, Collection<String> tags) {
        ResponseDef response = new ResponseDef();
        for (String resp : responses.keySet()) {
            if (resp.equals("200")) {
                if (responses.get(resp).getContentMediaTypes() != null) {
                    if (responses.get(resp).getContentMediaTypes().keySet().isEmpty()) {
                        response.setIsVoid(true);
                    }
                    for (String media : responses.get(resp).getContentMediaTypes().keySet()) {
                        response.setMediaType(media);
                        if (((SchemaImpl) responses.get(resp).getContentMediaTypes().get(media).getSchema())
                            .getReference() != null) {
                            String[] mp =
                                ((SchemaImpl) responses.get(resp).getContentMediaTypes().get(media).getSchema())
                                    .getReference().getFragment().split("/");
                            response.setType(mp[mp.length - 1]);
                            response.setIsEntity(true);
                        } else if (((SchemaImpl) responses.get(resp).getContentMediaTypes().get(media).getSchema())
                            .getType().equals(Constants.ARRAY)) {
                            if (((SchemaImpl) ((SchemaImpl) responses.get(resp).getContentMediaTypes().get(media)
                                .getSchema()).getItemsSchema()).getReference() != null) {
                                String[] mp = ((SchemaImpl) ((SchemaImpl) responses.get(resp).getContentMediaTypes()
                                    .get(media).getSchema()).getItemsSchema()).getReference().getFragment().split("/");
                                response.setType(mp[mp.length - 1]);
                                response.setIsEntity(true);
                            } else {
                                response.setType(((SchemaImpl) ((SchemaImpl) responses.get(resp).getContentMediaTypes()
                                    .get(media).getSchema()).getItemsSchema()).getType());
                            }
                            if (tags.contains(Constants.PAGINATED)) {
                                response.setIsPaginated(true);
                            } else {
                                response.setIsArray(true);
                            }

                        } else if (((SchemaImpl) responses.get(resp).getContentMediaTypes().get(media).getSchema())
                            .getType() != null) {
                            response.setType(
                                ((SchemaImpl) responses.get(resp).getContentMediaTypes().get(media).getSchema())
                                    .getType());
                            response.setFormat(
                                ((SchemaImpl) responses.get(resp).getContentMediaTypes().get(media).getSchema())
                                    .getFormat());
                        } else {
                            response.setIsVoid(true);
                        }
                    }
                } else {
                    response.setIsVoid(true);
                }
                break;
            } else {
                response.setIsVoid(true);
            }
        }
        return response;
    }

    @Override
    public Object read(java.nio.file.Path path, Charset inputCharset, Object... additionalArguments)
        throws InputReaderException {
        if (!Files.isRegularFile(path)) {
            throw new InputReaderException("Path " + path.toAbsolutePath().toUri().toString() + " is not a file!");
        }
        OpenApi3 openApi = (OpenApi3) new OpenApi3Parser().parse(path.toUri());
        if (openApi == null) {
            throw new InputReaderException(path + " is not a valid OpenAPI file");
        }
        return new OpenAPIFile(path, openApi);
    }

}
