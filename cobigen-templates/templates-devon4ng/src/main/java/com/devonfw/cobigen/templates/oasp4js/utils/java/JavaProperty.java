package com.devonfw.cobigen.templates.oasp4js.utils.java;

import net.sf.mmm.util.pojo.descriptor.api.PojoPropertyDescriptor;
import net.sf.mmm.util.pojo.descriptor.api.accessor.PojoPropertyAccessorNonArg;
import net.sf.mmm.util.pojo.descriptor.api.accessor.PojoPropertyAccessorNonArgMode;
import net.sf.mmm.util.pojo.descriptor.api.accessor.PojoPropertyAccessorOneArg;
import net.sf.mmm.util.pojo.descriptor.api.accessor.PojoPropertyAccessorOneArgMode;
import net.sf.mmm.util.reflect.api.GenericType;

/**
 * Represents a property of a {@link JavaBean}.
 *
 * @see JavaBean#getProperty(String)
 */
public class JavaProperty {

  private final JavaBean bean;

  private final PojoPropertyDescriptor descriptor;

  private final PojoPropertyAccessorNonArg getter;

  private final PojoPropertyAccessorOneArg setter;

  /**
   * The constructor.
   *
   * @param bean the owining {@link JavaBean}.
   * @param descriptor the {@link PojoPropertyDescriptor} of the property.
   */
  public JavaProperty(JavaBean bean, PojoPropertyDescriptor descriptor) {
    super();
    this.bean = bean;
    this.descriptor = descriptor;
    this.getter = this.descriptor.getAccessor(PojoPropertyAccessorNonArgMode.GET);
    this.setter = this.descriptor.getAccessor(PojoPropertyAccessorOneArgMode.SET);
  }

  /**
   * @return the name of the property (e.g. "name" derived from "getName()" method or "readable" for "isReadable()").
   */
  public String getName() {

    return this.descriptor.getName();
  }

  /**
   * @return {@code true} if this property can be read (has a getter method or corresponds to a field).
   */
  public boolean isReadable() {

    return (this.getter != null);
  }

  /**
   * @return {@code true} if this property can be written (has a setter method or corresponds to a non-final field).
   */
  public boolean isWritable() {

    return (this.setter != null);
  }

  /**
   * @return {@code true} if this property is declared by its {@link #getBean() owning bean}.
   */
  public boolean isDeclared() {

    boolean declared;
    if (this.getter != null) {
      declared = this.getter.getDeclaringClass() == this.bean.getBeanClass();
    } else if (this.setter != null) {
      declared = this.setter.getDeclaringClass() == this.bean.getBeanClass();
    } else {
      declared = false;
    }
    return declared;
  }

  /**
   * @return the {@link JavaBean} owning this property.
   */
  public JavaBean getBean() {

    return this.bean;
  }

  /**
   * @return the {@link Class} reflecting the type of this property (return type of getter or type of field).
   * @see net.sf.mmm.util.pojo.descriptor.api.accessor.PojoPropertyAccessor#getPropertyClass()
   */
  public Class<?> getPropertyClass() {

    if (this.getter == null) {
      return null;
    }
    return this.getter.getPropertyClass();
  }

  /**
   * @return the {@link GenericType} reflecting the type of this property (return type of getter or type of field).
   * @see net.sf.mmm.util.pojo.descriptor.api.accessor.PojoPropertyAccessor#getPropertyType()
   */
  public GenericType<?> getPropertyType() {

    if (this.getter == null) {
      return null;
    }
    return this.getter.getPropertyType();
  }

  @Override
  public String toString() {

    return this.descriptor.getName() + ":" + getPropertyType();
  }

}
