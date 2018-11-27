package com.devonfw.cobigen.javaplugin.merger.libextension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import com.thoughtworks.qdox.library.AbstractClassLibrary;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.library.OrderedClassLibraryBuilder;
import com.thoughtworks.qdox.library.SourceLibrary;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * {@link ClassLibraryBuilder} forwarding {@link #addSource(File)} to
 * {@link ModifyableSourceLibrary#addSource(File)}
 * @author mbrunnli (04.04.2013)
 */
public class ModifyableClassLibraryBuilder extends OrderedClassLibraryBuilder {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 6191405303611576834L;

    @Override
    public JavaSource addSource(File file) throws IOException {
        return ((ModifyableSourceLibrary) getSourceLibrary()).addSource(file);
    }

    @Override
    public JavaSource addSource(InputStream stream) throws IOException {
        return ((ModifyableSourceLibrary) getSourceLibrary()).addSource(stream);
    }

    @Override
    public JavaSource addSource(Reader reader) {
        return ((ModifyableSourceLibrary) getSourceLibrary()).addSource(reader);
    }

    @Override
    public JavaSource addSource(URL url) throws IOException {
        return ((ModifyableSourceLibrary) getSourceLibrary()).addSource(url);
    }

    @Override
    protected SourceLibrary newSourceLibrary(AbstractClassLibrary parentLibrary) {
        return new ModifyableSourceLibrary(parentLibrary);
    }

}
