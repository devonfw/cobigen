package com.devonfw.cobigen.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.devonfw.cobigen.api.extension.Priority;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;

/**
 * The priority to take into account when try reading an input. This annotation is meant to be set on
 * {@link TriggerInterpreter} classes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface ReaderPriority {

    /** The input readers priority */
    Priority value() default Priority.LOW;
}
