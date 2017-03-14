package com.capgemini.cobigen.impl.config.entity;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Virtual file system for generation target to evaluate path variables and cobigen specific symlinks.
 */
public class TemplateFolder {

    private static final String COBIGEN_PROPERTIES = "cobigen.properties";

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    /** Parent folder of the virtual file system. */
    private final TemplateFolder parent;

    /** @see #getTemplatePath() */
    private final Path templatePath;

    /** @see #getTargetPath() */
    private final Path targetPath;

    /** Children mapped from name to {@link TemplateFolder} instance - see {@link #getChild(String)}. */
    private final Map<String, TemplateFolder> children;

    /** @see #getVariables() */
    private final Properties variables;

    /**
     * Constructor for root folder.
     *
     * @param path
     *            see {@link #getResolvedAbsolutePath()}.
     */
    private TemplateFolder(Path path) {
        super();
        parent = null;
        templatePath = path;
        targetPath = templatePath; // TODO
        children = new HashMap<>();
        variables = readLocalProperties(path, new Properties());
    }

    /**
     * Constructor for child folder.
     *
     * @param parent
     *            the {@link #getParent() parent folder}.
     * @param name
     *            the name of this child folder to create.
     */
    private TemplateFolder(TemplateFolder parent, String name) {
        super();
        this.parent = parent;
        templatePath = this.parent.templatePath.resolve(name);
        targetPath = templatePath; // TODO
        children = new HashMap<>();
        variables = readLocalProperties(templatePath, this.parent.variables);
    }

    /**
     * @return the parent {@link TemplateFolder} or <code>null</code> if this is the root (top-level folder of
     *         the generation input source).
     */
    public TemplateFolder getParent() {
        return parent;
    }

    /**
     * Returns the resolved absolute {@link Path} for this {@link TemplateFolder} instance.
     * @return the resolved absolute {@link Path}
     */
    public Path getTemplatePath() {
        return templatePath;
    }

    /**
     *
     * ../api/src/main/java/.../${component}/../${pojo.name}Cto.java
     *
     * Returns the resolved absolute {@link Path} for this {@link TemplateFolder} instance.
     * @return the resolved absolute {@link Path}
     */
    public Path getTargetPath() {
        return targetPath;
    }

    /**
     * @return the {@link Map} with the variables given when the VFS was {@link VfsFactory#create(Path, Map)
     *         created} merged with potential variables from {@link Properties property-files}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, String> getVariables() {
        return (Map) variables;
    }

    /**
     * Returns the child with the given name or creates a new one if not already existing.
     * @param name
     *            of the child
     * @return the child with the given name.
     */
    public TemplateFolder getChild(String name) {
        TemplateFolder child = children.get(name);
        if (child == null) {
            child = new TemplateFolder(this, name);
            children.put(name, child);
        }
        return child;
    }

    private static final Properties readLocalProperties(Path folder, Properties parent) {

        Path propertiesPath = folder.resolve(COBIGEN_PROPERTIES);
        if (!Files.exists(propertiesPath)) {
            return parent;
        }

        Properties properties = new Properties(parent);
        try (Reader reader = Files.newBufferedReader(propertiesPath, UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new CobiGenRuntimeException("Failed to read " + COBIGEN_PROPERTIES + " from " + folder, e);
        }
        return properties;
    }

    /**
     * Navigates to the given sub path. Slash is considered as path delimiter.
     * @param subpath
     *            relative sub path to navigate to.
     * @return the {@link TemplateFolder} instance representing the sub path.
     */
    public TemplateFolder navigate(String subpath) {
        return null;
    }

    public static TemplateFolder create(Path rootPath) {

        return new TemplateFolder(rootPath);
    }

}
