package com.capgemini.cobigen.javaplugin.unittest.util.freemarkerutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.Test;

import com.capgemini.cobigen.javaplugin.util.freemarkerutil.IsSubtypeOfMethod;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModelException;

/**
 * This class contains test cases for {@link IsSubtypeOfMethod}
 */
public class IsSubtypeOfMethodTest {

    /**
     * Test method for {@link IsSubtypeOfMethod#exec(java.util.List)} where the first argument is a sub type
     * of the second.
     * @throws TemplateModelException
     *             test fails
     */
    @Test
    public void testExec_isSubtype() throws TemplateModelException {
        // create instance
        IsSubtypeOfMethod method = new IsSubtypeOfMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar childclass = new SimpleScalar("java.lang.String");
        SimpleScalar parentclass = new SimpleScalar("java.lang.Object");
        ArrayList<Object> args = new ArrayList<>();
        args.add(childclass);
        args.add(parentclass);

        // Execute and Check with correct arguments
        assertThat(method.exec(args)).isSameAs(TemplateBooleanModel.TRUE);
    }

    /**
     * Test method for {@link IsSubtypeOfMethod#exec(java.util.List)} where the first argument is not a sub
     * type of the second.
     * @throws TemplateModelException
     *             test fails
     */
    @Test
    public void testExec_isNotSubtype() throws TemplateModelException {
        // create instance
        IsSubtypeOfMethod method = new IsSubtypeOfMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar childclass = new SimpleScalar("java.lang.String");
        SimpleScalar parentclass = new SimpleScalar("java.lang.Object");
        ArrayList<Object> args = new ArrayList<>();
        args.add(parentclass);
        args.add(childclass);

        // Execute and Check with correct arguments
        assertThat(method.exec(args)).isSameAs(TemplateBooleanModel.FALSE);
    }

    /**
     * Test method for {@link IsSubtypeOfMethod#exec(java.util.List)} where the first argument has the same
     * type as the second.
     * @throws TemplateModelException
     *             test fails
     */
    @Test
    public void testExec_sameType() throws TemplateModelException {
        // create instance
        IsSubtypeOfMethod method = new IsSubtypeOfMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar sameType = new SimpleScalar("java.lang.String");
        ArrayList<Object> args = new ArrayList<>();
        args.add(sameType);
        args.add(sameType);

        // Execute and Check with correct arguments
        assertThat(method.exec(args)).isSameAs(TemplateBooleanModel.TRUE);
    }

    /**
     * Test method for {@link IsSubtypeOfMethod#exec(java.util.List)} with an argument which is not an
     * resolvable to a class. Therefore a TemplateModelException is expected.
     * @throws TemplateModelException
     *             this exception is expected because of the non resolvable argument.
     */
    @Test(expected = TemplateModelException.class)
    public void testExec_wrongArg() throws TemplateModelException {
        // create instance
        IsSubtypeOfMethod method = new IsSubtypeOfMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar anyType = new SimpleScalar("java.lang.String");
        SimpleScalar nonsense = new SimpleScalar("nonsense");
        ArrayList<Object> args = new ArrayList<>();
        args.add(anyType);
        args.add(nonsense);

        // Execute and Check with wrong correct arguments
        method.exec(args);
    }

    /**
     * Test method for {@link IsSubtypeOfMethod#exec(java.util.List)} with too many arguments. Therefore a
     * TemplateModelException is expected.
     * @throws TemplateModelException
     *             this exception is expected because there are too many arguments.
     */
    @Test(expected = TemplateModelException.class)
    public void testExec_tooManyArgs() throws TemplateModelException {
        // create instance
        IsSubtypeOfMethod method = new IsSubtypeOfMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar childclass = new SimpleScalar("java.lang.String");
        SimpleScalar parentclass = new SimpleScalar("java.lang.Object");
        ArrayList<Object> args = new ArrayList<>();
        args.add(childclass);
        args.add(childclass);
        args.add(parentclass);

        // Execute and Check with too many arguments
        method.exec(args);
    }

    /**
     * Test method for {@link IsSubtypeOfMethod#exec(java.util.List)} with too less arguments. Therefore a
     * TemplateModelException is expected.
     * @throws TemplateModelException
     *             this exception is expected because there are too many arguments.
     */
    @Test(expected = TemplateModelException.class)
    public void testExec_tooLessArgs() throws TemplateModelException {
        // create instance
        IsSubtypeOfMethod method = new IsSubtypeOfMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar anyClass = new SimpleScalar("java.lang.String");
        ArrayList<Object> args = new ArrayList<>();
        args.add(anyClass);

        // Execute and Check with too many arguments
        method.exec(args);
    }
}
