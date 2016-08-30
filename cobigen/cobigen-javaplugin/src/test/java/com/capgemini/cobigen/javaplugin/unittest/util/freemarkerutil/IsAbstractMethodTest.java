package com.capgemini.cobigen.javaplugin.unittest.util.freemarkerutil;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.util.freemarkerutil.IsAbstractMethod;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModelException;

/**
 * This class contains testcases for {@link IsAbstractMethod}
 * @author fkreis (23.10.2014)
 */
public class IsAbstractMethodTest {

    /**
     * Test method for {@link IsAbstractMethod#exec(java.util.List)} with correct argument which is an
     * abstract class.
     * @throws TemplateModelException
     *             test fails
     */
    @Test
    public void testExec_abstract() throws TemplateModelException {
        // create instance
        IsAbstractMethod method = new IsAbstractMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar abstractClass = new SimpleScalar(
            "com.capgemini.cobigen.javaplugin.unittest.util.freemarkerutil.SimpleAbstractClass");
        ArrayList<Object> args = new ArrayList<>();
        args.add(abstractClass);

        // Execute and Check with correct argument
        Assert.assertSame(TemplateBooleanModel.TRUE, method.exec(args));
    }

    /**
     * Test method for {@link IsAbstractMethod#exec(java.util.List)} with correct argument which is not an
     * abstract class.
     * @throws TemplateModelException
     *             test fails
     */
    @Test
    public void testExec_notAbstract() throws TemplateModelException {
        // create instance
        IsAbstractMethod method = new IsAbstractMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar notAbstractClass = new SimpleScalar("java.lang.Object");
        ArrayList<Object> args = new ArrayList<>();
        args.add(notAbstractClass);

        // Execute and Check with correct arguments
        Assert.assertSame(TemplateBooleanModel.FALSE, method.exec(args));
    }

    /**
     * Test method for {@link IsAbstractMethod#exec(java.util.List)} with an argument which is not an
     * resolvable to a class. Therefore a TemplateModelException is expected.
     * @throws TemplateModelException
     *             this Exception is excepted, because of the unresolvable argument.
     */
    @Test(expected = TemplateModelException.class)
    public void testExec_wrongArg() throws TemplateModelException {
        // create instance
        IsAbstractMethod method = new IsAbstractMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar nonsense = new SimpleScalar("nonsense");
        ArrayList<Object> args = new ArrayList<>();
        args.add(nonsense);

        // Execute and Check with wrong arguments
        method.exec(args);
    }

    /**
     * Test method for {@link IsAbstractMethod#exec(java.util.List)} with too many arguments. Therefore a
     * TemplateModelException is expected.
     * @throws TemplateModelException
     *             this Exception is excepted, because there are to many arguments
     */
    @Test(expected = TemplateModelException.class)
    public void testExec_tooManyArgs() throws TemplateModelException {
        // create instance
        IsAbstractMethod method = new IsAbstractMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar abstractClass =
            new SimpleScalar("com.capgemini.cobigen.javaplugin.util.freemarkerutil.SimpleAbstractClass");
        SimpleScalar notAbstractClass = new SimpleScalar("java.lang.Object");
        ArrayList<Object> args = new ArrayList<>();
        args.add(abstractClass);
        args.add(notAbstractClass);

        // Execute and Check with too many arguments
        method.exec(args);
    }

    /**
     * Test method for {@link IsAbstractMethod#exec(java.util.List)} with too less arguments. Therefore a
     * TemplateModelException is expected.
     * @throws TemplateModelException
     *             this Exception is excepted, because there are to less arguments
     */
    @Test(expected = TemplateModelException.class)
    public void testExec_tooLessArgs() throws TemplateModelException {
        // create instance
        IsAbstractMethod method = new IsAbstractMethod(this.getClass().getClassLoader());

        // create testdata
        ArrayList<Object> args = new ArrayList<>();

        // Execute and Check with too many arguments
        method.exec(args);
    }
}
