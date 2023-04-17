package com.devonfw.cobigen.templates.devon4j.test.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.constants.Field;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.TestClass;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.TestEntity;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.dataaccess.api.DeepEntity;
import com.devonfw.cobigen.templates.devon4j.utils.DevonfwUtil;

/**
 * Tests for {@link DevonfwUtil}
 */
public class DevonfwUtilTest {

  /**
   * Tests {@link DevonfwUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,Map,boolean,boolean,String)} <br/>
   * With
   * <ul>
   * <li>Class {@link TestClass}</li>
   * <li>field NAME="entity" TYPE="TestEntity", yielding a TestEntity field</li>
   * <li>byReference false</li>
   * <li>capitalize false</li>
   * <li>component ""</li>
   */
  @Test
  public void testResolveIdVariableNameOrSetterGetterSuffixWEntityFieldFFEmpty() throws Exception {

    Class<?> clazz = new TestClass().getClass();
    Map<String, Object> field = new HashMap<>();
    field.put(Field.NAME.toString(), "entity");
    field.put(Field.TYPE.toString(), "TestEntity");
    assertThat(new DevonfwUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, false, false, ""))
        .isEqualTo("entityId");
  }

  /**
   * Tests {@link DevonfwUtil#resolveIdGetter(Map,boolean,String)} <br/>
   * This method is handled in the generation of DAOs. We are testing a concrete case when the input Entity references
   * another Entity in the same component. Furthermore, verifies that the result is correct even if the entity name does
   * not end with "Entity".<br/>
   * <br/>
   * <b>With</b>
   * <ul>
   * <li>Class {@link DeepEntity}</li>
   * <li>field NAME="testEntityComponent" TYPE="TestEntityComponent", yielding a TestEntityComponent field</li>
   * <li>byReference true</li>
   * <li>component package of the entity</li>
   */
  @Test
  public void testResolveIdGetterEntitySameComponent() throws Exception {

    Map<String, Object> field = new HashMap<>();
    DeepEntity deepEntity = new DeepEntity();
    String component = "";

    field.put(Field.NAME.toString(), "testEntityComponent");
    field.put(Field.CANONICAL_TYPE.toString(), deepEntity.getTestEntityComponent().getClass().getCanonicalName());
    field.put(Field.TYPE.toString(), deepEntity.getTestEntityComponent().getClass().getTypeName());
    component = deepEntity.getClass().getPackage().getName();

    assertThat(new DevonfwUtil().resolveIdGetter(field, true, component)).isEqualTo("getTestEntityComponent().getId()");
  }

  /**
   * Tests {@link DevonfwUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)} <br/>
   * With
   * <ul>
   * <li>Class {@link TestClass}</li>
   * <li>fieldName "entity", yielding a TestEntity field</li>
   * <li>byReference false</li>
   * <li>capitalize true</li>
   * <li>component ""</li>
   */
  @Test
  public void testResolveIdVariableNameOrSetterGetterSuffixWEntityFieldFTEmpty() throws Exception {

    Class<?> clazz = new TestClass().getClass();
    Map<String, Object> field = new HashMap<>();
    field.put(Field.NAME.toString(), "entity");
    field.put(Field.TYPE.toString(), "TestEntity");

    assertThat(new DevonfwUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, false, true, ""))
        .isEqualTo("EntityId");
  }

  /**
   * Tests {@link DevonfwUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)} <br/>
   * With
   * <ul>
   * <li>Class {@link TestClass}</li>
   * <li>fieldName "object", yielding String field</li>
   * <li>byReference false</li>
   * <li>capitalize false</li>
   * <li>component ""</li>
   */
  @Test
  public void testResolveIdVariableNameOrSetterGetterSuffixWOEntityFieldFFEmpty() throws Exception {

    Class<?> clazz = new TestClass().getClass();
    Map<String, Object> field = new HashMap<>();
    field.put(Field.NAME.toString(), "object");
    field.put(Field.TYPE.toString(), "String");

    assertThat(new DevonfwUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, false, false, ""))
        .isEqualTo("object");
  }

  /**
   * Tests {@link DevonfwUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)} <br/>
   * With
   * <ul>
   * <li>Class {@link TestClass}</li>
   * <li>fieldName "entitys", yielding a {@link java.util.List}&lt;{@link TestEntity}> field</li>
   * <li>byReference false</li>
   * <li>capitalize false</li>
   * <li>component ""</li>
   */
  @Test
  public void testResolveIdVariableNameOrSetterGetterSuffixWEntityColFieldFFEmpty() throws Exception {

    Class<?> clazz = new TestClass().getClass();
    Map<String, Object> field = new HashMap<>();
    field.put(Field.NAME.toString(), "entitys");
    field.put(Field.TYPE.toString(), "List<TestEntity>");

    assertThat(new DevonfwUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, false, false, ""))
        .isEqualTo("entityIds");
  }

  /**
   * Tests {@link DevonfwUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)} <br/>
   * With
   * <ul>
   * <li>Class {@link TestClass}</li>
   * <li>fieldName "deepEntity", yielding a DeepEntity field</li>
   * <li>byReference true</li>
   * <li>capitalize false</li>
   * <li>component "resources"</li>
   */
  @Test
  public void testResolveIdVariableNameOrSetterGetterSuffixWEntityFieldTFResources() throws Exception {

    Class<?> clazz = new TestClass().getClass();
    Map<String, Object> field = new HashMap<>();
    field.put(Field.NAME.toString(), "deepEntity");
    field.put(Field.TYPE.toString(), "DeepEntity");

    assertThat(new DevonfwUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, true, false, "resources"))
        .isEqualTo("deepEntity().getId");
  }

  /**
   * Tests {@link DevonfwUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)} <br/>
   * With
   * <ul>
   * <li>Class {@link TestClass}</li>
   * <li>fieldName "deepEntity", yielding a DeepEntity field</li>
   * <li>byReference true</li>
   * <li>capitalize true</li>
   * <li>component "resources"</li>
   */
  @Test
  public void testResolveIdVariableNameOrSetterGetterSuffixWEntityFieldTTResources() throws Exception {

    Class<?> clazz = new TestClass().getClass();
    Map<String, Object> field = new HashMap<>();
    field.put(Field.NAME.toString(), "deepEntity");
    field.put(Field.TYPE.toString(), "DeepEntity");

    assertThat(new DevonfwUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, true, true, "resources"))
        .isEqualTo("DeepEntity().getId");
  }

  /**
   * Tests {@link DevonfwUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)} <br/>
   * With
   * <ul>
   * <li>Class {@link TestClass}</li>
   * <li>fieldName "deepEntity", yielding a DeepEntity field</li>
   * <li>byReference true</li>
   * <li>capitalize false</li>
   * <li>component "nomatch"</li>
   */
  @Test
  public void testResolveIdVariableNameOrSetterGetterSuffixWEntityFieldTFNomatch() throws Exception {

    Class<?> clazz = new TestClass().getClass();
    Map<String, Object> field = new HashMap<>();
    field.put(Field.NAME.toString(), "deepEntity");
    field.put(Field.TYPE.toString(), "DeepEntity");

    assertThat(new DevonfwUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, true, false, "nomatch"))
        .isEqualTo("deepEntityId");
  }
}