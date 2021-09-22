package com.devonfw.cobigen.templates.devon4j.utils.documentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 *
 */
public class JavaDocumentationUtil {

  /** Full qualified name of spring RequestMapping annotation */
  private final String requestMapping = "org_springframework_web_bind_annotation_RequestMapping";

  /** Full qualified name of javax Path annotation */
  private final String javaxPath = "javax_ws_rs_Path";

  /**
   * Creates a list of parameters of a specific method as an asciidoc string, including its name, type, description,
   * constraints
   *
   * @param pojoClass {@link Class}&lt;?&gt; the class object of the pojo
   * @param methodName {@link String} the name of the method to get the parameter info of
   * @param javaDoc the javadoc of the method, taken from the javaplugin model
   * @return A list of the parameters info in asciidoc code
   * @throws Exception When errors occur in invoking an annotations method
   */
  public String getParams(Class<?> pojoClass, String methodName, Map<String, Object> javaDoc) throws Exception {

    String result = "";
    Method m = findMethod(pojoClass, methodName);
    if (m.getParameterCount() < 1) {
      return "!-!-!-!-";
    }
    if (m.getParameterCount() == 1) {
      if (!m.getParameters()[0].getType().isPrimitive()) {
        return "!-!-!-!-";
      }
    }
    for (Parameter param : m.getParameters()) {
      // Add the name of the parameter as path or query parameter
      boolean isPath = param.isAnnotationPresent(javax.ws.rs.PathParam.class);
      boolean isQuery = param.isAnnotationPresent(javax.ws.rs.QueryParam.class);
      if (isPath || isQuery) {
        result += "!";
        if (isPath) {
          result += "{" + param.getAnnotation(javax.ws.rs.PathParam.class).value() + "}" + System.lineSeparator();
        } else if (isQuery) {
          result += "?" + param.getAnnotation(javax.ws.rs.QueryParam.class).value() + System.lineSeparator();
        }

        // Add the type
        String type = param.getType().getSimpleName();
        result += "!" + type + System.lineSeparator();

        // Add the constraints
        result += "!";
        int counter = 0;
        for (Annotation anno : param.getAnnotations()) {
          String annoName = anno.annotationType().getName();
          Pattern p = Pattern.compile("javax\\.validation\\.constraints\\.([^\\.]*)");
          Matcher match = p.matcher(annoName);
          if (match.find()) {
            counter++;
            String shortName = annoName.substring(annoName.lastIndexOf('.') + 1);
            Object value;
            Method method;
            try {
              method = anno.getClass().getMethod("value");
              value = method.invoke(anno);
              result += shortName + " = " + value + " +" + System.lineSeparator();
            } catch (NoSuchMethodException e) {
              result += shortName + " +" + System.lineSeparator();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | SecurityException e) {
              throw new Exception(e.getMessage());
            }
          }
        }
        if (counter == 0) {
          result += "-" + System.lineSeparator();
        }

        // Add Javadoc
        Map<String, String> params = (Map<String, String>) javaDoc.get("params");
        result += "!" + getJavaDocWithoutLink(params.get(param.getName())) + " +" + System.lineSeparator();
      }
    }
    return result;
  }

  /**
   * returns the javadoc of an element, stripped of any links to other sources
   *
   * @param doc the javadoc to be changed
   * @return the input string stripped of all links
   */
  public String getJavaDocWithoutLink(String doc) {

    Pattern p = Pattern.compile("(\\{@link ([^\\}]*)\\})");
    Matcher m = p.matcher(doc);
    while (m.find()) {
      doc = doc.replace(m.group(1), m.group(2));
    }
    return doc;
  }

  /**
   * Create a response in JSON format, iterating through non-primitive types to get their data as well
   *
   * @param pojoClass The input class
   * @param methodName The name of the operation to get the response of
   * @return A JSON representation of the response object
   * @throws Exception When Jackson fails
   */
  public String getJSONResponseBody(Class<?> pojoClass, String methodName) throws Exception {

    Class<?> responseType = findMethod(pojoClass, methodName).getReturnType();
    if (hasBody(pojoClass, methodName, true)) {
      return getJSON(responseType);
    }
    return "-";
  }

  /**
   * Create a request in JSON format, iterating through non-primitive types to get their data as well
   *
   * @param pojoClass The input class
   * @param methodName The name of the operation to get the request of
   * @return A JSON representation of the request object
   * @throws Exception When Jackson fails
   */
  public String getJSONRequestBody(Class<?> pojoClass, String methodName) throws Exception {

    Method m = findMethod(pojoClass, methodName);
    if (hasBody(pojoClass, methodName, false)) {
      Parameter param = m.getParameters()[0];
      Class<?> requestType = param.getType();
      return getJSON(requestType);
    }
    return "-";
  }

  /**
   * Using Jackson, creates a JSON string for Asciidoc
   *
   * @param clazz The class to create the JSON string of
   * @return A JSON representation of the given class
   * @throws Exception When Jackson fails
   */
  public String getJSON(Class<?> clazz) throws Exception {

    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    try {
      Object obj = clazz.newInstance();
      return "...." + System.lineSeparator() + mapper.writeValueAsString(obj) + System.lineSeparator() + "....";
    } catch (InstantiationException | IllegalAccessException | JsonProcessingException e) {
      throw new Exception(e.getMessage());
    }
  }

