package com.devonfw.cobigen.api.code;

import java.io.IOException;
import java.util.List;

/**
 * Abstract base implementation of {@link CobiGenCodeBlock}. Typical usage shall work on the interface. However, all
 * implementations need to extend this {@link AbstractCobiGenCodeBlock} class. Thus, it is legal to downcast if required
 * to mutate the block structure.
 */
public abstract class AbstractCobiGenCodeBlock implements CobiGenCodeBlock {

  private final String name;

  private AbstractCobiGenCodeBlock next;

  /** The default indent for this block or {@code null} if lines shall be added raw. */
  protected String indent;

  /**
   * The constructor.
   *
   * @param name the {@link #getName() name}.
   */
  public AbstractCobiGenCodeBlock(String name) {

    super();
    this.name = name;
  }

  @Override
  public String getName() {

    return this.name;
  }

  /**
   * @return the indent for all lines in this block (that are added dynamically).
   */
  public String getIndent() {

    return this.indent;
  }

  /**
   * @param indent the new value of {@link #getIndent()}. Can only be changed once.
   */
  public void setIndent(String indent) {

    if ((this.indent != null) && !this.indent.equals(indent)) {
      throw new IllegalStateException("Indent cannot be changed!");
    }
    this.indent = indent;
  }

  @Override
  public AbstractCobiGenCodeBlock getNext() {

    return this.next;
  }

  @Override
  public AbstractCobiGenCodeBlock getNext(String blockName) {

    AbstractCobiGenCodeBlock block = this;
    while (block != null) {
      if (block.getName().equals(blockName)) {
        break;
      }
      block = block.next;
    }
    if (block == null) {
      // build-in fallbacks
      if (NAME_SETTERS.equals(blockName)) {
        return getNext(NAME_GETTERS);
      }
    }
    return block;
  }

  /**
   * @param blockName the {@link #getName() name} of the requested {@link #getNext() sibling}.
   * @param composite - {@code true} to create a composite block, {@code false} otherwise to create an atomic block.
   * @return the first {@link CobiGenCodeBlock} with the given {@link #getName() name} from this
   *         {@link CobiGenCodeBlock} recursively along its {@link #getNext() siblings}. If not found, it will be
   *         created as {@link #getLast() last} {@link #getNext() sibling}.
   * @see CobiGenCompositeCodeBlock#getOrCreateChild(String, boolean)
   */
  public AbstractCobiGenCodeBlock getOrCreateNext(String blockName, boolean composite) {

    AbstractCobiGenCodeBlock block = this;
    while (true) {
      if (block.getName().equals(blockName)) {
        return block;
      }
      if (block.next == null) {
        if (composite) {
          block.next = new CobiGenCompositeCodeBlock(blockName);
        } else {
          block.next = new CobiGenAtomicCodeBlock(blockName);
        }
        return block.next;
      }
      block = block.next;
    }
  }

  @Override
  public CobiGenAtomicCodeBlock addLine(String line) {

    return addLine(false, line);
  }

  @Override
  public CobiGenAtomicCodeBlock addLines(String... lines) {

    return addLines(false, lines);
  }

  /**
   * @param raw - {@code true} to add the given {@code line} unmodified, {@code false} otherwise (to auto-indent).
   * @param line the source-code line to add to the end of this block.
   * @return the {@link CobiGenAtomicCodeBlock} where the given {@code line} has been added.
   */
  public abstract CobiGenAtomicCodeBlock addLine(boolean raw, String line);

  /**
   * @param raw - {@code true} to add the given {@code lines} unmodified, {@code false} otherwise (to auto-indent).
   * @param lines the source-code lines to add to the end of this block.
   * @return the {@link CobiGenAtomicCodeBlock} where the given {@code lines} have been added.
   */
  public abstract CobiGenAtomicCodeBlock addLines(boolean raw, String... lines);

