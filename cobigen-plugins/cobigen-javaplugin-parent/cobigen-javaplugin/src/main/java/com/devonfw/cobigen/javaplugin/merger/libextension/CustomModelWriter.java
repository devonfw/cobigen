/*
 * Custom implementation derived from com.thoughtworks.qdox.model.impl.DefaultJavaClass,
 * which itself has been published under Apache Software Foundation (ASF) available at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 */
package com.devonfw.cobigen.javaplugin.merger.libextension;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaInitializer;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaModuleDescriptor;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaExports;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaOpens;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaProvides;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaRequires;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaUses;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.Expression;
import com.thoughtworks.qdox.writer.ModelWriter;
import com.thoughtworks.qdox.writer.impl.DefaultModelWriter;
import com.thoughtworks.qdox.writer.impl.IndentBuffer;

/**
 * Custom implementation derived from {@link DefaultModelWriter} to fix some issues with annotation and javaDoc
 * printing.
 */
@SuppressWarnings("javadoc")
public class CustomModelWriter implements ModelWriter {

  private IndentBuffer buffer = new IndentBuffer();

  /**
   * All information is written to this buffer. When extending this class you should write to this buffer
   *
   * @return the buffer
   */
  protected final IndentBuffer getBuffer() {

    return this.buffer;
  }

  @Override
  public ModelWriter writeSource(JavaSource source) {

    // package statement
    writePackage(source.getPackage());

    // import statement
    for (String imprt : source.getImports()) {
      this.buffer.write("import ");
      this.buffer.write(imprt);
      this.buffer.write(';');
      this.buffer.newline();
    }
    if (source.getImports().size() > 0) {
      this.buffer.newline();
    }

    // classes
    for (ListIterator<JavaClass> iter = source.getClasses().listIterator(); iter.hasNext();) {
      JavaClass cls = iter.next();
      writeClass(cls);
      if (iter.hasNext()) {
        this.buffer.newline();
      }
    }
    return this;
  }

  @Override
  public ModelWriter writePackage(JavaPackage pckg) {

    if (pckg != null) {
      commentHeader(pckg);
      this.buffer.write("package ");
      this.buffer.write(pckg.getName());
      this.buffer.write(';');
      this.buffer.newline();
      this.buffer.newline();
    }
    return this;
  }

  @Override
  public ModelWriter writeClass(JavaClass cls) {

    commentHeader(cls);

    writeAccessibilityModifier(cls.getModifiers());
    writeNonAccessibilityModifiers(cls.getModifiers());

    this.buffer.write(
        cls.isEnum() ? "enum " : cls.isInterface() ? "interface " : cls.isAnnotation() ? "@interface " : "class ");
    this.buffer.write(cls.getName());

    writeTypeParameters(cls);

    // subclass
    if (cls.getSuperClass() != null) {
      String className = cls.getSuperClass().getFullyQualifiedName();
      if (!"java.lang.Object".equals(className) && !"java.lang.Enum".equals(className)) {
        this.buffer.write(" extends ");
        this.buffer.write(cls.getSuperClass().getGenericValue());
      }
    }

    // implements
    if (cls.getImplements().size() > 0) {
      this.buffer.write(cls.isInterface() ? " extends " : " implements ");

      for (ListIterator<JavaType> iter = cls.getImplements().listIterator(); iter.hasNext();) {
        this.buffer.write(iter.next().getGenericValue());
        if (iter.hasNext()) {
          this.buffer.write(", ");
        }
      }
    }

    return writeClassBody(cls);
  }

  private ModelWriter writeClassBody(JavaClass cls) {

    this.buffer.write(" {");
    this.buffer.newline();
    this.buffer.indent();

    // fields
    if (cls.getSuperClass() != null && "java.lang.Enum".equals(cls.getSuperClass().getFullyQualifiedName())) {
      Iterator<JavaField> it = cls.getFields().iterator();
      while (it.hasNext()) {
        JavaField curr = it.next();
        commentHeader(curr);
        this.buffer.newline();
        this.buffer.write(curr.getName());
        if (it.hasNext()) {
          this.buffer.write(", ");
        }
      }
    } else {
      for (JavaField javaField : cls.getFields()) {
        this.buffer.newline();
        writeField(javaField);
      }
    }

    // constructors
    for (JavaConstructor javaConstructor : cls.getConstructors()) {
      this.buffer.newline();
      writeConstructor(javaConstructor);
    }

    // initializer
    for (JavaInitializer innerInitializer : cls.getInitializers()) {
      this.buffer.newline();
      writeInitializer(innerInitializer);
    }

    // methods
    for (JavaMethod javaMethod : cls.getMethods()) {
      this.buffer.newline();
      writeMethod(javaMethod);
    }

    // inner-classes
    for (JavaClass innerCls : cls.getNestedClasses()) {
      this.buffer.newline();
      writeClass(innerCls);
    }

    this.buffer.deindent();
    this.buffer.newline();
    this.buffer.write('}');
    this.buffer.newline();
    return this;
  }

