package com.devonfw.cobigen.javaplugin.inputreader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.devonfw.cobigen.api.util.StringUtil;
import com.devonfw.cobigen.javaplugin.model.ModelConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;

/** The {@link ParsedJavaModelBuilder} builds a model using QDox as a Java parser */
public class ParsedJavaModelBuilder {

    /** Cached input pojo class in order to avoid unnecessary efforts */
    private JavaClass cachedPojo;

    /** Cached model related to the cached input pojo */
    private Map<String, Object> cachedModel;

    /**
     * Creates the object model for the template instantiation.
     *
     * @param javaClass
     *            {@link Class} object of the pojo all information should be retrieved from
     * @return A {@link Map} of a {@link String} key to {@link Object} mapping keys as described before to the
     *         corresponding information. Learn more about the FreeMarker data model at http
     *         ://freemarker.sourceforge.net/docs/dgui_quickstart.html
     */
    Map<String, Object> createModel(final JavaClass javaClass) {

        if (cachedPojo != null && cachedPojo.equals(javaClass)) {
            return new HashMap<>(cachedModel);
        }
        cachedPojo = javaClass;

        cachedModel = new HashMap<>();
        Map<String, Object> pojoModel = new HashMap<>();
        pojoModel.put(ModelConstant.NAME, javaClass.getName());
        if (javaClass.getPackage() != null) {
            pojoModel.put(ModelConstant.PACKAGE, javaClass.getPackage().getName());
        } else {
            pojoModel.put(ModelConstant.PACKAGE, "");
        }
        pojoModel.put(ModelConstant.CANONICAL_NAME, javaClass.getCanonicalName());

        Map<String, Object> javaDoc = extractJavaDoc(javaClass);
        if (javaDoc != null) {
            pojoModel.put(ModelConstant.JAVADOC, javaDoc);
        }

        Map<String, Object> annotations = new HashMap<>();
        extractAnnotationsRecursively(annotations, javaClass.getAnnotations());
        pojoModel.put(ModelConstant.ANNOTATIONS, annotations);

        List<Map<String, Object>> fields = extractFields(javaClass);
        pojoModel.put(ModelConstant.FIELDS_DEPRECATED, fields);
        pojoModel.put(ModelConstant.FIELDS, fields);
        determinePojoIds(javaClass, fields);
        collectAnnotations(javaClass, fields);

        List<Map<String, Object>> accessibleAttributes = extractMethodAccessibleFields(javaClass);
        pojoModel.put(ModelConstant.METHOD_ACCESSIBLE_FIELDS, accessibleAttributes);
        determinePojoIds(javaClass, accessibleAttributes);
        collectAnnotations(javaClass, accessibleAttributes);

        Map<String, Object> superclass = extractSuperclass(javaClass);
        pojoModel.put(ModelConstant.EXTENDED_TYPE, superclass);

        List<Map<String, Object>> interfaces = extractInterfaces(javaClass);
        pojoModel.put(ModelConstant.IMPLEMENTED_TYPES, interfaces);

        pojoModel.put(ModelConstant.METHODS, extractMethods(javaClass));
        cachedModel.put(ModelConstant.MODEL_ROOT, pojoModel);

        return new HashMap<>(cachedModel);
    }

    /**
     * Extracts all fields from the given pojo, which are visible by using setter and getter methods
     * @param javaClass
     *            source {@link JavaClass} to determine all fields accessible via methods from
     * @return a list of field properties equivalently to {@link #extractFields(JavaClass)}
     */
    private List<Map<String, Object>> extractMethodAccessibleFields(JavaClass javaClass) {
        List<Map<String, Object>> fields = Lists.newLinkedList();

        List<BeanProperty> beanProperties = javaClass.getBeanProperties(true);
        for (BeanProperty property : beanProperties) {
            if (property.getAccessor() != null && property.getMutator() != null) {
                fields.add(extractField(property.getName(), property.getType(), null));
            }
        }
        return fields;
    }

