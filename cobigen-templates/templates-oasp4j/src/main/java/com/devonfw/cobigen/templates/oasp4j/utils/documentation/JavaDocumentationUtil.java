package com.devonfw.cobigen.templates.oasp4j.utils.documentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

    /**
     * Creates a list of parameters of a specific method as an asciidoc string, including its name, type,
     * description, constraints
     * @param pojoClass
     *            {@link Class}&lt;?> the class object of the pojo
     * @param methodName
     *            {@link String} the name of the method to get the parameter info of
     * @param javaDoc
     *            the javadoc of the method, taken from the javaplugin model
     * @return A list of the parameters info in asciidoc code
     * @throws Exception
     *             When errors occur in invoking an annotations method
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
                    result +=
                        "{" + param.getAnnotation(javax.ws.rs.PathParam.class).value() + "}" + System.lineSeparator();
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
     * @param doc
     *            the javadoc to be changed
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
     * @param pojoClass
     *            The input class
     * @param methodName
     *            The name of the operation to get the response of
     * @return A JSON representation of the response object
     * @throws Exception
     *             When Jackson fails
     */
    public String getJSONResponseBody(Class<?> pojoClass, String methodName) throws Exception {
        Class<?> responseType = findMethod(pojoClass, methodName).getReturnType();
        if (hasBody(pojoClass, methodName, true)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                Object obj = responseType.newInstance();
                return "...." + System.lineSeparator() + mapper.writeValueAsString(obj) + System.lineSeparator()
                    + "....";
            } catch (InstantiationException | IllegalAccessException | JsonProcessingException e) {
                throw new Exception(e.getMessage());
            }
        }
        return "-";
    }

    /**
     * Create a request in JSON format, iterating through non-primitive types to get their data as well
     * @param pojoClass
     *            The input class
     * @param methodName
     *            The name of the operation to get the request of
     * @return A JSON representation of the request object
     * @throws Exception
     *             When Jackson fails
     */
    public String getJSONRequestBody(Class<?> pojoClass, String methodName) throws Exception {
        Method m = findMethod(pojoClass, methodName);
        if (hasBody(pojoClass, methodName, false)) {
            Parameter param = m.getParameters()[0];
            Class<?> requestType = param.getType();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                Object obj = requestType.newInstance();
                return "...." + System.lineSeparator() + mapper.writeValueAsString(obj) + System.lineSeparator()
                    + "....";
            } catch (InstantiationException | IllegalAccessException | JsonProcessingException e) {
                throw new Exception(e.getMessage());
            }
        }
        return "-";
    }

    /**
     * Checks if a request/response has a body
     * @param pojoClass
     *            {@link Class}&lt;?> the class object of the pojo
     * @param methodName
     *            The name of the operation to be checked
     * @param isResponse
     *            true if the response of the operation should be checked, false if the request should be
     *            checked
     * @return true if the response/request has a body, false if not
     * @throws SecurityException
     *             If no method of the given name can be found
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
     * Checks the class path for an application.properties file and extracts the port and path from it
     * @param pojoClass
     *            {@link Class}&lt;?> the class object of the pojo
     * @return The root path of the application
     * @throws IOException
     *             If no application.properties file could be found
     */
    public String extractRootPath(Class<?> pojoClass) throws IOException {

        if (pojoClass == null) {
            throw new IllegalAccessError(
                "Class object is null. Cannot generate template as it might obviously depend on reflection.");
        }
        String t = "";
        StringBuilder sb = new StringBuilder("http://localhost:");
        InputStream in = pojoClass.getClassLoader().getResourceAsStream("application.properties");
        if (in != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((t = br.readLine()) != null) {
                if (t.matches("server\\.port=(\\d{0,5})") || t.matches("server\\.context-path=([^\\s]*)")) {
                    sb.append(t.substring(t.indexOf('=') + 1));
                }
            }
            return sb.toString();
        } else {
            throw new IOException("application.properties file not found!");
        }
    }

    /**
     * Helper method to find a class's specific method
     * @param pojoClass
     *            {@link Class}&lt;?> the class object of the pojo
     * @param methodName
     *            The name of the method to be found
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