  @Override
  public ModelWriter writeInitializer(JavaInitializer init) {

    if (init.isStatic()) {
      this.buffer.write("static ");
    }
    this.buffer.write('{');
    this.buffer.newline();
    this.buffer.indent();

    this.buffer.write(init.getBlockContent());

    this.buffer.deindent();
    this.buffer.newline();
    this.buffer.write('}');
    this.buffer.newline();
    return this;
  }

  @Override
  public ModelWriter writeField(JavaField field) {

    commentHeader(field);

    writeAllModifiers(field.getModifiers());
    if (!field.isEnumConstant()) {
      this.buffer.write(field.getType().getGenericValue());
      this.buffer.write(' ');
    }
    this.buffer.write(field.getName());

    if (field.isEnumConstant()) {
      if (field.getEnumConstantArguments() != null && !field.getEnumConstantArguments().isEmpty()) {
        this.buffer.write("( ");
        for (Iterator<Expression> iter = field.getEnumConstantArguments().listIterator(); iter.hasNext();) {
          this.buffer.write(iter.next().getParameterValue().toString());
          if (iter.hasNext()) {
            this.buffer.write(", ");
          }
        }
        this.buffer.write(" )");
      }
      if (field.getEnumConstantClass() != null) {
        writeClassBody(field.getEnumConstantClass());
      }
    } else {
      if (field.getInitializationExpression() != null) {
        String fieldExpression = field.getInitializationExpression();
        fieldExpression = StringUtils.strip(fieldExpression, "\n\r");
        fieldExpression = fieldExpression.trim();
        if (!fieldExpression.isEmpty()) {
          {
            this.buffer.write(" = ");
          }
          this.buffer.write(field.getInitializationExpression());

        }
      }
    }
    this.buffer.write(';');
    this.buffer.newline();
    return this;
  }

  @Override
  public ModelWriter writeConstructor(JavaConstructor constructor) {

    commentHeader(constructor);
    writeAllModifiers(constructor.getModifiers());

    this.buffer.write(constructor.getName());
    this.buffer.write('(');
    for (ListIterator<JavaParameter> iter = constructor.getParameters().listIterator(); iter.hasNext();) {
      writeParameter(iter.next());
      if (iter.hasNext()) {
        this.buffer.write(", ");
      }
    }
    this.buffer.write(')');

    if (constructor.getExceptions().size() > 0) {
      this.buffer.write(" throws ");
      for (Iterator<JavaClass> excIter = constructor.getExceptions().iterator(); excIter.hasNext();) {
        this.buffer.write(excIter.next().getGenericValue());
        if (excIter.hasNext()) {
          this.buffer.write(", ");
        }
      }
    }

    this.buffer.write(" {");
    if (constructor.getSourceCode() != null) {
      this.buffer.write(constructor.getSourceCode());
    }
    this.buffer.write('}');
    this.buffer.newline();

    return this;
  }

  @Override
  public ModelWriter writeMethod(JavaMethod method) {

    commentHeader(method);
    writeAccessibilityModifier(method.getModifiers());
    writeNonAccessibilityModifiers(method.getModifiers());

    if (writeTypeParameters(method)) {
      this.buffer.write(' ');
    }

    this.buffer.write(method.getReturnType().getGenericValue());
    this.buffer.write(' ');
    this.buffer.write(method.getName());
    this.buffer.write('(');
    for (ListIterator<JavaParameter> iter = method.getParameters().listIterator(); iter.hasNext();) {
      writeParameter(iter.next());
      if (iter.hasNext()) {
        this.buffer.write(", ");
      }

    }
    this.buffer.write(')');
    if (method.getExceptions().size() > 0) {
      this.buffer.write(" throws ");
      for (Iterator<JavaClass> excIter = method.getExceptions().iterator(); excIter.hasNext();) {
        this.buffer.write(excIter.next().getGenericValue());
        if (excIter.hasNext()) {
          this.buffer.write(", ");
        }
      }
    }
    if (method.getSourceCode() != null && method.getSourceCode().length() > 0) {
      this.buffer.write(" {");
      this.buffer.write(method.getSourceCode());
      this.buffer.write('}');
      this.buffer.newline();
    } else {
      this.buffer.write(';');
      this.buffer.newline();
    }
    return this;
  }

