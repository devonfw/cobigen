package com.devonfw.cobigen.templates.devon4j.test.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.assertj.core.api.Condition;
import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.TestCat;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.TestSqlTypeAnnotations;
import com.devonfw.cobigen.templates.devon4j.utils.SQLUtil;

/**
 * Test class for {@link SQLUtil}
 */
public class SQLUtilTest {

  // Annotation test class
  private static Class<?> testSqlTypeAnnotations;

  // Annotation test fields
  private static Field fieldTestSimpleString, fieldTestAtSize, fieldTestSizeMissing, fieldTestSimpleInteger,
      fieldTestAtColumnNullableAtNotNull, fieldTestAtColumnNullable, fieldTestAtColumnNotNullableAtNotNull,
      fieldTestAtColumnNotNullable, fieldTestAtNotNull, fieldTestAtSizeAtNotNull,
      fieldTestEntityAtColumnNotNullableAtSizeAtNotNull, fieldTestEntityAtTable, fieldTestAnonymousClassEntity,
      fieldTestEntityAtTableNameDefault, fieldTestAnonymousEntityAtTableNameDefault, fieldTestEntityAtTableNull,
      fieldTestAnonymousEntityAtTableNull;

  // Class and Field lookup test class
  private static Class<?> testTestCatClass;

  // Field test fields for lookup comparison
  private static Field testResultLegs;

