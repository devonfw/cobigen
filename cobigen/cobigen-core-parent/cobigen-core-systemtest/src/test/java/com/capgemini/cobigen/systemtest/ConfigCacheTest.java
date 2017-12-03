package com.capgemini.cobigen.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.systemtest.common.AbstractApiTest;

/** This test suite is focused on tests for resource caching like resources of the configuration. */
public class ConfigCacheTest extends AbstractApiTest {

    /**
     * Root path to all resources used in this test case
     */
    private static final String testFileRootPath = apiTestsRootPath + "ConfigCacheTest/";

    /** JUnit rule to create temporary test folder */
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Tests the automatic reload of the context configuration
     * @throws Exception
     *             test fails
     */
    @Test
    public void testReloadOfContextConfig() throws Exception {

        // copy all test files to be potentially changed into temporary location
        Path testSrc = new File(testFileRootPath + "templates").toPath();
        Path configFolderUnderTest = tempFolder.newFolder("testReloadOfContextConfig").toPath();
        Path testInput = new File(testFileRootPath + "input/WhateverDao.java").toPath();

        Files.walk(testSrc).forEach(sourcePath -> {
            try {
                Files.copy(sourcePath, configFolderUnderTest.resolve(testSrc.relativize(sourcePath)),
                    StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new AssertionError("Could not copy temporary files", e);
            }
        });

        // act
        CobiGen cobigen = CobiGenFactory.create(configFolderUnderTest.toUri());
        Object input = cobigen.read("java", testInput, Charset.forName("UTF-8"));
        assertThat(cobigen.getMatchingTriggerIds(input)).containsExactly("t1");

        // change context config
        Path contextConfig = configFolderUnderTest.resolve("context.xml");
        try (Stream<String> lines = Files.lines(contextConfig)) {
            List<String> replaced = lines.map(line -> line.replace("Dao", "Entity")).collect(Collectors.toList());
            Files.write(contextConfig, replaced);
        }
        Thread.sleep(200);

        // check again
        assertThat(cobigen.getMatchingTriggerIds(input)).isEmpty();
    }

    /**
     * Tests the automatic reload of the context configuration
     * @throws Exception
     *             test fails
     */
    @Test
    public void testReloadOfTemplatesConfig() throws Exception {

        // copy all test files to be potentially changed into temporary location
        Path testSrc = new File(testFileRootPath + "templates").toPath();
        Path configFolderUnderTest = tempFolder.newFolder("testReloadOfTemplatesConfig").toPath();
        Path testInput = new File(testFileRootPath + "input/WhateverDao.java").toPath();

        Files.walk(testSrc).forEach(sourcePath -> {
            try {
                Files.copy(sourcePath, configFolderUnderTest.resolve(testSrc.relativize(sourcePath)),
                    StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new AssertionError("Could not copy temporary files", e);
            }
        });

        // act
        CobiGen cobigen = CobiGenFactory.create(configFolderUnderTest.toUri());
        Object input = cobigen.read("java", testInput, Charset.forName("UTF-8"));
        assertThat(cobigen.getMatchingTemplates(input)).hasSize(1);
        assertThat(cobigen.getMatchingTemplates(input).get(0).getId()).isEqualTo("_template.txt");

        // change context config
        Path contextConfig = configFolderUnderTest.resolve("testTemplates/templates.xml");
        try (Stream<String> lines = Files.lines(contextConfig)) {
            List<String> replaced =
                lines.map(line -> line.replace("templateNamePrefix=\"_\"", "templateNamePrefix=\"prefix_\""))
                    .collect(Collectors.toList());
            Files.write(contextConfig, replaced);
        }
        Thread.sleep(200);

        // check again
        assertThat(cobigen.getMatchingTemplates(input)).hasSize(1);
        assertThat(cobigen.getMatchingTemplates(input).get(0).getId()).isEqualTo("prefix_template.txt");
    }
}
