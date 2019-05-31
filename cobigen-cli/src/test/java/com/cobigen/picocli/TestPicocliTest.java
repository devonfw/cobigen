package com.cobigen.picocli;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPicocliTest {
    private static Logger logger = LoggerFactory.getLogger(TestPicocliTest.class);

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testMain() throws IOException {
        File inputFile = new File(testFileRootPath + "localmavenproject/maven.project/core/");
        File baseProject = inputFile.toPath()
            .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/SampleDataEntity.java")
            .toFile();

        try {
            String args[] = new String[3];
            args[0] = "generate";
            args[1] = baseProject.getAbsolutePath();
            args[2] = inputFile.getAbsolutePath();

            TestPicocli.main(args);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        } finally {
            File generatedFiles = baseProject.toPath()
                .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/src").toFile();
            // If you want to remove the generated files
            // FileUtils.deleteDirectory(generatedFiles);
        }
    }

}
