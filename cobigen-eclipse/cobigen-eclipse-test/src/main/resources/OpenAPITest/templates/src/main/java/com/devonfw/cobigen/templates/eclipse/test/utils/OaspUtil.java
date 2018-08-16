package com.devonfw.cobigen.templates.eclipse.test.utils;

import java.util.Map;

/**
 * A class for shared oasp4j specific functions in the templates
 *
 */
public class OaspUtil {

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
}