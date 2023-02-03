package com.devonfw.cobigen.impl.config.entity;

import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.model.AbstractCobiGenModel;
import com.devonfw.cobigen.impl.config.reader.CobiGenPropertiesReader;

import io.github.mmm.base.text.CaseSyntax;

/**
 * This class is a container for variables that can inherit from parent {@link Variables} building a hierarchy. The
 * {@link #containsKey(String) keys} for {@link #get(String) getting} and {@link #put(String, Object) setting} variables
 * are internally normalized (see {@link CaseSyntax#normalizeExample(String)}) and therefore treated case-insensitive as
 * well as stripped from special characters. Hence, you should name and use variable names in CobiGen templates and
 * their paths accordingly. For legacy support also the original variable name is used with priority so that special
 * characters are still supported for legacy syntax (e.g. <code>${Variable-Name}</code>).
 */
public class Variables extends AbstractCobiGenModel {

  /** The {@link Properties} containing the local variables. */
  private final Properties properties;

  /**
   * The constructor for the root variables.
   */
  public Variables() {

    this(null, null);
  }

  /**
   * The constructor for the root variables.
   *
   * @param properties the internal {@link Properties} with the variables locally defined here.
   */
  public Variables(Properties properties) {

    this(properties, null);
  }

  /**
   * The constructor for the child variables.
   *
   * @param parent the parent {@link Variables} to inherit from or {@code null} for the root {@link Variables}.
   */
  public Variables(Variables parent) {

    this(null, parent);
  }

  /**
   * The constructor for the child variables.
   *
   * @param properties the internal {@link Properties} with the variables locally defined here.
   * @param parent the parent {@link Variables} to inherit from or {@code null} for the root {@link Variables}.
   */
  public Variables(Properties properties, Variables parent) {

    super(parent);
    if (properties == null) {
      this.properties = new Properties();
    } else {
      this.properties = properties;
      putAll(getOriginalMap(), true);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Map<String, Object> getOriginalMap() {

    return (Map) this.properties;
  }

  /**
   * Creates a new variables instance from the given map.
   *
   * @param map variable entries to be added.
   * @return the newly created instance.
   */
  public static Variables fromMap(Map<String, Object> map) {

    Variables variables = new Variables();
    variables.putAll(map);
    return variables;
  }

  /**
   * @param folder the {@link Path} pointing to a child-folder potentially containing
   *        {@link ConfigurationConstants#COBIGEN_PROPERTIES cobigen.properties}.
   * @return a new {@link Variables} instance inherited from this one with the
   *         {@link ConfigurationConstants#COBIGEN_PROPERTIES cobigen.properties} set or this {@link Variables} if no
   *         such properties exists.
   */
  public Variables forChildFolder(Path folder) {

    Properties childProperties = CobiGenPropertiesReader.load(folder);
    if (childProperties == null) {
      return this;
    }
    return new Variables(childProperties, this);
  }

}
