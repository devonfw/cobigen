package com.capgemini.cobigen.systemtest.testdata;

import java.util.List;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * This class a copy of the javaplugin to test the template methods as unit test in the cobigen-core
 *
 * @author fkreis (22.10.2014)
 */
public class IsSubtypeOfMethod implements TemplateMethodModelEx {
    /**
     * the {@link ClassLoader} needed to load classes of fully qualified class names.
     */
    private ClassLoader classLoader;

    /**
     * Creates a new instance of the {@link IsSubtypeOfMethod} for the given input {@link ClassLoader}.
     * @param classLoader
     *            {@link ClassLoader} needed to load classes of fully qualified class names.
     * @author fkreis (22.10.2014)
     */
    public IsSubtypeOfMethod(ClassLoader classLoader) {
        super();
        this.classLoader = classLoader;
    }

    /**
     * This method will be called by the FTL, if you call the corresponding method with a method call
     * expression in a template. All the values of the method call are contained in the args parameter. In
     * this case two String arguments are expected, which should be full qualified names of classes. This
     * method checks whether the corresponding class of the first string (subType) is a sub type of the
     * corresponding class of the second String (super type).
     * @param args
     *            a List of the method call arguments. Two String values are expected
     * @return <code>TemplateBooleanModel.TRUE</code> if the corresponding class of the first String is a sub
     *         type of corresponding class of the second String<br>
     *         <code>TemplateBooleanModel.FALSE</code> otherwise
     * @throws TemplateModelException
     *             if one of the given classes could not be found or if the number of arguments in args is not
     *             2.
     * @author fkreis (22.10.2014)
     */
    @Override
    public TemplateBooleanModel exec(List args) throws TemplateModelException {
        if (args.size() != 2) {
            throw new TemplateModelException("Wrong number of arguments. 2 arguments are expected.");
        }
        try {
            boolean isSubtypeOf = isSubtypeOf(((SimpleScalar) args.get(0)).getAsString(),
                ((SimpleScalar) args.get(1)).getAsString());
            if (isSubtypeOf) {
                return TemplateBooleanModel.TRUE;
            } else {
                return TemplateBooleanModel.FALSE;
            }
        } catch (ClassNotFoundException e) {
            throw new TemplateModelException(e);
        }
    }

    /**
     * Checks whether the given subType is a sub type of the given super type
     * @param subType
     *            qualified name of the sub type
     * @param superType
     *            qualified name of the super type
     * @return <code>true</code> if the given subtype is a sub type of the given supertype<br>
     *         <code>false</code> otherwise
     * @throws ClassNotFoundException
     *             if one of the given classes could not be found
     * @author fkreis (22.10.2014)
     */
    private boolean isSubtypeOf(String subType, String superType) throws ClassNotFoundException {
        return classLoader.loadClass(superType).isAssignableFrom(classLoader.loadClass(subType));
    }

}
