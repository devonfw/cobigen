package com.devonfw.cobigen.openapiplugin.unittest.matcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.MatcherTo;
import com.devonfw.cobigen.api.to.VariableAssignmentTo;
import com.devonfw.cobigen.openapiplugin.matcher.OpenAPIMatcher;
import com.devonfw.cobigen.openapiplugin.model.ComponentDef;
import com.devonfw.cobigen.openapiplugin.model.EntityDef;
import com.devonfw.cobigen.openapiplugin.util.constants.Constants;

/**
 * Test suite for {@link OpenAPIMatcher}
 */
public class OpenAPIMatcherTest {

  /**
   * Test valid {@link EntityDef} matching
   */
  @Test
  public void testValidEntityDefMatching() {

    EntityDef entityDef = new EntityDef();
    entityDef.setComponentName("Tablemanagement");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    boolean matches = matcher.matches(new MatcherTo("element", "EntityDef", entityDef));

    assertThat(matches).isTrue();
  }

  /**
   * Test valid {@link ComponentDef} matching
   */
  @Test
  public void testValidComponentDefMatching() {

    ComponentDef componentDef = new ComponentDef();
    componentDef.setName("Tablemanagement");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    boolean matches = matcher.matches(new MatcherTo("element", "ComponentDef", componentDef));

    assertThat(matches).isTrue();
  }

  /**
   * Test non valid {@link EntityDef} matching
   */
  @Test
  public void testInvalidEntityDefMatching() {

    EntityDef entityDef = new EntityDef();
    entityDef.setComponentName("Tablemanagement");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    boolean matches = matcher.matches(new MatcherTo("element", "EntityDefs", entityDef));

    assertThat(matches).isFalse();
  }

  /**
   * Test non valid {@link ComponentDef} matching
   */
  @Test
  public void testInvalidComponentDefMatching() {

    ComponentDef componentDef = new ComponentDef();
    componentDef.setName("Tablemanagement");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    boolean matches = matcher.matches(new MatcherTo("element", "ComponentDefs", componentDef));

    assertThat(matches).isFalse();
  }

  /**
   * Test if the generation is successful and without warnings, if a requested variable is assigned
   */
  @Test
  public void testXRootPackageVariableAssigned() {

    ComponentDef componentDef = new ComponentDef();
    componentDef.setName("Tablemanagement");
    componentDef.setUserProperty("x-rootpackage", "com.devonfw");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    GenerationReportTo report = new GenerationReportTo();
    List<VariableAssignmentTo> va = new ArrayList<>();
    va.add(new VariableAssignmentTo("extension", "rootPackage", "x-rootpackage", false));

    matcher.resolveVariables(new MatcherTo("element", "ComponentDef", componentDef), va, report);
    assertThat(report.getWarnings().size()).isEqualTo(0);
  }

  /**
   * Test if the generation is successful and the report contains warnings, if a requested but not mandatory variable
   * isn't given.
   */
  @Test
  public void testMissingXRootPackageVariableNotMandatory() {

    ComponentDef componentDef = new ComponentDef();
    componentDef.setName("Tablemanagement");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    GenerationReportTo report = new GenerationReportTo();
    List<VariableAssignmentTo> vaOptionalXRootPackage = new ArrayList<>();
    vaOptionalXRootPackage.add(new VariableAssignmentTo("extension", "rootPackage", "x-rootpackage", false));

    matcher.resolveVariables(new MatcherTo("element", "ComponentDef", componentDef), vaOptionalXRootPackage, report);
    assertThat(report.getWarnings().get(0)).containsSequence(Constants.getMandatoryWarning("x-rootPackage"));

    List<VariableAssignmentTo> vaMandatoryXRootPackage = new ArrayList<>();
    vaMandatoryXRootPackage.add(new VariableAssignmentTo("extension", "rootpackage", "x-rootpackage", true));

    matcher.resolveVariables(new MatcherTo("element", "ComponentDef", componentDef), vaMandatoryXRootPackage, report);
    assertThat(report.getErrors().get(0).getMessage())
        .containsSequence(Constants.getMandatoryErrorMessage("x-rootpackage"));
  }

  /**
   * Test if the generation isn't successful and the report contains errors, if a mandatory variable isn't given
   */
  @Test
  public void testMissingXRootPackageVariableMandatory() {

    ComponentDef componentDef = new ComponentDef();
    componentDef.setName("Tablemanagement");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    GenerationReportTo report = new GenerationReportTo();
    List<VariableAssignmentTo> vaMandatoryXRootPackage = new ArrayList<>();
    vaMandatoryXRootPackage.add(new VariableAssignmentTo("extension", "rootPackage", "x-rootpackage", true));

    matcher.resolveVariables(new MatcherTo("element", "ComponentDef", componentDef), vaMandatoryXRootPackage, report);
    assertThat(report.getErrors().get(0).getMessage())
        .containsSequence("The property x-rootpackage was required in a variable assignment "
            + "although the input does not provide this property. "
            + "Please add the required attribute in your input file or set the \"mandatory\" attribute to \"false\". ");
  }
}
