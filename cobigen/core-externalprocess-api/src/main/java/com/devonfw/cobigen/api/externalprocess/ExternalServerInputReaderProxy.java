package com.devonfw.cobigen.api.externalprocess;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.externalprocess.to.InputFileTo;

/**
 * Default external server proxy for {@link InputReader}
 */
public abstract class ExternalServerInputReaderProxy implements InputReader {

  /** The external process for the plugin */
  protected ExternalProcess externalProcess;

  /**
   * Create new proxy which automatically communicates with the external process by JSON communication
   *
   * @param externalProcess of the plugin
   */
  public ExternalServerInputReaderProxy(ExternalProcess externalProcess) {

    this.externalProcess = externalProcess;
  }

  @Override
  public boolean isValidInput(Object input) {

    throw new CobiGenRuntimeException("This method should be implemented in Java for performance reasons!");
  }

  @Override
  public Object read(Path path, Charset inputCharset, Object... additionalArguments) throws InputReaderException {

    String fileContents;
    String fileName = path.toString();
    try {
      fileContents = String.join("", Files.readAllLines(path, inputCharset));
    } catch (IOException e) {
      throw new InputReaderException("Could not read input file!" + fileName, e);
    }

    InputFileTo inputFile = new InputFileTo(fileName, fileContents, inputCharset.name());

    return this.externalProcess.postJsonRequest("getInputModel", inputFile);
  }

  @Override
  public boolean isMostLikelyReadable(Path path) {

    throw new CobiGenRuntimeException("This method should be implemented in Java for performance reasons!");
  }

}