  private boolean writeTypeParameters(JavaGenericDeclaration decl) {

    List<JavaTypeVariable<JavaGenericDeclaration>> typeParameters = decl.getTypeParameters();
    if (typeParameters.size() == 0) {
      return false;
    }

    this.buffer.write("<");
    boolean first = true;
    for (JavaTypeVariable<?> v : typeParameters) {
      if (!first) {
        this.buffer.write(",");
        this.buffer.write(' ');
      } else {
        first = false;
      }
      this.buffer.write(v.getGenericValue());
    }
    this.buffer.write(">");
    return true;
  }

  private void writeNonAccessibilityModifiers(Collection<String> modifiers) {

    for (String modifier : modifiers) {
      if (!modifier.startsWith("p")) {
        this.buffer.write(modifier);
        this.buffer.write(' ');
      }
    }
  }

  private void writeAccessibilityModifier(Collection<String> modifiers) {

    for (String modifier : modifiers) {
      if (modifier.startsWith("p")) {
        this.buffer.write(modifier);
        this.buffer.write(' ');
      }
    }
  }

  private void writeAllModifiers(List<String> modifiers) {

    for (String modifier : modifiers) {
      this.buffer.write(modifier);
      this.buffer.write(' ');
    }
  }

  @Override
  public ModelWriter writeAnnotation(JavaAnnotation annotation) {

    this.buffer.write('@');
    this.buffer.write(annotation.getType().getGenericValue());
    if (!annotation.getPropertyMap().isEmpty()) {
      this.buffer.indent();
      this.buffer.write('(');
      Set<Entry<String, AnnotationValue>> annotationEntrySet = annotation.getPropertyMap().entrySet();
      Iterator<Map.Entry<String, AnnotationValue>> iterator = annotationEntrySet.iterator();
      while (iterator.hasNext()) {
        Map.Entry<String, AnnotationValue> entry = iterator.next();
        if (annotationEntrySet.size() != 1 || !"value".equals(entry.getKey())) {
          this.buffer.write(entry.getKey());
          this.buffer.write('=');
        }

        if (entry.getValue().getParameterValue() instanceof JavaAnnotation) {
          writeAnnotation((JavaAnnotation) entry.getValue().getParameterValue());
        } else if (entry.getValue().getParameterValue() instanceof Collection<?>) {
          Collection<?> annotations = (Collection<?>) entry.getValue().getParameterValue();
          Object[] a = annotations.toArray();
          this.buffer.write("{");
          for (int i = 0; i < annotations.toArray().length; i++) {
            if (a[i] instanceof JavaAnnotation) {
              if (i > 0) {
                this.buffer.write(", ");
              }
              writeAnnotation((JavaAnnotation) a[i]);
            } else {
              if (i > 0) {
                this.buffer.write(", " + a[i].toString());
              } else {
                this.buffer.write(a[i].toString());
              }
            }
          }
          this.buffer.write("}");
        } else {
          this.buffer.write(entry.getValue().toString());
        }

        if (iterator.hasNext()) {
          this.buffer.write(',');
          this.buffer.newline();
        }
      }
      this.buffer.write(')');
      this.buffer.deindent();
    }
    this.buffer.newline();
    return this;
  }

  @Override
  public ModelWriter writeParameter(JavaParameter parameter) {

    commentHeader(parameter);
    writeAllModifiers(((ExtendedJavaParameter) parameter).getModifiers());
    this.buffer.write(parameter.getGenericValue());
    if (parameter.isVarArgs()) {
      this.buffer.write("...");
    }
    this.buffer.write(' ');
    this.buffer.write(parameter.getName());
    return this;
  }

