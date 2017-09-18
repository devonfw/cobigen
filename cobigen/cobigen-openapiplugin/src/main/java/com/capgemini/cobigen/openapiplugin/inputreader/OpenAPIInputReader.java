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
        constraints.put(ModelConstant.MAXIMUM, schema.getMaximum());
        constraints.put(ModelConstant.MINIMUM, schema.getMinimum());
        if (schema.getType().equals("array")) {
            constraints.put(ModelConstant.MAX_LENGTH, schema.getMaxItems());
            constraints.put(ModelConstant.MIN_LENGTH, schema.getMinItems());
            constraints.put(ModelConstant.UNIQUE, schema.isUniqueItems());
        } else if (schema.getType().equals("string")) {
            constraints.put(ModelConstant.MAX_LENGTH, schema.getMaxLength());
            constraints.put(ModelConstant.MIN_LENGTH, schema.getMinLength());
        }
        constraints.put(ModelConstant.NOTNULL, schema.isNullable());

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
            entityDef.setProperties(getFields(openApi.getSchema(key).getProperties(), openApi));
            entityDef.setComponentName(openApi.getSchema(key).getTitle());
            componentDef.setPaths(getPaths(openApi.getPaths(), openApi.getSchema(key).getTitle(), key));
            entityDef.setComponent(componentDef);
            objects.add(entityDef);
        }
        return objects;

    }

    /**
     * Get the fields of an entity returning a list of {@link PropertyDef}'s
     *
     * @param properties
     *            map of properties of the OpenApi3 entity definition
     * @param openApi
     *            the OpenApi3 model
     * @return List of {@link PropertyDef}'s
     */
    private List<PropertyDef> getFields(Map<String, ? extends Schema> properties, OpenApi3 openApi) {
        List<PropertyDef> objects = new LinkedList<>();
        for (String key : properties.keySet()) {
            PropertyDef property = new PropertyDef();
            property.setName(key);
            property.setDescription(properties.get(key).getDescription());
            if (properties.get(key).getType().equals(Constants.ARRAY)) {
                property.setIsCollection(true);
                String[] mp =
                    ((SchemaImpl) properties.get(key).getItemsSchema()).getReference().getFragment().split("/");
                property.setType(mp[mp.length - 1]);
                if (openApi.getSchema(mp[mp.length - 1]) != null) {
                    property.setIsEntity(true);
                }
            } else if (properties.get(key).getType().equals(Constants.OBJECT)) {
                String[] mp = ((SchemaImpl) properties.get(key)).getReference().getFragment().split("/");
                property.setType(mp[mp.length - 1]);
                property.setIsEntity(true);
                ;
            } else {
                property.setType(properties.get(key).getType());
                if (properties.get(key).getFormat() != null) {
                    property.setFormat(properties.get(key).getFormat());
                }
            }
            if (properties.get(key).getRequiredFields().contains(key)) {
                property.setRequired(true);
            } else {
                property.setRequired(false);
            }
            property.setConstraints(getConstraints(properties.get(key)));
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
            if (pathKey.contains(component)) {
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
            parameter.setConstraints(getConstraints(param.getSchema()));
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
                if (tags.contains(Constants.SEARCH_CRITERIA)
                    || tags.contains(Constants.SEARCH_CRITERIA.toLowerCase())) {
                    parameter.setIsSearchCriteria(true);
                    parameter.setName("criteria");
                }
                if (((SchemaImpl) requestBody.getContentMediaTypes().get(media).getSchema()).getReference() != null) {
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
