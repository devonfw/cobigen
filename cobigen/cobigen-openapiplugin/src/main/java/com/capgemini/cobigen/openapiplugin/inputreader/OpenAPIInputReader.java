package com.capgemini.cobigen.openapiplugin.inputreader;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.openapiplugin.inputreader.to.ComponentDef;
import com.capgemini.cobigen.openapiplugin.inputreader.to.OpenAPIDef;
import com.capgemini.cobigen.openapiplugin.inputreader.to.OpenAPIFile;
import com.capgemini.cobigen.openapiplugin.inputreader.to.PathDef;
import com.capgemini.cobigen.openapiplugin.utils.constants.Constants;

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
import io.swagger.models.properties.PasswordProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

/**
 * Extension for the {@link InputReader} Interface of the CobiGen, to be able to read Swagger definition files
 * into FreeMarker models
 */
public class OpenAPIInputReader implements InputReader {

    @Override
    public boolean isValidInput(Object input) {
        if (input instanceof OpenAPIFile) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Object> createModel(Object input) {
        Map<String, Object> pojoModel = new HashMap<>();

        ModelImpl model = ((OpenAPIDef) input).getModel();
        pojoModel.put(ModelConstant.NAME, model.getName());
        if (model.getDescription() != null) {
            pojoModel.put(ModelConstant.DESCRIPTION, model.getDescription());

        }
        pojoModel.put(ModelConstant.COMPONENT, model.getReference().toLowerCase());
        if (model.getRequired() != null) {
            pojoModel.put(ModelConstant.FIELDS, getFields(model.getProperties(), model.getRequired()));
        } else {
            pojoModel.put(ModelConstant.FIELDS, getFields(model.getProperties(), new LinkedList<String>()));
        }

        List<PathDef> paths = ((OpenAPIDef) input).getPaths();

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
                if (properties.get(key) instanceof RefProperty) {
                    fieldValues.put(ModelConstant.TYPE, ((RefProperty) properties.get(key)).getSimpleRef());
                    fieldValues.put(ModelConstant.IS_ENTITY, true);
                } else if (properties.get(key) instanceof ArrayProperty) {
                    ArrayProperty array = (ArrayProperty) properties.get(key);
                    if (array.getItems() instanceof RefProperty) {
                        fieldValues.put(ModelConstant.TYPE, ((RefProperty) array.getItems()).getSimpleRef());
                        fieldValues.put(ModelConstant.IS_COLLECTION, true);
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
        if (input instanceof OpenAPIFile) {
            return true;
        }
        return false;
    }

    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        List<Object> inputs = new LinkedList<>();
        if (input instanceof Swagger) {
            inputs.addAll(getComponents((Swagger) input));
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

    private List<ComponentDef> getComponents(Swagger input) {
        List<ComponentDef> objects = new LinkedList<>();
        List<String> added = new LinkedList<>();
        for (String key : input.getPaths().keySet()) {
            String[] mp = key.split("/");
            if (mp[0].equals("")) {
                added.add(mp[1]);
                ComponentDef component = new ComponentDef();
                component.setComponent(mp[1]);
                component.setVersion(mp[2]);
                objects.add(component);
            } else {
                added.add(mp[0]);
                ComponentDef component = new ComponentDef();
                component.setComponent(mp[0]);
                component.setVersion(mp[1]);
                objects.add(component);
            }
        }
        return objects;
    }

    // /**
    // * @param input
    // * @return
    // */
    // private List<SwaggerDef> getObjectDefinitions(Swagger input) {
    // List<SwaggerDef> objects = new LinkedList<>();
    // for (String key : input.getDefinitions().keySet()) {
    // if (input.getDefinitions().get(key) instanceof ModelImpl) {
    // ModelImpl inputComponentObject = (ModelImpl) input.getDefinitions().get(key);
    // if (inputComponentObject.getDescription() != null) {
    // if (inputComponentObject.getType().equals("object")
    // && inputComponentObject.getDescription().equals("oasp4j_component")) {
    // for (String keyEntity : inputComponentObject.getProperties().keySet()) {
    // if (inputComponentObject.getProperties().get(keyEntity) instanceof RefProperty) {
    // ModelImpl inputObject = (ModelImpl) input.getDefinitions().get(
    // ((RefProperty) inputComponentObject.getProperties().get(keyEntity)).getSimpleRef());
    // SwaggerDef definition = new SwaggerDef();
    // for (String path : input.getPaths().keySet()) {
    // if (path.startsWith("/" + key.toLowerCase() + "/")
    // && path.contains("/" + keyEntity.toLowerCase() + "/")) {
    // PathDef pathDef = new PathDef();
    // pathDef.setPathURI(path);
    // pathDef.setPath(input.getPaths().get(path));
    // definition.addPath(pathDef);
    // }
    // }
    // inputObject.setName(keyEntity);
    // inputObject.setReference(key);
    // definition.setModel(inputObject);
    // objects.add(definition);
    // }
    // }
    // }
    // }
    // }
    // }
    // return objects;
    // }
}