  /**
   * @param raw - {@code true} to add the given {@code lines} unmodified, {@code false} otherwise (to auto-indent).
   * @param lines the source-code lines to add to the end of this block.
   * @return the {@link CobiGenAtomicCodeBlock} where the given {@code lines} have been added.
   */
  public CobiGenAtomicCodeBlock addLines(boolean raw, List<String> lines) {

    String[] linesArray = null;
    if ((lines != null) && !lines.isEmpty()) {
      linesArray = lines.toArray(new String[lines.size()]);
    }
    return addLines(raw, linesArray);
  }

  @Override
  public abstract AbstractCobiGenCodeBlock getChild();

  @Override
  public AbstractCobiGenCodeBlock getChild(String blockName) {

    AbstractCobiGenCodeBlock child = getChild();
    if (child == null) {
      return null;
    }
    return child.getNext(blockName);
  }

  @Override
  public AbstractCobiGenCodeBlock getLast() {

    AbstractCobiGenCodeBlock block = this;
    while (block.next != null) {
      block = block.next;
    }
    return block;
  }

  /**
   * Appends as {@link #getLast() last} {@link #getNext() sibling} so to the very end of the code. In most cases you
   * want to use {@link CobiGenCompositeCodeBlock#appendChild(AbstractCobiGenCodeBlock)} instead.<br>
   * <b>ATTENTION:</b> It is highly recommended to only have {@link #getNext() siblings} of the same kind. So
   * {@link CobiGenCompositeCodeBlock}s should only have the same type of {@link #getNext() siblings} as the same
   * applies for {@link CobiGenAtomicCodeBlock}s.
   *
   * @param last the {@link AbstractCobiGenCodeBlock} to append as the {@link #getLast() last} {@link #getNext()
   *        sibling}.
   */
  public void appendLast(AbstractCobiGenCodeBlock last) {

    getLast().next = last;
  }

  /**
   * @param blockName the {@link #getName() name} of the next block to insert.
   * @return the new {@link CobiGenCompositeCodeBlock} that has been inserted.
   */
  public CobiGenCompositeCodeBlock insert(String blockName) {

    CobiGenCompositeCodeBlock composite = new CobiGenCompositeCodeBlock(blockName);
    AbstractCobiGenCodeBlock block = composite;
    block.next = this.next;
    this.next = composite;
    return composite;
  }

  /**
   * @return {@code true} if entirely empty meaning it has no {@link #getChild() child}ren nor
   *         {@link CobiGenAtomicCodeBlock#getLineCount() lines}.
   */
  public abstract boolean isEmpty();

  /**
   * Clears this block so it is empty. It will remove the {@link #getChild() children} and all code-lines.
   */
  public abstract void clear();

  /**
   * @param writer the {@link Appendable} to write to.
   * @throws IOException if thrown by {@link Appendable}.
   */
  public void write(Appendable writer) throws IOException {

    write(writer, true);
  }

  /**
   * @param writer the {@link Appendable} to write to.
   * @param withSiblings - {@code true} to include all {@link #getNext() next} siblings, {@code false} to only write
   *        this block itself.
   * @throws IOException if thrown by {@link Appendable}.
   */
  public void write(Appendable writer, boolean withSiblings) throws IOException {

    writeLines(writer);
    AbstractCobiGenCodeBlock child = getChild();
    if (child != null) {
      child.write(writer, true);
    }
    if (withSiblings && (this.next != null)) {
      this.next.write(writer, true);
    }
  }

  /**
   * Writes the direct source-code lines of this block.
   *
   * @param writer the {@link Appendable} to write to.
   * @throws IOException if thrown by {@link Appendable}.
   */
  protected void writeLines(Appendable writer) throws IOException {

  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    toString(sb, "|-");
    return sb.toString();
  }

  private void toString(StringBuilder sb, String prefix) {

    try {
      sb.append(prefix);
      sb.append(this.name);
      sb.append(" ");
      sb.append(getClass().getSimpleName());
      sb.append('\n');
      writeLines(sb);
      AbstractCobiGenCodeBlock child = getChild();
      if (child != null) {
        child.toString(sb, prefix + "-");
      }
      if (this.next != null) {
        this.next.toString(sb, prefix);
      }
    } catch (IOException e) {
      sb.append(e.toString());
    }
  }

}
