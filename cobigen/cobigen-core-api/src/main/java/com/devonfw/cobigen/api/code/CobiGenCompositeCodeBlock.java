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

    adopt(child);
    this.child = child;
  }

  @Override
  public CobiGenCompositeCodeBlock insertNext(String blockName) {

    CobiGenCompositeCodeBlock composite = new CobiGenCompositeCodeBlock(blockName);
    insertNext(composite);
    return composite;
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
      return addAtomicChild(NAME_CODE);
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
   * @param lastChild the {@link AbstractCobiGenCodeBlock} to append as {@link #getLast() last} {@link #getChild()
   *        child} so to the end of this block.
   */
  public void appendChild(AbstractCobiGenCodeBlock lastChild) {

    if (this.child == null) {
      setChild(lastChild);
    } else {
      this.child.getLast().insertNext(lastChild);
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
