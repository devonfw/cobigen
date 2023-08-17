package com.devonfw.cobigen.api.code;

import com.devonfw.cobigen.api.template.out.CobiGenOutput;

/**
 * Interface for a section of the {@link CobiGenOutput}.
 *
 * @see CobiGenOutput#getCode()
 */
public interface CobiGenCodeBlock {

  // top-level

  /** @{@link #getName() Name} for the file header. */
  String NAME_HEADER = "header";

  /** @{@link #getName() Name} for the package. */
  String NAME_PACKAGE = "package";

  /** @{@link #getName() Name} for the imports. */
  String NAME_IMPORTS = "imports";

  /** @{@link #getName() Name} for the type declaration including doc. */
  String NAME_DECLARATION = "declaration";

  /** @{@link #getName() Name} for the fields. */
  String NAME_FIELDS = "fields";

  /** @{@link #getName() Name} for the constructors. */
  String NAME_CONSTRUCTORS = "constructors";

  /** @{@link #getName() Name} for the methods. */
  String NAME_METHODS = "methods";

  /** @{@link #getName() Name} for the getters. */
  String NAME_GETTERS = "getters";

  /** @{@link #getName() Name} for the setters. */
  String NAME_SETTERS = "setters";

  /** @{@link #getName() Name} for the nests (inner types). */
  String NAME_NESTS = "nests";

  /** @{@link #getName() Name} for the footer (typically just "}"). */
  String NAME_FOOTER = "footer";

  // children

  /**
   * @{@link #getName() Name} for the "intro" of a block like a field, constructor, method, etc. containing potential
   *         {@link #NAME_APIDOC apidoc}, {@link #NAME_COMMENT comment}(s), or {@link #NAME_ANNOTATIONS annotations}.
   */
  String NAME_INTRO = "intro";

  // atomics (leaves)

  /** @{@link #getName() Name} for the api-doc(s). */
  String NAME_APIDOC = "comment";

  /** @{@link #getName() Name} for the comment(s). */
  String NAME_COMMENT = "comment";

  /** @{@link #getName() Name} for the annotations(s). */
  String NAME_ANNOTATIONS = "annotations";

  /**
   * @{@link #getName() Name} for the actual code (field, constructor, method, etc.) without {@link #NAME_INTRO intro}.
   */
  String NAME_CODE = "code";

  /**
   * @return the name of this code-block. Top-level blocks will have names for the according section such as
   *         {@link #NAME_HEADER}, {@link #NAME_PACKAGE}, {@link #NAME_IMPORTS}, {@link #NAME_DECLARATION},
   *         {@link #NAME_FIELDS}, {@link #NAME_CONSTRUCTORS}, {@link #NAME_METHODS}, etc. Child blocks such as fields
   *         or methods will have the according field name. They may even have {@link #getChild() children} named
   */
  String getName();

  /**
   * @param blockName the {@link #getName() name} of the {@link #getChild() child block} to create and append in case of
   *        a {@link CobiGenCompositeCodeBlock composite block}.
   * @return the {@link CobiGenAtomicCodeBlock}. Will be this block itself if already {@link CobiGenAtomicCodeBlock
   *         atomic}.
   */
  CobiGenAtomicCodeBlock addAtomicChild(String blockName);

  /**
   * @param line the source-code line to add to the end of this block.
   * @return the {@link CobiGenAtomicCodeBlock} where the given {@code line} has been added.
   */
  CobiGenAtomicCodeBlock addLine(String line);

  /**
   * @param lines the source-code lines to add to the end of this block.
   * @return the {@link CobiGenAtomicCodeBlock} where the given {@code lines} have been added.
   */
  CobiGenAtomicCodeBlock addLines(String... lines);

  /**
   * @return the next {@link CobiGenCodeBlock code-block}.
   */
  CobiGenCodeBlock getNext();

  /**
   * @param name the {@link #getName() name} of the requested {@link #getNext() sibling} {@link CobiGenCodeBlock}.
   * @return this {@link CobiGenCodeBlock} or any {@link #getNext() next} successor with the given {@link #getName()
   *         name} or {@code null} if not found.
   */
  CobiGenCodeBlock getNext(String name);

  /**
   * @return the last {@link #getNext() sibling}. Will be this block itself if {@link #getNext() next} is {@code null}.
   */
  AbstractCobiGenCodeBlock getLast();

  /**
   * @return the first child or {@code null} if this {@link CobiGenCodeBlock} does not have children.
   */
  CobiGenCodeBlock getChild();

  /**
   * @param name the {@link #getName() name} of the requested {@link #getChild() child} {@link CobiGenCodeBlock}.
   * @return the first {@link #getChild()} with the given {@link #getName() name} or {@code null} if not found.
   */
  CobiGenCodeBlock getChild(String name);

}
