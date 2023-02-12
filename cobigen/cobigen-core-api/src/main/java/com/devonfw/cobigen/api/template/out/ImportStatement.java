package com.devonfw.cobigen.api.template.out;

/**
 * TODO hohwille This type ...
 *
 */
public interface ImportStatement {

  default boolean isStatic() {

    return getStaticReference() != null;
  }

  int getKeyCount();

  default String getKey() {

    return getKey(0);
  }

  String getKey(int i);

  String getTarget();

  default String getStaticReference() {

    return null;
  }

  default String getAlias() {

    return null;
  }

}
