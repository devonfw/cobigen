package com.devonfw.cobigen.javaplugin.merger.libextension;

import java.util.Iterator;
import java.util.List;

import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.type.TypeResolver;

/**
 * This class exists to use a custom implementation of the methods that take care of the generics.
 */
public class ModifyableJavaTypeVariable<D extends JavaGenericDeclaration> extends ModifyableJavaType
    implements JavaTypeVariable<D> {

    private List<JavaType> bounds;

    private D genericDeclaration;

    public ModifyableJavaTypeVariable(String name, TypeResolver typeResolver) {
        super(name, typeResolver);
    }

    /** {@inheritDoc} */
    @Override
    public List<JavaType> getBounds() {
        return bounds;
    }

    /**
     * @param bounds
     *            the bounds to set
     */
    public void setBounds(List<JavaType> bounds) {
        this.bounds = bounds;
    }

    /** {@inheritDoc} */
    @Override
    public D getGenericDeclaration() {
        return genericDeclaration;
    }

    @Override
    public String getFullyQualifiedName() {
        return getValue();
    }

    @Override
    public String getGenericFullyQualifiedName() {
        StringBuilder result = new StringBuilder();
        // result.append('<');
        result.append(super.getFullyQualifiedName());
        if (bounds != null && !bounds.isEmpty()) {
            result.append(" extends ");
            for (Iterator<JavaType> iter = bounds.iterator(); iter.hasNext();) {
                result.append(iter.next().getGenericFullyQualifiedName());
                if (iter.hasNext()) {
                    result.append(" & ");
                }
            }
        }
        // result.append('>');
        return result.toString();
    }

    @Override
    public String getCanonicalName() {
        return super.getValue();
    }

    @Override
    public String getGenericCanonicalName() {
        StringBuilder result = new StringBuilder();
        result.append(super.getGenericCanonicalName());
        if (bounds != null && !bounds.isEmpty()) {
            result.append(" extends ");
            for (Iterator<JavaType> iter = bounds.iterator(); iter.hasNext();) {
                result.append(iter.next().getGenericCanonicalName());
                if (iter.hasNext()) {
                    result.append(" & ");
                }
            }
        }
        return result.toString();
    }

    @Override
    public String getGenericValue() {
        StringBuilder result = new StringBuilder();
        result.append(getValue());
        if (bounds != null && !bounds.isEmpty()) {
            result.append(" extends ");
            for (Iterator<JavaType> iter = bounds.iterator(); iter.hasNext();) {
                result.append(iter.next().getGenericValue());
                if (iter.hasNext()) {
                    result.append(",");
                }
            }
        }
        return result.toString();
    }

    @Override
    public String getName() {
        return getValue();
    }
}
