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
package com.capgemini.cobigen.javaplugin.merger.libextension;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.builder.TypeAssembler;
import com.thoughtworks.qdox.builder.impl.DefaultJavaAnnotationAssembler;
import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.model.expression.Expression;
import com.thoughtworks.qdox.model.impl.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.impl.DefaultJavaConstructor;
import com.thoughtworks.qdox.model.impl.DefaultJavaField;
import com.thoughtworks.qdox.model.impl.DefaultJavaInitializer;
import com.thoughtworks.qdox.model.impl.DefaultJavaMethod;
import com.thoughtworks.qdox.model.impl.DefaultJavaPackage;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameter;
import com.thoughtworks.qdox.model.impl.DefaultJavaSource;
import com.thoughtworks.qdox.model.impl.DefaultJavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaTypeVariable;
import com.thoughtworks.qdox.parser.expression.ExpressionDef;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.InitDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.TypeVariableDef;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Robert Scholte
 */
@SuppressWarnings("javadoc")
public class ModifyableModelBuilder implements Builder {

    private final DefaultJavaSource source;

    private LinkedList<ModifyableJavaClass> classStack = new LinkedList<>();

    private List<DefaultJavaParameter> parameterList = new LinkedList<>();

    private DefaultJavaConstructor currentConstructor;

    private DefaultJavaMethod currentMethod;

    private DefaultJavaField currentField;

    private List<AnnoDef> currentAnnoDefs;

    private List<ExpressionDef> currentArguments;

    private String lastComment;

    private List<TagDef> lastTagSet = new LinkedList<>();

    private DocletTagFactory docletTagFactory;

    private ModelWriterFactory modelWriterFactory;

    public ModifyableModelBuilder(ClassLibrary classLibrary, DocletTagFactory docletTagFactory) {

        this.docletTagFactory = docletTagFactory;
        this.source = new DefaultJavaSource(classLibrary);
        this.currentAnnoDefs = new LinkedList<>();
        this.currentArguments = new LinkedList<>();
    }

    /** {@inheritDoc} */
    @Override
    public void setModelWriterFactory(ModelWriterFactory modelWriterFactory) {

        this.modelWriterFactory = modelWriterFactory;
        this.source.setModelWriterFactory(modelWriterFactory);
    }

    /** {@inheritDoc} */
    @Override
    public void addPackage(PackageDef packageDef) {

        DefaultJavaPackage jPackage = new DefaultJavaPackage(packageDef.getName());
        jPackage.setClassLibrary(this.source.getJavaClassLibrary());
        jPackage.setLineNumber(packageDef.getLineNumber());
        jPackage.setModelWriterFactory(this.modelWriterFactory);
        addJavaDoc(jPackage);
        setAnnotations(jPackage);
        this.source.setPackage(jPackage);
    }

    /** {@inheritDoc} */
    @Override
    public void addImport(String importName) {

        this.source.addImport(importName);
    }

    /** {@inheritDoc} */
    @Override
    public void addJavaDoc(String text) {

        this.lastComment = text;
    }

    /** {@inheritDoc} */
    @Override
    public void addJavaDocTag(TagDef tagDef) {

        this.lastTagSet.add(tagDef);
    }

    /** {@inheritDoc} */
    @Override
    public void beginClass(ClassDef def) {

        ModifyableJavaClass newClass = new ModifyableJavaClass(this.source);
        newClass.setLineNumber(def.getLineNumber());
        newClass.setModelWriterFactory(this.modelWriterFactory);

        // basic details
        newClass.setName(def.getName());
        newClass.setInterface(ClassDef.INTERFACE.equals(def.getType()));
        newClass.setEnum(ClassDef.ENUM.equals(def.getType()));
        newClass.setAnnotation(ClassDef.ANNOTATION_TYPE.equals(def.getType()));

        // superclass
        if (newClass.isInterface()) {
            newClass.setSuperClass(null);
        } else if (!newClass.isEnum()) {
            newClass.setSuperClass(def.getExtends().size() > 0 ? createType(def.getExtends().iterator()
                .next(), 0) : null);
        }

        // implements
        Set<TypeDef> implementSet = newClass.isInterface() ? def.getExtends() : def.getImplements();
        List<JavaClass> implementz = new LinkedList<>();
        for (TypeDef implementType : implementSet) {
            implementz.add(createType(implementType, 0));
        }
        newClass.setImplementz(implementz);

        // modifiers
        newClass.setModifiers(new LinkedList<>(def.getModifiers()));

        // typeParameters
        if (def.getTypeParameters() != null) {
            List<DefaultJavaTypeVariable<JavaClass>> typeParams = new LinkedList<>();
            for (TypeVariableDef typeVariableDef : def.getTypeParameters()) {
                typeParams.add(createTypeVariable(typeVariableDef, (JavaClass) newClass));
            }
            newClass.setTypeParameters(typeParams);
        }

        // javadoc
        addJavaDoc(newClass);

        // // ignore annotation types (for now)
        // if (ClassDef.ANNOTATION_TYPE.equals(def.type)) {
        // System.out.println( currentClass.getFullyQualifiedName() );
        // return;
        // }

        // annotations
        setAnnotations(newClass);

        this.classStack.addFirst(bindClass(newClass));
    }

