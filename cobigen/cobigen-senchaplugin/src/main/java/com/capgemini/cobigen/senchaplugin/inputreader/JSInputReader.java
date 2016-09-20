package com.capgemini.cobigen.senchaplugin.inputreader;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.senchaplugin.util.freemarkerutil.IsAbstractMethod;
import com.capgemini.cobigen.senchaplugin.util.freemarkerutil.IsSubtypeOfMethod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * Extension for the {@link InputReader} Interface of the CobiGen, to be able to read Java classes into
 * FreeMarker models
 *
 * @author mbrunnli (15.10.2013)
 */
public class JSInputReader implements InputReader {

    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(JSInputReader.class);

    @Override
    public boolean isValidInput(Object input) {

        if (input instanceof Class<?> || input instanceof JavaClass) {
            return true;
        } else if (input instanceof Object[]) {
            // check whether the same Java class has been provided as parser as well as reflection object
            Object[] inputArr = (Object[]) input;
            if (inputArr.length == 2) {
                if (inputArr[0] instanceof JavaClass && inputArr[1] instanceof Class<?>) {
                    if (((JavaClass) inputArr[0]).getFullyQualifiedName()
                        .equals(((Class<?>) inputArr[1]).getCanonicalName())) {
                        return true;
                    }
                } else if (inputArr[0] instanceof Class<?> && inputArr[1] instanceof JavaClass) {
                    if (((Class<?>) inputArr[0]).getCanonicalName()
                        .equals(((JavaClass) inputArr[1]).getFullyQualifiedName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> createModel(Object o) {

        System.out.println("entr una vez");
        Map<String, Object> model = new HashMap<>();
        if (o instanceof Class<?>) {
            model = new ReflectedJavaModelBuilder().createModel((Class<?>) o);
            model.put(ModelConstant.MODELID, createRandomString(32));
            model.put(ModelConstant.CONTROLLERID, createRandomString(32));
            return model;
        }
        if (o instanceof JavaClass) {
            model = new ParsedJavaModelBuilder().createModel((JavaClass) o);
            model.put(ModelConstant.MODELID, createRandomString(32));
            model.put(ModelConstant.CONTROLLERID, createRandomString(32));
            return model;
        }
        if (o instanceof Object[] && isValidInput(o)) {
            Object[] inputArr = (Object[]) o;
            Object parsedModel;
            Object reflectionModel;
            if (inputArr[0] instanceof JavaClass) {
                parsedModel = new ParsedJavaModelBuilder().createModel((JavaClass) inputArr[0]);
                reflectionModel = new ReflectedJavaModelBuilder().createModel((Class<?>) inputArr[1]);
            } else {
                parsedModel = new ParsedJavaModelBuilder().createModel((JavaClass) inputArr[1]);
                reflectionModel = new ReflectedJavaModelBuilder().createModel((Class<?>) inputArr[0]);
            }
            model = (Map<String, Object>) mergeModelsRecursively(parsedModel, reflectionModel);
            model.put(ModelConstant.MODELID, createRandomString(32));
            model.put(ModelConstant.CONTROLLERID, createRandomString(32));
            return model;
        }
        return null;
    }

    /**
     * Generates random hexadecimal ID for Architect objects
     * @param length
     *            of the ID
     * @return id
     * @author rudiazma (Sep 19, 2016)
     */
    public static String createRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        String id = sb.toString();
        String resultId = id.substring(0, 8) + '-' + id.substring(8, 12) + '-' + id.substring(12, 16) + '-'
            + id.substring(16, 20) + '-' + id.substring(20, 32);
        return resultId;
    }

    @Override
    public boolean combinesMultipleInputObjects(Object input) {
        return false;
    }

    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        List<Object> emptyList = new LinkedList<>();
        return emptyList;
    }

    @Override
    public Map<String, Object> getTemplateMethods(Object input) {

        // prepare result
        Map<String, Object> methodMap = new HashMap<>();
        ClassLoader classloader = null;

        // find corresponding ClassLoader dependent on input type
        if (isValidInput(input)) {

            if (input instanceof JavaClass) {
                // currently it is not possible to access the classloader of an instance of JavaClass,
                // therefore we pass the dafault classloader here.
                classloader = getClass().getClassLoader();
            } else if (input instanceof Object[]) {
                Object[] inputArr = (Object[]) input;
                if ((inputArr[0] instanceof JavaClass) && (inputArr[1] instanceof Class<?>)) {
                    classloader = ((Class<?>) inputArr[1]).getClassLoader();
                } else if ((inputArr[0] instanceof Class<?>) && (inputArr[1] instanceof JavaClass)) {
                    classloader = ((Class<?>) inputArr[0]).getClassLoader();
                }
            } else if (input instanceof Class<?>) {
                classloader = ((Class<?>) input).getClassLoader();
            }
        }

        // create result
        if (classloader == null) {
            throw new IllegalArgumentException(
                "There is no ClassLoader for the given input. Perhaps you used a bootstrap class (e.g.java.lang.String) as input which is not supported");
        }
        methodMap.put("isAbstract", new IsAbstractMethod(classloader));
        methodMap.put("isSubtypeOf", new IsSubtypeOfMethod(classloader));
        return methodMap;
    }

    /**
     * Merges two models recursively. The current implementation only merges Lists and Maps recursively.
     * Structures will be merged as follows:<br>
     * <table>
     * <tr>
     * <td>Maps:</td>
     * <td>equal map entries will be discovered and the values will be merged recursively</td>
     * </tr>
     * <tr>
     * <td>Lists:</td>
     * <td>Lists will only be handled if their elements are {@link Map Maps}. If so, the {@link Map Maps} will
     * be compared due to their {@link ModelConstant#NAME} value. If equal, the elements will be recursively
     * merged.</td>
     * </tr>
     * </table>
     *
     * @param parsedModel
     *            model created by parsing to be merged and preferred in case of conflicts
     * @param reflectionModel
     *            model created by reflection to be merged
     * @return the merged model. Due to implementation restrictions a {@link Map} of {@link String} to
     *         {@link Object}
     * @author mbrunnli (17.11.2014)
     */
    @SuppressWarnings("unchecked")
    private Object mergeModelsRecursively(Object parsedModel, Object reflectionModel) {

        if (parsedModel == null && reflectionModel == null) {
            return null;
        } else if (parsedModel == null) {
            return reflectionModel;
        } else if (reflectionModel == null) {
            return parsedModel;
        } else if (parsedModel.equals(reflectionModel)) {
            return parsedModel;
        }

        if (parsedModel.getClass().equals(reflectionModel.getClass())) {
            if (parsedModel instanceof Map && reflectionModel instanceof Map) {
                Map<String, Object> mergedModel = Maps.newHashMap();
                Map<String, Object> model1Map = (Map<String, Object>) parsedModel;
                Map<String, Object> model2Map = (Map<String, Object>) reflectionModel;

                Set<String> union = Sets.newHashSet(model1Map.keySet());
                union.addAll(model2Map.keySet());
                for (String unionKey : union) {
                    if (model1Map.containsKey(unionKey) && model2Map.containsKey(unionKey)) {
                        // Recursively merge equal keys
                        mergedModel.put(unionKey,
                            mergeModelsRecursively(model1Map.get(unionKey), model2Map.get(unionKey)));
                    } else if (model1Map.containsKey(unionKey)) {
                        mergedModel.put(unionKey, model1Map.get(unionKey));
                    } else {
                        mergedModel.put(unionKey, model2Map.get(unionKey));
                    }
                }
                return mergedModel;
            }
            // Case: List<Map<String, Object>> available in attributes and methods
            else if (parsedModel instanceof List && reflectionModel instanceof List) {
                if (!((List<?>) parsedModel).isEmpty() && ((List<?>) parsedModel).get(0) instanceof Map
                    || !((List<?>) reflectionModel).isEmpty() && ((List<?>) reflectionModel).get(0) instanceof Map) {
                    List<Map<String, Object>> model1List = Lists.newLinkedList((List<Map<String, Object>>) parsedModel);
                    List<Map<String, Object>> model2List =
                        Lists.newLinkedList((List<Map<String, Object>>) reflectionModel);
                    List<Object> mergedModel = Lists.newLinkedList();

                    // recursively merge list entries. Match them by name attribute. This is currently valid
                    // and might be adapted if there are greater model changes in future
                    Iterator<Map<String, Object>> model1ListIt = model1List.iterator();
                    while (model1ListIt.hasNext()) {
                        Map<String, Object> model1Entry = model1ListIt.next();
                        Iterator<Map<String, Object>> model2ListIt = model2List.iterator();
                        while (model2ListIt.hasNext()) {
                            Map<String, Object> model2Entry = model2ListIt.next();
                            // valid merging for fields and methods
                            if (model1Entry.get(ModelConstant.NAME) != null) {
                                if (model1Entry.get(ModelConstant.NAME).equals(model2Entry.get(ModelConstant.NAME))) {
                                    mergedModel.add(mergeModelsRecursively(model1Entry, model2Entry));

                                    // remove both entries as they have been matched and recursively merged
                                    model1ListIt.remove();
                                    model2ListIt.remove();
                                    break;
                                }
                            } else
                            // this is the case for merging recursive annotation arrays
                            if (model1Entry.size() == 1 && model2Entry.size() == 1) {
                                mergeModelsRecursively(model1Entry.get(model1Entry.keySet().iterator().next()),
                                    model2Entry.get(model2Entry.keySet().iterator().next()));
                            } else {
                                throw new IllegalStateException(
                                    "Anything unintended happened. Please state an issue at GitHub or mail one of the developers");
                            }
                        }
                    }

                    // append not matched entries from list1 and list2
                    mergedModel.addAll(model1List);
                    mergedModel.addAll(model2List);
                    return mergedModel;
                }
                // we will prefer parsed model if the values of the parsed result list are of type String.
                // This is the case for annotation values. QDox will always return the expression,
                // which is a assigned to the annotation's value, as a string.
                else if (!((List<?>) parsedModel).isEmpty() && ((List<?>) parsedModel).get(0) instanceof String) {
                    return parsedModel;
                } else {
                    if (reflectionModel instanceof Object[]) {
                        return Lists.newLinkedList(Arrays.asList(reflectionModel));
                    } else {
                        return reflectionModel;
                    }
                }
            } else {
                // any other type might not be merged. As the values are not equal, this might be a conflict,
                // so take model1 as documented
                return parsedModel;
            }
        } else if (parsedModel instanceof String[]) {
            return Lists.newLinkedList(Arrays.asList(parsedModel));
        }
        // we will prefer parsed model if parsed value of type String. This is the case for annotation values.
        // QDox will always return the expression, which is a assigned to the annotation's value, as a string.
        else {
            return parsedModel;
        }
    }

    @Override
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {
        return null;
    }

}
