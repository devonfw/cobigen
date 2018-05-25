package com.devonfw.cobigen.impl.config.entity;

import java.nio.file.Path;

/**
 * Interface as abstraction for a {@link Path} in a {@link TemplateFolder}.
 */
public abstract class TemplatePath {

    /** Parent folder of the virtual file system. */
    private final TemplateFolder parent;

    /** @see #getPath() */
    private final Path path;

    /**
     * @param templatePath
     *            the {@link #getPath() template path}.
     */
    protected TemplatePath(Path templatePath) {
        this(templatePath, null);
    }

    /**
     * @param templatePath
     *            the {@link #getPath() template path}.
     * @param parent
     *            the {@link #getParent() parent folder}.
     */
    protected TemplatePath(Path templatePath, TemplateFolder parent) {
        super();
        this.parent = parent;
        path = templatePath;
    }

    /**
     * @return the physical {@link Path} pointing to the actual {@link TemplateFolder} or
     *         {@link TemplateFile}.
     */
    public final Path getPath() {
        return path;
    }

    /**
     * @return the relative path to the {@link #getRoot()} path.
     */
    public final Path getRootRelativePath() {
        return getRoot().getPath().relativize(path);
    }

    /**
     * @return the parent {@link TemplateFolder} or <code>null</code> if this is the root (top-level folder of
     *         the generation input source).
     */
    public final TemplateFolder getParent() {
        return parent;
    }

    /**
     * @return the root {@link TemplateFolder}.
     */
    public final TemplateFolder getRoot() {

        TemplatePath folder = this;
        while (folder.parent != null) {
            folder = folder.parent;
        }
        return (TemplateFolder) folder;
    }

    /**
     * @return {@code true} if this is a {@link TemplateFolder}, {@code false} in case of a
     *         {@link TemplateFile}.
     */
    public abstract boolean isFolder();

    /**
     * @return {@code true} if this is a {@link TemplateFile}, {@code false} in case of a
     *         {@link TemplateFolder}.
     */
    public boolean isFile() {

        return !isFolder();
    }

    /**
     * @return the filename of this {@link TemplatePath}.
     */
    public String getFileName() {

        return path.getFileName().toString();
    }

    @Override
    public String toString() {
        return getRootRelativePath().toString().replace('\\', '/');
    }

}
