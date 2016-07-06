package com.capgemini.cobigen.javaplugin.merger.libextension;

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
import com.thoughtworks.qdox.parser.ParseException;

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

    /**
     * {@inheritDoc}
     * @author mbrunnli (04.04.2013)
     */
    @Override
    public JavaSource addSource(File file) throws IOException, ParseException {
        return ((ModifyableSourceLibrary) getSourceLibrary()).addSource(file);
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 26, 2015)
     */
    @Override
    public JavaSource addSource(InputStream stream) throws IOException, ParseException {
        return ((ModifyableSourceLibrary) getSourceLibrary()).addSource(stream);
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 26, 2015)
     */
    @Override
    public JavaSource addSource(Reader reader) throws ParseException {
        return ((ModifyableSourceLibrary) getSourceLibrary()).addSource(reader);
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 26, 2015)
     */
    @Override
    public JavaSource addSource(URL url) throws IOException, ParseException {
        return ((ModifyableSourceLibrary) getSourceLibrary()).addSource(url);
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (04.04.2013)
     */
    @Override
    protected SourceLibrary newSourceLibrary(AbstractClassLibrary parentLibrary) {
        return new ModifyableSourceLibrary(parentLibrary);
    }

}
