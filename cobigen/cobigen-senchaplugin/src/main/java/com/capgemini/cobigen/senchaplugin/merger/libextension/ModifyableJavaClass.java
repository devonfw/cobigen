/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.capgemini.cobigen.senchaplugin.merger.libextension;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaInitializer;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.AbstractInheritableJavaEntity;
import com.thoughtworks.qdox.model.impl.DefaultBeanProperty;
import com.thoughtworks.qdox.model.impl.DefaultJavaTypeVariable;
import com.thoughtworks.qdox.model.impl.JavaClassParent;
import com.thoughtworks.qdox.model.impl.JavaMethodDelegate;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @author Malte Brunnlieb (adaption of DefaultJavaClass -> ModifyableJavaClass)
 */
@SuppressWarnings({ "javadoc", "deprecation" })
public class ModifyableJavaClass extends AbstractInheritableJavaEntity implements JavaClass {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -3844544030545877483L;

    private List<JavaConstructor> constructors = new LinkedList<>();

    private List<JavaMethod> methods = new LinkedList<>();

    private List<JavaField> fields = new LinkedList<>();

    private List<JavaClass> classes = new LinkedList<>();

    private boolean anInterface;

    private boolean anEnum;

    private boolean anAnnotation;

    private JavaType superClass;

    private List<JavaClass> implementz = new LinkedList<>();

    private List<JavaInitializer> initializers = new LinkedList<>();

    private List<DefaultJavaTypeVariable<JavaClass>> typeParameters = new LinkedList<>();

    // sourceless class can use this property
    private JavaPackage javaPackage;

    protected ModifyableJavaClass() {
    }

    public ModifyableJavaClass(String name) {
        setName(name);
    }

