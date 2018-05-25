package com.devonfw.cobigen.impl.config.entity;

import java.nio.file.Path;

/**
 * Virtual file system for generation target to evaluate path variables and cobigen specific symlinks.
 */
public class TemplateFile extends TemplatePath {

    /**
     * Constructor for root folder.
     *
     * @param templatePath
     *            the {@link #getPath() template path}.
     * @param parent
     *            the {@link #getParent() parent folder}.
     */
    TemplateFile(Path templatePath, TemplateFolder parent) {
        super(templatePath, parent);
    }

    @Override
    public boolean isFolder() {
        return false;
    }

}
