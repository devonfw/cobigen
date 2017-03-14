package com.capgemini.cobigen.impl.config.vfs;

import java.nio.file.Path;
import java.util.Map;

/**
 * Virtual file system for generation target to evaluate path variables and cobigen specific symlinks.
 */
public class VfsFolder {

    /** Parent folder of the virtual file system. */
    private VfsFolder parent;

    /** Children mapped from name to vfs instance */
    private Map<String, VfsFolder> children;

    /** Values for variable substitution. */
    private Map<String, String> variables;

    /**
     * Returns the resolved absolute {@link Path} for this {@link VfsFolder} instance.
     * @return the resolved absolute {@link Path}
     */
    public Path getResolvedAbsolutePath() {
        return null;
    }

    /**
     * Returns the child with the given name or creates a new one if not already existing.
     * @param name
     *            of the child
     * @return the child with the given name.
     */
    public VfsFolder getChild(String name) {
        VfsFolder child = children.get(name);
        if (child == null) {
            child = new VfsFolder();
            children.put(name, child);
        }
        return child;
    }

    /**
     * Navigates to the given sub path. Slash is considered as path delimiter.
     * @param subpath
     *            relative sub path to navigate to.
     * @return the {@link VfsFolder} instance representing the sub path.
     */
    public VfsFolder navigate(String subpath) {
        return null;
    }
}
