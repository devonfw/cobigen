package com.devonfw.cobigen.templates.devon4j.utils;

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
   *
   * @param constraints OpenAPI model constraints
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
   *
   * @param response the OpenAPI model of a response
   * @return simple type of the response return type in Java syntax
   */
  public String printJavaServiceResponseReturnType(Map<String, Object> response) {

    String returnType = toJavaType(response, false);
    if ((boolean) response.get("isVoid")) {
      return "void";
    }
    if ((boolean) response.get("isArray")) {
      if ((boolean) response.get("isEntity")) {
        if (returnType.contains("Entity")) {
          return "List<" + returnType + ">";
        } else {
          return "List<" + returnType + "Entity>";
        }
      } else {
        return "List<" + returnType + ">";
      }
    } else if ((boolean) response.get("isPaginated")) {
      if ((boolean) response.get("isEntity")) {
        if (returnType.contains("Entity")) {
          return "Page<" + returnType + ">";
        } else {
          return "Page<" + returnType + "Entity>";
        }
      } else {
        return "Page<" + returnType + ">";
      }
    } else {
      return returnType;
    }
  }

  /**
   * Returns the Java type corresponding to the OpenAPI type definition. If the type could not be matched, Object will
   * be returned.
   *
   * @param parameter OpenAPI model of a parameter
   * @param simpleType if a Java simple type should be returned if possible
   * @return the Java type
   */
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
        return "List<" + parameter.get("type") + ">";
      } else {
        return "List<" + typeConverted + ">";
      }
    } else if (isEntity) {
      return (String) parameter.get("type");
    }
    return typeConverted;
  }

  /**
   * Returns the TypeScript type corresponding to the OpenAPI type definition. If the type could not be matched, the
   * same value will be returned.
   *
   * @param parameter OpenAPI model of a parameter
   * @return the Java type
   */
  // getOaspTypeFromOpenAPI
  public String toTypeScriptType(Map<String, Object> parameter) {

    String typeConverted = null;
    String type = (String) parameter.get("type");
    boolean isCollection = false;
    if (parameter.get("isCollection") != null) {
      isCollection = (boolean) parameter.get("isCollection");
    }

    boolean isEntity = (boolean) parameter.get("isEntity");

    if (type != null) {
      switch (type.toLowerCase()) {
        case "integer":
          typeConverted = "number";
          break;
        default:
          typeConverted = type;
          break;
      }
    } else {
      typeConverted = "undefined";
    }

    if (isCollection) {
      if (isEntity) {
        return parameter.get("type") + "[]";
      } else {
        return typeConverted + "[]";
      }
    } else if (isEntity) {
      return (String) parameter.get("type");
    }
    return typeConverted;
  }

  /**
   * Prints the service operation name based on the operationId or generates one based on the servicePath while printing
   * a comment how to get better service names.
   *
   * @param operation operation Model of the OpenAPI model
   * @param servicePath service path of the
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
      return ((String) operation.get("type")) + lastSegment;
    } else {
      return operationId;
    }
  }
}