    public ModifyableJavaClass(JavaSource source) {
        setSource(source);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInterface() {
        return anInterface;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPrimitive() {
        final String name = getName();
        return "void".equals(name) || "boolean".equals(name) || "byte".equals(name) || "char".equals(name)
            || "short".equals(name) || "int".equals(name) || "long".equals(name) || "float".equals(name)
            || "double".equals(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isVoid() {
        return "void".equals(getName());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEnum() {
        return anEnum;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAnnotation() {
        return anAnnotation;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isArray() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public JavaClass getComponentType() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getDimensions() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public JavaType getSuperClass() {
        JavaType result = null;
        JavaClass OBJECT_JAVACLASS = getJavaClassLibrary().getJavaClass("java.lang.Object");
        JavaClass ENUM_JAVACLASS = getJavaClassLibrary().getJavaClass("java.lang.Enum");

        boolean iAmJavaLangObject = OBJECT_JAVACLASS.equals(this);

        if (anEnum) {
            result = ENUM_JAVACLASS;
        } else if (!anInterface && !anAnnotation && (superClass == null) && !iAmJavaLangObject) {
            result = OBJECT_JAVACLASS;
        } else {
            result = superClass;
        }
        return result;
    }

    /**
     * Shorthand for getSuperClass().getJavaClass() with null checking.
     */
    @Override
    public JavaClass getSuperJavaClass() {
        JavaClass result = null;
        JavaType superType = getSuperClass();
        if (superType instanceof JavaClass) {
            result = (JavaClass) superType;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaType> getImplements() {
        return new LinkedList<JavaType>(implementz);
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaClass> getImplementedInterfaces() {
        return new LinkedList<>(implementz);
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaClass> getInterfaces() {
        return new LinkedList<>(implementz);
    }

    /** {@inheritDoc} */
    @Override
    public String getCodeBlock() {
        return getModelWriter().writeClass(this).toString();
    }

    public void setInterface(boolean anInterface) {
        this.anInterface = anInterface;
    }

    public void setEnum(boolean anEnum) {
        this.anEnum = anEnum;
    }

    public void setAnnotation(boolean anAnnotation) {
        this.anAnnotation = anAnnotation;
    }

    public void addConstructor(JavaConstructor constructor) {
        constructors.add(constructor);
    }

    public void addMethod(JavaMethod meth) {
        methods.add(meth);
    }

    public void setSuperClass(JavaType type) {
        if (anEnum) {
            throw new IllegalArgumentException("enums cannot extend other classes");
        }
        superClass = type;
    }

    public void setImplementz(List<JavaClass> implementz) {
        this.implementz = implementz;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<DefaultJavaTypeVariable<JavaClass>> getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(List<DefaultJavaTypeVariable<JavaClass>> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public void addField(JavaField javaField) {
        fields.add(javaField);
    }

    /**
     * Only used when constructing the model by hand / without source
     *
     * @param javaPackage
     */
    public void setJavaPackage(JavaPackage javaPackage) {
        this.javaPackage = javaPackage;
    }

    /** {@inheritDoc} */
    @Override
    public JavaSource getParentSource() {
        return (getParentClass() != null ? getParentClass().getParentSource() : super.getSource());
    }

    /** {@inheritDoc} */
    @Override
    public JavaSource getSource() {
        return getParentSource();
    }

    /** {@inheritDoc} */
    @Override
    public JavaPackage getPackage() {
        return getParentSource() != null ? getParentSource().getPackage() : javaPackage;
    }

    /** {@inheritDoc} */
    @Override
    public JavaClassParent getParent() {
        JavaClassParent result = getParentClass();
        if (result == null) {
            result = getParentSource();
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String getPackageName() {
        JavaPackage pckg = getPackage();
        return (pckg != null && pckg.getName() != null) ? pckg.getName() : "";
    }

    /** {@inheritDoc} */
    @Override
    public String getFullyQualifiedName() {
        return (getParentClass() != null ? (getParentClass().getClassNamePrefix())
            : getPackage() != null ? (getPackage().getName() + ".") : "") + getName();
    }

    /** {@inheritDoc} */
    @Override
    public String getGenericFullyQualifiedName() {
        return getFullyQualifiedName();
    }

    /** {@inheritDoc} */
    @Override
    public String getCanonicalName() {
        return getFullyQualifiedName().replace('$', '.');
    }

    /** {@inheritDoc} */
    @Override
    public String getGenericCanonicalName() {
        return getCanonicalName();
    }

    /** {@inheritDoc} */
    @Override
    public String getValue() {
        return getCanonicalName().substring(getSource().getClassNamePrefix().length());
    }

    /** {@inheritDoc} */
    @Override
    public String getGenericValue() {
        return getValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInner() {
        return getParentClass() != null;
    }

    /** {@inheritDoc} */
    @Override
    public String resolveType(String typeName) {
        // since this method is deprecated but still called from other structures it's simply a wrap around
        // for the proposed method
        String result;
        /*
         * JavaClass resolvedClass = getNestedClassByName(typeName); if (resolvedClass != null) { result =
         * resolvedClass.getFullyQualifiedName(); } else { result = getParent().resolveType(typeName); }
         */
        result = resolveFullyQualifiedName(typeName);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String resolveCanonicalName(String name) {
        // Maybe it's an inner class?
        for (JavaClass innerClass : getNestedClasses()) {
            if (innerClass.getName().equals(name)) {
                return innerClass.getName();
            }
        }
        return getParent().resolveCanonicalName(name);
    }

    /** {@inheritDoc} */
    @Override
    public String resolveFullyQualifiedName(String name) {
        // Maybe it's an inner class?
        for (JavaClass innerClass : getNestedClasses()) {
            if (innerClass.getName().equals(name)) {
                return innerClass.getFullyQualifiedName();
            }
        }
        String result = // getParent().resolveFullyQualifiedName(name); //replaced since getParent() is
                        // deprecated
            getParentSource().resolveFullyQualifiedName(name);
        if (result != null) { // by sholzer 18-06-15 to fix issue #108
            result = result.replace('$', '.');
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String getClassNamePrefix() {
        return getFullyQualifiedName() + "$";
    }

    /** {@inheritDoc} */
    @Override
    public JavaType asType() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaInitializer> getInitializers() {
        return initializers;
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaConstructor> getConstructors() {
        return constructors;
    }

    /** {@inheritDoc} */
    @Override
    public JavaConstructor getConstructor(List<JavaType> parameterTypes) {
        return getConstructor(parameterTypes, false);
    }

    /** {@inheritDoc} */
    @Override
    public JavaConstructor getConstructor(List<JavaType> parameterTypes, boolean varArgs) {
        for (JavaConstructor constructor : getConstructors()) {
            if (constructor.signatureMatches(parameterTypes, varArgs)) {
                return constructor;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaMethod> getMethods() {
        return methods;
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaMethod> getMethods(boolean superclasses) {
        if (superclasses) {
            return new LinkedList<>(getMethodsFromSuperclassAndInterfaces(this, this).values());
        } else {
            return getMethods();
        }
    }

    private static Map<String, JavaMethod> getMethodsFromSuperclassAndInterfaces(JavaClass rootClass,
        JavaClass callingClazz) {

        Map<String, JavaMethod> result = new LinkedHashMap<>();

        for (JavaMethod method : callingClazz.getMethods()) {
            if (!method.isPrivate()) {
                String signature = method.getDeclarationSignature(false);
                result.put(signature, method);
            }
        }

        JavaClass superclass = callingClazz.getSuperJavaClass();
        if (superclass != null) {
            Map<String, JavaMethod> superClassMethods =
                getMethodsFromSuperclassAndInterfaces(callingClazz, superclass);
            for (Map.Entry<String, JavaMethod> methodEntry : superClassMethods.entrySet()) {
                if (!result.containsKey(methodEntry.getKey())) {
                    JavaMethod method;
                    if (superclass.equals(rootClass)) {
                        method = methodEntry.getValue();
                    } else {
                        method = new JavaMethodDelegate(callingClazz, methodEntry.getValue());
                    }
                    result.put(methodEntry.getKey(), method);
                }
            }

        }

        for (JavaClass clazz : callingClazz.getImplementedInterfaces()) {
            Map<String, JavaMethod> interfaceMethods =
                getMethodsFromSuperclassAndInterfaces(callingClazz, clazz);
            for (Map.Entry<String, JavaMethod> methodEntry : interfaceMethods.entrySet()) {
                if (!result.containsKey(methodEntry.getKey())) {
                    JavaMethod method;
                    if (clazz.equals(rootClass)) {
                        method = methodEntry.getValue();
                    } else {
                        method = new JavaMethodDelegate(callingClazz, methodEntry.getValue());
                    }
                    result.put(methodEntry.getKey(), method);
                }
            }

        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public JavaMethod getMethodBySignature(String name, List<JavaType> parameterTypes) {
        return getMethod(name, parameterTypes, false);
    }

    /** {@inheritDoc} */
    @Override
    public JavaMethod getMethod(String name, List<JavaType> parameterTypes, boolean varArgs) {
        for (JavaMethod method : getMethods()) {
            if (method.signatureMatches(name, parameterTypes, varArgs)) {
                return method;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public JavaMethod getMethodBySignature(String name, List<JavaType> parameterTypes, boolean superclasses) {
        return getMethodBySignature(name, parameterTypes, superclasses, false);
    }

    /** {@inheritDoc} */
    @Override
    public JavaMethod getMethodBySignature(String name, List<JavaType> parameterTypes, boolean superclasses,
        boolean varArg) {

        List<JavaMethod> result = getMethodsBySignature(name, parameterTypes, superclasses, varArg);

        return (result.size() > 0) ? result.get(0) : null;
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaMethod> getMethodsBySignature(String name, List<JavaType> parameterTypes,
        boolean superclasses) {
        return getMethodsBySignature(name, parameterTypes, superclasses, false);
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaMethod> getMethodsBySignature(String name, List<JavaType> parameterTypes,
        boolean superclasses, boolean varArg) {
        List<JavaMethod> result = new LinkedList<>();

        JavaMethod methodInThisClass = getMethod(name, parameterTypes, varArg);

        if (methodInThisClass != null) {
            result.add(methodInThisClass);
        }

        if (superclasses) {
            JavaClass superclass = getSuperJavaClass();

            if (superclass != null) {
                JavaMethod method = superclass.getMethodBySignature(name, parameterTypes, true, varArg);

                // todo: ideally we should check on package privacy too. oh well.
                if ((method != null) && !method.isPrivate()) {
                    result.add(new JavaMethodDelegate(this, method));
                }
            }

            for (JavaClass clazz : getImplementedInterfaces()) {
                JavaMethod method = clazz.getMethodBySignature(name, parameterTypes, true, varArg);
                if (method != null) {
                    result.add(new JavaMethodDelegate(this, method));
                }
            }
        }

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaField> getFields() {
        return fields;
    }

    /** {@inheritDoc} */
    @Override
    public JavaField getFieldByName(String name) {
        for (JavaField field : getFields()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaField> getEnumConstants() {
        List<JavaField> result = null;
        if (isEnum()) {
            result = new LinkedList<>();
            for (JavaField field : getFields()) {
                if (field.isEnumConstant()) {
                    result.add(field);
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public JavaField getEnumConstantByName(String name) {
        JavaField field = getFieldByName(name);
        return field.isEnumConstant() ? field : null;
    }

    public void addInitializer(JavaInitializer initializer) {
        initializers.add(initializer);
    }

    public void addClass(JavaClass cls) {
        classes.add(cls);
    }

    /**
     * @deprecated Use {@link #getNestedClasses()} instead.
     */
    @Override
    @Deprecated
    public List<JavaClass> getClasses() {
        return getNestedClasses();
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaClass> getNestedClasses() {
        return classes;
    }

    /** {@inheritDoc} */
    @Override
    public JavaClass getInnerClassByName(String name) {
        return getNestedClassByName(name);
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaClass> getInnerClasses() {
        return getNestedClasses();
    }

    /** {@inheritDoc} */
    @Override
    public JavaClass getNestedClassByName(String name) {
        int separatorIndex = name.indexOf('.');
        String directInnerClassName = (separatorIndex > 0 ? name.substring(0, separatorIndex) : name);
        for (JavaClass jClass : getNestedClasses()) {
            if (jClass.getName().equals(directInnerClassName)) {
                if (separatorIndex > 0) {
                    return jClass.getNestedClassByName(name.substring(separatorIndex + 1));
                } else {
                    return jClass;
                }
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isA(String fullClassName) {
        if (fullClassName == null) {
            return false;
        }
        if (fullClassName.equals(getFullyQualifiedName())) {
            return true;
        }
        for (JavaClass implementz : getImplementedInterfaces()) {
            if (implementz.isA(fullClassName)) {
                return true;
            }
        }
        JavaClass superClass = getSuperJavaClass();
        if (superClass != null) {
            return superClass.isA(fullClassName);
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isA(JavaClass javaClass) {
        if (this == javaClass) {
            return true;
        } else if (equals(javaClass)) {
            return true;
        } else if (javaClass != null) {
            // ask our interfaces
            for (JavaClass intrfc : getImplementedInterfaces()) {
                if (intrfc.isA(javaClass)) {
                    return true;
                }
            }
            // ask our superclass
            JavaClass superClass = getSuperJavaClass();
            if (superClass != null) {
                return superClass.isA(javaClass);
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public List<BeanProperty> getBeanProperties() {
        return getBeanProperties(false);
    }

    /** {@inheritDoc} */
    @Override
    public List<BeanProperty> getBeanProperties(boolean superclasses) {
        Map<String, BeanProperty> beanPropertyMap = getBeanPropertyMap(superclasses);
        Collection<BeanProperty> beanPropertyCollection = beanPropertyMap.values();

        return new LinkedList<>(beanPropertyCollection);
    }

    private Map<String, BeanProperty> getBeanPropertyMap(boolean superclasses) {
        List<JavaMethod> superMethods = getMethods(superclasses);
        Map<String, DefaultBeanProperty> beanPropertyMap = new LinkedHashMap<>();

        // loop over the methods.
        for (JavaMethod superMethod : superMethods) {
            if (superMethod.isPropertyAccessor()) {
                String propertyName = superMethod.getPropertyName();
                DefaultBeanProperty beanProperty = getOrCreateProperty(beanPropertyMap, propertyName);

                beanProperty.setAccessor(superMethod);
                beanProperty.setType(superMethod.getPropertyType());
            } else if (superMethod.isPropertyMutator()) {
                String propertyName = superMethod.getPropertyName();
                DefaultBeanProperty beanProperty = getOrCreateProperty(beanPropertyMap, propertyName);

                beanProperty.setMutator(superMethod);
                beanProperty.setType(superMethod.getPropertyType());
            }
        }
        return new LinkedHashMap<String, BeanProperty>(beanPropertyMap);
    }

    private DefaultBeanProperty getOrCreateProperty(Map<String, DefaultBeanProperty> beanPropertyMap,
        String propertyName) {
        DefaultBeanProperty result = beanPropertyMap.get(propertyName);

        if (result == null) {
            result = new DefaultBeanProperty(propertyName);
            beanPropertyMap.put(propertyName, result);
        }

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public BeanProperty getBeanProperty(String propertyName) {
        return getBeanProperty(propertyName, false);
    }

    /** {@inheritDoc} */
    @Override
    public BeanProperty getBeanProperty(String propertyName, boolean superclasses) {
        return getBeanPropertyMap(superclasses).get(propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaClass> getDerivedClasses() {
        List<JavaClass> result = new LinkedList<>();
        for (JavaClass clazz : getSource().getJavaClassLibrary().getJavaClasses()) {
            if (clazz.isA(this) && !(clazz == this)) {
                result.add(clazz);
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public JavaClass getDeclaringClass() {
        return getParentClass();
    }

    /** {@inheritDoc} */
    @Override
    public List<DocletTag> getTagsByName(String name, boolean superclasses) {
        return getTagsRecursive(this, name, superclasses);
    }

    private List<DocletTag> getTagsRecursive(JavaClass javaClass, String name, boolean superclasses) {
        Set<DocletTag> result = new LinkedHashSet<>();
        result.addAll(javaClass.getTagsByName(name));
        if (superclasses) {
            JavaClass superclass = javaClass.getSuperJavaClass();

            if (superclass != null) {
                result.addAll(getTagsRecursive(superclass, name, superclasses));
            }

            for (JavaClass intrfc : javaClass.getImplementedInterfaces()) {
                if (intrfc != null) {
                    result.addAll(getTagsRecursive(intrfc, name, superclasses));
                }
            }
        }
        return new LinkedList<>(result);
    }

    /**
     * @see java.lang.Class#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isPrimitive()) {
            sb.append(getName());
        } else {
            sb.append(isInterface() ? "interface" : "class");
            sb.append(" ");
            sb.append(getFullyQualifiedName());
        }
        return sb.toString();
    }

    @Override
    public String toGenericString() {
        return toString();
    }

    @Override
    public int hashCode() {
        return 2 + getFullyQualifiedName().hashCode();
    }

    // ideally this shouldn't be required, but we must as long as Types can be created without classLibrary
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JavaClass)) {
            return false;
        }
        JavaClass clazz = (JavaClass) obj;
        return getFullyQualifiedName().equals(clazz.getFullyQualifiedName());
    }

    /** {@inheritDoc} */
    @Override
    public ClassLibrary getJavaClassLibrary() {
        return getSource().getJavaClassLibrary();
    }

    /**
     * Replaces the given baseField with the given patchField. If the baseField does not exist, this method
     * will do nothing
     * @param baseField
     *            to be replaced
     * @param patchField
     *            to replace the baseField
     * @author Malte Brunnlieb
     */
    public void replace(JavaField baseField, JavaField patchField) {
        JavaField definedField = getFieldByName(baseField.getName());
        int i = fields.indexOf(definedField);
        if (i != -1) {
            fields.set(i, patchField);
        }
    }

    /**
     * Replaces the given baseMethod with the given patchMethod. If the baseMethod does not exist, this method
     * will do nothing
     * @param baseMethod
     *            to be replaced
     * @param patchMethod
     *            to replace the baseMethod
     * @author Malte Brunnlieb
     */
    public void replace(JavaMethod baseMethod, JavaMethod patchMethod) {
        JavaMethod definedMethod = getMethodBySignature(baseMethod.getName(), baseMethod.getParameterTypes());
        int i = methods.indexOf(definedMethod);
        if (i != -1) {
            methods.set(i, patchMethod);
        }
    }

    /**
     * Replaces the given baseConstructor with the given patchConstructor. If the baseConstructor does not
     * exist, this method will do nothing
     * @param baseConstructor
     *            to be replaced
     * @param patchConstructor
     *            to replace the baseMethod
     * @author Malte Brunnlieb
     */
    public void replace(JavaConstructor baseConstructor, JavaConstructor patchConstructor) {
        JavaConstructor definedConstructor = getConstructor(baseConstructor.getParameterTypes());
        int i = constructors.indexOf(definedConstructor);
        if (i != -1) {
            constructors.set(i, patchConstructor);
        }
    }

}
