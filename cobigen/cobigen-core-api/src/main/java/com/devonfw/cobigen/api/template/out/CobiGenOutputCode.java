package com.devonfw.cobigen.api.template.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.code.AbstractCobiGenCodeBlock;
import com.devonfw.cobigen.api.code.CobiGenAtomicCodeBlock;
import com.devonfw.cobigen.api.code.CobiGenCodeBlock;
import com.devonfw.cobigen.api.code.CobiGenCompositeCodeBlock;
import com.devonfw.cobigen.api.util.StringUtil;

/**
 * Extends {@link AbstractCobiGenOutput} for source-code for programming language. For languages other than Java,
 * various methods need to be overridden.
 */
public abstract class CobiGenOutputCode extends AbstractCobiGenOutput {

  /** Keyword modifier {@value}. */
  protected static final String KEYWORD_PUBLIC = "public";

  /** Keyword modifier {@value}. */
  protected static final String KEYWORD_PROTECTED = "protected";

  /** Keyword modifier {@value}. */
  protected static final String KEYWORD_PRIVATE = "private";

  /** Keyword modifier {@value}. */
  protected static final String KEYWORD_ABSTRACT = "abstract";

  /** Keyword modifier {@value}. */
  protected static final String KEYWORD_STATIC = "static";

  /** Keyword modifier {@value}. */
  protected static final String KEYWORD_FINAL = "final";

  /** Keyword modifier {@value}. */
  protected static final String KEYWORD_SYNCHRONIZED = "synchronized";

  /** Keyword modifier {@value}. */
  protected static final String KEYWORD_NATIVE = "native";

  /** Keyword modifier {@value}. */
  protected static final String KEYWORD_VOLATILE = "volatile";

  /** Keyword modifier {@value}. */
  protected static final String KEYWORD_STRICTFP = "strictfp";

  /** Keyword type {@value}. */
  protected static final String KEYWORD_CLASS = "class";

  /** Keyword type {@value}. */
  protected static final String KEYWORD_INTERFACE = "interface";

  /** Keyword type {@value}. */
  protected static final String KEYWORD_ENUM = "enum";

  /** Keyword type {@value}. */
  protected static final String KEYWORD_ANNOTATION = "@interface";

  /** Keyword type {@value}. */
  protected static final String KEYWORD_RECORD = "record";

  /** Keyword {@value}. */
  protected static final String KEYWORD_IMPORT = "import";

  /** Keyword {@value}. */
  protected static final String KEYWORD_PACKAGE = "package";

  private static final Logger LOG = LoggerFactory.getLogger(CobiGenOutputCode.class);

  private static final Set<String> JAVA_MODIFIERS = Set.of(KEYWORD_PUBLIC, KEYWORD_PROTECTED, KEYWORD_PRIVATE,
      KEYWORD_ABSTRACT, KEYWORD_STATIC, KEYWORD_FINAL, KEYWORD_SYNCHRONIZED, KEYWORD_VOLATILE, KEYWORD_NATIVE,
      KEYWORD_STRICTFP);

  private static final Map<String, String> JAVA_TYPES = Map.of(KEYWORD_CLASS, CATEGORY_CLASS, KEYWORD_INTERFACE,
      CATEGORY_INTERFACE, KEYWORD_ENUM, CATEGORY_ENUMERATION, KEYWORD_ANNOTATION, CATEGORY_ANNOTATION, KEYWORD_RECORD,
      CATEGORY_RECORD);

  private static final int MULTILINE_BLOCK = -42;

  private final Map<String, ImportStatement> imports;

  private final List<String> buffer = new ArrayList<>();

  private int bufferIndent = -1;

  /** @see #getCategory() */
  protected String category;

  private String typeName;

  private CobiGenCompositeCodeBlock block;

  private CobiGenAtomicCodeBlock member;

  /**
   * The constructor for a nested-type output.
   *
   * @param parent the parent output.
   */
  public CobiGenOutputCode(CobiGenOutputCode parent) {

    super(parent);
    this.imports = parent.imports;
    this.category = CATEGORY_CLASS; // fallback
  }

  /**
   * The constructor.
   *
   * @param filename the {@link #getFilename() filename}.
   */
  public CobiGenOutputCode(String filename) {

    super(filename);
    this.imports = new HashMap<>();
    this.category = CATEGORY_CLASS; // fallback
  }

