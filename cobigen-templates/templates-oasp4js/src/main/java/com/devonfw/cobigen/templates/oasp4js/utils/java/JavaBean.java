package com.devonfw.cobigen.templates.oasp4js.utils.java;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.mmm.util.exception.api.ObjectNotFoundException;
import net.sf.mmm.util.pojo.descriptor.api.PojoDescriptor;
import net.sf.mmm.util.pojo.descriptor.api.PojoPropertyDescriptor;
import net.sf.mmm.util.reflect.api.GenericType;

/**
 * Comfortable representation of a Java {@link Class} with build in bean propery introspection.
 */
public class JavaBean {

  private final Class<?> beanClass;

  private final PojoDescriptor<?> descriptor;

  private Map<String, JavaProperty> propertyMap;

  /**
   * The constructor.
   *
   * @param descriptor the {@link PojoDescriptor} of the reflected Java {@link Class}.
   */
  public JavaBean(PojoDescriptor<?> descriptor) {
    super();
    this.beanClass = descriptor.getPojoClass();
    this.descriptor = descriptor;
  }

  /**
   * @return the reflected Java {@link Class}.
   * @see PojoDescriptor#getPojoClass()
   */
  public Class<?> getBeanClass() {

    return this.beanClass;
  }

  private Map<String, JavaProperty> getPropertyMap() {

    if (this.propertyMap == null) {
      Map<String, JavaProperty> map = new HashMap<>();
      for (PojoPropertyDescriptor propertyDescriptor : this.descriptor.getPropertyDescriptors()) {
        JavaProperty property = new JavaProperty(this, propertyDescriptor);
        map.put(property.getName(), property);
      }
      this.propertyMap = Collections.unmodifiableMap(map);
    }
    return this.propertyMap;
  }

  /**
   * @param name the {@link JavaProperty#getName() name} of the requested bean property.
   * @return the requested {@link JavaProperty} or {@code null} if no such property exists.
   */
  public JavaProperty getProperty(String name) {

    return getPropertyMap().get(name);
  }

  /**
   * @param name the {@link JavaProperty#getName() name} of the requested bean property.
   * @return the requested {@link JavaProperty}.
   * @throws ObjectNotFoundException in case the requested property does not exist.
   */
  public JavaProperty getRequiredProperty(String name) throws ObjectNotFoundException {

    JavaProperty property = getProperty(name);
    if (property == null) {
      throw new ObjectNotFoundException(JavaProperty.class, name);
    }
    return property;
  }

  /**
   * @return a {@link Collection} of all available bean properties.
   */
  public Collection<JavaProperty> getAllProperties() {

    return getPropertyMap().values();
  }

  /**
   * @return a {@link Collection} of all bean properties declared by the {@link #getBeanClass() bean class} itself (no
   *         inherited properties).
   */
  public Collection<JavaProperty> getDeclaredProperties() {

    return getPropertyMap().values().stream().filter(x -> x.isDeclared() && x.isReadable())
        .collect(Collectors.toList());
  }

  /**
   * @return a {@link Set} with the {@link GenericType types} of the {@link #getDeclaredProperties() declared
   *         properties}.
   */
  public Set<GenericType<?>> getDeclaredPropertyTypes() {

    return getDeclaredProperties().stream().map(x -> x.getPropertyType()).collect(Collectors.toSet());
  }

  /**
   * @return a {@link Set} with the {@link GenericType types} of {@link #getAllProperties() all properties}.
   */
  public Set<GenericType<?>> getAllPropertyTypes() {

    return getAllProperties().stream().map(x -> x.getPropertyType()).collect(Collectors.toSet());
  }

}
