package com.devonfw.cobigen.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** The TemplateAdapter implements methods for adapting template jars */
public interface TemplateAdapter {

  /**
   * Adapt a given set of template set jars.
   *
   * @param templateSetJars A {@link List} of the {@link Path} of the template set jars to adapt
   * @param forceOverride Indicator whether an already adapted template set should be overridden
   * @throws IOException If CobiGen is not able to extract the jar file to the destination folder
   */
  public void adaptTemplateSets(List<Path> templateSetJars, boolean forceOverride) throws IOException;

  /**
   * Adapt a set of template set jars to a given destination folder.
   *
   * @param templateSetJars A {@link List} of the {@link Path} of the template set jars to adapt
   * @param destinationPath The parent folder where the jars should be extracted to
   * @param forceOverride Indicator whether an already adapted template set should be overridden
   * @throws IOException If CobiGen is not able to extract the jar file to the destination folder
   */
  public void adaptTemplateSets(List<Path> templateSetJars, Path destinationPath, boolean forceOverride)
      throws IOException;

  /**
   * Adapt an old monolithic template jar structure to a given destination folder.
   *
   * @param destinationPath The folder where the jars should be extracted to
   * @param forceOverride Indicator whether an already adapted template set should be overridden
   * @throws IOException If CobiGen is not able to extract the jar file to the destination folder
   */
  public void adaptMonolithicTemplates(Path destinationPath, boolean forceOverride) throws IOException;

  /**
   * Get the list of template set jar files
   *
   * @return A {@link List} of {@link Path} with all template set jar files found.
   */
  public List<Path> getTemplateSetJarPaths();

  /**
   *
   *
   * @return Returns {@code true} if the template structure consists of an old monolithic template set. Otherwise false.
   */
  public boolean isMonolithicTemplatesConfiguration();

  /**
   * Get the location of the templates.
   *
   * @return The {@link Path} of the templates location.
   */
  public Path getTemplatesLocation();
}
