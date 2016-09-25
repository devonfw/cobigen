package com.capgemini.cobigen.systemtest;

import static com.capgemini.cobigen.common.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.CobiGenFactory;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.systemtest.common.AbstractApiTest;
import com.capgemini.cobigen.systemtest.util.PluginMockFactory;

/**
 * Test suite for generation purposes.
 */
public class GenerationTest extends AbstractApiTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = apiTestsRootPath + "GenerationTest/";

    /**
     * Tests that sources get overwritten if merge strategy override is configured.
     * @throws Exception
     *             test fails.
     */
    @Test
    public void testOverrideMergeStrategy() throws Exception {
        Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

        File folder = tmpFolder.newFolder("GenerationTest");
        File target = new File(folder, "generated.txt");
        FileUtils.write(target, "base");

        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "templates").toURI());
        List<TemplateTo> templates = cobigen.getMatchingTemplates(input);

        assertThat(templates).hasSize(1);

        GenerationReportTo report = cobigen.generate(input, templates.get(0), Paths.get(folder.toURI()));

        assertThat(report).isSuccessful();
        assertThat(target).hasContent("overwritten");
    }

}
