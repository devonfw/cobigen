package com.cobigen.picocli;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides type operations, mainly checks and casts for Java Primitives, to be used in the templates
 *
 */
public class JavaUtil {

    /**
     * Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JavaUtil.class);

    /**
     * The constructor.
     */
    public JavaUtil() {
        // Empty for CobiGen to automatically instantiate it
    }

    /**
     * Returns the Object version of a Java primitive or the input if the input isn't a java primitive
     *
     * @param simpleType
     *            String
     * @return the corresponding object wrapper type simple name of the input if the input is the name of a
     *         primitive java type. The input itself if not. (e.g. "int" results in "Integer")
     * @throws ClassNotFoundException
     *             should not occur.
     */
    public String boxJavaPrimitives(String simpleType) throws ClassNotFoundException {

        if (equalsJavaPrimitive(simpleType)) {
            return ClassUtils.primitiveToWrapper(ClassUtils.getClass(simpleType)).getSimpleName();
        } else {
            return simpleType;
        }

    }

    /**
     * Returns the simple name of the type of a field in the pojoClass. If the type is a java primitive the
     * name of the wrapper class is returned
     *
     * @param pojoClass
     *            {@link Class}&lt;?> the class object of the pojo
     * @param fieldName
     *            {@link String} the name of the field
     * @return String. The simple name of the field's type. The simple name of the wrapper class in case of
     *         java primitives
     * @throws NoSuchFieldException
     *             indicating something awefully wrong in the used model
     * @throws SecurityException
     *             if the field cannot be accessed.
     */
    public String boxJavaPrimitives(Class<?> pojoClass, String fieldName)
        throws NoSuchFieldException, SecurityException {

        if (equalsJavaPrimitive(pojoClass, fieldName)) {
            return ClassUtils.primitiveToWrapper(pojoClass.getDeclaredField(fieldName).getType()).getSimpleName();
        } else {
            return pojoClass.getDeclaredField(fieldName).getType().getSimpleName();
        }
    }

    /**
     * Checks if the given type is a Java primitive
     *
     * @param simpleType
     *            the type to be checked
     * @return true iff simpleType is a Java primitive
     */
    public boolean equalsJavaPrimitive(String simpleType) {

        try {
            return ClassUtils.getClass(simpleType).isPrimitive();
        } catch (ClassNotFoundException e) {
            LOG.warn("{}: Could not find {}", e.getMessage(), simpleType);
            return false;
        }
    }

    /**
     * Checks if the type of the field in the pojo's class is a java primitive
     *
     * @param pojoClass
     *            the {@link Class} object of the pojo
     * @param fieldName
     *            the name of the field to be checked
     * @return true iff the field is a java primitive
     * @throws NoSuchFieldException
     *             indicating something awefully wrong in the used model
     * @throws SecurityException
     *             if the field cannot be accessed.
     */
    public boolean equalsJavaPrimitive(Class<?> pojoClass, String fieldName)
        throws NoSuchFieldException, SecurityException {

        return pojoClass.getDeclaredField(fieldName).getType().isPrimitive();
    }

    /**
     * Checks if the given type is a Java primitive or a Java primitive array
     *
     * @param simpleType
     *            the Type name to be checked
     * @return true iff {@link #equalsJavaPrimitive(String)} is true or if simpleType is an array with a
     *         primitive component
     */
    public boolean equalsJavaPrimitiveIncludingArrays(String simpleType) {

        Class<?> klasse;

        try {
            klasse = ClassUtils.getClass(simpleType).getComponentType();
        } catch (ClassNotFoundException e) {
            LOG.warn("{}: Could not find {}", e.getMessage(), simpleType);
            return false;
        }
        return equalsJavaPrimitive(simpleType) || (klasse != null && klasse.isPrimitive());
    }

    /**
     * Checks if the given field in the pojo class is a java primitive or an array of java primitives
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldName
     *            the name of the field to be checked
     * @return true iff {@link #equalsJavaPrimitive(Class, String)} is true or the field is an array of
     *         primitives
     * @throws NoSuchFieldException
     *             indicating something awfully wrong in the used model
     * @throws SecurityException
     *             if the field cannot be accessed.
     */
    public boolean equalsJavaPrimitiveIncludingArrays(Class<?> pojoClass, String fieldName)
        throws NoSuchFieldException, SecurityException {

        return equalsJavaPrimitive(pojoClass, fieldName) || (pojoClass.getDeclaredField(fieldName).getType().isArray()
            && pojoClass.getDeclaredField(fieldName).getType().getComponentType().isPrimitive());
    }

    /**
     * Returns a cast statement for a given (java primitive, variable name) pair or nothing if the type isn't
     * a java primitive
     *
     * @param simpleType
     *            Java Type
     * @param varName
     *            Variable name
     * @return String either of the form '((Java Primitive Object Type)varName)' if simpleType is a primitive
     *         or the emtpy String otherwise
     * @throws ClassNotFoundException
     *             should not occur
     */
    public String castJavaPrimitives(String simpleType, String varName) throws ClassNotFoundException {

        if (equalsJavaPrimitive(simpleType)) {
            return String.format("((%1$s)%2$s)", boxJavaPrimitives(simpleType), varName);
        } else {
            return "";
        }
    }

    /**
     * Returns a cast statement for a given (java primitive, variable name) pair or nothing if the type isn't
     * a java primitive
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldName
     *            the name of the field to be casted
     * @return if fieldName points to a primitive field then a casted statement (e.g. for an int field:
     *         '((Integer)field)') or an empty String otherwise
     * @throws NoSuchFieldException
     *             indicating something awefully wrong in the used model
     * @throws SecurityException
     *             if the field cannot be accessed.
     */
    public String castJavaPrimitives(Class<?> pojoClass, String fieldName)
        throws NoSuchFieldException, SecurityException {

        if (equalsJavaPrimitive(pojoClass, fieldName)) {
            return String.format("((%1$s)%2$s)", boxJavaPrimitives(pojoClass, fieldName), fieldName);
        } else {
            return "";
        }
    }

    /**
     * returns the sencha type associated with a Java primitive or {@link String} or {@link java.util.Date}
     *
     * @param simpleType
     *            :{@link String} the type to be parsed
     * @return the corresponding sencha type or 'auto' otherwise
     */
    public String getSenchaType(String simpleType) {

        switch (simpleType) {
        case "boolean":
        case "Boolean":
            return "boolean";
        case "short":
        case "Short":
        case "int":
        case "Integer":
        case "long":
        case "Long":
            return "int";
        case "float":
        case "Float":
        case "double":
        case "Double":
            return "float";
        case "char":
        case "Character":
        case "String":
            return "string";
        case "Date":
            return "date";
        default:
            return "auto";
        }
    }

    /**
     * Returns the Ext Type to a given java type
     *
     * @param simpleType
     *            any java type's simple name
     * @return corresponding Ext type
     */
    public String getExtType(String simpleType) {

        switch (simpleType) {
        case "short":
        case "Short":
        case "int":
        case "Integer":
        case "long":
        case "Long":
            return "Integer";
        case "float":
        case "Float":
        case "double":
        case "Double":
            return "Number";
        case "boolean":
        case "Boolean":
            return "Boolean";
        case "char":
        case "Character":
        case "String":
            return "String";
        case "Date":
            return "Date";
        default:
            return "Field";
        }
    }
}
