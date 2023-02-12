package com.devonfw.cobigen.api.code;

/**
 * Base class for a composite {@link CobiGenCodeBlock}. It has children but no direct source-code.
 */
public class CobiGenCompositeCodeBlock extends AbstractCobiGenCodeBlock {

  private AbstractCobiGenCodeBlock child;

  /**
   * The constructor.
   *
   * @param name the {@link #getName() name}.
   */
  public CobiGenCompositeCodeBlock(String name) {

    super(name);
  }

  @Override
  public final AbstractCobiGenCodeBlock getChild() {

    return this.child;
  }

  void setChild(AbstractCobiGenCodeBlock child) {

    this.child = child;
  }

  @Override
  public CobiGenAtomicCodeBlock addAtomicChild(String blockName) {

    CobiGenAtomicCodeBlock block = new CobiGenAtomicCodeBlock(blockName);
    appendChild(block);
    return block;
  }

  /**
   * @param blockName the {@link #getName() name} of the new block.
   * @return the new {@link CobiGenCompositeCodeBlock} with the given {@link #getName() name} appended as last child.
   */
  public CobiGenCompositeCodeBlock addCompositeChild(String blockName) {

    CobiGenCompositeCodeBlock block = new CobiGenCompositeCodeBlock(blockName);
    appendChild(block);
    return block;
  }

  private CobiGenAtomicCodeBlock getOrCreateLastAtomicChild() {

    if (this.child == null) {
      return getOrCreateAtomicChild(NAME_CODE);
    }
    AbstractCobiGenCodeBlock lastChild = this.child.getLast();
    if (lastChild instanceof CobiGenAtomicCodeBlock) {
      return (CobiGenAtomicCodeBlock) lastChild;
    } else {
      return ((CobiGenCompositeCodeBlock) lastChild).getOrCreateLastAtomicChild();
    }

  }

  @Override
  public CobiGenAtomicCodeBlock addLine(boolean raw, String codeLine) {

    CobiGenAtomicCodeBlock block = getOrCreateLastAtomicChild();
    block.addLine(raw, codeLine);
    return block;
  }

  @Override
  public CobiGenAtomicCodeBlock addLines(boolean raw, String... codeLines) {

    CobiGenAtomicCodeBlock block = getOrCreateLastAtomicChild();
    block.addLines(raw, codeLines);
    return block;
  }

  /**
   * @param blockName the {@link #getName() name} of the requested {@link #getNext() child}.
   * @param composite - {@code true} to create a composite block, {@code false} otherwise to create an atomic block.
   * @return the first {@link CobiGenCodeBlock} with the given {@link #getName() name} from this
   *         {@link CobiGenCodeBlock} recursively along its {@link #getNext() siblings}. If not found, it will be
   *         created as {@link #getLast() last} {@link #getNext() sibling}.
   * @see #getOrCreateNext(String, boolean)
   */
  public AbstractCobiGenCodeBlock getOrCreateChild(String blockName, boolean composite) {

    if (this.child == null) {
      if (composite) {
        this.child = new CobiGenCompositeCodeBlock(blockName);
      } else {
        this.child = new CobiGenAtomicCodeBlock(blockName);
      }
      return this.child;
    } else {
      return this.child.getOrCreateNext(blockName, composite);
    }
  }

  /**
   * @param blockName the {@link #getName() name} of the requested {@link #getNext() child}.
   * @return the first {@link CobiGenCodeBlock} with the given {@link #getName() name} from this
   *         {@link CobiGenCodeBlock} recursively along its {@link #getNext() siblings}. If not found, it will be
   *         created as {@link #getLast() last} {@link #getNext() sibling}.
   * @see #getOrCreateChild(String, boolean)
   */
  public CobiGenAtomicCodeBlock getOrCreateAtomicChild(String blockName) {

    return (CobiGenAtomicCodeBlock) getOrCreateChild(blockName, false);
  }

  /**
   * @param lastChild the {@link AbstractCobiGenCodeBlock} to append as {@link #getLast() last} {@link #getChild()
   *        child} so to the end of this block.
   * @see #appendLast(AbstractCobiGenCodeBlock)
   */
  public void appendChild(AbstractCobiGenCodeBlock lastChild) {

    if (lastChild.indent == null) {
      lastChild.indent = this.indent;
    }
    if (this.child == null) {
      this.child = lastChild;
    } else {
      this.child.appendLast(lastChild);
    }
  }

  @Override
  public boolean isEmpty() {

    return this.child == null;
  }

  @Override
  public final void clear() {

    this.child = null;
  }

}