  @Override
  protected CobiGenCompositeCodeBlock createCode() {

    CobiGenCompositeCodeBlock start;
    CobiGenCompositeCodeBlock current;
    int widthMain = 0;
    AbstractCobiGenOutput parent = getParent();
    if (parent == null) {
      start = new CobiGenCompositeCodeBlock(CobiGenCodeBlock.NAME_HEADER);
      current = start;
      current = current.insertNext(CobiGenCodeBlock.NAME_PACKAGE);
      current = current.insertNext(CobiGenCodeBlock.NAME_IMPORTS);
      current = current.insertNext(CobiGenCodeBlock.NAME_DECLARATION);
    } else {
      current = (CobiGenCompositeCodeBlock) parent.code.getNext(CobiGenCodeBlock.NAME_NESTS);
      widthMain = current.getIndentFallbackWidth();
      start = current.addCompositeChild(CobiGenCodeBlock.NAME_DECLARATION);
      current = start;
    }
    int widthBody = widthMain + 2;
    current = appendBlock(current, CobiGenCodeBlock.NAME_FIELDS, widthBody);
    current = appendBlock(current, CobiGenCodeBlock.NAME_CONSTRUCTORS, widthBody);
    current = appendBlock(current, CobiGenCodeBlock.NAME_GETTERS, widthBody); // mix getterns and setters by default
    current = appendBlock(current, CobiGenCodeBlock.NAME_METHODS, widthBody);
    current = appendBlock(current, CobiGenCodeBlock.NAME_NESTS, widthBody);
    current = appendBlock(current, CobiGenCodeBlock.NAME_FOOTER, widthMain);
    return start;
  }

  /**
   * @param current the current {@link CobiGenCompositeCodeBlock} to append to.
   * @param blockName the {@link CobiGenCompositeCodeBlock#getName() block name}.
   * @param width the indent width (number of spaces).
   * @return the created and appended {@link CobiGenCompositeCodeBlock}.
   */
  protected CobiGenCompositeCodeBlock appendBlock(CobiGenCompositeCodeBlock current, String blockName, int width) {

    current = current.insertNext(blockName);
    current.setIndentFallbackWidth(width);
    return current;
  }

  /**
   * @return the {@link QualifiedName#getSimpleName() simple name} of this type.
   */
  public String getTypeName() {

    return this.typeName;
  }

  @Override
  public String getCategory() {

    return this.category;
  }

  /**
   * @param qualifiedName the qualified name.
   * @return the parsed {@link QualifiedName}.
   */
  protected QualifiedName parseName(String qualifiedName) {

    return QualifiedName.of(qualifiedName);
  }

  @Override
  public void addProperty(String name, String qualifiedTypeName, String description) {

    addProperty(name, parseName(qualifiedTypeName), description);
  }

  @Override
  public void addProperty(String name, QualifiedName type, String description) {

    boolean annotation = isAnnotation();
    addProperty(name, type, description, true, !(isInterface() || annotation), true, !annotation);
  }

  @Override
  public void addProperty(String name, QualifiedName type, String description, boolean addImport, boolean addField,
      boolean addGetter, boolean addSetter) {

    String typeSimpleName = type.getSimpleName();
    if (isImportRequired(type)) {
      boolean imported = false;
      if (addImport) {
        imported = addImport(type);
      }
      if (!imported) {
        ImportStatement importStatement = getImport(typeSimpleName);
        if ((importStatement == null) || !importStatement.getTarget().equals(type.getQualifiedName())) {
          typeSimpleName = type.getQualifiedName();
        }
      }
    }
    if (addField) {
      addField(name, typeSimpleName, description);
    }
    if (addGetter) {
      addGetter(name, typeSimpleName, description);
    }
    if (addSetter) {
      addSetter(name, typeSimpleName, description);
    }
  }

  /**
   * @param name the name of the field to add.
   * @param type the (local) property type.
   * @param description the description of the property used for documentation generation.
   */
  protected void addField(String name, String type, String description) {

    if (isInterface()) {
      LOG.warn("Interface can not declare fields - omitting field {}", name);
      return;
    }
    CobiGenCodeBlock fields = this.code.getNext(CobiGenCodeBlock.NAME_FIELDS).addAtomicChild(name);
    fields.addLine("");
    if (isAnnotation()) {
      addGetterDoc(description, fields);
      fields.addLine(type + " " + name + "();");
    } else {
      fields.addLine("private " + type + " " + name + ";");
    }
  }

