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
import com.capgemini.cobigen.openapiplugin.utils.constants.Constants;
import com.reprezen.kaizen.oasparser.OpenApi3Parser;
import com.reprezen.kaizen.oasparser.model3.OpenApi3;
import com.reprezen.kaizen.oasparser.model3.Parameter;
import com.reprezen.kaizen.oasparser.model3.Path;
import com.reprezen.kaizen.oasparser.model3.RequestBody;
import com.reprezen.kaizen.oasparser.model3.Response;
import com.reprezen.kaizen.oasparser.model3.Schema;
import com.reprezen.kaizen.oasparser.ovl3.SchemaImpl;

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
     * @param property
     * @param required
     * @param key
     * @return
     */
    private Map<String, Object> getConstraints(Schema property) {
        Map<String, Object> constraints = new HashMap<>();
        constraints.put(ModelConstant.MAXIMUM, property.getMaximum());
        constraints.put(ModelConstant.MINIMUM, property.getMinimum());
        if (property.getType().equals("array")) {
            constraints.put(ModelConstant.MAX_LENGTH, property.getMaxItems());
            constraints.put(ModelConstant.MIN_LENGTH, property.getMinItems());
            constraints.put(Constants.UNIQUE, property.isUniqueItems());
        } else if (property.getType().equals("string")) {
            constraints.put(ModelConstant.MAX_LENGTH, property.getMaxLength());
            constraints.put(ModelConstant.MIN_LENGTH, property.getMinLength());
        }
        constraints.put(Constants.NOTNULL, property.isNullable());

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
     * @param openApi
     * @return
     */
    private List<EntityDef> getEntities(OpenApi3 openApi) {
        List<EntityDef> objects = new LinkedList<>();
        List<String> added = new LinkedList<>();
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
     * @param properties
     * @param openApi
     * @return
     */
    private List<PropertyDef> getFields(Map<String, ? extends Schema> properties, OpenApi3 openApi) {
        List<PropertyDef> objects = new LinkedList<>();
        for (String key : properties.keySet()) {
            PropertyDef property = new PropertyDef();
            property.setName(key);
            property.setDescription(properties.get(key).getDescription());
            if (properties.get(key).getType().equals("array")) {
                property.setIsCollection(true);
                String[] mp =
                    ((SchemaImpl) properties.get(key).getItemsSchema()).getReference().getFragment().split("/");
                property.setType(mp[mp.length - 1]);
                if (openApi.getSchema(mp[mp.length - 1]) != null) {
                    property.setIsEntity(true);
                }
            } else if (properties.get(key).getType().equals("object")) {
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
     * @param paths
     * @param component
     * @param key
     * @return
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
     * @param parameters
     * @param tags
     * @param requestBody
     * @param collection
     * @return
     */
    private Collection<? extends ParameterDef> getParameters(Collection<? extends Parameter> parameters,
        Collection<String> tags, RequestBody requestBody) {
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
            if (param.getSchema().getType().equals("array")) {
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
        parameter = new ParameterDef();
        if (tags.contains("searchCriteria") || tags.contains("searchcriteria")) {
            parameter.setIsSearchCriteria(true);
        }
        if (requestBody != null && ((SchemaImpl) requestBody).getReference().getFragment() != null) {
            String[] mp = ((SchemaImpl) requestBody).getReference().getFragment().split("/");
            parameter.setType(mp[mp.length - 1]);
        } else if (requestBody != null) {
            parameter.setType(((SchemaImpl) requestBody).getType());
            parameter.setFormat(((SchemaImpl) requestBody).getFormat());
        }
        parametersList.add(parameter);
        return parametersList;
    }

    /**
     * @param responses
     * @param tags
     * @return
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
                            .getType().equals("array")) {
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
                            if (tags.contains("paginated")) {
                                response.setIsPaginated(true);
                            } else {
                                response.setIsArray(true);
                            }

                        } else if (((SchemaImpl) responses.get(resp).getContentMediaTypes().get(media).getSchema())
                            .getType() != null) {
                            response.setType(
                                ((SchemaImpl) responses.get(resp).getContentMediaTypes().get(media).getSchema())
                                    .getType());
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
