package com.devonfw.cobigen.impl.config.reader;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.ConfigurationProperties;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.util.FileSystemUtil;

import java.net.URI;
import java.nio.file.Path;

public class ConfigurationReaderFactory {

  /**
   * Location where the properties are saved
   */
  protected ConfigurationProperties configurationProperties;

  public static ConfigurationReader create(URI configurationLocation) {

//  this.configurationLocation = configurationLocation;
    Path configRoot = FileSystemUtil.createFileSystemDependentPath(configurationLocation);

    // updates the root template path and informs all of its observers
    PluginRegistry.notifyPlugins(configRoot);

    if(isTemplateSetConfiguration(configRoot)) {
      return new TemplateSetsConfigReader(configRoot);
    } else {
      return new MonolithicConfigReader(configRoot);
    }
  }

  /**
   * Checks if this a template set configuration or a templates configuration (true if templateSetConfiguration)
   *
   * @return true if the template folder structure consists of template sets or false if the monolithic structure is
   * used.
   */
  private static boolean isTemplateSetConfiguration(Path configRoot) {

    // TODO: Replace with a better logic for template set detection later f.e. groupid, see:
    // https://github.com/devonfw/cobigen/issues/1660
    return !configRoot.toUri().getScheme().equals("jar")
      && configRoot.getFileName().toString().equals(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
  }
}
