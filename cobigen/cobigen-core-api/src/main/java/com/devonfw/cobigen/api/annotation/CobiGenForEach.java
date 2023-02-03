package com.devonfw.cobigen.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.devonfw.cobigen.api.template.mapper.CobiGenModelMapper;
import com.devonfw.cobigen.api.template.provider.CobiGenCollectionProvider;

/**
 * Annotation to mark and configure a template written in Java.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CobiGenForEach {

  Class<? extends CobiGenCollectionProvider> value();

  @SuppressWarnings("rawtypes")
  Class<? extends CobiGenModelMapper> mapper() default CobiGenModelMapper.class;

}
