/*
 * Custom implementation derived from com.thoughtworks.qdox.model.impl.DefaultJavaClass,
 * which itself has been published under Apache Software Foundation (ASF) available at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 */
package com.devonfw.cobigen.javaplugin.merger.libextension;

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
import com.thoughtworks.qdox.model.JavaExecutable;
import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaModule;
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
import com.thoughtworks.qdox.model.impl.DefaultJavaModule;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor.DefaultJavaExports;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor.DefaultJavaOpens;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor.DefaultJavaProvides;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor.DefaultJavaRequires;
import com.thoughtworks.qdox.model.impl.DefaultJavaModuleDescriptor.DefaultJavaUses;
import com.thoughtworks.qdox.model.impl.DefaultJavaPackage;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameter;
import com.thoughtworks.qdox.model.impl.DefaultJavaSource;
import com.thoughtworks.qdox.model.impl.DefaultJavaType;
import com.thoughtworks.qdox.parser.expression.ExpressionDef;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.InitDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef.ExportsDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef.OpensDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef.ProvidesDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef.RequiresDef;
import com.thoughtworks.qdox.parser.structs.ModuleDef.UsesDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.TypeVariableDef;
import com.thoughtworks.qdox.type.TypeResolver;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

/**
 * Custom implementation derived from {@link ModelBuilder} to fix some issues with annotation and javaDoc parsing.
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

  private ClassLibrary classLibrary;

  private DefaultJavaModule module;

  private DefaultJavaModuleDescriptor moduleDescriptor;

  public ModifyableModelBuilder(ClassLibrary classLibrary, DocletTagFactory docletTagFactory) {

    this.docletTagFactory = docletTagFactory;
    this.classLibrary = classLibrary;
    this.source = new DefaultJavaSource(classLibrary);
    this.currentAnnoDefs = new LinkedList<>();
    this.currentArguments = new LinkedList<>();
  }

  @Override
  public void setModelWriterFactory(ModelWriterFactory modelWriterFactory) {

    this.modelWriterFactory = modelWriterFactory;
    this.source.setModelWriterFactory(modelWriterFactory);
  }

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

  @Override
  public void addImport(String importName) {

    this.source.addImport(importName);
  }

  @Override
  public void addJavaDoc(String text) {

    this.lastComment = text;
  }

  @Override
  public void addJavaDocTag(TagDef tagDef) {

    this.lastTagSet.add(tagDef);
  }

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
      newClass.setSuperClass(def.getExtends().size() > 0 ? createType(def.getExtends().iterator().next(), 0) : null);
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
      List<ModifyableJavaTypeVariable<JavaClass>> typeParams = new LinkedList<>();
      for (TypeVariableDef typeVariableDef : def.getTypeParameters()) {
        typeParams.add(createTypeVariable(typeVariableDef, (JavaClass) newClass));
      }
      newClass.setTypeParameters(typeParams);
    }

    // javadoc
    addJavaDoc(newClass);

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
      newClass.setDeclaringClass(this.classStack.getFirst());
    } else {
      this.source.addClass(newClass);
    }
    return newClass;
  }

  @Override
  public void endClass() {

    this.classStack.removeFirst();
  }

  /**
   * this one is specific for those cases where dimensions can be part of both the type and identifier i.e. private
   * String[] matrix[]; //field public abstract String[] getMatrix[](); //method
   *
   * @param typeDef
   * @param dimensions
   * @return the Type
   */
  private DefaultJavaType createType(TypeDef typeDef, int dimensions) {

    if (typeDef == null) {
      return null;
    }
    TypeResolver typeResolver;
    if (this.classStack.isEmpty()) {
      typeResolver = TypeResolver.byPackageName(this.source.getPackageName(), this.classLibrary,
          this.source.getImports());
    } else {
      typeResolver = TypeResolver.byClassName(this.classStack.getFirst().getBinaryName(), this.classLibrary,
          this.source.getImports());
    }

    return TypeAssembler.createUnresolved(typeDef, dimensions, typeResolver);
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

  @Override
  public void beginConstructor() {

    this.currentConstructor = new DefaultJavaConstructor();

    this.currentConstructor.setDeclaringClass(this.classStack.getFirst());

    this.currentConstructor.setModelWriterFactory(this.modelWriterFactory);

    addJavaDoc(this.currentConstructor);
    setAnnotations(this.currentConstructor);

    this.classStack.getFirst().addConstructor(this.currentConstructor);
  }

  @Override
  public void endConstructor(MethodDef def) {

    this.currentConstructor.setLineNumber(def.getLineNumber());

    // basic details
    this.currentConstructor.setName(def.getName());

    // typeParameters
    if (def.getTypeParams() != null) {
      List<JavaTypeVariable<JavaConstructor>> typeParams = new LinkedList<>();
      for (TypeVariableDef typeVariableDef : def.getTypeParams()) {
        typeParams.add(createTypeVariable(typeVariableDef, (JavaConstructor) this.currentConstructor));
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

  @Override
  public void beginMethod() {

    this.currentMethod = new DefaultJavaMethod();
    if (this.currentField == null) {
      this.currentMethod.setDeclaringClass(this.classStack.getFirst());
      this.classStack.getFirst().addMethod(this.currentMethod);
    }
    this.currentMethod.setModelWriterFactory(this.modelWriterFactory);

    addJavaDoc(this.currentMethod);
    setAnnotations(this.currentMethod);
  }

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

  private <G extends JavaGenericDeclaration> ModifyableJavaTypeVariable<G> createTypeVariable(
      TypeVariableDef typeVariableDef, G genericDeclaration) {

    if (typeVariableDef == null) {
      return null;
    }

    JavaClass declaringClass = getContext(genericDeclaration);

    if (declaringClass == null) {
      return null;
    }

    TypeResolver typeResolver = TypeResolver.byClassName(declaringClass.getBinaryName(), this.classLibrary,
        this.source.getImports());

    ModifyableJavaTypeVariable<G> result = new ModifyableJavaTypeVariable<G>(typeVariableDef.getName(), typeResolver);

    if (typeVariableDef.getBounds() != null && !typeVariableDef.getBounds().isEmpty()) {
      List<JavaType> bounds = new LinkedList<JavaType>();
      for (TypeDef typeDef : typeVariableDef.getBounds()) {
        bounds.add(createType(typeDef, 0));
      }
      result.setBounds(bounds);
    }
    return result;
  }

  private static JavaClass getContext(JavaGenericDeclaration genericDeclaration) {

    JavaClass result;
    if (genericDeclaration instanceof JavaClass) {
      result = (JavaClass) genericDeclaration;
    } else if (genericDeclaration instanceof JavaExecutable) {
      result = ((JavaExecutable) genericDeclaration).getDeclaringClass();
    } else {
      throw new IllegalArgumentException("Unknown JavaGenericDeclaration implementation");
    }
    return result;
  }

  @Override
  public void beginField(FieldDef def) {

    this.currentField = new DefaultJavaField(def.getName());
    this.currentField.setDeclaringClass(this.classStack.getFirst());
    this.currentField.setLineNumber(def.getLineNumber());
    this.currentField.setModelWriterFactory(this.modelWriterFactory);

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

  @Override
  public void endField() {

    if (this.currentArguments != null && !this.currentArguments.isEmpty()) {
      TypeResolver typeResolver;
      if (this.classStack.isEmpty()) {
        typeResolver = TypeResolver.byPackageName(this.source.getPackageName(), this.classLibrary,
            this.source.getImports());
      } else {
        typeResolver = TypeResolver.byClassName(this.classStack.getFirst().getBinaryName(), this.classLibrary,
            this.source.getImports());
      }

      // DefaultExpressionTransformer??
      DefaultJavaAnnotationAssembler assembler = new DefaultJavaAnnotationAssembler(
          this.currentField.getDeclaringClass(), this.classLibrary, typeResolver);

      List<Expression> arguments = new LinkedList<Expression>();
      for (ExpressionDef annoDef : this.currentArguments) {
        arguments.add(assembler.assemble(annoDef));
      }
      this.currentField.setEnumConstantArguments(arguments);
      this.currentArguments.clear();
    }

    this.classStack.getFirst().addField(this.currentField);

    this.currentField = null;
  }

  @Override
  public void addParameter(FieldDef fieldDef) {

    DefaultJavaParameter jParam = new ExtendedJavaParameter(createType(fieldDef.getType(), fieldDef.getDimensions()),
        fieldDef.getName(), fieldDef.getModifiers(), fieldDef.isVarArgs());
    if (this.currentMethod != null) {
      jParam.setExecutable(this.currentMethod);
    } else {
      jParam.setExecutable(this.currentConstructor);
    }
    jParam.setModelWriterFactory(this.modelWriterFactory);
    addJavaDoc(jParam);
    setAnnotations(jParam);
    this.parameterList.add(jParam);
  }

  private void setAnnotations(final AbstractBaseJavaEntity entity) {

    if (!this.currentAnnoDefs.isEmpty()) {
      TypeResolver typeResolver;
      if (this.classStack.isEmpty()) {
        typeResolver = TypeResolver.byPackageName(this.source.getPackageName(), this.classLibrary,
            this.source.getImports());
      } else {
        typeResolver = TypeResolver.byClassName(this.classStack.getFirst().getBinaryName(), this.classLibrary,
            this.source.getImports());
      }

      DefaultJavaAnnotationAssembler assembler = new DefaultJavaAnnotationAssembler(entity.getDeclaringClass(),
          this.classLibrary, typeResolver);

      List<JavaAnnotation> annotations = new LinkedList<JavaAnnotation>();
      for (AnnoDef annoDef : this.currentAnnoDefs) {
        annotations.add(assembler.assemble(annoDef));
      }
      entity.setAnnotations(annotations);
      this.currentAnnoDefs.clear();
    }
  }

  // Don't resolve until we need it... class hasn't been defined yet.
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

  @Override
  public void setModule(ModuleDef moduleDef) {

    this.moduleDescriptor = new DefaultJavaModuleDescriptor(moduleDef.getName());
    this.module = new DefaultJavaModule(moduleDef.getName(), this.moduleDescriptor);
  }

  @Override
  public void addExports(ExportsDef exportsDef) {

    List<JavaModule> targets = new ArrayList<JavaModule>(exportsDef.getTargets().size());
    for (String moduleName : exportsDef.getTargets()) {
      targets.add(new DefaultJavaModule(moduleName, null));
    }

    DefaultJavaExports exports = new DefaultJavaExports(new DefaultJavaPackage(exportsDef.getSource()), targets);
    exports.setLineNumber(exportsDef.getLineNumber());
    exports.setModelWriterFactory(this.modelWriterFactory);
    this.moduleDescriptor.addExports(exports);
  }

  @Override
  public void addRequires(RequiresDef requiresDef) {

    JavaModule module = new DefaultJavaModule(requiresDef.getName(), null);
    DefaultJavaRequires requires = new DefaultJavaRequires(module, requiresDef.getModifiers());
    requires.setLineNumber(requiresDef.getLineNumber());
    requires.setModelWriterFactory(this.modelWriterFactory);
    this.moduleDescriptor.addRequires(requires);
  }

  @Override
  public void addOpens(OpensDef opensDef) {

    List<JavaModule> targets = new ArrayList<JavaModule>(opensDef.getTargets().size());
    for (String moduleName : opensDef.getTargets()) {
      targets.add(new DefaultJavaModule(moduleName, null));
    }

    DefaultJavaOpens exports = new DefaultJavaOpens(new DefaultJavaPackage(opensDef.getSource()), targets);
    exports.setLineNumber(opensDef.getLineNumber());
    exports.setModelWriterFactory(this.modelWriterFactory);
    this.moduleDescriptor.addOpens(exports);
  }

  @Override
  public void addProvides(ProvidesDef providesDef) {

    JavaClass service = createType(providesDef.getService(), 0);
    List<JavaClass> implementations = new LinkedList<JavaClass>();
    for (TypeDef implementType : providesDef.getImplementations()) {
      implementations.add(createType(implementType, 0));
    }
    DefaultJavaProvides provides = new DefaultJavaProvides(service, implementations);
    provides.setLineNumber(providesDef.getLineNumber());
    provides.setModelWriterFactory(this.modelWriterFactory);
    this.moduleDescriptor.addProvides(provides);
  }

  @Override
  public void addUses(UsesDef usesDef) {

    DefaultJavaUses uses = new DefaultJavaUses(createType(usesDef.getService(), 0));
    uses.setLineNumber(usesDef.getLineNumber());
    uses.setModelWriterFactory(this.modelWriterFactory);
    this.moduleDescriptor.addUses(uses);
  }

  @Override
  public JavaModule getModuleInfo() {

    return this.module;
  }
}