  protected void commentHeader(JavaAnnotatedElement entity) {

    if (entity.getComment() != null || (entity.getTags().size() > 0)) {
      this.buffer.write("/**");
      this.buffer.newline();

      if (entity.getComment() != null && entity.getComment().length() > 0) {
        this.buffer.write(" * ");

        this.buffer.write(entity.getComment().replaceAll("\n", "\n * "));

        this.buffer.newline();
      }

      if (entity.getTags().size() > 0) {
        if (entity.getComment() != null && entity.getComment().length() > 0) {
          this.buffer.write(" *");
          this.buffer.newline();
        }
        for (DocletTag docletTag : entity.getTags()) {
          this.buffer.write(" * @");
          this.buffer.write(docletTag.getName());
          if (docletTag.getValue().length() > 0) {
            this.buffer.write(' ');
            this.buffer.write(docletTag.getValue());
          }
          this.buffer.newline();
        }
      }

      this.buffer.write(" */");
      this.buffer.newline();
    }
    if (entity.getAnnotations() != null) {
      for (JavaAnnotation annotation : entity.getAnnotations()) {
        if (entity.getAnnotations().get(entity.getAnnotations().size() - 1) != null) {
          writeAnnotation(annotation);
        }
      }
    }
  }

  @Override
  public String toString() {

    return this.buffer.toString();
  }

  /** {@inheritDoc} */
  @Override
  public ModelWriter writeModuleDescriptor(JavaModuleDescriptor descriptor) {

    if (descriptor.isOpen()) {
      this.buffer.write("open ");
    }
    this.buffer.write("module " + descriptor.getName() + " {");
    this.buffer.newline();
    this.buffer.indent();

    for (JavaRequires requires : descriptor.getRequires()) {
      this.buffer.newline();
      writeModuleRequires(requires);
    }

    for (JavaExports exports : descriptor.getExports()) {
      this.buffer.newline();
      writeModuleExports(exports);
    }

    for (JavaOpens opens : descriptor.getOpens()) {
      this.buffer.newline();
      writeModuleOpens(opens);
    }

    for (JavaProvides provides : descriptor.getProvides()) {
      this.buffer.newline();
      writeModuleProvides(provides);
    }

    for (JavaUses uses : descriptor.getUses()) {
      this.buffer.newline();
      writeModuleUses(uses);
    }

    this.buffer.newline();
    this.buffer.deindent();
    this.buffer.write('}');
    this.buffer.newline();
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public ModelWriter writeModuleExports(JavaExports exports) {

    this.buffer.write("exports ");
    this.buffer.write(exports.getSource().getName());
    if (!exports.getTargets().isEmpty()) {
      this.buffer.write(" to ");
      Iterator<JavaModule> targets = exports.getTargets().iterator();
      while (targets.hasNext()) {
        JavaModule target = targets.next();
        this.buffer.write(target.getName());
        if (targets.hasNext()) {
          this.buffer.write(", ");
        }
      }
    }
    this.buffer.write(';');
    this.buffer.newline();
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public ModelWriter writeModuleOpens(JavaOpens opens) {

    this.buffer.write("opens ");
    this.buffer.write(opens.getSource().getName());
    if (!opens.getTargets().isEmpty()) {
      this.buffer.write(" to ");
      Iterator<JavaModule> targets = opens.getTargets().iterator();
      while (targets.hasNext()) {
        JavaModule target = targets.next();
        this.buffer.write(target.getName());
        if (targets.hasNext()) {
          this.buffer.write(", ");
        }
      }
    }
    this.buffer.write(';');
    this.buffer.newline();
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public ModelWriter writeModuleProvides(JavaProvides provides) {

    this.buffer.write("provides ");
    this.buffer.write(provides.getService().getName());
    this.buffer.write(" with ");
    Iterator<JavaClass> providers = provides.getProviders().iterator();
    while (providers.hasNext()) {
      JavaClass provider = providers.next();
      this.buffer.write(provider.getName());
      if (providers.hasNext()) {
        this.buffer.write(", ");
      }
    }
    this.buffer.write(';');
    this.buffer.newline();
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public ModelWriter writeModuleRequires(JavaRequires requires) {

    this.buffer.write("requires ");
    writeAccessibilityModifier(requires.getModifiers());
    writeNonAccessibilityModifiers(requires.getModifiers());
    this.buffer.write(requires.getModule().getName());
    this.buffer.write(';');
    this.buffer.newline();
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public ModelWriter writeModuleUses(JavaUses uses) {

    this.buffer.write("uses ");
    this.buffer.write(uses.getService().getName());
    this.buffer.write(';');
    this.buffer.newline();
    return this;
  }

}