    /**
     * Extracts all methods and the method properties for the model
     *
     * @param javaClass
     *            input java class
     * @return a {@link List} of methods mapping each property to its value
     */
    private List<Map<String, Object>> extractMethods(JavaClass javaClass) {

        List<Map<String, Object>> methods = new LinkedList<>();
        for (JavaMethod method : javaClass.getMethods()) {
            Map<String, Object> methodAttributes = new HashMap<>();
            methodAttributes.put(ModelConstant.NAME, method.getName());
            if (method.getComment() != null) {
                Map<String, Object> javaDoc = extractJavaDoc(method);
                if (javaDoc != null) {
                    methodAttributes.put(ModelConstant.JAVADOC, javaDoc);
                }
            }
            Map<String, Object> annotations = new HashMap<>();
            extractAnnotationsRecursively(annotations, method.getAnnotations());
            methodAttributes.put(ModelConstant.ANNOTATIONS, annotations);
            methods.add(methodAttributes);
        }
        return methods;
    }

    /**
     * Extracts the attributes from the given POJO
     *
     * @param pojo
     *            {@link Class} object of the POJO the data should be retrieved from
     * @return a {@link Set} of attributes, where each attribute is represented by a {@link Map} of a
     *         {@link String} key to the corresponding {@link String} value of meta information
     */
    private List<Map<String, Object>> extractFields(JavaClass pojo) {

        List<Map<String, Object>> fields = new LinkedList<>();
        for (JavaField f : pojo.getFields()) {
            if (f.isStatic()) {
                continue;
            }
            fields.add(extractField(f.getName(), f.getType(), f));
        }
        return fields;
    }

    /**
     * Extracts all properties needed for model building from a given field
     * @param fieldName
     *            the field's name
     * @param field
     *            the values should be extracted for
     * @param annotatedElement
     *            Annotated Element the field type is source of
     * @return the mapping of property names to their values
     */
    private Map<String, Object> extractField(String fieldName, JavaType field, JavaAnnotatedElement annotatedElement) {
        Map<String, Object> fieldValues = new HashMap<>();
        fieldValues.put(ModelConstant.NAME, fieldName);
        // currently there is a problem with qDox. It provides the canonical type for supertype fields when
        // calling "field.getGenericValue()". Thats why we need the JavaParserUtil here.
        fieldValues.put(ModelConstant.TYPE, JavaParserUtil.resolveToSimpleType(field.getGenericValue()));
        fieldValues.put(ModelConstant.CANONICAL_TYPE, field.getGenericCanonicalName());

        if (annotatedElement != null) {
            Map<String, Object> javaDoc = extractJavaDoc(annotatedElement);
            if (javaDoc != null) {
                fieldValues.put(ModelConstant.JAVADOC, javaDoc);
            }
        }

        return fieldValues;
    }

    /**
     * Extracts the superclass from the given POJO
     *
     * @param pojo
     *            {@link Class} object of the POJO the super type should be retrieved from
     * @return the super type, represented by a {@link Map} of a {@link String} key to the corresponding
     *         {@link String} value of meta information or {@code null} if no superclass exist
     */
    private Map<String, Object> extractSuperclass(JavaClass pojo) {

        Map<String, Object> superclassModel = new HashMap<>();

        JavaClass superclass = pojo.getSuperJavaClass();
        if (superclass != null) {
            superclassModel.put(ModelConstant.NAME, superclass.getName());
            superclassModel.put(ModelConstant.CANONICAL_NAME, superclass.getCanonicalName());
            if (superclass.getPackage() != null) {
                superclassModel.put(ModelConstant.PACKAGE, superclass.getPackage().getName());
            } else {
                superclassModel.put(ModelConstant.PACKAGE, "");
            }

            Map<String, Object> javaDoc = extractJavaDoc(superclass);
            if (javaDoc != null) {
                superclassModel.put(ModelConstant.JAVADOC, javaDoc);
            }
            return superclassModel;
        } else {
            return null;
        }
    }

