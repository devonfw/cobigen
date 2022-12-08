package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.MavenConstants;
import com.devonfw.cobigen.api.util.MavenUtil;

/**
 * Test class for maven utilities
 */
public class MavenUtilTest {

  /**
   * Enviroment rule to set custom enviroment variables
   */
  @Rule
  public EnvironmentVariables enviromentVariables = new EnvironmentVariables();

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenUtilTest";

  /**
   * Tests to check if a correct cache will be validated right
   *
   * @throws Exception
   */
  @Test
  public void testValidateCacheSuccess() throws Exception {

    File cli_pom = this.temp.newFolder("playground", "cli-pom");
    Path m2repo = MavenUtil.determineMavenRepositoryPath();
    FileUtils.copyFileToDirectory(new File(testdataRoot, "pom.xml"), cli_pom);
    String hash = MavenUtil.generatePomFileHash(cli_pom.toPath().resolve("pom.xml"), m2repo);
    File cache = this.temp.newFile("playground/cli-pom/pom-cp-" + hash + ".txt");
    String result = Files.readString(Paths.get(testdataRoot + "/pom-cp.txt"));
    Files.writeString(cache.toPath(), result);
    MavenUtil.addURLsFromCachedClassPathsFile(cache.toPath(), cli_pom.toPath().resolve("pom.xml"),
        this.getClass().getClassLoader());
    assertThat(Files.readString(cache.toPath())).contains(result);

  }

  /**
   * Tests to check if a dependency in the cache pointing to a wrong repository will be detected and the cache will be
   * updated
   *
   * @throws Exception
   */
  @Ignore
  @Test
  public void testValidateCacheWrongRepository() throws Exception {

    File cli_pom = this.temp.newFolder("playground", "cli-pom");
    File m2repo = this.temp.newFolder("playground", "m2repo");
    File secondM2Repo = this.temp.newFolder("playground", "secondM2repo");
    FileUtils.copyFileToDirectory(new File(testdataRoot, "pom.xml"), cli_pom);
    File dependency1 = this.temp.newFile("playground/m2repo/dependency1.jar");
    File dependency2 = this.temp.newFile("playground/m2repo/dependency2.jar");
    this.enviromentVariables.set(MavenConstants.M2_REPO_SYSTEMVARIABLE, secondM2Repo.getAbsolutePath());
    String hash = MavenUtil.generatePomFileHash(cli_pom.toPath().resolve("pom.xml"), null);
    File cache = this.temp.newFile("playground/cli-pom/pom-cp-" + hash + ".txt");
    String result = "" + dependency1.getAbsolutePath() + ";" + dependency2.getAbsolutePath() + ";";
    try (FileWriter fw = new FileWriter(cache); BufferedWriter bw = new BufferedWriter(fw)) {

      bw.append(dependency1.getAbsolutePath() + ";");
      bw.append(dependency2.getAbsolutePath() + ";");
    }
    // the path to the files in the cache, should be removed after that
    MavenUtil.addURLsFromCachedClassPathsFile(cache.toPath(), cli_pom.toPath().resolve("pom.xml"),
        this.getClass().getClassLoader());
    assertThat(Files.readAllLines(cache.toPath())).doesNotContain(result);
  }

  /**
   * Tests to check if a missing dependency file from the cache will be detected and the cache will be updated
   *
   * @throws Exception
   */
  @Ignore
  @Test
  public void testValidateCacheFileNotExistend() throws Exception {

    File cli_pom = this.temp.newFolder("playground", "cli-pom");
    File secondM2Repo = this.temp.newFolder("playground", "secondM2repo");
    FileUtils.copyFileToDirectory(new File(testdataRoot, "pom.xml"), cli_pom);
    File dependency1 = this.temp.newFile("playground/secondM2repo/dependency1.jar");
    Path dependencyP = dependency1.getParentFile().toPath().resolve("someFileThatNotExist.jar");
    this.enviromentVariables.set(MavenConstants.M2_REPO_SYSTEMVARIABLE, secondM2Repo.getAbsolutePath());
    String hash = MavenUtil.generatePomFileHash(cli_pom.toPath().resolve("pom.xml"), null);
    File cache = this.temp.newFile("playground/cli-pom/pom-cp-" + hash + ".txt");
    try (FileWriter fw = new FileWriter(cache); BufferedWriter bw = new BufferedWriter(fw)) {
      bw.append(dependencyP.toString()); // file not existing

    }
    // the path to the files in the cache, should be removed after that
    MavenUtil.addURLsFromCachedClassPathsFile(cache.toPath(), cli_pom.toPath().resolve("pom.xml"),
        this.getClass().getClassLoader());
    assertThat(Files.readAllLines(cache.toPath())).doesNotContain(dependencyP.toString());

  }

  /**
   * Testing if the current maven repository is taken into account to calculate the hash for the cache
   *
   * @throws Exception
   *
   */
  @Test
  public void testGeneratePomFileHash() throws Exception {

    File repo1 = this.temp.newFolder("playground", "repo1");
    File repo2 = this.temp.newFolder("playground", "repo2");
    FileUtils.copyFileToDirectory(new File(testdataRoot, "pom.xml"), repo2.getParentFile());
    String hash1 = MavenUtil.generatePomFileHash(repo1.getParentFile().toPath().resolve("pom.xml"), repo1.toPath());
    String hash2 = MavenUtil.generatePomFileHash(repo2.getParentFile().toPath().resolve("pom.xml"), repo2.toPath());
    assertThat(hash1).isNotEmpty();
    assertThat(hash2).isNotEmpty();
    assertThat(hash1).isNotEqualTo(hash2);

  }

}
