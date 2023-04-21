package com.devonfw.cobigen.javaplugin.merger.libextension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.builder.impl.ModelBuilder;
import com.thoughtworks.qdox.library.AbstractClassLibrary;
import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.library.SourceLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.impl.DefaultDocletTagFactory;
import com.thoughtworks.qdox.parser.JavaLexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;

/**
 * {@link SourceLibrary} parsing inputs into the {@link JavaSource} representation by using a
 * {@link ModifyableModelBuilder} in order to get the internal representation of {@link JavaClass JavaClasses} as
 * {@link ModifyableJavaClass ModifyableJavaClasses}
 */
public class ModifyableSourceLibrary extends SourceLibrary {

  /**
   * Serial version UID
   */
  private static final long serialVersionUID = 6865328738451045200L;

  /**
   * Assigning logger to ModifyableSourceLibrary
   */
  private static final Logger LOG = LoggerFactory.getLogger(ModifyableSourceLibrary.class);

  /**
   * Creates a new {@link ModifyableJavaClass}
   *
   * @param parent {@link ClassLibrary}
   */
  public ModifyableSourceLibrary(AbstractClassLibrary parent) {

    super(parent);
  }

  /**
   * Adds a source to the source repository and returns it representative {@link JavaSource}
   *
   * @param source to be parsed
   * @return the created {@link JavaSource}
   * @throws ParseException if the given source could not be parsed
   * @throws IOException if the given source was a file which could not be accessed
   */
  public JavaSource addSource(Object source) throws ParseException, IOException {

    JavaSource resultSource = null;
    Builder modelBuilder = getModelBuilder();
    resultSource = parseSource(modelBuilder, source);
    registerJavaSource(resultSource);
    return resultSource;
  }

  /**
   * Parses the given source using the given {@link ModelBuilder}
   *
   * @param modelBuilder {@link ModelBuilder} to be used to build the {@link JavaSource}
   * @param source to be parsed
   * @return the created {@link JavaSource}
   * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some
   *         other reason cannot be opened for reading.
   * @throws MalformedURLException If a protocol handler for the URL could not be found, or if some other error occurred
   *         while constructing the URL
   * @throws UnsupportedEncodingException If the named charset is not supported
   * @throws IOException if any exception occurred while accessing the source
   */
  private JavaSource parseSource(Builder modelBuilder, Object source)
      throws FileNotFoundException, MalformedURLException, UnsupportedEncodingException, IOException {

    JavaSource resultSource = null;
    if (source instanceof File) {
      resultSource = parse(new FileInputStream((File) source), ((File) source).toURI().toURL(), modelBuilder);
    } else if (source instanceof Reader) {
      resultSource = parse((Reader) source, null, modelBuilder);
    } else if (source instanceof InputStream) {
      resultSource = parse((InputStream) source, null, modelBuilder);
    } else if (source instanceof URL) {
      resultSource = parse(new InputStreamReader(((URL) source).openStream(), getEncoding()), (URL) source,
          modelBuilder);
    } else {
      // throw runtimeexception?
    }
    return resultSource;
  }

  /**
   * Parses the given stream contents and closes the stream if any exception occurs
   *
   * @param stream to be parsed
   * @param url of the source
   * @param modelBuilder used to construct the model
   * @return the created {@link JavaSource}
   * @throws ParseException if any exception occurs while parsing
   */
  protected JavaSource parse(InputStream stream, URL url, Builder modelBuilder) throws ParseException {

    try {
      return parse(new JFlexLexer(stream), url, modelBuilder);
    } finally {
      try {
        stream.close();
      } catch (IOException e) {
        LOG.error("{}", "ParseException", e);
      }
    }
  }

  /**
   * Parses the given reader contents and closes the reader if any exception occurs
   *
   * @param reader to be parsed
   * @param url of the source
   * @param modelBuilder used to construct the model
   * @return the created {@link JavaSource}
   * @throws ParseException if any exception occurs while parsing
   */
  protected JavaSource parse(Reader reader, URL url, Builder modelBuilder) throws ParseException {

    try {
      return parse(new JFlexLexer(reader), url, modelBuilder);
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        LOG.error("{}", "ParseException", e);
      }
    }
  }

  /**
   * Parses the given reader contents and closes the reader if any exception occurs
   *
   * @param lexer to be parsed
   * @param url of the source
   * @param builder used to construct the model
   * @return the created {@link JavaSource}
   * @throws ParseException if any exception occurs while parsing
   */
  private JavaSource parse(JavaLexer lexer, URL url, Builder builder) throws ParseException {

    JavaSource result = null;
    builder.setUrl(url);
    Parser parser = new Parser(lexer, builder);
    parser.setDebugLexer(isDebugLexer());
    parser.setDebugParser(isDebugParser());
    try {
      if (parser.parse()) {
        result = builder.getSource();
      }
    } catch (ParseException pe) {
      if (url != null) {
        pe.setSourceInfo(url.toExternalForm());
      }
      if (getErrorHandler() != null) {
        getErrorHandler().handle(pe);
      } else {
        throw pe;
      }
    }
    return result;
  }

  @Override
  protected Builder getModelBuilder() {

    ModifyableModelBuilder modelBuilder = new ModifyableModelBuilder(this, new DefaultDocletTagFactory());
    modelBuilder.setModelWriterFactory(new CustomModelWriterFactory());
    return modelBuilder;
  }
}
