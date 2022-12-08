package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.util.MavenUtil;

/**
 * Test class for maven utilities
 */
public class MavenUtilTest {

  /**
   * Temp folder for test execution
   */
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
    MavenUtil.cacheMavenClassPath(cli_pom.toPath().resolve("pom.xml"), cache.toPath());
    String result = FileUtils.readFileToString(cache, Charset.defaultCharset());
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
  @Test
  public void testValidateCacheWrongRepository() throws Exception {

    File cli_pom = this.temp.newFolder("playground", "cli-pom");
    Path m2repo = MavenUtil.determineMavenRepositoryPath();
    FileUtils.copyFileToDirectory(new File(testdataRoot, "pom.xml"), cli_pom);
    String hash = MavenUtil.generatePomFileHash(cli_pom.toPath().resolve("pom.xml"), m2repo);
    File cache = this.temp.newFile("playground/cli-pom/pom-cp-" + hash + ".txt");
    MavenUtil.cacheMavenClassPath(cli_pom.toPath().resolve("pom.xml"), cache.toPath());
    String result = Files.readString(cache.toPath());
    String cacheWithWrongRepo = result.replace(m2repo.getFileName().toString(), "WrongRepository");
    Files.write(cache.toPath(), cacheWithWrongRepo.getBytes());
    MavenUtil.addURLsFromCachedClassPathsFile(cache.toPath(), cli_pom.toPath().resolve("pom.xml"),
        this.getClass().getClassLoader());
    assertThat(Files.readString(cache.toPath())).doesNotContain(cacheWithWrongRepo);
  }

  /**
   * Tests to check if a missing dependency file from the cache will be detected and the cache will be updated
   *
   * @throws Exception
   */
  @Test
  public void testValidateCacheFileNotExistend() throws Exception {

    File cli_pom = this.temp.newFolder("playground", "cli-pom");
    Path m2repo = MavenUtil.determineMavenRepositoryPath();
    FileUtils.copyFileToDirectory(new File(testdataRoot, "pom.xml"), cli_pom);
    String hash = MavenUtil.generatePomFileHash(cli_pom.toPath().resolve("pom.xml"), m2repo);
    File cache = this.temp.newFile("playground/cli-pom/pom-cp-" + hash + ".txt");
    MavenUtil.cacheMavenClassPath(cli_pom.toPath().resolve("pom.xml"), cache.toPath());
    String result = Files.readString(cache.toPath());
    String cacheWithNotExistingFile = result + ";" + m2repo.toString() + "/SomeNonExistingFile.jar";
    Files.write(cache.toPath(), cacheWithNotExistingFile.getBytes());
    MavenUtil.addURLsFromCachedClassPathsFile(cache.toPath(), cli_pom.toPath().resolve("pom.xml"),
        this.getClass().getClassLoader());
    assertThat(Files.readString(cache.toPath())).doesNotContain("SomeNonExistingFile.jar");

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
