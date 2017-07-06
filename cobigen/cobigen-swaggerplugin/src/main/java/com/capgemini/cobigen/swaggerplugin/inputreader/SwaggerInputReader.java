package com.capgemini.cobigen.swaggerplugin.inputreader;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.swaggerplugin.inputreader.to.SwaggerFile;
import com.capgemini.cobigen.swaggerplugin.utils.constants.Constants;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;
import io.swagger.parser.SwaggerParser;

/**
 * Extension for the {@link InputReader} Interface of the CobiGen, to be able to read Swagger definition files
 * into FreeMarker models
 */
public class SwaggerInputReader implements InputReader {

    @Override
    public boolean isValidInput(Object input) {
        if (input instanceof File) {
            Swagger swagger = new SwaggerParser().read(((File) input).getAbsolutePath());
            if (swagger == null) {
                return false;
            } else {
                Map<String, Model> definitions = swagger.getDefinitions();
                for (String key : definitions.keySet()) {
                    if (((ModelImpl) definitions.get(key)).getType().equals(Constants.OBJECT_TYPE)
                        && definitions.get(key).getDescription() == null) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Object> createModel(Object input) {
        Map<String, Object> pojoModel = new HashMap<>();
        List<Map<String, Object>> fields = new LinkedList<>();
        ModelImpl model = (ModelImpl) input;
        pojoModel.put(ModelConstant.NAME, model.getName());
        pojoModel.put(ModelConstant.COMPONENT, model.getDescription());
        pojoModel.put(ModelConstant.FIELDS, getFields(model.getProperties()));

        return null;
    }

    /**
     * @param properties
     * @return
     */
    private List<Map<String, Object>> getFields(Map<String, Property> properties) {

        List<Map<String, Object>> fields = new LinkedList<>();
        Map<String, Object> fieldValues;
        for (String key : properties.keySet()) {
            if (key != "id") {
                fieldValues = new HashMap<>();
                fieldValues.put(ModelConstant.NAME, key);

                fieldValues.put(ModelConstant.TYPE,
                    buildType(properties.get(key).getType(), properties.get(key).getFormat()));
                if (properties.get(key).getRequired()) {
                    fieldValues.put(key, "true");
                } else {
                    fieldValues.put(key, "false");
                }
                fields.add(fieldValues);
            }
        }
        return fields;
    }

    /**
     * @param type
     * @param format
     * @return
     */
    private String buildType(String type, String format) {
        switch (type) {
        case "integer":
            switch (format) {
            case "int32":
                return ModelConstant.INTEGER;
            case "int64":
                return ModelConstant.LONG;
            default:
                return ModelConstant.LONG;
            }
        case "number":
            switch (format) {
            case "float":
                return ModelConstant.FLOAT;
            case "double":
                return ModelConstant.DOUBLE;
            default:
                return ModelConstant.DOUBLE;
            }
        case "boolean":
            return ModelConstant.BOOLEAN;
        case "string":
            switch (format) {
            case "byte":
                return ModelConstant.BYTE;
            case "date":
                return ModelConstant.DATE;
            case "date-time":
                return ModelConstant.TIMESTAMP;
            default:
                return ModelConstant.STRING;
            }
        default:
            return type;
        }
    }

    @Override
    public boolean combinesMultipleInputObjects(Object input) {
        if (input instanceof SwaggerFile) {
            return true;
        }
        return false;
    }

    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        List<Object> inputs = new LinkedList<>();
        if (input instanceof SwaggerFile) {
            Swagger swagger = new SwaggerParser().read(((SwaggerFile) input).getLocation().getPath());
            for (String key : swagger.getDefinitions().keySet()) {
                ModelImpl inputObject = (ModelImpl) swagger.getDefinitions().get(key);
                inputObject.setName(key);
                inputs.add(inputObject);
                // TODO - add as input Definitions created inside properties
            }
        }
        return inputs;
    }

    @Override
    public Map<String, Object> getTemplateMethods(Object input) {
        return new HashMap<>();
    }

    @Override
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {
        return new LinkedList<>();
    }

}