  /**
   * @param name the name of the property.
   * @param type the (local) property type.
   * @param description the description of the property used for documentation generation.
   */
  protected void addGetter(String name, String type, String description) {

    String methodPrefix = "get";
    if ("boolean".equals(type)) {
      methodPrefix = "is";
    }
    String methodName = methodPrefix + StringUtil.capFirst(name);
    CobiGenCodeBlock getters = this.code.getNext(CobiGenCodeBlock.NAME_GETTERS).addAtomicChild(methodName);
    getters.addLine("");
    if (description == null) {
      if (isClass()) {
        getters.addLine("@Override");
      }
    } else if (!description.isEmpty()) {
      addGetterDoc(description, getters);
    }
    String visibility = "public ";
    if (isAnnotation()) {
      getters.addLine(type + " " + name + "();");
      return;
    } else if (isInterface()) {
      visibility = "";
    }
    String signatureSuffix = ";";
    if (!isInterface()) {
      signatureSuffix = " {";
    }
    getters.addLine(visibility + type + " " + methodName + "()" + signatureSuffix);
    if (!isInterface()) {
      getters.addLines("  return this." + name + ";", "}");
    }
  }

  /**
   * @param name the name of the property.
   * @param type the (local) property type.
   * @param description the description of the property used for documentation generation.
   */
  protected void addSetter(String name, String type, String description) {

    if (isAnnotation()) {
      LOG.warn("Annotation can not declare setter - omitting property {}.", name);
      return;
    }
    String methodName = "set" + StringUtil.capFirst(name);
    CobiGenCodeBlock setters = this.code.getNext(CobiGenCodeBlock.NAME_SETTERS).addAtomicChild(methodName);
    setters.addLine("");
    String visibility = "public ";
    if ((description == null) && isClass()) {
      setters.addLine("@Override");
    }
    String signatureSuffix = ";";
    if (!isInterface()) {
      signatureSuffix = " {";
    }
    setters.addLine(visibility + "void " + methodName + "(" + type + " " + name + ")" + signatureSuffix);
    if (!isInterface()) {
      setters.addLines("  this." + name + " = " + name + ";", "}");
    }
  }

  /**
   * @param description the documentation of the property.
   * @param getters the {@link CobiGenCodeBlock} where to append the documentation.
   */
  protected void addGetterDoc(String description, CobiGenCodeBlock getters) {

    if (description != null) {
      int newline = description.indexOf('\n');
      if (newline >= 0) {
        String[] descriptions = description.split("\n");
        String[] lines = new String[descriptions.length + 2];
        int lineIndex = 0;
        lines[lineIndex++] = "/**";
        String prefix = " * @return ";
        for (String d : descriptions) {
          lines[lineIndex++] = prefix + d;
          prefix = " *         ";
        }
        lines[lineIndex++] = " */";
      } else {
        getters.addLines("/**", " * @return " + description, " */");
      }
    }
  }

  @Override
  public ImportStatement getImport(String name) {

    return this.imports.get(name);
  }

  @Override
  public boolean addImport(String qualifiedName) {

    return addImport(parseName(qualifiedName));
  }

  /**
   * @param qualifiedName the {@link QualifiedName} to check.
   * @return {@code true} if an {@link ImportStatement} is required for the type identified by the given
   *         {@link QualifiedName}, {@code false} otherwise (e.g. for "String" or "java.lang.String" in case of Java).
   */
  protected abstract boolean isImportRequired(QualifiedName qualifiedName);

  @Override
  public boolean addImport(ImportStatement importStatement) {

    boolean added = doAddImport(importStatement);
    if (added) {
      getRoot().code.getNext(CobiGenCodeBlock.NAME_IMPORTS).addLine(true, importStatement.toString());
    }
    return added;
  }

  /**
   * @param importStatement the {@link ImportStatement} to add.
   * @return {@code true} if added as new import, {@code false} otherwise (already present before hence ignoring).
   */
  protected boolean doAddImport(ImportStatement importStatement) {

    boolean added = false;
    int count = importStatement.getKeyCount();
    assert (count > 0);
    for (int i = 0; i < count; i++) {
      String key = importStatement.getKey(i);
      ImportStatement existing = this.imports.get(key);
      if (existing == null) {
        this.imports.put(key, importStatement);
        added = true;
      } else {
        if (existing.equals(importStatement)) {
          LOG.debug("Omitting duplicated import for '{}'.", key);
        } else {
          LOG.warn("Duplicate import for '{}': Having '{}' thus omitting '{}'.", key, existing, importStatement);
        }
      }
    }
    return added;
  }

