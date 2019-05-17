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
        File baseFile = new File(testFileRootPath + "localmavenproject/maven.project/core/");
        try {
            String args[] = new String[1];
            args[0] = baseFile.getAbsolutePath();

            TestPicocli.main(args);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        } finally {
            File generatedFiles = baseFile.toPath()
                .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/src").toFile();
            // FileUtils.deleteDirectory(generatedFiles);
        }
    }

}
