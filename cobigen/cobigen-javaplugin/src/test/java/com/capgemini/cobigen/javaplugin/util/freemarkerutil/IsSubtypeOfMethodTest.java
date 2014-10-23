package com.capgemini.cobigen.javaplugin.util.freemarkerutil;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModelException;

/**
 * This class contains testcases for {@link IsSubtypeOfMethod}
 * @author fkreis (23.10.2014)
 */
public class IsSubtypeOfMethodTest {

    /**
     * Test method for {@link IsSubtypeOfMethod#exec(java.util.List)}.
     * @throws TemplateModelException
     *             test fails
     */
    @Test
    public void testExec() throws TemplateModelException {
        // create instance
        IsSubtypeOfMethod method = new IsSubtypeOfMethod(this.getClass().getClassLoader());

        // create testdata
        SimpleScalar childclass = new SimpleScalar("java.lang.String");
        SimpleScalar parentclass = new SimpleScalar("java.lang.Object");
        SimpleScalar nonsense = new SimpleScalar("nonsense");

        // Execute and Check with correct arguments
        ArrayList<Object> args = new ArrayList<>();
        args.add(childclass);
        args.add(parentclass);
        Assert.assertSame(TemplateBooleanModel.TRUE, method.exec(args));
        args.clear();
        args.add(childclass);
        args.add(childclass);
        Assert.assertSame(TemplateBooleanModel.TRUE, method.exec(args));
        args.clear();
        args.add(parentclass);
        args.add(childclass);
        Assert.assertSame(TemplateBooleanModel.FALSE, method.exec(args));

        // Execute and Check with wrong arguments
        args.clear();
        args.add(nonsense);
        args.add(childclass);
        try {
            method.exec(args);
            Assert.fail("Exception should me thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof TemplateModelException);
        }

        // Execute and Check with too many arguments
        args.clear();
        args.add(nonsense);
        args.add(childclass);
        args.add(childclass);
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
