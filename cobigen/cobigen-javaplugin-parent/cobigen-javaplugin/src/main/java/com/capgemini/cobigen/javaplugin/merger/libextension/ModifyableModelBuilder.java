/*
 * Custom implementation derived from com.thoughtworks.qdox.model.impl.DefaultJavaClass,
 * which itself has been published under Apache Software Foundation (ASF) available at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import com.thoughtworks.qdox.builder.impl.ModelBuilder;
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
 * Custom implementation derived from {@link ModelBuilder} to fix some issues with annotation and javaDoc
 * parsing.
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
        source = new DefaultJavaSource(classLibrary);
        currentAnnoDefs = new LinkedList<>();
        currentArguments = new LinkedList<>();
    }

    @Override
    public void setModelWriterFactory(ModelWriterFactory modelWriterFactory) {

        this.modelWriterFactory = modelWriterFactory;
        source.setModelWriterFactory(modelWriterFactory);
    }

    @Override
    public void addPackage(PackageDef packageDef) {

        DefaultJavaPackage jPackage = new DefaultJavaPackage(packageDef.getName());
        jPackage.setClassLibrary(source.getJavaClassLibrary());
        jPackage.setLineNumber(packageDef.getLineNumber());
        jPackage.setModelWriterFactory(modelWriterFactory);
        addJavaDoc(jPackage);
        setAnnotations(jPackage);
        source.setPackage(jPackage);
    }

    @Override
    public void addImport(String importName) {

        source.addImport(importName);
    }

    @Override
    public void addJavaDoc(String text) {

        lastComment = text;
    }

    @Override
    public void addJavaDocTag(TagDef tagDef) {

        lastTagSet.add(tagDef);
    }

    @Override
    public void beginClass(ClassDef def) {

        ModifyableJavaClass newClass = new ModifyableJavaClass(source);
        newClass.setLineNumber(def.getLineNumber());
        newClass.setModelWriterFactory(modelWriterFactory);

        // basic details
        newClass.setName(def.getName());
        newClass.setInterface(ClassDef.INTERFACE.equals(def.getType()));
        newClass.setEnum(ClassDef.ENUM.equals(def.getType()));
        newClass.setAnnotation(ClassDef.ANNOTATION_TYPE.equals(def.getType()));

        // superclass
        if (newClass.isInterface()) {
            newClass.setSuperClass(null);
        } else if (!newClass.isEnum()) {
            newClass
                .setSuperClass(def.getExtends().size() > 0 ? createType(def.getExtends().iterator().next(), 0) : null);
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

        classStack.addFirst(bindClass(newClass));
    }

    protected ModifyableJavaClass bindClass(ModifyableJavaClass newClass) {

        if (currentField != null) {
            classStack.getFirst().addClass(newClass);
            currentField.setEnumConstantClass(newClass);
        } else if (!classStack.isEmpty()) {
            classStack.getFirst().addClass(newClass);
            newClass.setParentClass(classStack.getFirst());
        } else {
            source.addClass(newClass);
        }
        return newClass;
    }

    @Override
    public void endClass() {

        classStack.removeFirst();
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
        return TypeAssembler.createUnresolved(typeDef, dimensions,
            classStack.isEmpty() ? source : classStack.getFirst());
    }

    private void addJavaDoc(AbstractBaseJavaEntity entity) {

        entity.setComment(lastComment);
        List<DocletTag> tagList = new LinkedList<>();
        for (TagDef tagDef : lastTagSet) {
            tagList.add(docletTagFactory.createDocletTag(tagDef.getName(), tagDef.getText(),
                (JavaAnnotatedElement) entity, tagDef.getLineNumber()));
        }
        entity.setTags(tagList);

        lastTagSet.clear();
        lastComment = null;
    }

    @Override
    public void addInitializer(InitDef def) {

        DefaultJavaInitializer initializer = new DefaultJavaInitializer();
        initializer.setLineNumber(def.getLineNumber());

        initializer.setBlock(def.getBlockContent());
        initializer.setStatic(def.isStatic());

        classStack.getFirst().addInitializer(initializer);

    }

    @Override
    public void beginConstructor() {

        currentConstructor = new DefaultJavaConstructor();

        currentConstructor.setParentClass(classStack.getFirst());

        currentConstructor.setModelWriterFactory(modelWriterFactory);

        addJavaDoc(currentConstructor);
        setAnnotations(currentConstructor);

        classStack.getFirst().addConstructor(currentConstructor);
    }

    @Override
    public void endConstructor(MethodDef def) {

        currentConstructor.setLineNumber(def.getLineNumber());

        // basic details
        currentConstructor.setName(def.getName());

        // typeParameters
        if (def.getTypeParams() != null) {
            List<JavaTypeVariable<JavaConstructor>> typeParams = new LinkedList<>();
            for (TypeVariableDef typeVariableDef : def.getTypeParams()) {
                typeParams.add(createTypeVariable(typeVariableDef, (JavaConstructor) currentConstructor));
            }
            currentConstructor.setTypeParameters(typeParams);
        }

        // exceptions
        List<JavaClass> exceptions = new LinkedList<>();
        for (TypeDef type : def.getExceptions()) {
            exceptions.add(createType(type, 0));
        }
        currentConstructor.setExceptions(exceptions);

        // modifiers
        currentConstructor.setModifiers(new LinkedList<>(def.getModifiers()));

        if (!parameterList.isEmpty()) {
            currentConstructor.setParameters(new ArrayList<JavaParameter>(parameterList));
            parameterList.clear();
        }

        currentConstructor.setSourceCode(def.getBody());
    }

    @Override
    public void beginMethod() {

        currentMethod = new DefaultJavaMethod();
        if (currentField == null) {
            currentMethod.setParentClass(classStack.getFirst());
            classStack.getFirst().addMethod(currentMethod);
        }
        currentMethod.setModelWriterFactory(modelWriterFactory);

        addJavaDoc(currentMethod);
        setAnnotations(currentMethod);
    }

    @Override
    public void endMethod(MethodDef def) {

        currentMethod.setLineNumber(def.getLineNumber());

        // basic details
        currentMethod.setName(def.getName());
        currentMethod.setReturns(createType(def.getReturnType(), def.getDimensions()));

        // typeParameters
        if (def.getTypeParams() != null) {
            List<JavaTypeVariable<JavaMethod>> typeParams = new LinkedList<>();
            for (TypeVariableDef typeVariableDef : def.getTypeParams()) {
                typeParams.add(createTypeVariable(typeVariableDef, (JavaMethod) currentMethod));
            }
            currentMethod.setTypeParameters(typeParams);
        }

        // exceptions
        List<JavaClass> exceptions = new LinkedList<>();
        for (TypeDef type : def.getExceptions()) {
            exceptions.add(createType(type, 0));
        }
        currentMethod.setExceptions(exceptions);

        // modifiers
        currentMethod.setModifiers(new LinkedList<>(def.getModifiers()));

        if (!parameterList.isEmpty()) {
            currentMethod.setParameters(new ArrayList<JavaParameter>(parameterList));
            parameterList.clear();
        }

        currentMethod.setSourceCode(def.getBody());
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

    @Override
    public void beginField(FieldDef def) {

        currentField = new DefaultJavaField();
        currentField.setParentClass(classStack.getFirst());
        currentField.setLineNumber(def.getLineNumber());
        currentField.setModelWriterFactory(modelWriterFactory);

        currentField.setName(def.getName());
        currentField.setType(createType(def.getType(), def.getDimensions()));

        currentField.setEnumConstant(def.isEnumConstant());

        // modifiers
        {
            currentField.setModifiers(new LinkedList<>(def.getModifiers()));
        }

        // code body
        currentField.setInitializationExpression(def.getBody());

        // javadoc
        addJavaDoc(currentField);

        // annotations
        setAnnotations(currentField);
    }

    @Override
    public void endField() {

        if (currentArguments != null && !currentArguments.isEmpty()) {
            // DefaultExpressionTransformer??
            DefaultJavaAnnotationAssembler assembler = new DefaultJavaAnnotationAssembler(currentField);

            List<Expression> arguments = new LinkedList<>();
            for (ExpressionDef annoDef : currentArguments) {
                arguments.add(assembler.assemble(annoDef));
            }
            currentField.setEnumConstantArguments(arguments);
            currentArguments.clear();
        }

        classStack.getFirst().addField(currentField);

        currentField = null;
    }

    @Override
    public void addParameter(FieldDef fieldDef) {

        DefaultJavaParameter jParam =
            new ExtendedJavaParameter(createType(fieldDef.getType(), fieldDef.getDimensions()), fieldDef.getName(),
                fieldDef.getModifiers(), fieldDef.isVarArgs());
        if (currentMethod != null) {
            jParam.setDeclarator(currentMethod);
        } else {
            jParam.setDeclarator(currentConstructor);
        }
        jParam.setModelWriterFactory(modelWriterFactory);
        addJavaDoc(jParam);
        setAnnotations(jParam);
        parameterList.add(jParam);
    }

    private void setAnnotations(final AbstractBaseJavaEntity entity) {

        if (!currentAnnoDefs.isEmpty()) {
            DefaultJavaAnnotationAssembler assembler =
                new DefaultJavaAnnotationAssembler((JavaAnnotatedElement) entity);

            List<JavaAnnotation> annotations = new LinkedList<>();
            for (AnnoDef annoDef : currentAnnoDefs) {
                annotations.add(assembler.assemble(annoDef));
            }
            entity.setAnnotations(annotations);
            currentAnnoDefs.clear();
        }
    }

    // Don't resolve until we need it... class hasn't been defined yet.
    @Override
    public void addAnnotation(AnnoDef annotation) {

        currentAnnoDefs.add(annotation);
    }

    @Override
    public void addArgument(ExpressionDef argument) {

        currentArguments.add(argument);
    }

    @Override
    public JavaSource getSource() {

        return source;
    }

    @Override
    public void setUrl(URL url) {

        source.setURL(url);
    }
}
