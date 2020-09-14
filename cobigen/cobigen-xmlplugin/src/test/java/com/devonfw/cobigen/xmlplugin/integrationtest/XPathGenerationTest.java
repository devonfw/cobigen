package com.devonfw.cobigen.xmlplugin.integrationtest;

import static com.devonfw.cobigen.test.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;

/**
 * Test suite for integrating XPath typed matchers and variable assignments
 */
public class XPathGenerationTest {

    /** JUnit rule to savely create and cleanup temporary test folders */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Testing basic Xpath Access
     * @throws Exception
     *             test fails
     */
    @Test
    public void testXpathAccess() throws Exception {
        Path cobigenConfigFolder = new File("src/test/resources/testdata/integrationtest/uml-basic-test").toPath();
        Path input = cobigenConfigFolder.resolve("uml.xml");

        CobiGen cobigen = CobiGenFactory.create(cobigenConfigFolder.toUri());
        Object compliantInput = cobigen.read(input, Charset.forName("UTF-8"));
        List<TemplateTo> matchingTemplates = cobigen.getMatchingTemplates(compliantInput);
        assertThat(matchingTemplates).isNotNull().hasSize(1);

        File targetFolder = tmpFolder.newFolder("testXpathAccess");
        GenerationReportTo report = cobigen.generate(compliantInput, matchingTemplates.get(0), targetFolder.toPath());

        assertThat(report).isSuccessful();
        assertThat(targetFolder.toPath().resolve("DocXPath.txt")).hasContent("Bill");
    }

}
