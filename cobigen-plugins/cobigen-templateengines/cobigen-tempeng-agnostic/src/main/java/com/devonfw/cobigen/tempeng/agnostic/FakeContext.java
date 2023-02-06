package com.devonfw.cobigen.tempeng.agnostic;

import java.lang.reflect.Type;

import io.github.mmm.code.api.element.CodeElementWithDeclaringType;
import io.github.mmm.code.api.language.CodeLanguage;
import io.github.mmm.code.base.AbstractBaseContext;
import io.github.mmm.code.base.AbstractBaseContextWithCache;
import io.github.mmm.code.base.loader.BaseLoader;
import io.github.mmm.code.base.source.BaseSourceImpl;
import io.github.mmm.code.base.type.BaseGenericType;

/**
 * Fake implementation of {@link io.github.mmm.code.api.CodeContext}.
 */
public class FakeContext extends AbstractBaseContextWithCache {

  private final FakeLanguage language;

  /**
   * The constructor.
   *
   * @param language the {@link FakeLanguage}.
   * @param source the {@link BaseSourceImpl}.
   */
  public FakeContext(FakeLanguage language, BaseSourceImpl source) {

    super(source);
    this.language = language;
  }

  @Override
  public CodeLanguage getLanguage() {

    return this.language;
  }

  @Override
  public BaseGenericType getType(Type type, CodeElementWithDeclaringType declaringElement) {

    return null;
  }

  @Override
  protected BaseLoader getLoader() {

    return null;
  }

  @Override
  public AbstractBaseContext getParent() {

    return null;
  }

}