    protected ModifyableJavaClass bindClass(ModifyableJavaClass newClass) {

        if (this.currentField != null) {
            this.classStack.getFirst().addClass(newClass);
            this.currentField.setEnumConstantClass(newClass);
        } else if (!this.classStack.isEmpty()) {
            this.classStack.getFirst().addClass(newClass);
            newClass.setParentClass(this.classStack.getFirst());
        } else {
            this.source.addClass(newClass);
        }
        return newClass;
    }

    /** {@inheritDoc} */
    @Override
    public void endClass() {

        this.classStack.removeFirst();
    }

    /**
     * this one is specific for those cases where dimensions can be part of both the type and identifier i.e.
     * private String[] matrix[]; //field public abstract String[] getMatrix[](); //method
     *
     * @param typeDef
     * @param dimensions
     * @return the Type
     */
    private DefaultJavaType createType(TypeDef typeDef, int dimensions) {

        if (typeDef == null) {
            return null;
        }
        return TypeAssembler.createUnresolved(typeDef, dimensions, this.classStack.isEmpty() ? this.source
            : this.classStack.getFirst());
    }

    private void addJavaDoc(AbstractBaseJavaEntity entity) {

        entity.setComment(this.lastComment);
        List<DocletTag> tagList = new LinkedList<>();
        for (TagDef tagDef : this.lastTagSet) {
            tagList.add(this.docletTagFactory.createDocletTag(tagDef.getName(), tagDef.getText(),
                (JavaAnnotatedElement) entity, tagDef.getLineNumber()));
        }
        entity.setTags(tagList);

        this.lastTagSet.clear();
        this.lastComment = null;
    }

    @Override
    public void addInitializer(InitDef def) {

        DefaultJavaInitializer initializer = new DefaultJavaInitializer();
        initializer.setLineNumber(def.getLineNumber());

        initializer.setBlock(def.getBlockContent());
        initializer.setStatic(def.isStatic());

        this.classStack.getFirst().addInitializer(initializer);

    }

    /** {@inheritDoc} */
    @Override
    public void beginConstructor() {

        this.currentConstructor = new DefaultJavaConstructor();

        this.currentConstructor.setParentClass(this.classStack.getFirst());

        this.currentConstructor.setModelWriterFactory(this.modelWriterFactory);

        addJavaDoc(this.currentConstructor);
        setAnnotations(this.currentConstructor);

        this.classStack.getFirst().addConstructor(this.currentConstructor);
    }

    /** {@inheritDoc} */
    @Override
    public void endConstructor(MethodDef def) {

        this.currentConstructor.setLineNumber(def.getLineNumber());

        // basic details
        this.currentConstructor.setName(def.getName());

        // typeParameters
        if (def.getTypeParams() != null) {
            List<JavaTypeVariable<JavaConstructor>> typeParams = new LinkedList<>();
            for (TypeVariableDef typeVariableDef : def.getTypeParams()) {
                typeParams
                    .add(createTypeVariable(typeVariableDef, (JavaConstructor) this.currentConstructor));
            }
            this.currentConstructor.setTypeParameters(typeParams);
        }

        // exceptions
        List<JavaClass> exceptions = new LinkedList<>();
        for (TypeDef type : def.getExceptions()) {
            exceptions.add(createType(type, 0));
        }
        this.currentConstructor.setExceptions(exceptions);

        // modifiers
        this.currentConstructor.setModifiers(new LinkedList<>(def.getModifiers()));

        if (!this.parameterList.isEmpty()) {
            this.currentConstructor.setParameters(new ArrayList<JavaParameter>(this.parameterList));
            this.parameterList.clear();
        }

        this.currentConstructor.setSourceCode(def.getBody());
    }

    /** {@inheritDoc} */
    @Override
    public void beginMethod() {

        this.currentMethod = new DefaultJavaMethod();
        if (this.currentField == null) {
            this.currentMethod.setParentClass(this.classStack.getFirst());
            this.classStack.getFirst().addMethod(this.currentMethod);
        }
        this.currentMethod.setModelWriterFactory(this.modelWriterFactory);

        addJavaDoc(this.currentMethod);
        setAnnotations(this.currentMethod);
    }

