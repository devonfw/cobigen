package utils;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/** Utils on OpenAPI template model */
public class OpenApiUtil {

    /**
     * Prints javax validation constraints from an OpenAPI model
     * @param constraints
     *            OpenAPI model constraints
     * @return the list of supported annotations in Java syntax
     */
    public String printJavaConstraints(Map<String, Object> constraints) {

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

    /**
     * Prints the return type of a response based on the OpenAPI model
     * @param response
     *            the OpenAPI model of a response
     * @return simple type of the response return type in Java syntax
     */
    public String printJavaServiceResponseReturnType(Map<String, Object> response) {
        String returnType = toJavaType(response, false);
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

    /**
     * Returns the Java type corresponding to the OpenAPI type definition. If the type could not be matched,
     * Object will be returned.
     * @param parameter
     *            OpenAPI model of a parameter
     * @param simpleType
     *            if a Java simple type should be returned if possible
     * @return the Java type
     */
    // getOaspTypeFromOpenAPI
    public String toJavaType(Map<String, Object> parameter, boolean simpleType) {
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
                } else {
                    typeConverted = "Object";
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
                } else {
                    typeConverted = "Object";
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

    /**
     * Prints the service operation name based on the operationId or generates one based on the servicePath
     * while printing a comment how to get better service names.
     * @param operation
     *            operation Model of the OpenAPI model
     * @param servicePath
     *            service path of the
     * @return the service method name
     */
    public String printServiceOperationName(Map<String, Object> operation, String servicePath) {
        String operationId = (String) operation.get("operationId");
        if (StringUtils.isEmpty(operationId)) {
            String[] split = servicePath.split("/");
            String lastSegment;
            if (!split[split.length - 1].isEmpty()) {
                lastSegment = split[split.length - 1];
            } else {
                lastSegment = split[split.length - 2];
            }
            return "/* Generated value! You might want to set the operation name by operationId (https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#fixed-fields-8) in the openAPI definition */"
                + ((String) operation.get("type")) + lastSegment;
        } else {
            return operationId;
        }
    }
}
