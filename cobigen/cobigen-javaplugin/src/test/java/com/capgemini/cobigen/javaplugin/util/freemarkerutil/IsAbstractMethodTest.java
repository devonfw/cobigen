package com.capgemini.cobigen.javaplugin.util.freemarkerutil;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModelException;

/**
 * This class contains testcases for {@link IsAbstractMethod}
 * @author fkreis (23.10.2014)
 */
public class IsAbstractMethodTest {

    /**
     * Test method for {@link IsAbstractMethod#exec(java.util.List)}.
     * @throws TemplateModelException
     *             test fails
     */
    @Test
    public void testExec() throws TemplateModelException {
        // create instance
        IsAbstractMethod method = new IsAbstractMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar abstractClass =
            new SimpleScalar("com.capgemini.cobigen.javaplugin.util.freemarkerutil.SimpleAbstractClass");
        SimpleScalar notAbstractClass = new SimpleScalar("java.lang.Object");
        SimpleScalar nonsense = new SimpleScalar("nonsense");

        // Execute and Check with correct arguments
        ArrayList<Object> args = new ArrayList<>();
        args.add(abstractClass);
        Assert.assertSame(TemplateBooleanModel.TRUE, method.exec(args));

        args.clear();
        args.add(notAbstractClass);
        Assert.assertSame(TemplateBooleanModel.FALSE, method.exec(args));

        // Execute and Check with wrong arguments
        args.clear();
        args.add(nonsense);
        try {
            method.exec(args);
            Assert.fail("Exception should me thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof TemplateModelException);
        }

        // Execute and Check with too many arguments
        args.clear();
        args.add(abstractClass);
        args.add(notAbstractClass);
        try {
            method.exec(args);
            Assert.fail("Exception should me thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof TemplateModelException);
        }

        // Execute and Check with too less arguments
        args.clear();
        try {
            method.exec(args);
            Assert.fail("Exception should me thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof TemplateModelException);
        }

    }
}
