package com.devonfw.cobigen.cli.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.Test;

import com.devonfw.cobigen.api.util.MavenUtil;

public class AttachArtifactTest {

  /**
   * To check if the snapshot file is in the repository folder
   */
  @Test
  public void testCheckTemplatesetFilePath() {

    Path m2Repository = MavenUtil.determineMavenRepositoryPath();// gives path to the m2 home

    Path artifactPath = m2Repository.resolve("testing").resolve("attachartifact.project").resolve("dev-SNAPSHOT")
        .resolve("attachartifact.project-dev-SNAPSHOT.xml");
    assertThat(artifactPath).exists();

  }
}