    /** {@inheritDoc} */
    @Override
    public void endMethod(MethodDef def) {

        this.currentMethod.setLineNumber(def.getLineNumber());

        // basic details
        this.currentMethod.setName(def.getName());
        this.currentMethod.setReturns(createType(def.getReturnType(), def.getDimensions()));

        // typeParameters
        if (def.getTypeParams() != null) {
            List<JavaTypeVariable<JavaMethod>> typeParams = new LinkedList<>();
            for (TypeVariableDef typeVariableDef : def.getTypeParams()) {
                typeParams.add(createTypeVariable(typeVariableDef, (JavaMethod) this.currentMethod));
            }
            this.currentMethod.setTypeParameters(typeParams);
        }

        // exceptions
        List<JavaClass> exceptions = new LinkedList<>();
        for (TypeDef type : def.getExceptions()) {
            exceptions.add(createType(type, 0));
        }
        this.currentMethod.setExceptions(exceptions);

        // modifiers
        this.currentMethod.setModifiers(new LinkedList<>(def.getModifiers()));

        if (!this.parameterList.isEmpty()) {
            this.currentMethod.setParameters(new ArrayList<JavaParameter>(this.parameterList));
            this.parameterList.clear();
        }

        this.currentMethod.setSourceCode(def.getBody());
    }

    private <G extends JavaGenericDeclaration> DefaultJavaTypeVariable<G> createTypeVariable(
        TypeVariableDef typeVariableDef, G genericDeclaration) {

        if (typeVariableDef == null) {
            return null;
        }
        DefaultJavaTypeVariable<G> result =
            new DefaultJavaTypeVariable<>(typeVariableDef.getName(), genericDeclaration);

        if (typeVariableDef.getBounds() != null && !typeVariableDef.getBounds().isEmpty()) {
            List<JavaType> bounds = new LinkedList<>();
            for (TypeDef typeDef : typeVariableDef.getBounds()) {
                bounds.add(createType(typeDef, 0));
            }
            result.setBounds(bounds);
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void beginField(FieldDef def) {

        this.currentField = new DefaultJavaField();
        this.currentField.setParentClass(this.classStack.getFirst());
        this.currentField.setLineNumber(def.getLineNumber());
        this.currentField.setModelWriterFactory(this.modelWriterFactory);

        this.currentField.setName(def.getName());
        this.currentField.setType(createType(def.getType(), def.getDimensions()));

        this.currentField.setEnumConstant(def.isEnumConstant());

        // modifiers
        {
            this.currentField.setModifiers(new LinkedList<>(def.getModifiers()));
        }

        // code body
        this.currentField.setInitializationExpression(def.getBody());

        // javadoc
        addJavaDoc(this.currentField);

        // annotations
        setAnnotations(this.currentField);
    }

    /** {@inheritDoc} */
    @Override
    public void endField() {

        if (this.currentArguments != null && !this.currentArguments.isEmpty()) {
            // DefaultExpressionTransformer??
            DefaultJavaAnnotationAssembler assembler = new DefaultJavaAnnotationAssembler(this.currentField);

            List<Expression> arguments = new LinkedList<>();
            for (ExpressionDef annoDef : this.currentArguments) {
                arguments.add(assembler.assemble(annoDef));
            }
            this.currentField.setEnumConstantArguments(arguments);
            this.currentArguments.clear();
        }

        this.classStack.getFirst().addField(this.currentField);

        this.currentField = null;
    }

    /** {@inheritDoc} */
    @Override
    public void addParameter(FieldDef fieldDef) {

        DefaultJavaParameter jParam =
            new ExtendedJavaParameter(createType(fieldDef.getType(), fieldDef.getDimensions()),
                fieldDef.getName(), fieldDef.getModifiers(), fieldDef.isVarArgs());
        // jParam.setParentMethod(currentMethod); -> not available any more since 2.0-M2
        jParam.setModelWriterFactory(this.modelWriterFactory);
        addJavaDoc(jParam);
        setAnnotations(jParam);
        this.parameterList.add(jParam);
    }

    private void setAnnotations(final AbstractBaseJavaEntity entity) {

        if (!this.currentAnnoDefs.isEmpty()) {
            DefaultJavaAnnotationAssembler assembler =
                new DefaultJavaAnnotationAssembler((JavaAnnotatedElement) entity);

            List<JavaAnnotation> annotations = new LinkedList<>();
            for (AnnoDef annoDef : this.currentAnnoDefs) {
                annotations.add(assembler.assemble(annoDef));
            }
            entity.setAnnotations(annotations);
            this.currentAnnoDefs.clear();
        }
    }

    // Don't resolve until we need it... class hasn't been defined yet.
    /** {@inheritDoc} */
    @Override
    public void addAnnotation(AnnoDef annotation) {

        this.currentAnnoDefs.add(annotation);
    }

    @Override
    public void addArgument(ExpressionDef argument) {

        this.currentArguments.add(argument);
    }

    @Override
    public JavaSource getSource() {

        return this.source;
    }

    @Override
    public void setUrl(URL url) {

        this.source.setURL(url);
    }
}