  /**
   * Get all Classes for testing
   *
   * @throws SecurityException
   * @throws NoSuchFieldException
   */
  @SuppressWarnings("javadoc")
  @BeforeClass
  public static void beforeAll() throws NoSuchFieldException, SecurityException {

    // Annotation class
    testSqlTypeAnnotations = new TestSqlTypeAnnotations().getClass();
    // Annotation fields
    fieldTestSimpleString = testSqlTypeAnnotations.getDeclaredField("testSimpleString");
    fieldTestAtSize = testSqlTypeAnnotations.getDeclaredField("testAtSize");
    fieldTestSizeMissing = testSqlTypeAnnotations.getDeclaredField("testSizeMissing");
    fieldTestSimpleInteger = testSqlTypeAnnotations.getDeclaredField("testSimpleInteger");
    fieldTestAtColumnNullableAtNotNull = testSqlTypeAnnotations.getDeclaredField("testAtColumnNullableAtNotNull");
    fieldTestAtColumnNullable = testSqlTypeAnnotations.getDeclaredField("testAtColumnNullable");
    fieldTestAtColumnNotNullableAtNotNull = testSqlTypeAnnotations.getDeclaredField("testAtColumnNotNullableAtNotNull");
    fieldTestAtColumnNotNullable = testSqlTypeAnnotations.getDeclaredField("testAtColumnNotNullable");
    fieldTestAtNotNull = testSqlTypeAnnotations.getDeclaredField("testAtNotNull");
    fieldTestAtSizeAtNotNull = testSqlTypeAnnotations.getDeclaredField("testAtSizeAtNotNull");
    fieldTestAtSizeAtNotNull = testSqlTypeAnnotations.getDeclaredField("testAtSizeAtNotNull");
    fieldTestEntityAtColumnNotNullableAtSizeAtNotNull = testSqlTypeAnnotations
        .getDeclaredField("testEntityAtColumnNotNullableAtSizeAtNotNull");
    fieldTestEntityAtTable = testSqlTypeAnnotations.getDeclaredField("testEntityAtTable");
    fieldTestAnonymousClassEntity = testSqlTypeAnnotations.getDeclaredField("testAnonymousEntityAtTable");
    fieldTestEntityAtTableNameDefault = testSqlTypeAnnotations.getDeclaredField("testEntityAtTableNameDefault");
    fieldTestAnonymousEntityAtTableNameDefault = testSqlTypeAnnotations
        .getDeclaredField("testAnonymousEntityAtTableNameDefault");
    fieldTestEntityAtTableNull = testSqlTypeAnnotations.getDeclaredField("testEntityAtTableNull");
    fieldTestAnonymousEntityAtTableNull = testSqlTypeAnnotations.getDeclaredField("testAnonymousEntityAtTableNull");
    // Class and Field lookup class
    testTestCatClass = new TestCat("Molly", 15).getClass();

    // Field test fields for lookup comparison
    testResultLegs = testTestCatClass.getDeclaredField("legs");

  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string when the type of the current
   * {@linkplain java.lang.reflect.Field field} is translated to the SQL Type VARCHAR.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationForStringType() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestSimpleString)).isEqualTo("VARCHAR");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string when the type of the current
   * {@linkplain java.lang.reflect.Field field} is translated to the SQL Type VARCHAR(SIZE) when the
   * {@linkplain java.lang.reflect.Field field} is provided with the annotation
   * {@linkplain jakarta.validation.constraints.Size @Size}.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationIsVarcharSize() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestAtSize)).isEqualTo("VARCHAR(2147483647)");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string when the type of the current
   * {@linkplain java.lang.reflect.Field field} is translated to the SQL Type INTEGER and the
   * {@linkplain java.lang.reflect.Field field} is provided with the annotation
   * {@linkplain jakarta.validation.constraints.Size}.
   *
   * This scenario shows that the generation ignores the {@linkplain jakarta.validation.constraints.Size} annotation
   * when the current Java type is not a {@linkplain String}.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationSizeIsMissing() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestSizeMissing)).isEqualTo("INTEGER");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string when the type of the current
   * {@linkplain java.lang.reflect.Field field} is translated to the SQL Type INTEGER.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationForIntegerType() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestSimpleInteger)).isEqualTo("INTEGER");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string based on the provided annotations
   * {@linkplain javax.persistence.Column @Column} and {@linkplain jakarta.validation.constraints.NotNull @NotNull}.
   * When either {@linkplain javax.persistence.Column#nullable nullable} from
   * {@linkplain javax.persistence.Column @Column} is set to {@linkplain java.lang.Boolean#TRUE true} or the in this
   * case redundant annotation {@linkplain jakarta.validation.constraints.NotNull @NotNull} is provided. The return
   * string should contain NOT NULL.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationAtColumnNullableAtNotNull() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestAtColumnNullableAtNotNull)).isEqualTo("VARCHAR NOT NULL");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string based on the provided annotation
   * {@linkplain javax.persistence.Column @Column}. When {@linkplain javax.persistence.Column#nullable nullable} from
   * {@linkplain javax.persistence.Column @Column} is set to {@linkplain java.lang.Boolean#TRUE true} the return string
   * should contain NOT NULL.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationAtColumnNullable() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestAtColumnNullable)).isEqualTo("VARCHAR NOT NULL");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string based on the provided annotations
   * {@linkplain javax.persistence.Column @Column} and {@linkplain jakarta.validation.constraints.NotNull @NotNull}.
   * When either {@linkplain javax.persistence.Column#nullable nullable} from
   * {@linkplain javax.persistence.Column @Column} is set to {@linkplain java.lang.Boolean#FALSE false} or the in this
   * case redundant annotation {@linkplain jakarta.validation.constraints.NotNull @NotNull} is provided the return
   * string should contain NOT NULL.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationAtColumnNotNullableAtNotNull() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestAtColumnNotNullableAtNotNull)).isEqualTo("VARCHAR NOT NULL");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string based on the provided annotation
   * {@linkplain javax.persistence.Column @Column} when the {@linkplain javax.persistence.Column#nullable nullable}
   * option is set to {@linkplain java.lang.Boolean#FALSE false}. The return string should not contain NOT NULL.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationAtColumnNotNullable() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestAtColumnNotNullable)).isEqualTo("VARCHAR");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string based on the provided annotation
   * {@linkplain jakarta.validation.constraints.NotNull @NotNull}. The return string should not contain NOT NULL.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationAtNotNull() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestAtNotNull)).isEqualTo("VARCHAR NOT NULL");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string based on the provided annotations
   * {@linkplain jakarta.validation.constraints.Size @Size} and
   * {@linkplain jakarta.validation.constraints.NotNull @NotNull}. The default value of
   * {@linkplain jakarta.validation.constraints.Size#max() max(} from
   * {@linkplain jakarta.validation.constraints.Size @Size} defaults to {@linkplain java.lang.Integer#MAX_VALUE
   * Integer.max()} and therefore the result VARCHAR(2147483647) NOT NULL is expected.
   *
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationAtSizeAtNotNull() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestAtSizeAtNotNull)).isEqualTo("VARCHAR(2147483647) NOT NULL");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string based on the provided annotations
   * {@linkplain javax.persistence.Column @Column}, {@linkplain jakarta.validation.constraints.Size @Size} and
   * {@linkplain jakarta.validation.constraints.NotNull @NotNull}. The default value of
   * {@linkplain jakarta.validation.constraints.Size#max() max(} from
   * {@linkplain jakarta.validation.constraints.Size @Size} defaults to {@linkplain java.lang.Integer#MAX_VALUE
   * Integer.max()} and therefore the result VARCHAR(2147483647) NOT NULL is expected.
   *
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationAtColumnNotNullableAtSizeAtNotNull() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestEntityAtColumnNotNullableAtSizeAtNotNull))
        .isEqualTo("VARCHAR(2147483647) NOT NULL");
  }

  /**
   * Tests if {@linkplain SQLUtil#getFieldByName(Class, String)} finds a field in a certain class by its name.
   */
  @Test
  public void testGetFieldByNameSuccess() {

    assertThat(new SQLUtil().getFieldByName(testTestCatClass, "legs")).isEqualTo(testResultLegs);
  }

  /**
   * Tests if {@linkplain SQLUtil#getFieldByName(Class, String)} throws an {@linkplain java.lang.IllegalAccessError
   * Error} when the {@linkplain java.lang.reflect.Field field} doesn't exist.
   */
  @Test
  public void testGetFieldByNameThrowsIllegalAccessErrorByField() {

    assertThrows(IllegalAccessError.class, () -> {
      new SQLUtil().getFieldByName(testTestCatClass, null);
    });
  }

  /**
   * Tests if {@linkplain SQLUtil#getFieldByName(Class, String)} throws an {@linkplain java.lang.IllegalAccessError
   * Error} when the class doesn't exist.
   */
  @Test
  public void testGetFieldByNameThrowsIllegalAccessErrorByClass() {

    assertThrows(IllegalAccessError.class, () -> {
      new SQLUtil().getFieldByName(null, null);
    });
  }

  /**
   * Tests if {@linkplain SQLUtil#getFieldAnnotations(Class, String)} collects a {@linkplain java.lang.reflect.Field
   * field's} annotations.
   */
  @Test
  public void testGetFieldAnnotationsSuccess() {

    final int annotationCountHasElements = 3;
    final int annotationCountHasNoElements = 0;

    Condition<Annotation[]> hasElements = new Condition<Annotation[]>("A condition to ") {
      @Override
      public boolean matches(Annotation[] value) {

        return value.length == annotationCountHasElements || value.length == annotationCountHasNoElements;
      }
    };
    assertThat(new SQLUtil().getFieldAnnotations(testTestCatClass, "name")).has(hasElements);
    assertThat(new SQLUtil().getFieldAnnotations(testTestCatClass, "legs")).has(hasElements);
  }

  /**
   * Tests if {@linkplain SQLUtil#getFieldAnnotations(Class, String)} throws an {@linkplain java.lang.IllegalAccessError
   * Error} when the class doesn't exist.
   */
  @Test
  public void testGetFieldAnnotationsThrowsIllegalAccessErrorByClass() {

    assertThrows(IllegalAccessError.class, () -> {
      new SQLUtil().getFieldByName(null, "legs");
    });

  }

  /**
   * Tests if {@linkplain SQLUtil#getFieldAnnotations(Class, String)} throws an {@linkplain java.lang.IllegalAccessError
   * Error} when the provided field name can not be searched for.
   */
  @Test
  public void testGetFieldAnnotationsThrowsIllegalAccessErrorByFieldName() {

    assertThrows(IllegalAccessError.class, () -> {
      new SQLUtil().getFieldByName(testTestCatClass, "");
      new SQLUtil().getFieldByName(testTestCatClass, null);
    });

  }

  /**
   * Tests if {@linkplain SQLUtil#getEntityTableName(Field)} returns the table name of the provided entity class that
   * was annotated with the option {@linkplain javax.persistence.Table#name() name} from the
   * {@linkplain javax.persistence.Table @Table} annotation.
   *
   * @throws Exception
   */
  @Test
  public void testGetEntityTableNameAtTableSuccess() throws Exception {

    assertThat(new SQLUtil().getEntityTableName(fieldTestEntityAtTable)).isEqualTo("TEST_SIMPLE_ENTITY");

  }

  /**
   * Tests if {@linkplain SQLUtil#getEntityTableName(Field)} returns the table name of the provided entity class that
   * was annotated with the option {@linkplain javax.persistence.Table#name() name} from the
   * {@linkplain javax.persistence.Table @Table} annotation even when the provided class is an anonymous class.
   */
  @Test
  public void testGetEntityTableNameAtTableSuccessAnonymousClasses() throws Exception {

    assertThat(new SQLUtil().getEntityTableName(fieldTestAnonymousClassEntity)).isEqualTo("TEST_SIMPLE_ENTITY");

  }

  /**
   * Tests if {@linkplain SQLUtil#getEntityTableName(Field)} returns the table name of the provided entity class that
   * was not annotated with the option {@linkplain javax.persistence.Table#name() name} from the
   * {@linkplain javax.persistence.Table @Table} annotation.
   *
   * @throws Exception
   */
  @Test
  public void testGetEntityTableNameAtTableNameDefaultSuccess() throws Exception {

    assertThat(new SQLUtil().getEntityTableName(fieldTestEntityAtTableNameDefault)).isEqualTo("TestAnotherSimple");

  }

  /**
   * Tests if {@linkplain SQLUtil#getEntityTableName(Field)} returns the table name of the provided entity class that
   * was not annotated with the option {@linkplain javax.persistence.Table#name() name} from the
   * {@linkplain javax.persistence.Table @Table} annotation even when the provided class is an anonymous class.
   */
  @Test
  public void testGetEntityTableNameAtTableNameDefaultSuccessAnonymousClasses() throws Exception {

    assertThat(new SQLUtil().getEntityTableName(fieldTestAnonymousEntityAtTableNameDefault))
        .isEqualTo("TestAnotherSimple");

  }

  /**
   * Tests if {@linkplain SQLUtil#getEntityTableName(Field)} returns the table name of the provided entity class that
   * was not annotated with the {@linkplain javax.persistence.Table @Table} annotation.
   *
   * @throws Exception
   */
  @Test
  public void testGetEntityTableNameAtTableNullSuccess() throws Exception {

    assertThat(new SQLUtil().getEntityTableName(fieldTestEntityAtTableNull)).isEqualTo("TestNotSoSimple");

  }

  /**
   * Tests if {@linkplain SQLUtil#getEntityTableName(Field)} returns the table name of the provided entity class that
   * was not annotated with the {@linkplain javax.persistence.Table @Table} annotation even when the provided class is
   * an anonymous class.
   *
   * @throws Exception
   */
  @Test
  public void testGetEntityTableNameAtTableNNullSuccessAnonymousClasses() throws Exception {

    assertThat(new SQLUtil().getEntityTableName(fieldTestAnonymousEntityAtTableNull)).isEqualTo("TestNotSoSimple");

  }

  /**
   * Tests if {@linkplain SQLUtil#getEntityTableName(Field)} throws the Exception .
   *
   * @throws Exception
   */
  @Test
  public void testGetEntityTableNameThrowClassNotFoundExceptions() throws Exception {

    assertThrows(IllegalAccessError.class, () -> {
      new SQLUtil().getEntityTableName(null);
    });

  }

  /**
   * Tests if {@linkplain SQLUtil#getPrimaryKey(String)} returns the correct string when a
   * {@linkplain java.lang.reflect.Field field} of the provided class is annotated with the
   * {@linkplain javax.persistence.Id @Id} annotation.
   *
   */
  @Test
  public void testGetPrimaryKeyAtIdSuccess() {

    assertThat(new SQLUtil()
        .getPrimaryKey("com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.primarykeys.Primaryone"))
            .isEqualTo("java.lang.Long,id");

  }

  /**
   * Tests if {@linkplain SQLUtil#getPrimaryKey(String)} returns the correct string when a
   * {@linkplain java.lang.reflect.Field field} of the provided class is annotated with the
   * {@linkplain javax.persistence.Id @Id} annotation and the option and {@linkplain javax.persistence.Column#name()
   * name} from {@linkplain javax.persistence.Column @Column}.
   *
   */
  @Test
  public void testGetPrimaryKeyAtIdAtColumnSuccess() {

    assertThat(new SQLUtil()
        .getPrimaryKey("com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.primarykeys.Primarytwo"))
            .isEqualTo("java.lang.Long,TEST_ID");

  }

  /**
   * Tests if {@linkplain SQLUtil#getPrimaryKey(String)} returns the correct string when a method of the provided class
   * is annotated with the {@linkplain javax.persistence.Id @Id} annotation.
   *
   */
  @Test
  public void testGetPrimaryKeyMethodAtIdSuccess() {

    assertThat(new SQLUtil()
        .getPrimaryKey("com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.primarykeys.Primarythree"))
            .isEqualTo("java.lang.Long,getTestId");

  }

  /**
   * Tests if {@linkplain SQLUtil#getPrimaryKey(String)} returns the correct string when a method of the provided class
   * is annotated with the {@linkplain javax.persistence.Id @Id} annotation and the option and
   * {@linkplain javax.persistence.Column#name() name} from {@linkplain javax.persistence.Column @Column}.
   */
  @Test
  public void testGetPrimaryKeyMethodAtIdAtColumnSuccess() {

    assertThat(new SQLUtil()
        .getPrimaryKey("com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.primarykeys.Primaryfour"))
            .isEqualTo("java.lang.Long,TEST_ID");

  }

  /**
   * Tests if {@linkplain SQLUtil#getPrimaryKey(String)} throws the IllegalArgumentException when the provided class
   * doesn't exist .
   *
   * @throws IllegalArgumentException when non existing class
   */
  @Test
  public void testGetPrimaryKeyNoClass() throws IllegalArgumentException {

    assertThrows(IllegalArgumentException.class, () -> {
      new SQLUtil()
          .getPrimaryKey("com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.primarykeys.NoClass");
    });

  }

  /**
   * Tests if {@linkplain SQLUtil#getPrimaryKey(String)} throws the IllegalArgumentError when the provided class doesn't
   * exist .
   *
   * @throws IllegalAccessError when class object is null
   */
  @Test
  public void testGetPrimaryKeyStringNull() throws IllegalAccessError {

    assertThrows(IllegalAccessError.class, () -> {
      new SQLUtil().getPrimaryKey(null);
    });

  }

  /**
   * Tests if {@linkplain SQLUtil#getPrimaryKey(String)} throws the IllegalArgumentError when the provided class doesn't
   * have a {@linkplain java.lang.reflect.Field field} or a method annotated with the
   * {@linkplain javax.persistence.Id @Id} annotation.
   *
   * @throws IllegalAccessError when class object is null
   */
  @Test
  public void testGetPrimaryKeyNoFieldNoMethod() throws IllegalAccessError {

    assertThrows(IllegalAccessError.class, () -> {
      new SQLUtil()
          .getPrimaryKey("com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.primarykeys.Primaryfive");
    });

  }
}