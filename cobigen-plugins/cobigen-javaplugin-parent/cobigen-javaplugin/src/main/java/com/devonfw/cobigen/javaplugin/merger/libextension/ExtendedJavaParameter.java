package com.devonfw.cobigen.javaplugin.merger.libextension;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameter;

/**
 * Extension of the {@link JavaParameter} implementation in order to support modifiers
 * @author mbrunnli (10.04.2014)
 */
public class ExtendedJavaParameter extends DefaultJavaParameter {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 480836742752619326L;

    /**
     * {@link List} of parameter modifiers
     */
    private List<String> modifiers;

    /**
     * Creates a new {@link ExtendedJavaParameter} for the given properties
     * @param type
     *            {@link JavaClass} of the parameter
     * @param name
     *            of the parameter
     * @param modifiers
     *            list of modifiers of the parameter
     * @param varArgs
     *            of the parameter (??? see QDox)
     * @author mbrunnli (10.04.2014)
     */
    public ExtendedJavaParameter(JavaClass type, String name, Set<String> modifiers, boolean varArgs) {
        super(type, name, varArgs);
        this.modifiers = new LinkedList<>(modifiers);
    }

    /**
     * Returns all modifiers of the parameter
     * @return all modifiers of the parameter
     * @author mbrunnli (10.04.2014)
     */
    public List<String> getModifiers() {
        return modifiers;
    }

}
