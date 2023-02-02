package com.devonfw.cobigen.eclipse.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Class for testing Adapt Templates with a following Generate process
 *
 */
public class AdaptTemplatesTest extends SystemTest {

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /** Root path of the Test Resources */
  private static final String resourcesRootPath = "src/main/resources/OpenAPITest/";

  /** Line separator, e.g. for windows '\r\n' */
  public static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

  /**
   * Setup workbench appropriately for tests
   *
   * @throws Exception test fails
   */
  @BeforeClass
  public static void setupClass() throws Exception {

    EclipseUtils.cleanWorkspace(bot, true);
  }

  /**
   * Test for external projects (not in workspace) taken as input for generation
   *
   * @throws Exception test fails
   */
  public void testBasicOpenAPIGenerationWithAdaptTemplateSets() throws Exception {

    // copy sample project to external location and import it into the workspace
    String testProjName = "ExtTestProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjName);
    FileUtils.copyFile(new File(resourcesRootPath + "input/adapt-templates.yml"),
        project.getUnderlyingResource().getLocation().append("adapt-templates.yml").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule.updateProject();

    /**
     * TODO Before the templates are made available online, the update (download) command cannot be tested.
     */
    // EclipseCobiGenUtils.runAndCaptureUpdateTemplates(bot);
    EclipseCobiGenUtils.runAndCaptureAdaptTemplatesSets(bot);
    EclipseUtils.openErrorsTreeInProblemsView(bot);
    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjName, "adapt-templates.yml");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, 25000, "CRUD devon4j Server>CRUD REST services");
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    // increase timeout as the openAPI parser is slow on initialization
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot, 40000);

    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);
    IFile generationResult = proj.getFile(
        "src/main/java/com/devonfw/test/sampledatamanagement/service/impl/rest/SampledatamanagementRestServiceImpl.java");

    assertThat(generationResult.exists()).isTrue();
  }

  /**
   * Test for external projects (not in workspace) taken as input
   *
   * @throws Exception test fails
   */
  public void testAdaptTemplatesAndImportIntoEclipse() throws Exception {

    // copy sample project to external location and import it into the workspace
    String testProjName = "ExtTestProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjName);
    FileUtils.copyFile(new File(resourcesRootPath + "input/adapt-templates.yml"),
        project.getUnderlyingResource().getLocation().append("adapt-templates.yml").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule.updateProject();

    EclipseCobiGenUtils.runAndCaptureAdaptTemplatesSets(bot);

    EclipseUtils.openErrorsTreeInProblemsView(bot);

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjName, "adapt-templates.yml");
    javaClassItem.select();

    IProject generatorProjOfTempltesSets = ResourcesPlugin.getWorkspace().getRoot()
        .getProject(ResourceConstants.TEMPLATE_SETS_CONFIG_PROJECT_NAME);
    bot.waitUntil(new AllJobsAreFinished(), 10000000);
    IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);

    Path adaptedFolder = Paths.get(generatorProjOfTempltesSets.getLocationURI())
        .resolve(ResourceConstants.TEMPLATE_SETS_ADAPTED);

    assertThat(generatorProjOfTempltesSets.exists()).isTrue();
    assertThat(Files.exists(adaptedFolder)).isTrue();

  }

  /**
   * Test for external projects (not in workspace) taken as input for generation
   *
   * @throws Exception test fails
   */
  public void testBasicOpenAPIGenerationWithAdaptMonolithicTemplates() throws Exception {

    // copy sample project to external location and import it into the workspace
    String testProjName = "ExtTestProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjName);
    FileUtils.copyFile(new File(resourcesRootPath + "input/adapt-templates.yml"),
        project.getUnderlyingResource().getLocation().append("adapt-templates.yml").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule.updateProject();

    EclipseCobiGenUtils.runAndCaptureUpdateTemplates(bot);
    EclipseCobiGenUtils.runAndCaptureAdaptTemplates(bot);
    EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);

    EclipseUtils.openErrorsTreeInProblemsView(bot);

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjName, "adapt-templates.yml");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, 25000, "CRUD devon4j Server>CRUD REST services");
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    // increase timeout as the openAPI parser is slow on initialization
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot, 40000);

    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);
    IFile generationResult = proj.getFile(
        "src/main/java/com/devonfw/test/sampledatamanagement/service/impl/rest/SampledatamanagementRestServiceImpl.java");

    assertThat(generationResult.exists()).isTrue();
  }

  /*
   *
   * Test of testBasicOpenAPIGenerationWithAdaptTemplates with custom COBIGEN_HOME environment variable
   *
   */
  @Test
  public void testAdaptMonolithicTemplatesAndGenerate() throws Exception {

    File tmpProject = this.tempFolder.newFolder("playground", "project");
    withEnvironmentVariable("COBIGEN_HOME", tmpProject.toPath().toString())
        .execute(() -> testBasicOpenAPIGenerationWithAdaptMonolithicTemplates());
  }

  /**
   * TODO Test of testBasicOpenAPIGenerationWithAdaptTemplates with custom template-sets after adapt the jar files
   *
   * @throws Exception test fails
   */
  @Test
  @Ignore
  public void testAdaptTemplateSetsAndGenerate() throws Exception {

    Path devTemplatesPath = new File(
        AdaptTemplatesTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().toPath().resolve("cobigen-templates").resolve("crud-openapi-java-server-app")
            .resolve("target").resolve("crud-openapi-java-server-app-2021.12.007-SNAPSHOT.jar");

    File tmpProject = this.tempFolder.newFolder("playground", "project");
    File downloaded = this.tempFolder.newFolder("playground", "project", "template-sets", "downloaded");
    FileUtils.copyFileToDirectory(devTemplatesPath.toFile(), downloaded);
    withEnvironmentVariable("COBIGEN_HOME", tmpProject.toPath().toString())
        .execute(() -> testBasicOpenAPIGenerationWithAdaptTemplateSets());
  }

  /*
   *
   * Test adaption of template-sets/downloaded/.jar files and importing the project into Eclipse
   *
   */
  @Test
  public void testAdaptTemplateSetsAndImport() throws Exception {

    Path devTemplatesPath = new File(
        AdaptTemplatesTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().toPath().resolve("cobigen-templates").resolve("crud-openapi-java-server-app")
            .resolve("target").resolve("crud-openapi-java-server-app-2021.12.007-SNAPSHOT.jar");

    File tmpProject = this.tempFolder.newFolder("playground", "project");
    File downloaded = this.tempFolder.newFolder("playground", "project", "template-sets", "downloaded");
    FileUtils.copyFileToDirectory(devTemplatesPath.toFile(), downloaded);
    withEnvironmentVariable("COBIGEN_HOME", tmpProject.toPath().toString())
        .execute(() -> testAdaptTemplatesAndImportIntoEclipse());

  }
}
