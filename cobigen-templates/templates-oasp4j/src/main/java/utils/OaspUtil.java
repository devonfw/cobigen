package utils;

import java.util.Collection;
import java.util.Map;

import constants.pojo.Field;

/**
 * A class for shared oasp4j specific functions in the templates
 *
 */
public class OaspUtil {

    /**
     * Check whether the given 'canonicalType' is an OASP Entity, which is declared in the given 'component'
     *
     * @param canonicalType
     *            the type name
     * @param component
     *            the component name
     * @return true iff the canonicalType is an OASP Entity
     */
    public boolean isEntityInComponent(String canonicalType, String component) {

        return canonicalType.matches(String.format(".+%1$s\\.dataaccess\\.api\\.[A-Za-z0-9]+Entity(<.*)?", component));
    }

    /**
     * Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via
     * an object reference or a direct ID getter
     *
     * @param field
     *            the field
     * @param byObjectReference
     *            boolean
     * @param component
     *            the OASP4j component name
     * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} +
     *         '()' with capitalize=true
     */
    public String resolveIdGetter(Map<String, Object> field, boolean byObjectReference, String component) {

        return "get" + resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true, component) + "()";
    }

    /**
     * Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via
     * an object reference or a direct ID getter
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @param byObjectReference
     *            boolean
     * @param component
     *            the OASP4j component name
     * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} +
     *         '()' with capitalize=true
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdGetter(Class<?> pojoClass, Map<String, Object> fieldMap, boolean byObjectReference,
        String component) throws NoSuchFieldException, SecurityException {

        return "get"
            + resolveIdVariableNameOrSetterGetterSuffix(pojoClass, fieldMap, byObjectReference, true, component) + "()";
    }

    /**
     * same as {@link #resolveIdGetter(Map, boolean, String)} but with byObjectReference=false and
     * component=""
     *
     * @param field
     *            the field
     * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} + '()'
     *         with capitalize=true
     */
    public String resolveIdGetter(Map<String, Object> field) {

        return this.resolveIdGetter(field, false, "");
    }

    /**
     * same as {@link #resolveIdGetter(Class,Map,boolean,String)} but with byObjectReference=false and
     * component=""
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} + '()'
     *         with capitalize=true
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdGetter(Class<?> pojoClass, Map<String, Object> fieldMap)
        throws NoSuchFieldException, SecurityException {

        return resolveIdGetter(pojoClass, fieldMap, false, "");
    }

    /**
     * Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via
     * an object reference or a direct ID setter. In contrast to resolveIdGetter, this function does not
     * generate the function parenthesis to enable parameter declaration.
     *
     * @param field
     *            the field
     * @param byObjectReference
     *            boolean
     * @param component
     *            the OASP4j component name
     * @return 'set' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} with
     *         capitalize=true
     */
    public String resolveIdSetter(Map<String, Object> field, boolean byObjectReference, String component) {

        return "set" + resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true, component);
    }

    /**
     * same as {@link #resolveIdSetter(Map, boolean, String)} but with byObjectReference=false and
     * component=""
     *
     * @param field
     *            the field
     * @return 'set' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} with
     *         capitalize=true
     */
    public String resolveIdSetter(Map<String, Object> field) {

        return this.resolveIdSetter(field, false, "");
    }

    /**
     * Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via
     * an object reference or a direct ID setter. In contrast to resolveIdGetter, this function does not
     * generate the function parenthesis to enable parameter declaration.
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @param byObjectReference
     *            boolean
     * @param component
     *            the OASP4j component name
     * @return 'set'+ {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} with
     *         capitalize=true
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdSetter(Class<?> pojoClass, Map<String, Object> fieldMap, boolean byObjectReference,
        String component) throws NoSuchFieldException, SecurityException {

        return "set"
            + resolveIdVariableNameOrSetterGetterSuffix(pojoClass, fieldMap, byObjectReference, true, component);
    }

    /**
     * same as {@link #resolveIdSetter(Class,Map,boolean,String)} but with byObjectReference=false and
     * component=""
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @return 'set' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} with
     *         capitalize=true
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdSetter(Class<?> pojoClass, Map<String, Object> fieldMap)
        throws NoSuchFieldException, SecurityException {

        return resolveIdSetter(pojoClass, fieldMap, false, "");
    }

    /**
     * Determines the variable name for the id value of the 'field'
     *
     * @param field
     *            the field
     * @return {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)}) with
     *         byObjectReference=false, capitalize=false and component=""
     */
    public String resolveIdVariableName(Map<String, Object> field) {

        // the component is passed down as an empty string since byObjectReference is false and therefore the
        // component is
        // never touched
        return resolveIdVariableNameOrSetterGetterSuffix(field, false, false, "");
    }

    /**
     * Determines the variable name for the id value of the specified field in the pojo
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @return {@link #resolveIdVariableNameOrSetterGetterSuffix(Class, Map, boolean, boolean, String)}) with
     *         byObjectReference=false, capitalize=false and component=""
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdVariableName(Class<?> pojoClass, Map<String, Object> fieldMap)
        throws NoSuchFieldException, SecurityException {

        // the component is passed down as an empty string since byObjectReference is false and therefore the
        // component is
        // never touched
        return resolveIdVariableNameOrSetterGetterSuffix(pojoClass, fieldMap, false, false, "");
    }

    /**
     * Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter
     * should access the ID via an object reference or a direct ID setter/getter
     *
     * @param field
     *            the field
     * @param byObjectReference
     *            boolean
     * @param capitalize
     *            if the field name should be capitalized
     * @param component
     *            the oasp component. Only needed if $byObjectReference is true
     * @return idVariable name or getter/setter suffix
     */
    public String resolveIdVariableNameOrSetterGetterSuffix(Map<String, Object> field, boolean byObjectReference,
        boolean capitalize, String component) {

        String fieldName = (String) field.get(Field.NAME.toString());
        if (capitalize) {
            fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        }
        String suffix = "";

        String fieldType = (String) field.get(Field.TYPE.toString());
        String fieldCType = (String) field.get(Field.CANONICAL_TYPE.toString());
        if (fieldType.contains("Entity")) {
            if (fieldCType.startsWith("java.util.List") || fieldCType.startsWith("java.util.Set")) {
                suffix = "Ids";
                if (fieldName.endsWith("s")) {
                    // Assume trailing 's' as indicator for a plural
                    fieldName = fieldName.substring(0, fieldName.length() - 1);
                }
            } else {
                suffix = "Id";
            }
            if (byObjectReference && isEntityInComponent(fieldCType, component)) {
                // direct references for Entities in same component, so get id of the object reference
                suffix = "().getId";
            }
        }

        return fieldName + suffix;

    }

    /**
     * Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter
     * should access the ID via an object reference or a direct ID setter/getter
     *
     * @param pojoClass
     *            the {@link Class} object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @param byObjectReference
     *            boolean
     * @param capitalize
     *            if the field name should be capitalized
     * @param component
     *            the oasp component. Only needed if byObjectReference is true
     * @return idVariable name or getter/setter suffix
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdVariableNameOrSetterGetterSuffix(Class<?> pojoClass, Map<String, Object> fieldMap,
        boolean byObjectReference, boolean capitalize, String component)
        throws NoSuchFieldException, SecurityException {

        String resultName = (String) fieldMap.get(Field.NAME.toString());
        if (capitalize) {
            resultName = resultName.substring(0, 1).toUpperCase() + resultName.substring(1);
        }
        String suffix = "";
        String fieldType = (String) fieldMap.get(Field.TYPE.toString());
        String fieldName = (String) fieldMap.get(Field.NAME.toString());
        if (fieldType.contains("Entity")) {
            if (Collection.class.isAssignableFrom(pojoClass.getDeclaredField(fieldName).getType())) {
                suffix = "Ids";
                if (resultName.endsWith("s")) {
                    // Assume trailing 's' as indicator for a plural
                    resultName = resultName.substring(0, resultName.length() - 1);
                }
            } else {
                suffix = "Id";
            }
            if (byObjectReference
                && isEntityInComponent(pojoClass.getDeclaredField(fieldName).getType().getName(), component)) {
                // direct references for Entities in same component, so get id of the object reference
                suffix = "().getId";
            }
        }

        return resultName + suffix;

    }

    /**
     * Converts all occurrences of OASP Entities types in the given 'field' simple type (possibly generic) to
     * Longs
     *
     * @param field
     *            the field
     * @return the field type as String. If field type contains 'Entity' the result is the field type under
     *         the regex /[^<>]+Entity/Long/
     */
    public String getSimpleEntityTypeAsLongReference(Map<String, Object> field) {

        String fieldType = (String) field.get(Field.TYPE.toString());
        if (fieldType.contains("Entity")) {
            fieldType = fieldType.replaceAll("[^<>]+Entity", "Long");
        }
        return fieldType;
    }

    public String getOaspTypeFromOpenAPI(Map<String, Object> parameter, boolean simpleType) {
        String typeConverted = null;
        String format = (String) parameter.get("format");
        String type = (String) parameter.get("type");
        boolean isCollection = false;
        if (parameter.get("isCollection") != null) {
            isCollection = (boolean) parameter.get("isCollection");
        }

        boolean isEntity = (boolean) parameter.get("isEntity");

        if (type != null) {
            if (format != null) {
                if (type.equals("integer") && format.equals("int32")) {
                    if (simpleType) {
                        typeConverted = "int";
                    } else {
                        typeConverted = "Integer";
                    }
                } else if (type.equals("number") && format.equals("double")) {
                    if (simpleType) {
                        typeConverted = "double";
                    } else {
                        typeConverted = "Double";
                    }
                } else if (type.equals("integer") && format.equals("int64")) {
                    if (simpleType) {
                        typeConverted = "long";
                    } else {
                        typeConverted = "Long";
                    }
                } else if (type.equals("string") && format.equals("date")) {
                    typeConverted = "Date";
                } else if (type.equals("string") && format.equals("date-time")) {
                    typeConverted = "Timestamp";
                } else if (type.equals("string") && format.equals("binary")) {
                    typeConverted = "float";
                } else if (type.equals("string") && format.equals("email")) {
                    typeConverted = "String";
                } else if (type.equals("string") && format.equals("password")) {
                    typeConverted = "String";
                }
            } else {
                if (type.equals("boolean")) {
                    if (simpleType) {
                        typeConverted = "boolean";
                    } else {
                        typeConverted = "Boolean";
                    }
                } else if (type.equals("string")) {
                    typeConverted = "String";
                } else if (type.equals("integer")) {
                    typeConverted = "Integer";
                } else if (type.equals("number")) {
                    typeConverted = "BigDecimal";
                }
            }
        } else {
            return "void";
        }
        if (isCollection) {
            if (isEntity) {
                return "List<" + parameter.get("type") + ">";
            } else {
                return "List<" + typeConverted + ">";
            }
        } else {
            if (isEntity) {
                return (String) parameter.get("type");
            }
            return typeConverted;
        }
    }

    public boolean commonCRUDOperation(String operationId, String entityName) {

        String opIdLowerCase = operationId.toLowerCase();
        String entityNameLowerCase = entityName.toLowerCase();
        if (opIdLowerCase.contains(entityNameLowerCase)) {
            return opIdLowerCase.equals("find" + entityNameLowerCase)
                || opIdLowerCase.equals("find" + entityNameLowerCase + "etos")
                || opIdLowerCase.equals("delete" + entityNameLowerCase)
                || opIdLowerCase.equals("save" + entityNameLowerCase);
        } else {
            return false;
        }
    }

    public String returnType(Map<String, Object> response) {
        String returnType = getOaspTypeFromOpenAPI(response, false);
        if ((boolean) response.get("isVoid")) {
            return "void";
        }
        if ((boolean) response.get("isArray")) {
            if ((boolean) response.get("isEntity")) {
                return "List<" + returnType + ">";
            } else {
                return "List<" + returnType + ">";
            }
        } else if ((boolean) response.get("isPaginated")) {
            if ((boolean) response.get("isEntity")) {
                return "PaginatedListTo<" + returnType + "Eto>";
            } else {
                return "PaginatedListTo<" + returnType + ">";
            }
        } else {
            return returnType;
        }
    }

    public String getJAVAConstraint(Map<String, Object> constraints) {

        String consts = "";
        if (constraints.get("maximum") != null) {
            consts = consts + "@Max(" + constraints.get("maximum") + ")";
        }
        if (constraints.get("minimum") != null) {
            consts = consts + "@Min(" + constraints.get("minimum") + ")";
        }
        if (constraints.get("maxLength") != null) {
            consts = consts + "@Size(max=" + constraints.get("maxLength");
            if (constraints.get("minLength") != null) {
                consts = consts + ", min=" + constraints.get("minLength");
            }
            consts = consts + ")";
        } else {
            if (constraints.get("minLength") != null) {
                consts = consts + "@Size(min=" + constraints.get("minLength");
                if (constraints.get("maxLength") != null) {
                    consts = consts + ", max=" + constraints.get("maxLength");
                }
                consts = consts + ")";
            }
        }
        return consts;
    }

    public String getSpringMediaType(String mediaType) {
        switch (mediaType) {
        case "application/xml":
            return "APPLICATION_XML_VALUE";
        case "application / x-www-form-urlencoded":
            return "APPLICATION_FORM_URLENCODED_VALUE";

        case "multipart/form-data":
            return "MULTIPART_FORM_DATA_VALUE";

        case "text/plain":
            return "TEXT_PLAIN_VALUE";

        case "text/html":
            return "TEXT_HTML_VALUE";

        case "application/pdf":
            return "APPLICATION_PDF_VALUE";

        case "image/png":
            return "IMAGE_PNG_VALUE";

        default:
            return "APPLICATION_JSON_VALUE";

        }
    }

    public String getRelationShipAnnotation(Map<String, Object> rs, String entityName) {
        char c[] = ((String) rs.get("entity")).toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        String ent = new String(c);

        c = entityName.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        String entName = new String(c);
        switch ((String) rs.get("type")) {
        case "manytoone":
            return "@ManyToOne" + '\n' + "@JoinColumn(name = \"" + ent + "\")";
        case "onetomany":
            if ((boolean) rs.get("unidirectional")) {
                return "@OneToMany" + "\n" + "@JoinColumn(name = \"" + entName + "Id\")";
            }
            return "@OneToMany(mappedBy = \"" + entName + "\")";

        case "onetoone":
            return "@OneToOne" + '\n' + "@JoinColumn(name = \"" + ent + "\")";
        case "manytomany":
            return "@ManyToMany" + '\n' + "@JoinTable(name = \"" + entityName + (String) rs.get("entity")
                + "\", joinColumns = {" + '\n' + "@javax.persistence.JoinColumn(name = \"" + entName
                + "Id\") }, inverseJoinColumns = @javax.persistence.JoinColumn(name = \"" + ent + "Id\"))";
        default:
            return "";
        }
    }

}