  /**
   * @param tokenizer the {@link LineTokenizer} after the "import" keyword has been {@link LineTokenizer#next()
   *        consumed}.
   * @return the parsed {@link ImportStatement}.
   */
  protected abstract ImportStatement createImportStatement(LineTokenizer tokenizer);

  private void addImportInternal(LineTokenizer tokenizer) {

    doAddImport(createImportStatement(tokenizer));
  }

  /**
   * Override if other than Java.
   *
   * @param token the token (single word) to check.
   * @return {@code true} if modifier keyword, {@code false} otherwise.
   */
  protected boolean isModifierKeyword(String token) {

    return JAVA_MODIFIERS.contains(token);
  }

  /**
   * Override if other than Java.
   *
   * @param token the token (single word) to check.
   * @return the {@link #getCategory() category} or {@code null} if not a type keyword.
   */
  protected String getCategory(String token) {

    return JAVA_TYPES.get(token);
  }

  /**
   * Creates a new child type (nested type).
   *
   * @return the new {@link CobiGenOutputCode} instance.
   */
  protected abstract CobiGenOutputCode createChild();

  @Override
  public CobiGenOutputCode addLine(String line) {

    if (this.block == null) {
      this.block = this.code;
    }
    if (line == null) {
      // EOF
      addLine2Block(CobiGenCodeBlock.NAME_FOOTER, null, 0, null);
      return (CobiGenOutputCode) getRoot();
    }
    if (this.bufferIndent == MULTILINE_BLOCK) {
      this.buffer.add(line);
      if (line.trim().endsWith("*/")) {
        this.bufferIndent = -1;
      }
      return this;
    }
    LineTokenizer tokenizer = new LineTokenizer(line);
    if (this.bufferIndent != -1) {
      int tokenStart = tokenizer.getFirstTokenStart();
      if (tokenStart > this.bufferIndent) {
        this.buffer.add(line);
        return this;
      }
      this.bufferIndent = -1;
      if ((tokenStart == this.bufferIndent) && (tokenStart < tokenizer.len)) {
        char c = tokenizer.line.charAt(tokenStart);
        if (c == '}') {
          this.buffer.add(line); // assuming proper formatting and indentation
          return this;
        }
      }
    }
    if (this.member != null) {
      this.member = appendWhileIndent(tokenizer, this.member);
      return this;
    }
    String token = tokenizer.next();
    if (token.isEmpty()) {
      // omit multiple empty lines in a row
      if (this.buffer.isEmpty() || !this.buffer.get(this.buffer.size() - 1).isEmpty()) {
        this.buffer.add(line);
      }
      return this;
    } else if ("//".equals(token)) {
      this.buffer.add(line);
      return this;
    } else if (token.startsWith("/*")) {
      this.buffer.add(line);
      this.bufferIndent = MULTILINE_BLOCK;
      return this;
    }

    int firstNonSpace = tokenizer.getFirstTokenStart();
    if (KEYWORD_PACKAGE.equals(token)) {
      addLine2Block(CobiGenCodeBlock.NAME_PACKAGE, line, firstNonSpace, null);
      return this;
    } else if (KEYWORD_IMPORT.equals(token)) {
      addLine2Block(CobiGenCodeBlock.NAME_IMPORTS, line, firstNonSpace, null);
      addImportInternal(tokenizer);
      return this;
    }

    boolean hasModifiers = false;
    while (isModifierKeyword(token)) {
      hasModifiers = true;
      token = tokenizer.next();
    }
    if (token.isEmpty()) {
      LOG.warn("Unexpected code with only modifiers: {}", line);
      this.buffer.add(line);
      return this;
    }
    String cat = getCategory(token);
    if (cat != null) {
      CobiGenOutputCode type = this;
      String className = tokenizer.next();
      if (className == null) {
        LOG.warn("Missing {} name in {}", token, this.filename);
        className = "Unknown";
      }
      if (this.typeName != null) { // create nested type
        type = createChild();
        type.buffer.addAll(this.buffer);
        this.buffer.clear();
        type.bufferIndent = this.bufferIndent;
        this.bufferIndent = -1;
      }
      type.category = cat;
      type.typeName = className;
      type.addLine2Block(CobiGenCodeBlock.NAME_DECLARATION, line, firstNonSpace, className);
      if (line.trim().endsWith("{")) {
        this.block = (CobiGenCompositeCodeBlock) this.block.getNext();
      }
      return type;
    } else if (token.startsWith("@")) {
      this.buffer.add(line);
      this.bufferIndent = tokenizer.getFirstTokenStart();
    } else if (token.equals("}") && !hasModifiers) {
      if (this.member != null) {
        this.member.addLine(true, line);
        this.member = null;
        return this;
      } else {
        if (firstNonSpace > 0) {
          int indentCount = 0;
          String indent = this.block.getLast().getIndent();
          if (indent != null) {
            indentCount = indent.length();
          }
          if (firstNonSpace > indentCount) {
            LOG.info("Unexpected code: {}", line);
            this.buffer.add(line);
            return this;
          }
        }
      }
      if (this.typeName != null) {
        addLine2Block(CobiGenCodeBlock.NAME_FOOTER, line, firstNonSpace, null);
        return this;
      } else {
        LOG.info("Unexpected code: {}", line);
        this.buffer.add(line);
        return this;
      }
    } else {
      // private String name;
      // <T> T getName();
      // ClassName() {
      String blockName = null;
      String codeName = null;
      // String type = token;
      char signChar = 0;
      if (tokenizer.signChar == '(') { // constructor
        if (token.equals(this.typeName) || token.equals("constructor")) {
          blockName = CobiGenCodeBlock.NAME_CONSTRUCTORS;
          codeName = token;
        }
      } else {
        token = tokenizer.next();
        if (tokenizer.signChar == '(') { // method
          blockName = CobiGenCodeBlock.NAME_METHODS;
        } else {
          blockName = CobiGenCodeBlock.NAME_FIELDS;
          signChar = tokenizer.signChar;
        }
        codeName = token;
      }

      CobiGenAtomicCodeBlock newMember = addLine2Block(blockName, line, firstNonSpace, codeName);
      token = tokenizer.next();
      if (signChar == 0) {
        signChar = tokenizer.signChar;
      }
      if (signChar == '{') {
        this.member = newMember;
      } else if (signChar != ';') {
        LOG.info("Unexpected code: {}", line);
      }
    }
    return this;
  }

