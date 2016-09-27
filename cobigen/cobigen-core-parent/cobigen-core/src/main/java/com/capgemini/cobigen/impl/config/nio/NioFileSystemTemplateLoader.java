package com.capgemini.cobigen.impl.config.nio;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.TemplateLoader;

/**
 * Generic {@link TemplateLoader} for {@link FileSystem}
 * @author mbrunnli (13.02.2015)
 */
public class NioFileSystemTemplateLoader implements TemplateLoader {

    /**
     * SLF4J Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NioFileSystemTemplateLoader.class);

    /**
     * Root path to resolve templates from
     */
    private Path templatesRoot;

    /**
     * Creates a new file system template resolver, which resolves the templates from any {@link FileSystem}.
     * @param templatesRoot
     *            root path to resolve templates from
     * @author mbrunnli (13.02.2015)
     */
    public NioFileSystemTemplateLoader(Path templatesRoot) {
        this.templatesRoot = templatesRoot;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        return templatesRoot.resolve(name);
    }

    @Override
    public long getLastModified(Object templateSource) {
        BasicFileAttributes attrs;
        Path templatePath = (Path) templateSource;
        try {
            attrs = Files.readAttributes(templatePath, BasicFileAttributes.class);
            return attrs.lastModifiedTime().toMillis();
        } catch (IOException e) {
            LOG.warn("An error occured while resolving the last modified file attribute of path '{}'.",
                templatePath.toAbsolutePath().toString(), e);
        }
        return 0;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return new InputStreamReader(Files.newInputStream((Path) templateSource), encoding);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        // do nothing (referring to FreeMarkers FileTemlpateLoader.class implementation)
    }

    /**
     * Sets the field 'templateRoot'.
     * @param templateRoot
     *            new value of templateRoot
     * @author mbrunnli (13.02.2015)
     */
    public void setTemplateRoot(Path templateRoot) {
        templatesRoot = templateRoot;
    }

}
