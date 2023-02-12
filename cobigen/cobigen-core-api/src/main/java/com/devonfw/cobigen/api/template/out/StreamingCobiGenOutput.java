package com.devonfw.cobigen.api.template.out;

/**
 * Dummy implementation of {@link AbstractCobiGenOutput} to indicate that {@link #addLine(String) lines} shall not be
 * buffered but directly written to the output.
 */
public class StreamingCobiGenOutput extends AbstractCobiGenOutput {

  /**
   * The constructor.
   *
   * @param filename the {@link #getFilename() filename}.
   */
  public StreamingCobiGenOutput(String filename) {

    super(filename);
  }

  @Override
  public ImportStatement getImport(String name) {

    return null;
  }

  @Override
  public boolean addImport(String qualifiedName) {

    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addImport(QualifiedName qualifiedName) {

    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addImport(ImportStatement importStatement) {

    throw new UnsupportedOperationException();
  }

  @Override
  public void addProperty(String name, String qualifiedTypeName, String description) {

    throw new UnsupportedOperationException();
  }

  @Override
  public void addProperty(String name, QualifiedName type, String description) {

    throw new UnsupportedOperationException();
  }

  @Override
  public void addProperty(String name, QualifiedName type, String description, boolean addImport, boolean addField,
      boolean addGetter, boolean addSetter) {

    throw new UnsupportedOperationException();
  }

  @Override
  public AbstractCobiGenOutput addLine(String line) {

    return this;
  }

}
