package com.devonfw.cobigen.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.devonfw.cobigen.api.extension.Priority;

/**
 * The priority to take into account when try reading an input.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface ReaderPriority {

    /** The input readers priority */
    Priority value();
}
