package com.capgemini.cobigen.tempeng.velocity.systemtest;

import static com.capgemini.cobigen.test.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.IncrementTo;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.tempeng.velocity.systemtest.testobjects.Input;

/** Test suite integrating cobigen-core with the velocity template engine. */
public class VelocityTemplateEngineIntegrationTest {

    /** JUnit rule to create new temporary files/folder */
    @Rule
    public TemporaryFolder tempFolderRule = new TemporaryFolder();

    /**
     * Tests a basic generation integrated with cobigen-core
     * @throws Exception
     *             test fails
     */
    @Test
    public void testBasicGeneration() throws Exception {

        CobiGen cobigen = CobiGenFactory.create(new File("src/test/resources/systemtest").toURI());
        List<IncrementTo> increments = cobigen.getMatchingIncrements(Input.class);

        assertThat(increments).hasSize(1);
        assertThat(increments.get(0).getTemplates()).hasSize(1);

        File targetFolder = tempFolderRule.newFolder("cobigen-");
        GenerationReportTo report = cobigen.generate(Input.class, increments.get(0), targetFolder.toPath());
        assertThat(report).isSuccessful();

        assertThat(targetFolder.toPath().resolve("velocityTest.txt")).exists().hasContent("String,int,");
    }

}
