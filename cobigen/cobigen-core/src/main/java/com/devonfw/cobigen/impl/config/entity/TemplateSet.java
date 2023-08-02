package com.devonfw.cobigen.impl.config.entity;

import java.nio.file.Path;

public class TemplateSet {

  private final String name;

  private Path compressedPath;

  private Path extractedPath;

  /**
   * Creates a new template set instance understanding the name as unique identifier within one configuration.
   *
   * @param name the unique identifier of the template set
   * @param compressedPath the packed path pointing to the template set,i.e., the jar path
   */
  public TemplateSet(String name, Path compressedPath) {
    this.name = name;
    this.compressedPath = compressedPath;
  }

  /**
   * Creates a new template set instance for an extracted folder on the file system. The folder name will be
   * interpreted as the template set's name
   * @param extractedPath the directory of the template set
   */
  public TemplateSet(Path extractedPath) {
    this.name = extractedPath.getFileName().toString();
    this.extractedPath = extractedPath;
  }

  public String getName() {
    return name;
  }

  public Path getPath() {
    return extractedPath != null ? extractedPath : compressedPath;
  }

  public Path getCompressedPath() { return compressedPath; }

  public Path getExtractedPath() {
    return extractedPath;
  }

  public void setExtractedPath(Path extractedPath) {
    this.extractedPath = extractedPath;
  }

  public boolean isAdapted() {
    return extractedPath != null;
  }
}
