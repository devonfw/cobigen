package com.devonfw.cobigen.templates.devon4j.test.templates.testclasses;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Test class to test sql data type mapping
 *
 */
@Entity
@Table(name = "SQLDataTypeTest")
public class SQLTestEntityDataTypes {

  @Column()
  private int integer1;

  @Column()
  private Integer integer2;

  @Column()
  private long bigint;

  @Column()
  private short smallint;

  @Column()
  private BigDecimal numeric;

  @Column()
  private String varchar;

  @Column()
  private char char1;

  @Column()
  private Character char2;

  @Column()
  private byte tinyint;

  @Column()
  private boolean bit;

  @Column()
  private Date date;

  @Column()
  private Time time;

  @Column()
  private Timestamp timestamp;

  @Column()
  private Calendar timestamp2;

  @Column()
  private byte[] blob;

  @Column()
  private Blob blob2;

  @Column()
  private Clob clob;

  // @Column()
  // private Class<?> varchar2;

  @Column()
  private Locale varchar3;

  @Column()
  private TimeZone varchar4;

  @Column()
  private Currency varchar5;

  /**
   * @return integer1
   */
  public int getInteger1() {

    return this.integer1;
  }

  /**
   * @param integer1 new value of {@link #getinteger1}.
   */
  public void setInteger1(int integer1) {

    this.integer1 = integer1;
  }

  /**
   * @return integer2
   */
  public Integer getInteger2() {

    return this.integer2;
  }

  /**
   * @param integer2 new value of {@link #getinteger2}.
   */
  public void setInteger2(Integer integer2) {

    this.integer2 = integer2;
  }

  /**
   * @return bigint
   */
  public long getBigint() {

    return this.bigint;
  }

  /**
   * @param bigint new value of {@link #getbigint}.
   */
  public void setBigint(long bigint) {

    this.bigint = bigint;
  }

  /**
   * @return smallint
   */
  public short getSmallint() {

    return this.smallint;
  }

  /**
   * @param smallint new value of {@link #getsmallint}.
   */
  public void setSmallint(short smallint) {

    this.smallint = smallint;
  }

  /**
   * @return numeric
   */
  public BigDecimal getNumeric() {

    return this.numeric;
  }

  /**
   * @param numeric new value of {@link #getnumeric}.
   */
  public void setNumeric(BigDecimal numeric) {

    this.numeric = numeric;
  }

  /**
   * @return varchar
   */
  public String getVarchar() {

    return this.varchar;
  }

  /**
   * @param varchar new value of {@link #getvarchar}.
   */
  public void setVarchar(String varchar) {

    this.varchar = varchar;
  }

  /**
   * @return char1
   */
  public char getChar1() {

    return this.char1;
  }

  /**
   * @param char1 new value of {@link #getchar1}.
   */
  public void setChar1(char char1) {

    this.char1 = char1;
  }

  /**
   * @return char2
   */
  public Character getChar2() {

    return this.char2;
  }

  /**
   * @param char2 new value of {@link #getchar2}.
   */
  public void setChar2(Character char2) {

    this.char2 = char2;
  }

  /**
   * @return tinyint
   */
  public byte getTinyint() {

    return this.tinyint;
  }

  /**
   * @param tinyint new value of {@link #gettinyint}.
   */
  public void setTinyint(byte tinyint) {

    this.tinyint = tinyint;
  }

  /**
   * @return bit
   */
  public boolean isBit() {

    return this.bit;
  }

  /**
   * @param bit new value of {@link #getbit}.
   */
  public void setBit(boolean bit) {

    this.bit = bit;
  }

  /**
   * @return date
   */
  public Date getDate() {

    return this.date;
  }

  /**
   * @param date new value of {@link #getdate}.
   */
  public void setDate(Date date) {

    this.date = date;
  }

  /**
   * @return time
   */
  public Time getTime() {

    return this.time;
  }

  /**
   * @param time new value of {@link #gettime}.
   */
  public void setTime(Time time) {

    this.time = time;
  }

  /**
   * @return timestamp
   */
  public Timestamp getTimestamp() {

    return this.timestamp;
  }

  /**
   * @param timestamp new value of {@link #gettimestamp}.
   */
  public void setTimestamp(Timestamp timestamp) {

    this.timestamp = timestamp;
  }

  /**
   * @return timestamp2
   */
  public Calendar getTimestamp2() {

    return this.timestamp2;
  }

  /**
   * @param timestamp2 new value of {@link #gettimestamp2}.
   */
  public void setTimestamp2(Calendar timestamp2) {

    this.timestamp2 = timestamp2;
  }

  /**
   * @return blob
   */
  public byte[] getBlob() {

    return this.blob;
  }

  /**
   * @param blob new value of {@link #getblob}.
   */
  public void setBlob(byte[] blob) {

    this.blob = blob;
  }

  /**
   * @return blob2
   */
  public Blob getBlob2() {

    return this.blob2;
  }

  /**
   * @param blob2 new value of {@link #getblob2}.
   */
  public void setBlob2(Blob blob2) {

    this.blob2 = blob2;
  }

  /**
   * @return clob
   */
  public Clob getClob() {

    return this.clob;
  }

  /**
   * @param clob new value of {@link #getclob}.
   */
  public void setClob(Clob clob) {

    this.clob = clob;
  }

  // /**
  // * @return varchar2
  // */
  // public Class<?> getVarchar2() {
  //
  // return this.varchar2;
  // }
  //
  // /**
  // * @param varchar2 new value of {@link #getvarchar2}.
  // */
  // public void setVarchar2(Class<?> varchar2) {
  //
  // this.varchar2 = varchar2;
  // }

  /**
   * @return varchar3
   */
  public Locale getVarchar3() {

    return this.varchar3;
  }

  /**
   * @param varchar3 new value of {@link #getvarchar3}.
   */
  public void setVarchar3(Locale varchar3) {

    this.varchar3 = varchar3;
  }

  /**
   * @return varchar4
   */
  public TimeZone getVarchar4() {

    return this.varchar4;
  }

  /**
   * @param varchar4 new value of {@link #getvarchar4}.
   */
  public void setVarchar4(TimeZone varchar4) {

    this.varchar4 = varchar4;
  }

  /**
   * @return varchar5
   */
  public Currency getVarchar5() {

    return this.varchar5;
  }

  /**
   * @param varchar5 new value of {@link #getvarchar5}.
   */
  public void setVarchar5(Currency varchar5) {

    this.varchar5 = varchar5;
  }

}
