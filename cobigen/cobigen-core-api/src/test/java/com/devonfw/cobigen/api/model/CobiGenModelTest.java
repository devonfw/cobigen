package com.devonfw.cobigen.api.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Test of {@link CobiGenModel} with its implementation {@link CobiGenModelDefault}.
 */
public class CobiGenModelTest extends Assertions {

  /**
   * Test of {@link CobiGenModel#resolve(String, char, VariableSyntax)} with {@link VariableSyntax#AGNOSTIC}.
   */
  @Test
  public void testResolveAgnostic() {

    CobiGenModel model = new CobiGenModelDefault();
    CobiGenVariableDefinitions.ENTITY_NAME.setValue(model, "MyExample");
    CobiGenVariableDefinitions.ROOT_PACKAGE.setValue(model, "com.customer.app");
    CobiGenVariableDefinitions.COMPONENT_NAME.setValue(model, "MyComponent");
    VariableSyntax syntax = VariableSyntax.AGNOSTIC;
    assertThat(model.resolve("Hello X_EntityName_X or x-entity-name-x in resolve.", '.', syntax))
        .isEqualTo("Hello MyExample or my-example in resolve.");
    assertThat(model.resolve("X_EntityName_Xx-entity-name-x", '.', syntax)).isEqualTo("MyExamplemy-example");
    assertThat(model.resolve("X_ENTITY_NAME_Xx.entity.name.x", '.', syntax)).isEqualTo("MY_EXAMPLEmy.example");
    assertThat(model.resolve("x_rootpackage_x.x_component_x.layer.x_scope_x.x_detail_x", '.', syntax))
        .isEqualTo("com.customer.app.mycomponent.layer");
    assertThat(model.resolve("${EntityName}", '.', syntax)).isEqualTo("${EntityName}");
  }

  /**
   * Test of {@link CobiGenModel#resolve(String, char, VariableSyntax)} with {@link VariableSyntax#DOLLAR}.
   */
  @Test
  public void testResolveDollar() {

    CobiGenModel model = new CobiGenModelDefault();
    CobiGenVariableDefinitions.ENTITY_NAME.setValue(model, "MyExample");
    CobiGenVariableDefinitions.ROOT_PACKAGE.setValue(model, "com.customer.app");
    CobiGenVariableDefinitions.COMPONENT_NAME.setValue(model, "MyComponent");
    VariableSyntax syntax = VariableSyntax.DOLLAR;
    assertThat(model.resolve("Hello ${variables.EntityName} or ${entity-name?uncap_first} in resolve.", '.', syntax))
        .isEqualTo("Hello MyExample or myExample in resolve.");
    assertThat(model.resolve("${EntityName}${entity-name}", '.', syntax)).isEqualTo("MyExampleMyExample");
    assertThat(model.resolve("${EntityName?upper_case}${entity.name?lower_case}", '.', syntax))
        .isEqualTo("MYEXAMPLEmyexample");
    assertThat(model.resolve("${rootpackage}.${variables.component?lower_case}.layer.${scope}.${detail}", '.', syntax))
        .isEqualTo("com.customer.app.mycomponent.layer..");
    assertThat(model.resolve("${rootpackage?cap_first}", '/', syntax)).isEqualTo("Com/customer/app");
    assertThat(model.resolve("X_EntityName_X", '.', syntax)).isEqualTo("X_EntityName_X");
  }

  /**
   * Test of {@link CobiGenModel#resolve(String, char, VariableSyntax)} with {@link VariableSyntax#DOLLAR}.
   */
  @Test
  public void testResolveMixed() {

    CobiGenModel model = new CobiGenModelDefault();
    CobiGenVariableDefinitions.ENTITY_NAME.setValue(model, "MyExample");
    CobiGenVariableDefinitions.ROOT_PACKAGE.setValue(model, "com.customer.app");
    CobiGenVariableDefinitions.COMPONENT_NAME.setValue(model, "MyComponent");
    assertThat(model.resolve("Hello ${variables.EntityName} or x-entity-name-x in resolve.", '.'))
        .isEqualTo("Hello MyExample or my-example in resolve.");
  }

  /**
   * Test of hierarchical {@link CobiGenModel} with inheritance from parent model.
   */
  @Test
  public void testInheritance() {

    CobiGenModel root = new CobiGenModelDefault();
    CobiGenModel child = new CobiGenModelDefault(root);
    CobiGenModel baby = new CobiGenModelDefault(child);
    String key = "foo";
    assertThat(baby.getVariable(key)).isNull();
    root.put(key, "rootFoo");
    assertThat(baby.getVariable(key)).isEqualTo("rootFoo");
    child.put(key, "childFoo");
    assertThat(baby.getVariable(key)).isEqualTo("childFoo");
    baby.put(key, "babyFoo");
    assertThat(baby.getVariable(key)).isEqualTo("babyFoo");
  }

}
