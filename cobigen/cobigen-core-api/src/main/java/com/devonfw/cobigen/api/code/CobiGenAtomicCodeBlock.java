package com.devonfw.cobigen.api.code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for an atomic {@link CobiGenCodeBlock}. It will have source-code but can not have children.
 */
public class CobiGenAtomicCodeBlock extends AbstractCobiGenCodeBlock {

  private final List<String> lines;

  /**
   * The constructor.
   *
   * @param name the {@link #getName() name}.
   */
  public CobiGenAtomicCodeBlock(String name) {

    super(name);
    this.lines = new ArrayList<>();
  }

  @Override
  public final AbstractCobiGenCodeBlock getChild() {

    return null;
  }

  @Override
  public boolean isEmpty() {

    return getLineCount() == 0;
  }

  /**
   * @return the number of lines in this section.
   */
  public int getLineCount() {

    return this.lines.size();
  }

  @Override
  public CobiGenAtomicCodeBlock addAtomicChild(String blockName) {

    return this;
  }

  @Override
  public CobiGenAtomicCodeBlock addLine(boolean raw, String line) {

    assert (line != null);
    if (!raw && (this.indent != null)) {
      line = this.indent + line;
    }
    this.lines.add(line);
    return this;
  }

  @Override
  public CobiGenAtomicCodeBlock addLines(boolean raw, String... codeLines) {

    if (codeLines != null) {
      for (String line : codeLines) {
        addLine(raw, line);
      }
    }
    return this;
  }

  @Override
  public void clear() {

    this.lines.clear();
  }

  @Override
  protected void writeLines(Appendable writer) throws IOException {

    for (String line : this.lines) {
      writeLine(writer, line);
    }
  }

  /**
   * @param writer the {@link Appendable} to write to.
   * @param line the line to {@link Appendable#append(CharSequence) write}.
   * @throws IOException if thrown by {@link Appendable}.
   */
  protected void writeLine(Appendable writer, String line) throws IOException {

    writer.append(line);
    writer.append('\n');
  }

}
