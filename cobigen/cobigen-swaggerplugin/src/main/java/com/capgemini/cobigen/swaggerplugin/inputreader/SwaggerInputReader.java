package com.capgemini.cobigen.swaggerplugin.inputreader;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.swaggerplugin.inputreader.to.SwaggerFile;
import com.capgemini.cobigen.swaggerplugin.utils.constants.Constants;

import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
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
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.PasswordProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import io.swagger.parser.SwaggerParser;

/**
 * Extension for the {@link InputReader} Interface of the CobiGen, to be able to read Swagger definition files
 * into FreeMarker models
 */
public class SwaggerInputReader implements InputReader {

    @Override
    public boolean isValidInput(Object input) {
        if (input instanceof SwaggerFile) {
            Swagger swagger = new SwaggerParser().read(((SwaggerFile) input).getLocation().toString());
            if (swagger == null) {
                return false;
            } else {
                for (ModelImpl def : getObjectDefinitions(swagger)) {
                    if (def.getFormat() == null || def.getFormat().equals("")) {
                        // System.out.println(def.getFormat());
                        // System.out.println(def.getName());
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

        ModelImpl model = (ModelImpl) input;
        pojoModel.put(ModelConstant.NAME, model.getName());
        if (model.getDescription() != null) {
            pojoModel.put(ModelConstant.COMPONENT, model.getFormat());
            pojoModel.put(ModelConstant.DESCRIPTION, model.getDescription());

        }
        if (model.getRequired() != null) {
            pojoModel.put(ModelConstant.FIELDS, getFields(model.getProperties(), model.getRequired()));
        } else {
            pojoModel.put(ModelConstant.FIELDS, getFields(model.getProperties(), new LinkedList<String>()));
        }

        return pojoModel;
    }

    /**
     * @param properties
     * @return
     */
    private List<Map<String, Object>> getFields(Map<String, Property> properties, List<String> required) {

        List<Map<String, Object>> fields = new LinkedList<>();
        Map<String, Object> fieldValues;
        if (properties != null) {
            for (String key : properties.keySet()) {
                fieldValues = new HashMap<>();

                fieldValues.put(ModelConstant.NAME, key);
                fieldValues.put(ModelConstant.TYPE, buildType(properties.get(key).getType(),
                    properties.get(key).getFormat(), properties.get(key), key));
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
     * @return
     */
    private Object getConstraints(Property property, List<String> required, String key) {
        Map<String, Object> constraints = new HashMap<>();
        if (required.contains(key)) {
            constraints.put(ModelConstant.NOTNULL, true);
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
     * @param property
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
        if (input instanceof SwaggerFile) {
            return true;
        }
        return false;
    }

    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        List<Object> inputs = new LinkedList<>();
        if (input instanceof Swagger) {
            // Swagger swagger = new SwaggerParser().read(((SwaggerFile) input).getLocation().getPath());
            inputs.addAll(getObjectDefinitions((Swagger) input));
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

    private List<ModelImpl> getObjectDefinitions(Swagger input) {
        List<ModelImpl> objects = new LinkedList<>();
        for (String key : input.getDefinitions().keySet()) {
            if (input.getDefinitions().get(key) instanceof ModelImpl) {
                ModelImpl inputObject = (ModelImpl) input.getDefinitions().get(key);
                if (inputObject.getType().equals("object")) {
                    inputObject.setName(key);
                    objects.add(inputObject);
                    if (inputObject.getProperties() != null) {
                        for (String propKey : inputObject.getProperties().keySet()) {
                            if (inputObject.getProperties().get(propKey).getType().equals("object")) {
                                ObjectProperty tal = (ObjectProperty) inputObject.getProperties().get(propKey);
                                System.out.println(inputObject.getProperties().get(propKey).getFormat());
                                ModelImpl ni = new ModelImpl();
                                ni.setName(key);
                                ni.setDescription(tal.getDescription());
                                ni.setProperties(tal.getProperties());
                                ni.setRequired(tal.getRequiredProperties());
                                // System.out.println(tal.getFormat());
                                ni.setFormat(inputObject.getProperties().get(propKey).getFormat());
                                objects.add(ni);
                            }
                        }
                    }
                }
            }
        }
        return objects;
    }

}
