package com.capgemini.cobigen.impl.cache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation enables caching of method results for unary functions. The corresponding processor is the
 * {@link UnaryMethodReturnValueCache} implementation.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {

}
