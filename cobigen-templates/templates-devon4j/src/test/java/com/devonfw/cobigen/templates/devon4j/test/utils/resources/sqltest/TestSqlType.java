package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;

import com.devonfw.cobigen.templates.devon4j.test.utils.SQLUtilTest;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.entities.TestSimpleEntity;

import jakarta.validation.constraints.NotNull;

/**
 * This class is a test class for {@link SQLUtilTest}
 *
 */
public class TestSqlType {

  // Specific Statement variations
  @Id
  Long id;

  @Column(name = "TEST_AT_COLUMN_NULLABLE", length = 50, nullable = false)
  String testAtColumnNullable;

  @Column(name = "TEST_AT_COLUMN_NULLABLE_MISSING", length = 50)
  String testAtColumnNullableMissing;

  @Column(name = "TEST_AT_NULLABLE", length = 50)
  @NotNull
  String testAtNullable;

  @Column(name = "TEST_AT_NULLABLE_MISSING", length = 50)
  String testAtNullableMissing;

  // Integer
  Integer wrapperInteger;

  int primitiveInt;

  Year year;

  Month month;

  // BIGINT
  Long wrapperLong;

  long primitiveLong;

  Object object;

  // SMALLINT
  Short wrapperShort;

  short primitiveShort;

  // FLOAT
  Float wrapperFloat;

  float primitiveFloat;

  // DOUBLE
  Double wrapperDouble;

  double primitiveDouble;

  // NUMERIC
  BigDecimal bigDecimal;

  BigInteger bigInteger;

  // CHAR
  Character wrapperChar;

  char primitiveChar;

  // TINYINT
  Byte wrapperByte;

  byte primitiveByte;

  // BOOLEAN
  Boolean wrapperBool;

  boolean primitiveBool;

  // TIMESTAMP
  Instant instant;

  Timestamp timestamp;

  // DATE
  Date date;

  Calendar calendar;

  // TIME
  Time time;

  // BINARY
  UUID uuid;

  // BLOB
  Blob blob;

  // Entities
  TestSimpleEntity testSimpleEntity;

}
