package com.capgemini.cobigen.impl.config.vfs;

import java.nio.file.Path;
import java.util.Map;

/**
 * Virtual file system for generation target to evaluate path variables and cobigen specific symlinks.
 */
public class TargetFolder {

    private TargetFolder parent;

    /** Children mapped from name to vfs instance */
    private Map<String, TargetFolder> children;

    private Map<String, String> variables;

    public Path getResolvedAbsolutePath() {
        return null;
    }

    public TargetFolder getChild(String folderName) {
        TargetFolder child = children.get(folderName);
        if (child == null) {
            child = new TargetFolder();
            children.put(folderName, child);
        }
        return child;
    }

    public TargetFolder navigate(String subpath) {
        return null;
    }
}
