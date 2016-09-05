package com.capgemini.cobigen.impl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.capgemini.cobigen.impl.exceptions.CobiGenRuntimeException;
import com.capgemini.cobigen.impl.exceptions.PluginProcessingException;

/**
 * This annotations wraps each return value with a try catch block forwarding exceptions of type
 * {@link CobiGenRuntimeException} and wrapping any other exception into a {@link PluginProcessingException}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Inherited
public @interface ExceptionFacade {

}