  private CobiGenAtomicCodeBlock appendWhileIndent(LineTokenizer tokenizer, CobiGenAtomicCodeBlock codeBlock) {

    if (tokenizer.len == 0) {
      codeBlock.addLine(true, tokenizer.line); // assuming proper formatting and indentation of annotations
      return codeBlock;
    }
    int indentCount = 0;
    String indent = codeBlock.getIndent();
    if (indent != null) {
      indentCount = indent.length(); // assuming no mixture of space and tabs
    }
    int tokenStart = tokenizer.getFirstTokenStart();
    if (tokenStart > indentCount) {
      codeBlock.addLine(true, tokenizer.line); // assuming proper formatting and indentation of annotations
      return codeBlock;
    } else if ((tokenStart == indentCount) && (tokenStart < tokenizer.len)) {
      char c = tokenizer.line.charAt(tokenStart);
      if (c == '}') {
        codeBlock.addLine(true, tokenizer.line); // assuming proper formatting and indentation of annotations
      }
    }
    return null;
  }

  private CobiGenAtomicCodeBlock addLine2Block(String blockName, String line, int tokenStart, String memberName) {

    boolean unique = CobiGenCodeBlock.NAME_PACKAGE.equals(blockName);
    boolean blockMatched;
    if (blockName == null) {
      blockMatched = true;
    } else {
      blockMatched = false;
      AbstractCobiGenCodeBlock targetSection = this.block.getNext(blockName);
      if (targetSection != null) {
        if (!unique || targetSection.isEmpty()) {
          this.block = (CobiGenCompositeCodeBlock) targetSection;
          blockMatched = true;
        }
      }
    }
    if ((tokenStart > 0) && this.block.getIndent() == null) {
      String indent = line.substring(0, tokenStart);
      this.block.setIndent(indent);
    }
    AbstractCobiGenCodeBlock top;
    if (memberName == null) {
      top = this.block;
    } else {
      top = this.block.addAtomicChild(memberName);
    }
    if (!this.buffer.isEmpty()) {
      top.addLines(true, this.buffer);
      this.buffer.clear();
    }
    CobiGenAtomicCodeBlock result = top.addLine(true, line);
    if (unique) {
      AbstractCobiGenCodeBlock next = this.block.getNext();
      if (next != null) {
        this.block = (CobiGenCompositeCodeBlock) next;
      }
    }
    if (!blockMatched) {
      LOG.info("Unexpected code structure: expected block {} for '{}' but had to add to block {} instead.", blockName,
          line, this.block.getName());
    }
    return result;
  }

