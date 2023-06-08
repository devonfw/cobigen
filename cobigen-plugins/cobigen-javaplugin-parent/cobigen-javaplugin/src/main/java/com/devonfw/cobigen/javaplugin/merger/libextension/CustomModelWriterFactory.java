package com.devonfw.cobigen.javaplugin.merger.libextension;

import com.thoughtworks.qdox.writer.ModelWriter;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

/**
 * The {@link ModelWriterFactory} returning new instances of {@link CustomModelWriter}
 */
public class CustomModelWriterFactory implements ModelWriterFactory {

  @Override
  public ModelWriter newInstance() {

    return new CustomModelWriter();
  }

}
