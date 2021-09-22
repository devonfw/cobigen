package com.devonfw.cobigen.api.exception;

/** Raised if a plugin is configured in one of the configurations which cannot be found. */
public class PluginNotAvailableException extends InvalidConfigurationException {

  /** Default serial version UID */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link PluginNotAvailableException} with the given message
   *
   * @param component the component not found
   * @param plugintype the plugin type searched for
   */
  public PluginNotAvailableException(String component, String plugintype) {

    super((plugintype != null ? "The Plug-in with type " + plugintype + " did not serve any "
        : "There is no plug-in serving ") + component
        + ". Please make sure, that you installed all necessary plug-ins and there is no typo in "
        + "type='...' values of all triggers in your context configuration as well as there are no typos "
        + "in your templates configuration's mergeStrategy='...' values.");
  }
}
