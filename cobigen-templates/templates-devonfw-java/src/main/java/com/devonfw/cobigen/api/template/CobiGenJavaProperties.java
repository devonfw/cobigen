package com.devonfw.cobigen.api.template;

/**
 * The tags of this template set.
 */
public interface CobiGenJavaProperties {

  // -- scope --

  /** Key for the target scope of a template. */
  String KEY_SCOPE = "scope";

  /** {@link #KEY_SCOPE Scope} value {@value} */
  String VALUE_SCOPE_API = "api";

  /** {@link #KEY_SCOPE Scope} value {@value} */
  String VALUE_SCOPE_BASE = "base";

  /** {@link #KEY_SCOPE Scope} value {@value} */
  String VALUE_SCOPE_IMPL = "impl";

  // -- module --

  /** Key for the target (maven) module of a template. Will only be used in multi-module projects. */
  String KEY_MODULE = "module";

  /** {@link #KEY_MODULE Module} value {@value} */
  String VALUE_MODULE_API = "api";

  /** {@link #KEY_MODULE Module} value {@value} */
  String VALUE_MODULE_CORE = "core";

}
