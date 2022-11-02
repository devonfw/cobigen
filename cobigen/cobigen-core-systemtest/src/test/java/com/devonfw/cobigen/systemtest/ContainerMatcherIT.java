package com.devonfw.cobigen.systemtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static com.devonfw.cobigen.test.matchers.CustomHamcrestMatchers.hasItemsInList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.api.matchers.MatcherToMatcher;
import com.devonfw.cobigen.api.matchers.VariableAssignmentToMatcher;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * This test suite concentrates on the {@link ContainerMatcher} support and semantics
 */
public class ContainerMatcherIT extends AbstractApiTest {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = apiTestsRootPath + "ContainerMatcherTest/";

  /**
   * Tests whether a container matcher will not match iff there are no other matchers
   *
   * @throws Exception test fails
   */
  @Test
  public void testContainerMatcherDoesNotMatchWithoutMatcher() throws Exception {

    // Mocking
    Object containerInput = createTestDataAndConfigureMock(false);

    // Execution
    File templatesFolder = new File(testFileRootPath + "templates");
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);
    List<String> matchingTriggerIds = target.getMatchingTriggerIds(containerInput);

    // Verification
    Assert.assertNotNull(matchingTriggerIds);
    Assert.assertEquals(0, matchingTriggerIds.size());

  }

  /**
   * Tests whether a container matcher will match iff there are matchers matching the child resources
   *
   * @throws Exception test fails
   */
  @Test
  public void testContainerMatcherMatches() throws Exception {

    // Mocking
    Object containerInput = createTestDataAndConfigureMock(true);

    // Execution
    File templatesFolder = new File(testFileRootPath + "templates");
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);
    List<String> matchingTriggerIds = target.getMatchingTriggerIds(containerInput);

    // Verification
    assertThat(matchingTriggerIds).isNotNull().isNotEmpty();

  }

  /**
   * Tests whether variable resolving works for a container's children as the container itself does not include any
   * variable resolving
   *
   * @throws Exception test fails
   */
  @Test
  public void testContextVariableResolving() throws Exception {

    // Mocking
    Object containerInput = createTestDataAndConfigureMock(true);

    // Execution
    File templatesFolder = new File(testFileRootPath + "templates");
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);
    List<TemplateTo> matchingTemplates = target.getMatchingTemplates(containerInput);

    // Verification
    Assert.assertNotNull(matchingTemplates);
  }

  /**
   * Tests whether variable resolving works for a contains's children during generation
   *
   * @throws Exception test fails
   */
  @Test
  public void testContextVariableResolvingOnGeneration() throws Exception {

    // Mocking
    Object containerInput = createTestDataAndConfigureMock(true);
    File generationRootFolder = this.tmpFolder.newFolder("generationRootFolder");

    // pre-processing
    File templatesFolder = new File(testFileRootPath + "templates");
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);
    List<TemplateTo> templates = target.getMatchingTemplates(containerInput);

    // Execution
    GenerationReportTo report = target.generate(containerInput, templates.get(0),
        Paths.get(generationRootFolder.toURI()), false);

    // assertion
    assertThat(report).isSuccessful();
  }

  /**
   * Tests whether the increments can be correctly retrieved for container matchers
   *
   * @throws Exception test fails
   */
  @Test
  public void testGetAllIncrements() throws Exception {

    // Mocking
    Object containerInput = createTestDataAndConfigureMock(true, true);

    // pre-processing
    File templatesFolder = new File(testFileRootPath + "templates");
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);

    // Execution
    List<IncrementTo> increments = target.getMatchingIncrements(containerInput);

    // Verification
    Assert.assertNotNull(increments);
    Assert.assertTrue(increments.size() > 0);
  }

  /**
   * Tests whether multiple triggers will be activated if their container matcher matches a given input. <br/>
   * <a href="https://github.com/devonfw/cobigen/issues/57">(Bug #57)</a>
   *
   * @throws Exception test fails
   */
  @Test
  public void testMultipleTriggerWithContainerMatchers() throws Exception {

    // Mocking
    Object containerInput = createTestDataAndConfigureMock(true, false);

    // pre-processing
    File templatesFolder = new File(testFileRootPath + "templates");
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);

    // Execution
    List<String> triggerIds = target.getMatchingTriggerIds(containerInput);

    // Verification
    Assert.assertNotNull(triggerIds);
    Assert.assertEquals(2, triggerIds.size());
  }

  /**
   * Tests whether a single trigger will be activated if two triggers with different container matchers (different
   * accumulationTypes) were provided <br/>
   * <a href="https://github.com/devonfw/cobigen/issues/1299">(Bug #1299)</a>
   *
   * @throws Exception test fails
   */
  @Test
  public void testMultipleTriggerWithDifferentContainerMatchersAccumulationType() throws Exception {

    // Mocking
    Object containerInput = createTestDataAndConfigureMock(true, false);

    // pre-processing
    File templatesFolder = new File(testFileRootPath + "accumulationType");
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);

    // Execution
    List<String> triggerIds = target.getMatchingTriggerIds(containerInput);

    // Verification
    Assert.assertNotNull(triggerIds);
    Assert.assertEquals(1, triggerIds.size());
  }

  /**
   * Create a new {@link ContainerMatcher}, which contains two children which do not match the same trigger.
   *
   * @throws Exception test fails
   */
  @Test
  public void testContainerChildrenWillIndividuallyBeMatched() throws Exception {

    Object container = new Object() {
      @Override
      public String toString() {

        return "container";
      }
    };
    Object child1 = new Object() {
      @Override
      public String toString() {

        return "child1";
      }
    };
    Object child2 = new Object() {
      @Override
      public String toString() {

        return "child2";
      }
    };

    // Pre-processing: Mocking
    GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
    TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
    MatcherInterpreter matcher = mock(MatcherInterpreter.class);
    InputReader inputReader = mock(InputReader.class);

    when(triggerInterpreter.getType()).thenReturn("test");
    when(triggerInterpreter.getMatcher()).thenReturn(matcher);
    when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

    when(inputReader.isValidInput(ArgumentMatchers.any())).thenReturn(true);

    // Simulate container children resolution of any plug-in
    when(matcher.resolveVariables(argThat(new MatcherToMatcher(equalTo("or"), any(String.class), sameInstance(child1))),
        anyList(), ArgumentMatchers.any()))
            .thenReturn(ImmutableMap.<String, String> builder().put("variable", "child1").build());
    when(matcher.resolveVariables(argThat(new MatcherToMatcher(equalTo("or"), any(String.class), sameInstance(child2))),
        anyList(), ArgumentMatchers.any()))
            .thenReturn(ImmutableMap.<String, String> builder().put("variable", "child2").build());
    when(inputReader.getInputObjects(ArgumentMatchers.any(), ArgumentMatchers.any(Charset.class)))
        .thenReturn(Lists.newArrayList(child1, child2));

    // match container
    when(matcher
        .matches(argThat(new MatcherToMatcher(equalTo("container"), any(String.class), sameInstance(container)))))
            .thenReturn(true);

    // do not match first child
    when(matcher.matches(argThat(new MatcherToMatcher(equalTo("or"), any(String.class), sameInstance(child1)))))
        .thenReturn(true);
    when(matcher.matches(argThat(new MatcherToMatcher(equalTo("not"), any(String.class), sameInstance(child1)))))
        .thenReturn(true);

    // match second child
    when(matcher.matches(argThat(new MatcherToMatcher(equalTo("or"), any(String.class), sameInstance(child2)))))
        .thenReturn(true);
    when(matcher.matches(argThat(new MatcherToMatcher(equalTo("not"), any(String.class), sameInstance(child2)))))
        .thenReturn(false);

    PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

    // create CobiGen instance
    File templatesFolder = new File(testFileRootPath + "selectiveContainerGeneration");
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);
    File folder = this.tmpFolder.newFolder();

    // Execution
    GenerationReportTo report = target.generate(container, target.getMatchingTemplates(container),
        Paths.get(folder.toURI()), false);
    assertThat(report).isSuccessful();

    // Verification
    assertNotNull(folder.list());
    assertEquals(1, folder.list().length);
    assertEquals("child2.txt", folder.list()[0]);

  }

  // ######################### PRIVATE ##############################

  /**
   * calls {@link #createTestDataAndConfigureMock(boolean, boolean)
   * createTestDataAndConfigureMock(containerChildMatchesTrigger, false)}
   */
  private Object createTestDataAndConfigureMock(boolean containerChildMatchesTrigger) {

    return createTestDataAndConfigureMock(containerChildMatchesTrigger, false);
  }

  /**
   * Creates simple to debug test data, which includes on container object and one child of the container object. A
   * {@link TriggerInterpreter TriggerInterpreter} will be mocked with all necessary supplier classes to mock a simple
   * java trigger interpreter. Furthermore, the mocked trigger interpreter will be directly registered in the
   * {@link PluginRegistry}.
   *
   * @param containerChildMatchesTrigger defines whether the child of the container input should match any non-container
   *        matcher
   * @param multipleContainerChildren defines whether the container should contain multiple children
   * @return the container as input for generation interpreter for
   * @author mbrunnli (16.10.2014)
   */
  @SuppressWarnings("unchecked")
  private Object createTestDataAndConfigureMock(boolean containerChildMatchesTrigger,
      boolean multipleContainerChildren) {

    // we only need any objects for inputs to have a unique object reference to affect the mocked method
    // calls as intended
    Object container = new Object() {
      @Override
      public String toString() {

        return "container";
      }
    };
    Object firstChildResource = new Object() {
      @Override
      public String toString() {

        return "child";
      }
    };

    // Pre-processing: Mocking
    GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
    TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
    MatcherInterpreter matcher = mock(MatcherInterpreter.class);
    InputReader inputReader = mock(InputReader.class);

    when(triggerInterpreter.getType()).thenReturn("mockplugin");
    when(triggerInterpreter.getMatcher()).thenReturn(matcher);
    when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

    when(inputReader.isValidInput(ArgumentMatchers.any())).thenReturn(true);
    when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), any(String.class), sameInstance(container)))))
        .thenReturn(false);
    when(matcher.matches(argThat(new MatcherToMatcher(equalTo("package"), any(String.class), sameInstance(container)))))
        .thenReturn(true);

    // Simulate container children resolution of any plug-in
    if (multipleContainerChildren) {
      Object secondChildResource = new Object() {
        @Override
        public String toString() {

          return "child2";
        }
      };
      when(inputReader.getInputObjects(ArgumentMatchers.any(), ArgumentMatchers.any(Charset.class)))
          .thenReturn(Lists.newArrayList(firstChildResource, secondChildResource));
    } else {
      when(inputReader.getInputObjects(ArgumentMatchers.any(), ArgumentMatchers.any(Charset.class)))
          .thenReturn(Lists.newArrayList(firstChildResource));
    }

    when(matcher
        .matches(argThat(new MatcherToMatcher(equalTo("fqn"), any(String.class), sameInstance(firstChildResource)))))
            .thenReturn(containerChildMatchesTrigger);

    // Simulate variable resolving of any plug-in
    when(matcher.resolveVariables(
        argThat(new MatcherToMatcher(equalTo("fqn"), any(String.class), sameInstance(firstChildResource))),
        argThat(hasItemsInList(
            //
            new VariableAssignmentToMatcher(equalTo("regex"), equalTo("rootPackage"), equalTo("1"), equalTo(false)),
            new VariableAssignmentToMatcher(equalTo("regex"), equalTo("entityName"), equalTo("3"), equalTo(false)))),
        ArgumentMatchers.any()))
            .thenReturn(ImmutableMap.<String, String> builder().put("rootPackage", "com.devonfw")
                .put("entityName", "Test").build());

    PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

    return container;
  }
}
