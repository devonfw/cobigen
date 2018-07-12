package com.devonfw.cobigen.templates.oasp4j.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
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
            switch (type.toLowerCase()) {
            case "integer":
                if (format != null) {
                    switch (format) {
                    case "int32":
                        typeConverted = simpleType ? "int" : "Integer";
                        break;
                    case "int64":
                        typeConverted = simpleType ? "long" : "Long";
                        break;
                    default:
                        typeConverted = BigInteger.class.getSimpleName();
                        break;
                    }
                } else {
                    typeConverted = BigInteger.class.getSimpleName();
                }
                break;
            case "number":
                if (format != null) {
                    switch (format) {
                    case "float":
                        typeConverted = simpleType ? "float" : "Float";
                        break;
                    case "double":
                        typeConverted = simpleType ? "double" : "Double";
                        break;
                    default:
                        typeConverted = BigDecimal.class.getSimpleName();
                        break;
                    }
                } else {
                    typeConverted = BigDecimal.class.getSimpleName();
                }
                break;
            case "string":
                if (format != null) {
                    switch (format) {
                    case "date":
                        typeConverted = LocalDate.class.getSimpleName();
                        break;
                    case "date-time":
                        typeConverted = Instant.class.getSimpleName();
                        break;
                    case "binary":
                        typeConverted = simpleType ? "float" : "Float";
                        break;
                    case "email":
                    case "password":
                        typeConverted = String.class.getSimpleName();
                        break;
                    default:
                        typeConverted = "String";
                        break;
                    }
                } else {
                    typeConverted = "String";
                }
                break;
            case "boolean":
                typeConverted = simpleType ? "boolean" : "Boolean";
                break;
            default:
                typeConverted = "void";
                break;
            }
        } else {
            typeConverted = "void";
        }

        if (isCollection) {
            if (isEntity) {
                return "List<" + parameter.get("type") + "Entity>";
            } else {
                return "List<" + typeConverted + ">";
            }
        } else if (isEntity) {
            return (String) parameter.get("type");
        }
        return typeConverted;
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

    /**
     * Adds indicators to a parameter name based on them being a ?query or a {path} parameter
     * @param param
     *            the parameter to add indicators too
     * @return The name of the given parameter with the prefix ? or surrounded by {}
     */
    public String getParam(Map<String, Object> param) {
        String result = "";
        String type = (String) param.get("type");
        if (type == null || type.equals("void")) {
            result = "void";
        } else {
            if ((boolean) param.get("inPath")) {
                result += "{" + type + "}";
            } else if ((boolean) param.get("inQuery")) {
                result += "?" + type;
            }
        }
        return result;
    }

    /**
     * Lists the constraints of an operations parameter as asciidoc code
     * @param param
     *            The parameter which constraints should be listed
     * @return The list of constraints as asciidoc code
     */
    public String getConstraintList(Map<String, Object> param) {
        String result = "";
        Map<String, Object> constraints = (Map<String, Object>) param.get("constraints");
        boolean required = false;
        int nrParam = 0;
        if (constraints != null) {
            if (constraints.containsKey("notNull")) {
                required = (boolean) constraints.get("notNull");
            }
            if (required) {
                result += "[red]#__Required__# +" + System.lineSeparator();
                nrParam++;
            }
            for (String key : constraints.keySet()) {
                if (!key.equals("required")) {
                    Object val = constraints.get(key);
                    if (val != null) {
                        if (val instanceof Boolean) {
                            if ((boolean) val) {
                                result += key + " +" + System.lineSeparator();
                                nrParam++;
                            }
                        } else {
                            result += key + " = " + val + " +" + System.lineSeparator();
                            nrParam++;
                        }
                    }
                }
            }
        }
        if (nrParam == 0) {
            result = "-";
        }
        return result;
    }
}
