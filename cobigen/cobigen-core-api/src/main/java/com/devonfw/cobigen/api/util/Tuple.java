package com.devonfw.cobigen.api.util;

/**
 * Simple tuple implementation
 *
 * @param <A> First type of the tuple value
 * @param <B> Second type of the tuple value
 */
public class Tuple<A, B> {

  private A a;

  private B b;

  /**
   * @param a
   * @param b
   */
  public Tuple(A a, B b) {

    super();
    this.a = a;
    this.b = b;
  }

  public A getA() {

    return this.a;
  }

  public void setA(A a) {

    this.a = a;
  }

  public B getB() {

    return this.b;
  }

  public void setB(B b) {

    this.b = b;
  }

}
