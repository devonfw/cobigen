package com.capgemini.cobigen.javaplugin.integrationtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.CobiGenFactory;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;

import junit.framework.AssertionFailedError;

/**
 * Test suite for testing the provided template methods correctly integrated with cobigen-core
 */
public class TemplateMethodsTest extends AbstractIntegrationTest {

    /**
     * Tests the isAbstract template method integration
     * @throws Exception
     *             test fails
     */
    @Test
    public void testIsAbstractMethod() throws Exception {

        CobiGen cobiGen = CobiGenFactory.create(cobigenConfigFolder.toURI());
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");

        List<TemplateTo> templates = cobiGen.getMatchingTemplates(this.getClass());

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("isAbstract.txt")) {
                cobiGen.generate(getClass(), template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                File expectedFile = new File(
                    tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR + "isAbstract.txt");
                assertThat(expectedFile).exists();
                assertThat(expectedFile).hasContent("falsetruetrue");
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

    /**
     * Tests the isSubtypeOf template method integration
     * @throws Exception
     *             test fails
     */
    @Test
    public void testIsSubtypeOfMethod() throws Exception {

        CobiGen cobiGen = CobiGenFactory.create(cobigenConfigFolder.toURI());
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");

        List<TemplateTo> templates = cobiGen.getMatchingTemplates(this.getClass());

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("isSubtypeOf.txt")) {
                cobiGen.generate(getClass(), template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                File expectedFile = new File(
                    tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR + "isSubtypeOf.txt");
                assertThat(expectedFile).exists();
                assertThat(expectedFile).hasContent("truetruefalsefalsefalse");
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

    /**
     * Tests whether the methods could be also retrieved for array inputs
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectClassLoaderForMethods() throws Exception {

        CobiGen cobiGen = CobiGenFactory.create(cobigenConfigFolder.toURI());
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");

        Object[] inputArr = new Object[2];
        File thisClassFile =
            new File("src/test/java/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/"
                + getClass().getSimpleName() + ".java");
        inputArr[0] = JavaParserUtil.getFirstJavaClass(new FileReader(thisClassFile));
        inputArr[1] = getClass();

        List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputArr);

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("emptyTemplate.txt")) {
                cobiGen.generate(inputArr, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                File expectedFile = new File(
                    tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR + "emptyTemplate.txt");
                assertThat(expectedFile).exists();
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            throw new AssertionFailedError("No template found");
        }
    }
}
