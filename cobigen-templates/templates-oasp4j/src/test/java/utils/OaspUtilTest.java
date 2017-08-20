package utils;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import constants.pojo.Field;
import utils.resources.TestClass;
import utils.resources.TestEntity;

/**
 * Tests for {@link OaspUtil}
 */
public class OaspUtilTest {

    /**
     * Tests {@link OaspUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,Map,boolean,boolean,String)}
     * <br/>
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
        assertEquals("entityId",
            new OaspUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, false, false, ""));
    }

    /**
     * Tests {@link OaspUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)}
     * <br/>
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
        assertEquals("EntityId",
            new OaspUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, false, true, ""));
    }

    /**
     * Tests {@link OaspUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)}
     * <br/>
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
        assertEquals("object",
            new OaspUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, false, false, ""));
    }

    /**
     * Tests {@link OaspUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)}
     * <br/>
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
        assertEquals("entityIds",
            new OaspUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, false, false, ""));
    }

    /**
     * Tests {@link OaspUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)}
     * <br/>
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
        assertEquals("deepEntity().getId",
            new OaspUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, true, false, "resources"));
    }

    /**
     * Tests {@link OaspUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)}
     * <br/>
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
        assertEquals("DeepEntity().getId",
            new OaspUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, true, true, "resources"));
    }

    /**
     * Tests {@link OaspUtil#resolveIdVariableNameOrSetterGetterSuffix(Class,String,boolean,boolean,String)}
     * <br/>
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
        assertEquals("deepEntityId",
            new OaspUtil().resolveIdVariableNameOrSetterGetterSuffix(clazz, field, true, false, "nomatch"));
    }

}