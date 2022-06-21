package com.devonfw.cobigen.eclipse.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.devonfw.cobigen.api.util.JvmUtil;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.junit.TmpMavenProjectRule;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Test suite for class path loader issues.
 */
public class ClassPathLoadingTest extends SystemTest {

  /** Root path of the Test Resources */
  private static final String resourcesRootPath = "src/main/resources/ClassPathLoadingTest/";

  /** Rule for creating temporary {@link IJavaProject}s per test. */
  @Rule
  public TmpMavenProjectRule tmpMavenProjectRule2 = new TmpMavenProjectRule();

  /**
   * Setup workbench appropriately for tests
   *
   * @throws Exception test fails
   */
  @BeforeClass
  public static void setupClass() throws Exception {

    EclipseUtils.cleanWorkspace(bot, true);
    // import the configuration project for this test
    EclipseUtils.importExistingGeneralProject(bot, new File(resourcesRootPath + "templates").getAbsolutePath());
    EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);
  }

  /**
   * Tests the fix for Bug #258.
   *
   * @throws Exception Test fails
   */
  @Test
  @Ignore
  public void testPreventClassPathShading() throws Exception {

    // create a new temporary java project and copy java class used as an input for CobiGen
    String testProjectName = "TestInputProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjectName);
    this.tmpMavenProjectRule.createPom(
    // @formatter:off
            "<dependencies>" + "<dependency>" + "<groupId>javax.ws.rs</groupId>"
                + "<artifactId>javax.ws.rs-api</artifactId>" + "<version>2.0</version>" + "</dependency>"
                + "</dependencies>");
        // @formatter:on
    FileUtils.copyFile(new File(resourcesRootPath + "input/JavaClass.java"),
        project.getUnderlyingResource().getLocation().append("src/main/java/main/JavaClass.java").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule.updateProject();

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjectName, "src/main/java", "main",
        "JavaClass.java");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment1");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IFile generationResult = project.getProject().getFile("TestOutput.txt");
    try (InputStream in = generationResult.getContents()) {
      // parenthesis missing as of https://stackoverflow.com/a/40368694 for java 8, fixed in java 11
      // this is not a problem as this code is basically "wrong" by TestUtil design (part of testdata)
      // it's a good example and I will keep it to document this special case.
      if (JvmUtil.isRunningJava9OrLater()) {
        assertThat(IOUtils.toString(in)).isEqualTo("@javax.ws.rs.Path(value=\"/PATH\")");
      } else {
        assertThat(IOUtils.toString(in)).isEqualTo("@javax.ws.rs.Path(value=/PATH)");
      }
    }
  }

  /**
   * Tests the correct class path resolution for Java classes used as input for generation, especially if the input
   * class depends on any maven imported dependency.
   *
   * @throws Exception Test fails
   */
  @Test
  @Ignore
  public void testClassPathResolutionOnInput_dependsOnMavenDependency() throws Exception {

    // create a new temporary java project and copy java class used as an input for CobiGen
    String testProjectName = "TestInputProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjectName);
    this.tmpMavenProjectRule.createPom(
    // @formatter:off
            "<dependencies>" + "<dependency>" + "<groupId>io.oasp.java.modules</groupId>"
                + "<artifactId>oasp4j-jpa</artifactId>" + "<version>2.1.1</version>" + "</dependency>"
                + "</dependencies>");
        // @formatter:on
    FileUtils.copyFile(new File(resourcesRootPath + "input/SampleEntity.java"),
        project.getUnderlyingResource().getLocation().append("src/main/java/main/SampleEntity.java").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule.updateProject();

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjectName, "src/main/java", "main",
        "SampleEntity.java");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment1");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IFile generationResult = project.getProject().getFile("TestOutput.txt");
    assertThat(generationResult.exists()).isTrue();
  }

  /**
   * Tests the correct class path resolution for Java classes used as input for generation, especially if the input
   * class depends on any maven imported project of the workspace.
   *
   * @throws Exception Test fails
   */
  @Test
  @Ignore
  public void testClassPathResolutionOnInput_dependsOnMavenProject() throws Exception {

    // create a new temporary java project and copy java class used as an input for CobiGen
    IJavaProject projectDependency = this.tmpMavenProjectRule2.createProject("CommonTestProj");
    FileUtils.copyFile(new File(resourcesRootPath + "input/AnyImportedEntity.java"), projectDependency
        .getUnderlyingResource().getLocation().append("src/main/java/dependent/AnyImportedEntity.java").toFile());
    projectDependency.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule2.updateProject();

    String testProjectName = "TestInputProj";
    IJavaProject mainProject = this.tmpMavenProjectRule.createProject(testProjectName);
    this.tmpMavenProjectRule.createPom(
    // @formatter:off
            "<dependencies>" + "<dependency>" + this.tmpMavenProjectRule2.getMavenProjectSpecification() + "</dependency>"
                + "</dependencies>");
        // @formatter:on
    FileUtils.copyFile(new File(resourcesRootPath + "input/DependentEntity.java"),
        mainProject.getUnderlyingResource().getLocation().append("src/main/java/main/DependentEntity.java").toFile());
    mainProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule.updateProject();

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjectName, "src/main/java", "main",
        "DependentEntity.java");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment1");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IFile generationResult = mainProject.getProject().getFile("TestOutput.txt");
    assertThat(generationResult.exists()).isTrue();
  }

  /**
   * Tests class resolution of classes of template only dependencies.
   *
   * @throws Exception Test fails
   */
  @Test
  @Ignore
  public void testClassloadingOfTemplateDependencies() throws Exception {

    // create a new temporary java project and copy java class used as an input for CobiGen
    String testProjectName = "TestInputProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjectName);
    FileUtils.copyFile(new File(resourcesRootPath + "input/PlainInput.java"),
        project.getUnderlyingResource().getLocation().append("src/main/java/main/PlainInput.java").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule.updateProject();

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjectName, "src/main/java", "main",
        "PlainInput.java");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment2");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IFile generationResult = project.getProject().getFile("InternalClassloading.txt");
    try (InputStream in = generationResult.getContents()) {
      assertThat(IOUtils.toString(in)).isEqualTo("javax.ws.rs.Path");
    }
  }

  /**
   * Tests the fix for Bug #953.
   *
   * @throws Exception Test fails
   */
  @Test
  @Ignore
  public void testDotPathAcception() throws Exception {

    // create a new temporary java project and copy java class used as an input for CobiGen
    String testProjectName = "TestInputProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjectName);
    this.tmpMavenProjectRule.createPom(
    // @formatter:off
            "<dependencies>" + "<dependency>" + "<groupId>javax.ws.rs</groupId>"
                + "<artifactId>javax.ws.rs-api</artifactId>" + "<version>2.0</version>" + "</dependency>"
                + "</dependencies>");
        // @formatter:on
    FileUtils.copyFile(new File(resourcesRootPath + "input/JavaClass.java"),
        project.getUnderlyingResource().getLocation().append("src/main/java/main/JavaClass.java").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule.updateProject();

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    // input file doesn't matter for the used template
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjectName, "src/main/java", "main",
        "JavaClass.java");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment3");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IFile generationResult = project.getProject().getFile("x.y/dotPathInTemplate.txt");
    try (InputStream in = generationResult.getContents()) {
      assertThat(IOUtils.toString(in)).isEqualTo("dotPathGenerated");
    }
  }
}
