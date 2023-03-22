package com.devonfw.cobigen.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.devonfw.cobigen.api.exception.TemplateSelectionForAdaptionException;
import com.devonfw.cobigen.api.exception.UpgradeTemplatesNotificationException;

/** The TemplateAdapter implements methods for adapting template jars */
public interface TemplateAdapter {
  /**
   * Adapt the templates. Can either adapt an old monolithic template structure or independent template sets.
   *
   * @throws IOException If CobiGen is not able to extract the jar file to the destination folder
   * @throws UpgradeTemplatesNotificationException If an old monolithic structure was adapted. Can be catched to ask the
   *         user for an upgrade of the templates.
   * @throws TemplateSelectionForAdaptionException If a new template structure is given. To ask the user to select the
   *         template sets to adapt.
   */
  public void adaptTemplates()
      throws IOException, UpgradeTemplatesNotificationException, TemplateSelectionForAdaptionException;

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
   * Adapt an old monolithic template jar structure.
   *
   * @param forceOverride Indicator whether an already adapted template set should be overridden
   * @throws IOException If CobiGen is not able to extract the jar file to the destination folder
   */
  public void adaptMonolithicTemplates(boolean forceOverride) throws IOException;

  /**
   * Adapt an old monolithic template jar structure to a given destination folder.
   *
   * @param destinationPath The folder where the jars should be extracted to
   * @param forceOverride Indicator whether an already adapted template set should be overridden
   * @throws IOException If CobiGen is not able to extract the jar file to the destination folder
   */
  public void adaptMonolithicTemplates(Path destinationPath, boolean forceOverride) throws IOException;

  /**
   * Get a list of available template set jars to adapt.
   *
   * @return A {@link List} of {@link Path} with all template set jar files found.
   */
  public List<Path> getTemplateSetJars();

  /**
   * Checks if the template configuration consists of an old monolithic template set or independent template sets.
   *
   * @return Returns {@code true} if the template structure consists of an old monolithic template set. Otherwise false.
   */
  public boolean isMonolithicTemplatesConfiguration();

  /**
   * Get the parent location of the templates.
   *
   * @return The {@link Path} of the templates location.
   */
  public Path getTemplatesLocation();

  /**
   * Checks if a given template set is already adapted
   *
   * @param templateSetJar The {@link Path} to the template set to check.
   * @return Returns {@code true} if the template set is already adapted. Otherwise false.
   */
  public boolean isTemplateSetAlreadyAdapted(Path templateSetJar);

  /**
   * Upgrade an adapted monolithic template structure to the new template structure consisting of template sets.
   *
   * @param templatesProject path to the templates which should be upgraded.
   * @return the new path to the new template-sets
   */
  Path upgradeMonolithicTemplates(Path templatesProject);
}