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
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BaseIntegerProperty;
import io.swagger.models.properties.BinaryProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.ByteArrayProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DateTimeProperty;
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
        int descriptionIndex = model.getDescription().indexOf("-/-");
        if (descriptionIndex != 0) {
            pojoModel.put(ModelConstant.COMPONENT, model.getDescription().substring(0, descriptionIndex));
        }
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
                fieldValues.put(ModelConstant.TYPE, buildType(properties.get(key)));
                fieldValues.put(ModelConstant.CONSTRAINTS, getConstraints(properties.get(key)));

                fields.add(fieldValues);
            }
        }
        return fields;
    }

    /**
     * @param property
     * @return
     */
    private Object getConstraints(Property property) {
        Map<String, Object> constraints = new HashMap<>();
        if (property.getRequired()) {
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
            || property instanceof BinaryProperty || property instanceof PasswordProperty) {
            return Constants.LONG;
        } else if (property instanceof BooleanProperty) {
            return Constants.BOOLEAN;
        } else if (property instanceof StringProperty) {
            return Constants.STRING;
        } else if (property instanceof ObjectProperty) {
            return ((ObjectProperty) property).getName();
        } else if (property instanceof RefProperty) {
            return ((RefProperty) property).getName();
        } else if (property instanceof ByteArrayProperty) {
            return Constants.BYTE;
        } else if (property instanceof DateProperty) {
            return Constants.DATE;
        } else if (property instanceof DateTimeProperty) {
            return Constants.TIMESTAMP;
        } else if (property instanceof BinaryProperty) {
            return Constants.FLOAT;
        } else if (property instanceof EmailProperty) {
            return Constants.STRING;
        } else if (property instanceof PasswordProperty) {
            return Constants.STRING;
        } else {
            return Constants.STRING;
        }
        return constraints;
    }

    /**
     * @param property
     * @return
     */
    private String buildType(Property property) {
        if (property instanceof IntegerProperty) {
            return Constants.INTEGER;
        } else if (property instanceof DoubleProperty) {
            return Constants.DOUBLE;
        } else if (property instanceof LongProperty) {
            return Constants.LONG;
        } else if (property instanceof BooleanProperty) {
            return Constants.BOOLEAN;
        } else if (property instanceof StringProperty) {
            return Constants.STRING;
        } else if (property instanceof ObjectProperty) {
            return ((ObjectProperty) property).getName();
        } else if (property instanceof RefProperty) {
            return ((RefProperty) property).getName();
        } else if (property instanceof ByteArrayProperty) {
            return Constants.BYTE;
        } else if (property instanceof DateProperty) {
            return Constants.DATE;
        } else if (property instanceof DateTimeProperty) {
            return Constants.TIMESTAMP;
        } else if (property instanceof BinaryProperty) {
            return Constants.FLOAT;
        } else if (property instanceof EmailProperty) {
            return Constants.STRING;
        } else if (property instanceof PasswordProperty) {
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
