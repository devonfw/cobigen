package com.devonfw.cobigen.templates.devon4j.test.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.lang.reflect.Field;

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
      fieldTestEntityAtColumnNotNullableAtSizeAtNotNull;

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

    // Class and Field lookup class
    testTestCatClass = new TestCat("Molly", 15).getClass();

    // Field test fields for lookup comparison
    testResultLegs = testTestCatClass.getDeclaredField("legs");

  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string when the type of the current field
   * is translated to the SQL Type VARCHAR.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationForStringType() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestSimpleString)).isEqualTo("VARCHAR");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string when the type of the current field
   * is translated to the SQL Type VARCHAR(SIZE) when the field is provided with the annotation
   * {@linkplain jakarta.validation.constraints.Size @Size}.
   *
   */
  @Test
  public void testGetSimpleSqlTypeAnnotationIsVarcharSize() {

    assertThat(new SQLUtil().getSimpleSQLtype(fieldTestAtSize)).isEqualTo("VARCHAR(2147483647)");
  }

  /**
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string when the type of the current field
   * is translated to the SQL Type INTEGER and the field is provided with the annotation
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
   * Tests if {@linkplain SQLUtil#getSimpleSQLtype(Field)} returns the correct string when the type of the current field
   * is translated to the SQL Type INTEGER.
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
   * Tests if {@linkplain SQLUtil#getFieldByName(Class, String)} can lookup for a field in a certain class by its name.
   */
  @Test
  public void testGetFieldByNameSuccess() {

    assertThat(new SQLUtil().getFieldByName(testTestCatClass, "legs")).isEqualTo(testResultLegs);
  }

  /**
   * Tests if {@linkplain SQLUtil#getFieldByName(Class, String)} throws an {@linkplain java.lang.IllegalAccessError
   * Error} when the field doesn't exist.
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
}