  /**
   * Tokenizer for a line of source-code.
   */
  protected static final class LineTokenizer {

    private final String line;

    private final int len;

    /** current token start position */
    private int tokenStart;

    /** start position after indent (at the first character that is not a whitespace). */
    private int firstTokenStart;

    private char signChar;

    /**
     * The constructor.
     *
     * @param line the source-code line to tokenize.
     */
    public LineTokenizer(String line) {

      super();
      this.line = line;
      this.len = line.length();
      while (this.firstTokenStart < this.len) {
        char c = line.charAt(this.firstTokenStart);
        if (Character.isWhitespace(c)) {
          this.firstTokenStart++;
        } else {
          this.tokenStart = this.firstTokenStart;
          break;
        }
      }
    }

    /**
     * @return the {@link String#charAt(int) index} of the first {@link Character} that is NOT a
     *         {@link Character#isWhitespace(char) whitespace}.
     */
    public int getFirstTokenStart() {

      return this.firstTokenStart;
    }

    /**
     * @return the indent of the line.
     */
    public String getIndent() {

      if (this.firstTokenStart == 0) {
        return null;
      }
      return this.line.substring(0, this.firstTokenStart);
    }

    /**
     * @return the next token from the source-code line.
     */
    public String next() {

      this.signChar = 0;
      if (this.tokenStart >= this.len) {
        return "";
      }
      int tokenEnd = this.len;
      int i = this.tokenStart;
      while (i < this.len) {
        char c = this.line.charAt(i);
        if (Character.isWhitespace(c)) {
          if (tokenEnd == this.len) {
            tokenEnd = i;
          }
        } else {
          if (tokenEnd != this.len) {
            break;
          } else if (c == ';') {
            this.signChar = c;
            tokenEnd = i;
          } else if (c == '<') {
            tokenEnd = i;
            this.signChar = c;
            i = findEnd(i, '>', true);
          } else if (c == '(') {
            tokenEnd = i;
            this.signChar = c;
            i = findEnd(i, ')', true);
          } else if (c == '{') {
            tokenEnd = i;
            this.signChar = c;
            i = findEnd(i, '}', false);
          } else if (c == '"') {
            tokenEnd = i;
            this.signChar = c;
            // TODO multi-line string?
            i++;
            boolean escape = false;
            while (i < this.len) {
              c = this.line.charAt(i);
              if (c == '\\') {
                escape = !escape;
              } else {
                if (c == '"') {
                  if (!escape) {
                    break; // found end of string literal
                  }
                }
                escape = false;
              }
            }
          }
        }
        i++;
      }
      String token = this.line.substring(this.tokenStart, tokenEnd);
      if (this.tokenStart > this.firstTokenStart) { // not the first token?
        if ("//".equals(token)) {
          this.tokenStart = this.len;
          return null; // ignore comment at end of the line
        }

        if (token.startsWith("/*")) {
          int commentEnd = this.line.indexOf("*/", i);
          if (commentEnd > 0) {
            i = commentEnd;
            while (i < this.len) {
              char c = this.line.charAt(i);
              if (Character.isWhitespace(c)) {
                i++;
              } else {
                break;
              }
            }
            this.tokenStart = i;
            return next();
          }
          // otherwise we return the token and have to start a block-comment.
        }
      }
      this.tokenStart = i;
      return token;
    }

    private int findEnd(int i, char closingBrace, boolean expected) {

      int deepth = 1;
      i++;
      while (i < this.len) {
        char c = this.line.charAt(i);
        if (c == this.signChar) {
          deepth++;
        } else if (c == closingBrace) {
          deepth--;
          if (deepth == 0) {
            return i;
          }
        }
        i++;
      }
      if (expected) {
        LOG.info("Found opening character '{}' but missing closing character '{}' in line: {}", this.signChar,
            closingBrace, this.line);
      }
      return this.len;
    }

  }

}
