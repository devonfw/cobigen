package com.capgemini.cobigen.javaplugin.util.freemarkerutil;

import java.lang.reflect.Modifier;
import java.util.List;

import freemarker.ext.dom.NodeModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * This class implements the {@link TemplateMethodModelEx} to provide the IsAbstract method as template
 * method.
 * @author fkreis (22.10.2014)
 */
public class IsAbstractMethod implements TemplateMethodModelEx {
    /**
     * the {@link ClassLoader} needed to load classes of fully qualified class names.
     */
    private ClassLoader classLoader;

    /**
     * Creates a new instance of the {@link IsAbstractMethod} for the given input {@link ClassLoader}.
     * @param classLoader
     *            {@link ClassLoader} needed to load class of fully qualified class name.
     * @author fkreis (22.10.2014)
     */
    public IsAbstractMethod(ClassLoader classLoader) {
        super();
        this.classLoader = classLoader;
    }

    /**
     * {@inheritDoc}
     * @author fkreis (22.10.2014)
     */
    @Override
    public Object exec(List args) throws TemplateModelException {
        if (args.size() != 1) {
            throw new TemplateModelException("Wrong number of arguments. 1 argument is expected.");
        }

        // get argument as String if possible
        String input;
        if (args.get(0) instanceof NodeModel) {
            input = ((NodeModel) args.get(0)).getNode().getTextContent();
        } else if (args.get(0) instanceof SimpleScalar) {
            input = ((SimpleScalar) args.get(0)).getAsString();
        } else {
            throw new TemplateModelException(
                "The passed argument has an unexpected type. A String is expected");
        }

        try {
            boolean isAbstract = isAbstract(input);
            if (isAbstract) {
                return TemplateBooleanModel.TRUE;
            } else {
                return TemplateBooleanModel.FALSE;
            }
        } catch (ClassNotFoundException e) {
            throw new TemplateModelException(e);
        }

    }

    /**
     * Checks whether the given class is abstract
     * @param className
     *            class name of the class being checked
     * @return <code>true</code> if the class is abstract<br>
     *         <code>false</code> otherwise
     * @throws ClassNotFoundException
     *             if the class with the given className could not be found in the FreeMarkerUtil's
     *             classLoader
     * @author mbrunnli (12.04.2013)
     */
    private boolean isAbstract(String className) throws ClassNotFoundException {
        return Modifier.isAbstract(classLoader.loadClass(className).getModifiers());
    }

}
