package com.devonfw.cobigen.api.template.out;

import java.io.IOException;
import java.util.Objects;

import com.devonfw.cobigen.api.code.CobiGenCodeBlock;
import com.devonfw.cobigen.api.code.CobiGenCompositeCodeBlock;

/**
 * Abstract base implementation of {@link CobiGenOutput}.
 */
public abstract class AbstractCobiGenOutput implements CobiGenOutput {

  /** @see #getFilename() */
  protected final String filename;

  /** The {@link CobiGenCodeBlock}. */
  protected final CobiGenCompositeCodeBlock code;

  private final AbstractCobiGenOutput parent;

  /**
   * The constructor.
   *
   * @param parent the parent {@link CobiGenOutputCode}.
   */
  public AbstractCobiGenOutput(AbstractCobiGenOutput parent) {

    this(parent.filename, parent);
  }

  /**
   * The constructor.
   *
   * @param filename the {@link #getFilename() filename}.
   */
  public AbstractCobiGenOutput(String filename) {

    this(filename, null);
    Objects.requireNonNull(filename);
  }

  private AbstractCobiGenOutput(String filename, AbstractCobiGenOutput parent) {

    super();
    this.filename = filename;
    this.parent = parent;
    this.code = createCode();
  }

  /**
   * @return the {@link CobiGenCompositeCodeBlock} with the code structure of this output.
   */
  protected CobiGenCompositeCodeBlock createCode() {

    return new CobiGenCompositeCodeBlock(CobiGenCodeBlock.NAME_HEADER);
  }

  @Override
  public String getFilename() {

    return this.filename;
  }

  @Override
  public CobiGenCodeBlock getCode() {

    return this.code;
  }

  /**
   * @return the parent {@link AbstractCobiGenOutput} or {@code null} if this is the root.
   */
  public AbstractCobiGenOutput getParent() {

    return this.parent;
  }

  /**
   * @return the root output node.
   */
  protected AbstractCobiGenOutput getRoot() {

    AbstractCobiGenOutput current = this;
    while (current.parent != null) {
      current = current.parent;
    }
    return current;
  }

  /**
   * @param line the resolved line from the template to add.
   * @return this {@link AbstractCobiGenOutput output} or potentially a child of it (e.g. for nested structures like
   *         inner classes).
   */
  public abstract AbstractCobiGenOutput addLine(String line);

  /**
   * Actually writes the generated output.
   *
   * @param out the {@link Appendable} where to write to.
   */
  public void write(Appendable out) {

    try {
      this.code.write(out);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output file for template " + this.filename, e);
    }
  }

}