  /**
   * Checks if a request/response has a body
   *
   * @param pojoClass {@link Class}&lt;?&gt; the class object of the pojo
   * @param methodName The name of the operation to be checked
   * @param isResponse true if the response of the operation should be checked, false if the request should be checked
   * @return true if the response/request has a body, false if not
   * @throws SecurityException If no method of the given name can be found
   */
  public boolean hasBody(Class<?> pojoClass, String methodName, boolean isResponse) throws SecurityException {

    Method m = findMethod(pojoClass, methodName);
    if (isResponse) {
      Class<?> returnType = m.getReturnType();
      if (!returnType.isPrimitive() && !returnType.equals(Void.TYPE)) {
        return true;
      }
    } else {
      int nr = 0;
      int position = 0;
      for (Parameter param : m.getParameters()) {
        if (!param.isAnnotationPresent(javax.ws.rs.PathParam.class)
            && !param.isAnnotationPresent(javax.ws.rs.QueryParam.class)) {
          nr++;
          position = Integer.parseInt(param.getName().replace("arg", ""));
        }
      }
      if (nr == 1) {
        Parameter param = m.getParameters()[position];
        Class<?> requestType = param.getType();
        if (!requestType.isPrimitive() && !requestType.equals(Void.TYPE)
            && !param.isAnnotationPresent(javax.ws.rs.PathParam.class)
            && !param.isAnnotationPresent(javax.ws.rs.QueryParam.class)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * returns the HTTP request type corresponding to an annotation type
   *
   * @param annotations The annotation to get the type name of
   * @return the HTTP request type name of the selected annotation
   */
  public String getRequestType(Map<String, Object> annotations) {

    String type = "";
    if (annotations.containsKey(this.requestMapping)) {
      Map<String, Object> method = (Map<String, Object>) annotations.get(this.requestMapping);
      String rm = (String) method.get("method");
      type = rm.toLowerCase();
    }
    if (annotations.containsKey("javax_ws_rs_GET") || type.equals("requestmethod.get")) {
      return "GET";
    } else if (annotations.containsKey("javax_ws_rs_PUT") || type.equals("requestmethod.put")) {
      return "PUT";
    } else if (annotations.containsKey("javax_ws_rs_POST") || type.equals("requestmethod.post")) {
      return "POST";
    } else if (annotations.containsKey("javax_ws_rs_DELETE") || type.equals("requestmethod.delete")) {
      return "DELETE";
    } else if (annotations.containsKey("javax_ws_rs_PATCH") || type.equals("requestmethod.patch")) {
      return "PATCH";
    } else {
      return "-";
    }
  }

  /**
   * Gets the path of an operation
   *
   * @param pojoAnnotations the annotation map of the given pojo
   * @param method The method to get the operation path of
   * @return The path of an operation
   * @throws IOException If no application_properties file is found
   */
  public String getOperationPath(Map<String, Object> pojoAnnotations, Map<String, Object> method) throws IOException {

    String path = getPath(pojoAnnotations);
    Map<String, Object> pathAnno = new HashMap<>();
    if (pojoAnnotations.containsKey(this.javaxPath)) {
      pathAnno = (Map<String, Object>) pojoAnnotations.get(this.javaxPath);
    } else if (pojoAnnotations.containsKey(this.requestMapping)) {
      pathAnno = (Map<String, Object>) pojoAnnotations.get(this.requestMapping);
    }
    if (pathAnno.containsKey("value")) {
      String toAdd = (String) pathAnno.get("value");
      if (toAdd.startsWith("/") && path.endsWith("/")) {
        path += toAdd.substring(1);
      }
    }
    return path;
  }

  /**
   * Gets the path of a component
   *
   * @param pojoAnnotations the annotation map of the given pojo
   * @return The communal path of a component
   * @throws IOException If no application_properties file is found
   */
  public String getPath(Map<String, Object> pojoAnnotations) throws IOException {

    String path = extractRootPath();
    Map<String, Object> pathAnno = new HashMap<>();
    if (pojoAnnotations.containsKey(this.javaxPath)) {
      pathAnno = (Map<String, Object>) pojoAnnotations.get(this.javaxPath);
    } else if (pojoAnnotations.containsKey(this.requestMapping)) {
      pathAnno = (Map<String, Object>) pojoAnnotations.get(this.requestMapping);
    }
    if (pathAnno.containsKey("value")) {
      String toAdd = (String) pathAnno.get("value");
      if (toAdd.startsWith("/") && path.endsWith(":")) {
        path += toAdd.substring(1);
      }
    }
    return path;
  }

  /**
   * Checks the class path for an application.properties file and extracts the port and path from it
   *
   * @return The root path of the application
   * @throws IOException If no application.properties file could be found
   */
  private String extractRootPath() throws IOException {

    Class<?> clazz = this.getClass();
    String t = "";
    StringBuilder sb = new StringBuilder("http://localhost:");
    try (InputStream in = clazz.getClassLoader().getResourceAsStream("application.properties")) {
      if (in != null) {
        try (InputStreamReader reader = new InputStreamReader(in); BufferedReader br = new BufferedReader(reader)) {
          while ((t = br.readLine()) != null) {
            if (t.matches("server\\.port=(\\d{0,5})") || t.matches("server\\.context-path=([^\\s]*)")) {
              sb.append(t.substring(t.indexOf('=') + 1));
            }
          }
          return sb.toString();
        }
      } else {
        return "";
      }
    }
  }

  /**
   * Helper method to find a class's specific method
   *
   * @param pojoClass {@link Class}&lt;?&gt; the class object of the pojo
   * @param methodName The name of the method to be found
   * @return The method object of the method to be found, null if it wasn't found
   */
  private Method findMethod(Class<?> pojoClass, String methodName) {

    if (pojoClass == null) {
      throw new IllegalAccessError(
          "Class object is null. Cannot generate template as it might obviously depend on reflection.");
    }
    for (Method m : pojoClass.getMethods()) {
      if (m.getName().equals(methodName)) {
        return m;
      }
    }
    return null;
  }
}
