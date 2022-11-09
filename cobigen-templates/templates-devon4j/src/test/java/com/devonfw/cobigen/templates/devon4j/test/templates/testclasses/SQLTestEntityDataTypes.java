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
  private int _integer1;

  @Column()
  private Integer _integer2;

  @Column()
  private long _bigint;

  @Column()
  private short _smallint;

  @Column()
  private BigDecimal _numeric;

  @Column()
  private String _varchar;

  @Column()
  private char _char;

  @Column()
  private Character _char2;

  @Column()
  private byte _tinyint;

  @Column()
  private boolean _bit;

  @Column()
  private Date _date;

  @Column()
  private Time _time;

  @Column()
  private Timestamp _timestamp;

  @Column()
  private Calendar _timestamp2;

  @Column()
  private byte[] _blob;

  @Column()
  private Blob _blob2;

  @Column()
  private Clob _clob;

  @Column()
  private Class<?> _varchar2;

  @Column()
  private Locale _varchar3;

  private TimeZone _varchar4;

  @Column()
  private Currency _varchar5;

  /**
   * @return _integer1
   */
  public int get_integer1() {

    return this._integer1;
  }

  /**
   * @param _integer1 new value of {@link #get_integer1}.
   */
  public void set_integer1(int _integer1) {

    this._integer1 = _integer1;
  }

  /**
   * @return _integer2
   */
  public Integer get_integer2() {

    return this._integer2;
  }

  /**
   * @param _integer2 new value of {@link #get_integer2}.
   */
  public void set_integer2(Integer _integer2) {

    this._integer2 = _integer2;
  }

  /**
   * @return _bigint
   */
  public long get_bigint() {

    return this._bigint;
  }

  /**
   * @param _bigint new value of {@link #get_bigint}.
   */
  public void set_bigint(long _bigint) {

    this._bigint = _bigint;
  }

  /**
   * @return _smallint
   */
  public short get_smallint() {

    return this._smallint;
  }

  /**
   * @param _smallint new value of {@link #get_smallint}.
   */
  public void set_smallint(short _smallint) {

    this._smallint = _smallint;
  }

  /**
   * @return _numeric
   */
  public BigDecimal get_numeric() {

    return this._numeric;
  }

  /**
   * @param _numeric new value of {@link #get_numeric}.
   */
  public void set_numeric(BigDecimal _numeric) {

    this._numeric = _numeric;
  }

  /**
   * @return _varchar
   */
  public String get_varchar() {

    return this._varchar;
  }

  /**
   * @param _varchar new value of {@link #get_varchar}.
   */
  public void set_varchar(String _varchar) {

    this._varchar = _varchar;
  }

  /**
   * @return _char
   */
  public char get_char() {

    return this._char;
  }

  /**
   * @param _char new value of {@link #get_char}.
   */
  public void set_char(char _char) {

    this._char = _char;
  }

  /**
   * @return _character
   */
  public Character get_character() {

    return this._character;
  }

  /**
   * @param _character new value of {@link #get_character}.
   */
  public void set_character(Character _character) {

    this._character = _character;
  }

  /**
   * @return _tinyint
   */
  public byte get_tinyint() {

    return this._tinyint;
  }

  /**
   * @param _tinyint new value of {@link #get_tinyint}.
   */
  public void set_tinyint(byte _tinyint) {

    this._tinyint = _tinyint;
  }

  /**
   * @return _bit
   */
  public boolean is_bit() {

    return this._bit;
  }

  /**
   * @param _bit new value of {@link #get_bit}.
   */
  public void set_bit(boolean _bit) {

    this._bit = _bit;
  }

  /**
   * @return _date
   */
  public Date get_date() {

    return this._date;
  }

  /**
   * @param _date new value of {@link #get_date}.
   */
  public void set_date(Date _date) {

    this._date = _date;
  }

  /**
   * @return _time
   */
  public Time get_time() {

    return this._time;
  }

  /**
   * @param _time new value of {@link #get_time}.
   */
  public void set_time(Time _time) {

    this._time = _time;
  }

  /**
   * @return _timestamp
   */
  public Timestamp get_timestamp() {

    return this._timestamp;
  }

  /**
   * @param _timestamp new value of {@link #get_timestamp}.
   */
  public void set_timestamp(Timestamp _timestamp) {

    this._timestamp = _timestamp;
  }

  /**
   * @return _timestamp2
   */
  public Calendar get_timestamp2() {

    return this._timestamp2;
  }

  /**
   * @param _timestamp2 new value of {@link #get_timestamp2}.
   */
  public void set_timestamp2(Calendar _timestamp2) {

    this._timestamp2 = _timestamp2;
  }

  /**
   * @return _blob
   */
  public byte[] get_blob() {

    return this._blob;
  }

  /**
   * @param _blob new value of {@link #get_blob}.
   */
  public void set_blob(byte[] _blob) {

    this._blob = _blob;
  }

  /**
   * @return _blob2
   */
  public Blob get_blob2() {

    return this._blob2;
  }

  /**
   * @param _blob2 new value of {@link #get_blob2}.
   */
  public void set_blob2(Blob _blob2) {

    this._blob2 = _blob2;
  }

  /**
   * @return _clob
   */
  public Clob get_clob() {

    return this._clob;
  }

  /**
   * @param _clob new value of {@link #get_clob}.
   */
  public void set_clob(Clob _clob) {

    this._clob = _clob;
  }

  // /**
  // * @return _varchar2
  // */
  // public Class get_varchar2() {
  //
  // return this._varchar2;
  // }

  // /**
  // * @param _varchar2 new value of {@link #get_varchar2}.
  // */
  // public void set_varchar2(Class _varchar2) {
  //
  // this._varchar2 = _varchar2;
  // }

  /**
   * @return _varchar3
   */
  public Locale get_varchar3() {

    return this._varchar3;
  }

  /**
   * @param _varchar3 new value of {@link #get_varchar3}.
   */
  public void set_varchar3(Locale _varchar3) {

    this._varchar3 = _varchar3;
  }

  /**
   * @return _varchar4
   */
  public TimeZone get_varchar4() {

    return this._varchar4;
  }

  /**
   * @param _varchar4 new value of {@link #get_varchar4}.
   */
  public void set_varchar4(TimeZone _varchar4) {

    this._varchar4 = _varchar4;
  }

  /**
   * @return _carchar5
   */
  public Currency get_carchar5() {

    return this._varchar5;
  }

  /**
   * @param _carchar5 new value of {@link #get_carchar5}.
   */
  public void set_carchar5(Currency _carchar5) {

    this._varchar5 = _carchar5;
  }

}
