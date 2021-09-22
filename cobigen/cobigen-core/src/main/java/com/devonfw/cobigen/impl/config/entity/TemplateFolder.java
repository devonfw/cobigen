package com.devonfw.cobigen.impl.config.entity;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.impl.config.reader.CobiGenPropertiesReader;

/**
 * Virtual file system for generation target to evaluate path variables and cobigen specific symlinks.
 */
public class TemplateFolder extends TemplatePath {

  /** Children mapped from name to {@link TemplatePath} instance - see {@link #getChild(String)}. */
  private final Map<String, TemplatePath> children;

  /** @see #getVariables() */
  private final Variables variables;

  /** @see #getChildFiles() */
  private List<TemplateFile> childFiles;

  /** @see #getChildFolders() */
  private List<TemplateFolder> childFolders;

  /** {@code true} if children have been {@link #scanChildren() scanned}, {@code false} otherwise. */
  private boolean childrenScanned;

  /**
   * Constructor for root folder.
   *
   * @param templatePath the {@link #getPath() template path}.
   */
  private TemplateFolder(Path templatePath) {

    super(templatePath, null);
    this.children = new HashMap<>();
    this.variables = new Variables(CobiGenPropertiesReader.load(templatePath));
  }

  /**
   * Constructor for child folder.
   *
   * @param parent the {@link #getParent() parent folder}.
   * @param name the name of this child folder to create.
   */
  private TemplateFolder(TemplateFolder parent, String name) {

    this(parent.getPath().resolve(name), parent);
  }

  /**
   * Constructor for child folder.
   *
   * @param templatePath the {@link #getPath() template path}.
   * @param parent the {@link #getParent() parent folder}.
   */
  private TemplateFolder(Path templatePath, TemplateFolder parent) {

    super(templatePath, parent);
    this.children = new HashMap<>();
    this.variables = parent.variables.forChildFolder(templatePath);
  }

  /**
   * @return the {@link Map} with the variables for this {@link TemplateFolder}. Will be inherited from
   *         {@link #getParent() parent} and merged and overridden with potential properties defined in this folder.
   * @see CobiGenPropertiesReader
   */
  public Variables getVariables() {

    return this.variables;
  }

  /**
   * @param name the filename of the child in this folder.
   * @return the requested child or {@code null} if no such file exists.
   */
  public TemplatePath getChild(String name) {

    scanChildren();
    return this.children.get(name);
  }

  /**
   * @return the {@link Collection} with all {@link #getChild(String) child} {@link TemplatePath#isFile() files}.
   */
  public Collection<TemplatePath> getChildren() {

    scanChildren();
    return this.children.values();
  }

  /**
   * @return the {@link Collection} with all {@link #getChild(String) child} {@link TemplatePath#isFile() files}.
   */
  public Collection<TemplateFile> getChildFiles() {

    scanChildren();
    return this.childFiles;
  }

  /**
   * @return the {@link Collection} with all {@link #getChild(String) child} {@link TemplatePath#isFolder() folders}.
   */
  public Collection<TemplateFolder> getChildFolders() {

    scanChildren();
    return this.childFolders;
  }

  /**
   * Scans and creates all children.
   */
  private void scanChildren() {

    if (this.childrenScanned) {
      return;
    }
    Path templatePath = getPath();
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(templatePath)) {
      for (Path childName : directoryStream) {
        String filename = childName.getFileName().toString();
        if (filename.endsWith("/")) {
          filename = filename.substring(0, filename.length() - 1);
        }
        if (!ConfigurationConstants.COBIGEN_PROPERTIES.equals(filename) && !this.children.containsKey(filename)) {
          TemplatePath child = createChild(childName);
          this.children.put(filename, child);
        }
      }
      if (this.childFiles == null) {
        this.childFiles = Collections.emptyList();
      } else {
        this.childFiles = Collections.unmodifiableList(this.childFiles);
      }
      if (this.childFolders == null) {
        this.childFolders = Collections.emptyList();
      } else {
        this.childFolders = Collections.unmodifiableList(this.childFolders);
      }
      this.childrenScanned = true;
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Failed to list directory of " + templatePath, e);
    }
  }

  /**
   * @param childPath the {@link Path} pointing to the child.
   * @return the new {@link TemplatePath} representing the child.
   */
  private TemplatePath createChild(Path childPath) {

    if (Files.isDirectory(childPath)) {
      TemplateFolder templateFolder = new TemplateFolder(childPath, this);
      if (this.childFolders == null) {
        this.childFolders = new ArrayList<>();
      }
      this.childFolders.add(templateFolder);
      return templateFolder;
    } else if (Files.exists(childPath)) {
      TemplateFile templateFile = new TemplateFile(childPath, this);
      if (this.childFiles == null) {
        this.childFiles = new ArrayList<>();
      }
      this.childFiles.add(templateFile);
      return templateFile;
    } else {
      return null;
    }
  }

  /**
   * Navigates to the given sub path. Slash is considered as path delimiter.
   *
   * @param relativePath relative sub path to navigate to.
   * @return the {@link TemplateFolder} instance representing the sub path.
   */
  public TemplatePath navigate(String relativePath) {

    TemplateFolder folder = this;
    Iterator<Path> it = Paths.get(relativePath).iterator();
    while (it.hasNext()) {
      String filename = it.next().toString();
      if (filename.equals("..")) {
        TemplateFolder parent = folder.getParent();
        if (parent != null) {
          folder = parent;
        }
      } else if (!filename.equals(".")) {
        TemplatePath child = folder.getChild(filename);
        if (it.hasNext()) {
          if ((child == null) || child.isFile()) {
            return null;
          }
          folder = (TemplateFolder) child;
        } else {
          return child;
        }
      }
    }
    return folder;
  }

  /**
   * @param rootPath the root {@link Path} containing the templates (typically where {@code templates.xml} is located).
   * @return a new {@link TemplateFolder} instance for the given {@link Path}.
   */
  public static TemplateFolder create(Path rootPath) {

    if (!Files.isDirectory(rootPath)) {
      throw new CobiGenRuntimeException("Directory " + rootPath + " does not exist!");
    }
    return new TemplateFolder(rootPath);
  }

  @Override
  public boolean isFolder() {

    return true;
  }

}
