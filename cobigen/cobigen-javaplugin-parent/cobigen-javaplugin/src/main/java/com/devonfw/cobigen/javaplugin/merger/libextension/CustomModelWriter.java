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
 * Custom implementation derived from {@link DefaultModelWriter} to fix some issues with annotation and
 * javaDoc printing.
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
        return buffer;
    }

    @Override
    public ModelWriter writeSource(JavaSource source) {
        // package statement
        writePackage(source.getPackage());

        // import statement
        for (String imprt : source.getImports()) {
            buffer.write("import ");
            buffer.write(imprt);
            buffer.write(';');
            buffer.newline();
        }
        if (source.getImports().size() > 0) {
            buffer.newline();
        }

        // classes
        for (ListIterator<JavaClass> iter = source.getClasses().listIterator(); iter.hasNext();) {
            JavaClass cls = iter.next();
            writeClass(cls);
            if (iter.hasNext()) {
                buffer.newline();
            }
        }
        return this;
    }

    @Override
    public ModelWriter writePackage(JavaPackage pckg) {
        if (pckg != null) {
            commentHeader(pckg);
            buffer.write("package ");
            buffer.write(pckg.getName());
            buffer.write(';');
            buffer.newline();
            buffer.newline();
        }
        return this;
    }

    @Override
    public ModelWriter writeClass(JavaClass cls) {
        commentHeader(cls);

        writeAccessibilityModifier(cls.getModifiers());
        writeNonAccessibilityModifiers(cls.getModifiers());

        buffer.write(
            cls.isEnum() ? "enum " : cls.isInterface() ? "interface " : cls.isAnnotation() ? "@interface " : "class ");
        buffer.write(cls.getName());

        writeTypeParameters(cls);

        // subclass
        if (cls.getSuperClass() != null) {
            String className = cls.getSuperClass().getFullyQualifiedName();
            if (!"java.lang.Object".equals(className) && !"java.lang.Enum".equals(className)) {
                buffer.write(" extends ");
                buffer.write(cls.getSuperClass().getGenericValue());
            }
        }

        // implements
        if (cls.getImplements().size() > 0) {
            buffer.write(cls.isInterface() ? " extends " : " implements ");

            for (ListIterator<JavaType> iter = cls.getImplements().listIterator(); iter.hasNext();) {
                buffer.write(iter.next().getGenericValue());
                if (iter.hasNext()) {
                    buffer.write(", ");
                }
            }
        }

        return writeClassBody(cls);
    }

    private ModelWriter writeClassBody(JavaClass cls) {
        buffer.write(" {");
        buffer.newline();
        buffer.indent();

        // fields
        if (cls.getSuperClass() != null && "java.lang.Enum".equals(cls.getSuperClass().getFullyQualifiedName())) {
            Iterator<JavaField> it = cls.getFields().iterator();
            while (it.hasNext()) {
                JavaField curr = it.next();
                commentHeader(curr);
                buffer.newline();
                buffer.write(curr.getName());
                if (it.hasNext()) {
                    buffer.write(", ");
                }
            }
        } else {
            for (JavaField javaField : cls.getFields()) {
                buffer.newline();
                writeField(javaField);
            }
        }

        // constructors
        for (JavaConstructor javaConstructor : cls.getConstructors()) {
            buffer.newline();
            writeConstructor(javaConstructor);
        }

        // initializer
        for (JavaInitializer innerInitializer : cls.getInitializers()) {
            buffer.newline();
            writeInitializer(innerInitializer);
        }

        // methods
        for (JavaMethod javaMethod : cls.getMethods()) {
            buffer.newline();
            writeMethod(javaMethod);
        }

        // inner-classes
        for (JavaClass innerCls : cls.getNestedClasses()) {
            buffer.newline();
            writeClass(innerCls);
        }

        buffer.deindent();
        buffer.newline();
        buffer.write('}');
        buffer.newline();
        return this;
    }

    @Override
    public ModelWriter writeInitializer(JavaInitializer init) {
        if (init.isStatic()) {
            buffer.write("static ");
        }
        buffer.write('{');
        buffer.newline();
        buffer.indent();

        buffer.write(init.getBlockContent());

        buffer.deindent();
        buffer.newline();
        buffer.write('}');
        buffer.newline();
        return this;
    }

    @Override
    public ModelWriter writeField(JavaField field) {
        commentHeader(field);

        writeAllModifiers(field.getModifiers());
        if (!field.isEnumConstant()) {
            buffer.write(field.getType().getGenericValue());
            buffer.write(' ');
        }
        buffer.write(field.getName());

        if (field.isEnumConstant()) {
            if (field.getEnumConstantArguments() != null && !field.getEnumConstantArguments().isEmpty()) {
                buffer.write("( ");
                for (Iterator<Expression> iter = field.getEnumConstantArguments().listIterator(); iter.hasNext();) {
                    buffer.write(iter.next().getParameterValue().toString());
                    if (iter.hasNext()) {
                        buffer.write(", ");
                    }
                }
                buffer.write(" )");
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
                        buffer.write(" = ");
                    }
                    buffer.write(field.getInitializationExpression());

                }
            }
        }
        buffer.write(';');
        buffer.newline();
        return this;
    }

    @Override
    public ModelWriter writeConstructor(JavaConstructor constructor) {
        commentHeader(constructor);
        writeAllModifiers(constructor.getModifiers());

        buffer.write(constructor.getName());
        buffer.write('(');
        for (ListIterator<JavaParameter> iter = constructor.getParameters().listIterator(); iter.hasNext();) {
            writeParameter(iter.next());
            if (iter.hasNext()) {
                buffer.write(", ");
            }
        }
        buffer.write(')');

        if (constructor.getExceptions().size() > 0) {
            buffer.write(" throws ");
            for (Iterator<JavaClass> excIter = constructor.getExceptions().iterator(); excIter.hasNext();) {
                buffer.write(excIter.next().getGenericValue());
                if (excIter.hasNext()) {
                    buffer.write(", ");
                }
            }
        }

        buffer.write(" {");
        buffer.newline();
        if (constructor.getSourceCode() != null) {
            buffer.write(constructor.getSourceCode());
        }
        buffer.write('}');
        buffer.newline();

        return this;
    }

    @Override
    public ModelWriter writeMethod(JavaMethod method) {
        commentHeader(method);
        writeAccessibilityModifier(method.getModifiers());
        writeNonAccessibilityModifiers(method.getModifiers());

        if (writeTypeParameters(method)) {
            buffer.write(' ');
        }

        buffer.write(method.getReturnType().getGenericValue());
        buffer.write(' ');
        buffer.write(method.getName());
        buffer.write('(');
        for (ListIterator<JavaParameter> iter = method.getParameters().listIterator(); iter.hasNext();) {
            writeParameter(iter.next());
            if (iter.hasNext()) {
                buffer.write(", ");
            }

        }
        buffer.write(')');
        if (method.getExceptions().size() > 0) {
            buffer.write(" throws ");
            for (Iterator<JavaClass> excIter = method.getExceptions().iterator(); excIter.hasNext();) {
                buffer.write(excIter.next().getGenericValue());
                if (excIter.hasNext()) {
                    buffer.write(", ");
                }
            }
        }
        if (method.getSourceCode() != null && method.getSourceCode().length() > 0) {
            buffer.write(" {");
            buffer.write(method.getSourceCode());
            buffer.write('}');
            buffer.newline();
        } else {
            buffer.write(';');
            buffer.newline();
        }
        return this;
    }

    private boolean writeTypeParameters(JavaGenericDeclaration decl) {
        List<JavaTypeVariable<JavaGenericDeclaration>> typeParameters = decl.getTypeParameters();
        if (typeParameters.size() == 0) {
            return false;
        }

        buffer.write("<");
        boolean first = true;
        for (JavaTypeVariable<?> v : typeParameters) {
            if (!first) {
                buffer.write(",");
                buffer.write(' ');
            } else {
                first = false;
            }
            buffer.write(v.getGenericValue());
        }
        buffer.write(">");
        return true;
    }

    private void writeNonAccessibilityModifiers(Collection<String> modifiers) {
        for (String modifier : modifiers) {
            if (!modifier.startsWith("p")) {
                buffer.write(modifier);
                buffer.write(' ');
            }
        }
    }

    private void writeAccessibilityModifier(Collection<String> modifiers) {
        for (String modifier : modifiers) {
            if (modifier.startsWith("p")) {
                buffer.write(modifier);
                buffer.write(' ');
            }
        }
    }

    private void writeAllModifiers(List<String> modifiers) {
        for (String modifier : modifiers) {
            buffer.write(modifier);
            buffer.write(' ');
        }
    }

    @Override
    public ModelWriter writeAnnotation(JavaAnnotation annotation) {
        buffer.write('@');
        buffer.write(annotation.getType().getGenericValue());
        if (!annotation.getPropertyMap().isEmpty()) {
            buffer.indent();
            buffer.write('(');
            Set<Entry<String, AnnotationValue>> annotationEntrySet = annotation.getPropertyMap().entrySet();
            Iterator<Map.Entry<String, AnnotationValue>> iterator = annotationEntrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, AnnotationValue> entry = iterator.next();
                if (annotationEntrySet.size() != 1 || !"value".equals(entry.getKey())) {
                    buffer.write(entry.getKey());
                    buffer.write('=');
                }

                if (entry.getValue().getParameterValue() instanceof JavaAnnotation) {
                    writeAnnotation((JavaAnnotation) entry.getValue().getParameterValue());
                } else if (entry.getValue().getParameterValue() instanceof Collection<?>) {
                    Collection<?> annotations = (Collection<?>) entry.getValue().getParameterValue();
                    Object[] a = annotations.toArray();
                    buffer.write("{");
                    for (int i = 0; i < annotations.toArray().length; i++) {
                        if (a[i] instanceof JavaAnnotation) {
                            if (i > 0) {
                                buffer.write(", ");
                            }
                            writeAnnotation((JavaAnnotation) a[i]);
                        } else {
                            if (i > 0) {
                                buffer.write(", " + a[i].toString());
                            } else {
                                buffer.write(a[i].toString());
                            }
                        }
                    }
                    buffer.write("}");
                } else {
                    buffer.write(entry.getValue().toString());
                }

                if (iterator.hasNext()) {
                    buffer.write(',');
                    buffer.newline();
                }
            }
            buffer.write(')');
            buffer.deindent();
        }
        buffer.newline();
        return this;
    }

    @Override
    public ModelWriter writeParameter(JavaParameter parameter) {
        commentHeader(parameter);
        writeAllModifiers(((ExtendedJavaParameter) parameter).getModifiers());
        buffer.write(parameter.getGenericValue());
        if (parameter.isVarArgs()) {
            buffer.write("...");
        }
        buffer.write(' ');
        buffer.write(parameter.getName());
        return this;
    }

    protected void commentHeader(JavaAnnotatedElement entity) {
        if (entity.getComment() != null || (entity.getTags().size() > 0)) {
            buffer.write("/**");
            buffer.newline();

            if (entity.getComment() != null && entity.getComment().length() > 0) {
                buffer.write(" * ");

                buffer.write(entity.getComment().replaceAll("\n", "\n * "));

                buffer.newline();
            }

            if (entity.getTags().size() > 0) {
                if (entity.getComment() != null && entity.getComment().length() > 0) {
                    buffer.write(" *");
                    buffer.newline();
                }
                for (DocletTag docletTag : entity.getTags()) {
                    buffer.write(" * @");
                    buffer.write(docletTag.getName());
                    if (docletTag.getValue().length() > 0) {
                        buffer.write(' ');
                        buffer.write(docletTag.getValue());
                    }
                    buffer.newline();
                }
            }

            buffer.write(" */");
            buffer.newline();
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
        return buffer.toString();
    }

    /** {@inheritDoc} */
    @Override
    public ModelWriter writeModuleDescriptor(JavaModuleDescriptor descriptor) {
        if (descriptor.isOpen()) {
            buffer.write("open ");
        }
        buffer.write("module " + descriptor.getName() + " {");
        buffer.newline();
        buffer.indent();

        for (JavaRequires requires : descriptor.getRequires()) {
            buffer.newline();
            writeModuleRequires(requires);
        }

        for (JavaExports exports : descriptor.getExports()) {
            buffer.newline();
            writeModuleExports(exports);
        }

        for (JavaOpens opens : descriptor.getOpens()) {
            buffer.newline();
            writeModuleOpens(opens);
        }

        for (JavaProvides provides : descriptor.getProvides()) {
            buffer.newline();
            writeModuleProvides(provides);
        }

        for (JavaUses uses : descriptor.getUses()) {
            buffer.newline();
            writeModuleUses(uses);
        }

        buffer.newline();
        buffer.deindent();
        buffer.write('}');
        buffer.newline();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ModelWriter writeModuleExports(JavaExports exports) {
        buffer.write("exports ");
        buffer.write(exports.getSource().getName());
        if (!exports.getTargets().isEmpty()) {
            buffer.write(" to ");
            Iterator<JavaModule> targets = exports.getTargets().iterator();
            while (targets.hasNext()) {
                JavaModule target = targets.next();
                buffer.write(target.getName());
                if (targets.hasNext()) {
                    buffer.write(", ");
                }
            }
        }
        buffer.write(';');
        buffer.newline();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ModelWriter writeModuleOpens(JavaOpens opens) {
        buffer.write("opens ");
        buffer.write(opens.getSource().getName());
        if (!opens.getTargets().isEmpty()) {
            buffer.write(" to ");
            Iterator<JavaModule> targets = opens.getTargets().iterator();
            while (targets.hasNext()) {
                JavaModule target = targets.next();
                buffer.write(target.getName());
                if (targets.hasNext()) {
                    buffer.write(", ");
                }
            }
        }
        buffer.write(';');
        buffer.newline();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ModelWriter writeModuleProvides(JavaProvides provides) {
        buffer.write("provides ");
        buffer.write(provides.getService().getName());
        buffer.write(" with ");
        Iterator<JavaClass> providers = provides.getProviders().iterator();
        while (providers.hasNext()) {
            JavaClass provider = providers.next();
            buffer.write(provider.getName());
            if (providers.hasNext()) {
                buffer.write(", ");
            }
        }
        buffer.write(';');
        buffer.newline();
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ModelWriter writeModuleRequires(JavaRequires requires) {
        buffer.write("requires ");
        writeAccessibilityModifier(requires.getModifiers());
        writeNonAccessibilityModifiers(requires.getModifiers());
        buffer.write(requires.getModule().getName());
        buffer.write(';');
        buffer.newline();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ModelWriter writeModuleUses(JavaUses uses) {
        buffer.write("uses ");
        buffer.write(uses.getService().getName());
        buffer.write(';');
        buffer.newline();
        return this;
    }

}
