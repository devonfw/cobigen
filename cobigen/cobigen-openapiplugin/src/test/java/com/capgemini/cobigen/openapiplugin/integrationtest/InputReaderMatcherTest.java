package com.capgemini.cobigen.openapiplugin.integrationtest;

import static com.capgemini.cobigen.test.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.openapiplugin.model.OpenAPIFile;
import com.capgemini.cobigen.openapiplugin.util.TestConstants;

import junit.framework.AssertionFailedError;

/**
 * Testing the integration of input reader and matcher as the matchers algorithm depends on the model created
 * by the input reader.
 */
public class InputReaderMatcherTest {

    /** Testdata root path */
    private static final String testdataRoot = "src/test/resources/testdata/integrationtest/InputReaderMatcherTest";

    /** Temporary folder rule to create new temporary folder and files */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Tests the correct basic retrieval of ComponentDef inputs
     * @throws Exception
     *             test fails
     */
    @Test
    public void testBasicElementMatcher_oneComponent() throws Exception {
        CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

        Object openApiFile =
            cobigen.read("openapi", Paths.get(testdataRoot, "one-component.yaml"), TestConstants.UTF_8);
        assertThat(openApiFile).isNotNull();

        List<Object> inputObjects = cobigen.resolveContainers(openApiFile);
        assertThat(inputObjects).isNotNull().hasSize(1);
    }

    /**
     * Tests the correct basic retrieval of ComponentDef inputs
     * @throws Exception
     *             test fails
     */
    @Test
    public void testBasicElementMatcher_twoComponents() throws Exception {
        CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

        Object openApiFile =
            cobigen.read("openapi", Paths.get(testdataRoot, "two-components.yaml"), TestConstants.UTF_8);
        assertThat(openApiFile).isNotNull();

        List<Object> inputObjects = cobigen.resolveContainers(openApiFile);
        assertThat(inputObjects).isNotNull().hasSize(2);
    }

    /**
     * Tests variable assignment resolution of PROPERTY type at the example of the component version
     * @throws Exception
     *             test fails
     */
    @Test
    public void testVariableAssignment_propertyName() throws Exception {
        CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

        Object openApiFile =
            cobigen.read("openapi", Paths.get(testdataRoot, "one-component.yaml"), TestConstants.UTF_8);
        List<Object> inputObjects = cobigen.resolveContainers(openApiFile);

        String templateName = "testVariableAssignment_propertyName.txt";
        TemplateTo template = findTemplate(cobigen, inputObjects.get(0), templateName);

        File targetFolder = tmpFolder.newFolder();
        GenerationReportTo report = cobigen.generate(inputObjects.get(0), template, targetFolder.toPath());
        assertThat(report).isSuccessful();

        assertThat(targetFolder.toPath().resolve("testVariableAssignment_propertyName.txt").toFile()).exists()
            .hasContent("Table");
    }

    /**
     * Tests variable assignment resolution of ROOTPACKAGE type, thus that the user can define the root
     * package in the "info" part of the OpenAPI file
     * @throws Exception
     *             test fails
     */
    @Test
    public void testVariableAssignment_rootPackage() throws Exception {
        CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

        Object openApiFile = cobigen.read("openapi", Paths.get(testdataRoot, "root-package.yaml"), TestConstants.UTF_8);
        OpenAPIFile inputFile = (OpenAPIFile) openApiFile;

        // Previous version: List<Object> inputObjects = cobigen.getInputObjects(openApiFile,
        // TestConstants.UTF_8);
        List<Object> inputObjects = cobigen.resolveContainers(openApiFile);

        String templateName = "testVariableAssignment_rootPackage.txt";
        TemplateTo template = findTemplate(cobigen, inputObjects.get(0), templateName);

        File targetFolder = tmpFolder.newFolder();
        GenerationReportTo report = cobigen.generate(inputObjects.get(0), template, targetFolder.toPath());
        assertThat(report).isSuccessful();

        assertThat(targetFolder.toPath().resolve("testVariableAssignment_rootPackage.txt").toFile()).exists()
            .hasContent("testingRootName");
    }

    /**
     * Tests the case when no "x-rootPackage" tag is found, therefore the root package should be the default
     * value defined in the context.xml
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void testVariableAssignment_noRootPackageFound() throws Exception {
        CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

        Object openApiFile =
            cobigen.read("openapi", Paths.get(testdataRoot, "two-components.yaml"), TestConstants.UTF_8);
        OpenAPIFile inputFile = (OpenAPIFile) openApiFile;

        // Previous version: List<Object> inputObjects = cobigen.getInputObjects(openApiFile,
        // TestConstants.UTF_8);
        List<Object> inputObjects = cobigen.resolveContainers(openApiFile);

        String templateName = "testVariableAssignment_rootPackage.txt";
        TemplateTo template = findTemplate(cobigen, inputObjects.get(0), templateName);

        File targetFolder = tmpFolder.newFolder();
        GenerationReportTo report = cobigen.generate(inputObjects.get(0), template, targetFolder.toPath());
        assertThat(report).isSuccessful();

        assertThat(targetFolder.toPath().resolve("testVariableAssignment_rootPackage.txt").toFile()).exists()
            .hasContent("defaultValue");
    }

    /**
     * Tests variable assignment resolution of ATTRIBUTE type, thus that the user can define any custom
     * variables inside the schema of OpenAPI files
     * @throws Exception
     *             test fails
     */
    @Test
    public void testVariableAssignment_attribute() throws Exception {
        CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

        Object openApiFile =
            cobigen.read("openapi", Paths.get(testdataRoot, "two-components.yaml"), TestConstants.UTF_8);
        OpenAPIFile inputFile = (OpenAPIFile) openApiFile;

        // Previous version: List<Object> inputObjects = cobigen.getInputObjects(openApiFile,
        // TestConstants.UTF_8);
        List<Object> inputObjects = cobigen.resolveContainers(openApiFile);

        String templateName = "testVariableAssignment_attribute.txt";
        TemplateTo template = findTemplate(cobigen, inputObjects.get(0), templateName);

        File targetFolder = tmpFolder.newFolder();
        GenerationReportTo report = cobigen.generate(inputObjects.get(0), template, targetFolder.toPath());
        assertThat(report).isSuccessful();

        assertThat(targetFolder.toPath().resolve("testVariableAssignment_attribute.txt").toFile()).exists()
            .hasContent("testingAttributeTable");

        template = findTemplate(cobigen, inputObjects.get(1), templateName);
        report = cobigen.generate(inputObjects.get(1), template, targetFolder.toPath());
        assertThat(report).isSuccessful();

        assertThat(targetFolder.toPath().resolve("testVariableAssignment_attribute.txt").toFile()).exists()
            .hasContent("testingAttributeSales");
    }

    /**
     * Finds a template or throws an assertion error
     * @param cobigen
     *            {@link CobiGen} instance
     * @param inputObject
     *            input object to match against
     * @param templateName
     *            the id of the template
     * @return the found template
     */
    private TemplateTo findTemplate(CobiGen cobigen, Object inputObject, String templateName) {
        List<TemplateTo> matchingTemplates = cobigen.getMatchingTemplates(inputObject);
        for (TemplateTo t : matchingTemplates) {
            if (t.getId().equals(templateName)) {
                return t;
            }
        }
        throw new AssertionFailedError("Could not find template with id " + templateName);
    }
}