    /**
     * Extracts the implementedTypes (interfaces) from the given POJO
     *
     * @param pojo
     *            {@link Class} object of the POJO the interfaces should be retrieved from
     * @return a {@link Set} of implementedTypes (interfaces), where each is represented by a {@link Map} of a
     *         {@link String} key to the corresponding {@link String} value of meta information
     */
    private List<Map<String, Object>> extractInterfaces(JavaClass pojo) {

        List<Map<String, Object>> interfaceList = new LinkedList<>();
        for (JavaClass c : pojo.getInterfaces()) {
            Map<String, Object> interfaceModel = new HashMap<>();
            interfaceModel.put(ModelConstant.NAME, c.getName());
            interfaceModel.put(ModelConstant.CANONICAL_NAME, c.getCanonicalName());
            if (c.getPackage() != null) {
                interfaceModel.put(ModelConstant.PACKAGE, c.getPackage().getName());
            } else {
                interfaceModel.put(ModelConstant.PACKAGE, "");
            }

            Map<String, Object> javaDoc = extractJavaDoc(c);
            if (javaDoc != null) {
                interfaceModel.put(ModelConstant.JAVADOC, javaDoc);
            }
            interfaceList.add(interfaceModel);
        }

        return interfaceList;
    }

    /**
     * Collect all annotations for the given pojo from setter and getter methods by searching using the
     * attribute names. Annotation information retrieved from the setter and getter methods will be added the
     * the corresponding attribute meta data
     *
     * @param javaClass
     *            class for which the setter and getter should be evaluated according to their annotations
     * @param attributes
     *            list of attribute meta data for the generation (object model)
     */
    private void collectAnnotations(JavaClass javaClass, List<Map<String, Object>> attributes) {

        for (Map<String, Object> attr : attributes) {
            Map<String, Object> annotations = new HashMap<>();
            attr.put(ModelConstant.ANNOTATIONS, annotations);
            JavaField classField = javaClass.getFieldByName((String) attr.get(ModelConstant.NAME));

            if (classField != null) {
                extractAnnotationsRecursively(annotations, classField.getAnnotations());
            }

            JavaMethod getter =
                javaClass.getMethod("get" + StringUtils.capitalize((String) attr.get(ModelConstant.NAME)), null, false);
            if (getter != null) {
                extractAnnotationsRecursively(annotations, getter.getAnnotations());
            }

            getter =
                javaClass.getMethod("is" + StringUtils.capitalize((String) attr.get(ModelConstant.NAME)), null, false);
            if (getter != null) {
                extractAnnotationsRecursively(annotations, getter.getAnnotations());
            }

            List<JavaType> paramList = null;
            if (classField != null) {
                JavaType attrType = classField.getType();
                paramList = new ArrayList<>();
                paramList.add(attrType);
            }
            JavaMethod setter = javaClass
                .getMethod("set" + StringUtils.capitalize((String) attr.get(ModelConstant.NAME)), paramList, false);
            if (setter != null) {
                extractAnnotationsRecursively(annotations, setter.getAnnotations());
            }
        }
    }

    /**
     * Extracts all information of the given annotations recursively and writes them into the object model
     * (annotationsMap)
     *
     * @param annotationsMap
     *            object model for annotations
     * @param annotations
     *            to be analyzed
     */
    @SuppressWarnings("unchecked")
    private void extractAnnotationsRecursively(Map<String, Object> annotationsMap, List<JavaAnnotation> annotations) {

        for (JavaAnnotation annotation : annotations) {
            Map<String, Object> annotationParameters = new HashMap<>();
            annotationsMap.put(annotation.getType().getCanonicalName().replaceAll("\\.", "_"), annotationParameters);

            for (String propertyName : annotation.getPropertyMap().keySet()) {
                Object value = annotation.getNamedParameter(propertyName);
                if (value instanceof List<?> && ((List<?>) value).size() > 0
                    && ((List<?>) value).get(0) instanceof JavaAnnotation) {
                    List<Map<String, Object>> recursiveAnnotationList = Lists.newLinkedList();
                    annotationParameters.put(propertyName, recursiveAnnotationList);
                    for (JavaAnnotation a : (List<JavaAnnotation>) value) {
                        Map<String, Object> annotationParameterParameters = new HashMap<>();
                        extractAnnotationsRecursively(annotationParameterParameters, Lists.newArrayList(a));
                        recursiveAnnotationList.add(annotationParameterParameters);
                    }
                } else if (value instanceof Enum<?>[]) {
                    List<String> enumValues = Lists.newLinkedList();
                    for (Enum<?> e : ((Enum<?>[]) value)) {
                        enumValues.add(e.name());
                    }
                    annotationParameters.put(propertyName, enumValues);
                } else if (value instanceof Object[]) {
                    annotationParameters.put(propertyName, value);
                    // annotationParameters.put(propertyName, Lists.newLinkedList(Arrays.asList(value)));
                } else if (value instanceof Enum<?>) {
                    annotationParameters.put(propertyName, ((Enum<?>) value).name());
                } else if (value instanceof Collection<?>) {
                    annotationParameters.put(propertyName, value);
                } else if (value instanceof Byte || value instanceof Short || value instanceof Integer
                    || value instanceof Long || value instanceof Float || value instanceof Double
                    || value instanceof Boolean || value instanceof Character) {
                    annotationParameters.put(propertyName, value);
                } else if (value instanceof String) {
                    if (((String) value).matches("\".*\"")) {
                        value = ((String) value).replaceFirst("\"(.*)\"", "$1");
                    }
                    annotationParameters.put(propertyName, value);
                } else {
                    // currently QDox only returns the expression stated in the code as value, but not
                    // resolves it. So value is always of type String and for this ParsedJavaModelBuilder we
                    // always come into the else-part
                    annotationParameters.put(propertyName, value != null ? value.toString() : null);
                }
            }
        }
    }

    /**
     * Builds the model for javaDoc. This includes extraction of the comment (without doclets) as well as a
     * mapping of docletTags to its values.
     * @param annotatedElement
     *            Annotated element, which javaDoc should be parsed
     * @return the mapping of javaDoc elements to its values or <code>null</code> if the element does not
     *         declare javaDoc
     */
    private Map<String, Object> extractJavaDoc(JavaAnnotatedElement annotatedElement) {
        if (annotatedElement.getComment() == null) {
            return null;
        }
        Map<String, Object> javaDocModel = Maps.newHashMap();
        javaDocModel.put(ModelConstant.COMMENT, annotatedElement.getComment());
        Map<String, String> params = Maps.newHashMap();
        Map<String, String> thrown = Maps.newHashMap();
        for (DocletTag tag : annotatedElement.getTags()) {
            String tagValue = tag.getValue();
            String tagName = tag.getName();
            if (annotatedElement instanceof JavaMethod) {
                String name = StringUtils.substringBefore(tagValue, " ").trim();
                String value = StringUtils.substringAfter(tagValue, " ").trim();
                if (tagName.equals("param")) {
                    JavaMethod jm = (JavaMethod) annotatedElement;
                    int i = 0;
                    for (JavaParameter jp : jm.getParameters()) {
                        if (name.equals(jp.getName())) {
                            params.put(name, value);
                            params.put("arg" + i, value);
                        }
                        i++;
                    }
                } else if (tagName.equals("throws")) {
                    thrown.put(name, value);
                } else {
                    javaDocModel.put(tagName, tagValue);
                }
            } else {
                javaDocModel.put(tagName, tagValue);
            }
        }
        javaDocModel.put("params", params);
        javaDocModel.put("throws", thrown);
        return javaDocModel;
    }

    /**
     * Determines whether the given attributes behaving as IDs on the persistence layer. The information will
     * be integrated into the default model as stated in {@link #createModel(JavaClass)}
     *
     * @param javaClass
     *            {@link Class} object of the POJO the data should be retrieved from
     * @param attributes
     *            a {@link List} of all attributes and their properties
     */
    @Deprecated
    private void determinePojoIds(JavaClass javaClass, List<Map<String, Object>> attributes) {

        for (Map<String, Object> attr : attributes) {
            JavaMethod getter = null;
            try {
                getter = javaClass.getMethod("get" + StringUtil.capFirst((String) attr.get(ModelConstant.NAME)), null,
                    false);
            } catch (Exception e) {
                getter =
                    javaClass.getMethod("is" + StringUtil.capFirst((String) attr.get(ModelConstant.NAME)), null, false);
            }
            if (getter == null) {
                return;
            }

            List<JavaAnnotation> annotations = getter.getAnnotations();
            for (JavaAnnotation a : annotations) {
                if ("javax.persistence.Id".equals(a.getType().getCanonicalName())) {
                    attr.put("isId", "true");
                    break;
                }
            }
            if (attr.get("isId") == null) {
                attr.put("isId", "false");
            }
        }
    }

}
