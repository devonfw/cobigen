package com.devonfw.cobigen.impl.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.annotation.Cached;
import com.devonfw.cobigen.api.annotation.ExceptionFacade;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.google.common.collect.Maps;

/**
 * A factory for creating Proxy objects.
 */
public final class ProxyFactory {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ProxyFactory.class);

    /** Map representing the available AOP interceptors utilized by CobiGen */
    private static final Map<String, Class<? extends AbstractInterceptor>> annotationToInterceptorMap;

    /**
     * Cache of proxies. Mapping from target object to proxy object OR target object to boolean {@code false}
     * to mark objects which have been already checked to be not proxied. The {@link WeakHashMap} will assure,
     * that the garbage collector will also destroy the interceptor instances.
     */
    private static Map<Object, Object> _cache = new WeakHashMap<>();

    static {
        annotationToInterceptorMap = Maps.newHashMap();
        annotationToInterceptorMap.put(Cached.class.getCanonicalName(), CachedInterceptor.class);
        annotationToInterceptorMap.put(ExceptionFacade.class.getCanonicalName(), ExceptionFacadeInterceptor.class);
    }

    /**
     * Creates a new dynamic proxy for a given target object and registers the handlers.
     * @param <T>
     *            type of the target object.
     * @param targetObject
     *            the target object
     * @return the proxied targetObject
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(T targetObject) {

        // assure single wrapping
        if (Proxy.isProxyClass(targetObject.getClass())
            && Proxy.getInvocationHandler(targetObject) instanceof AbstractInterceptor) {
            return targetObject;
        }

        // ask cache
        T proxyObject = (T) _cache.get(targetObject);
        if (proxyObject != null) {
            if (Boolean.FALSE.equals(proxyObject)) {
                return targetObject;
            } else {
                LOG.debug("Taking proxy for {}", targetObject.getClass());
                return proxyObject;
            }
        }

        // create proxy if not cached
        boolean proxied = false;
        for (String annotationClass : collectAnnotations(targetObject.getClass())) {
            proxyObject = targetObject;
            Class<? extends AbstractInterceptor> interceptorClass = annotationToInterceptorMap.get(annotationClass);
            if (interceptorClass != null) {
                AbstractInterceptor interceptor;
                try {
                    interceptor = interceptorClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new CobiGenRuntimeException(
                        "Unable to instantiate class " + interceptorClass.getCanonicalName());
                }
                interceptor.setTargetObject(proxyObject);
                proxyObject = (T) Proxy.newProxyInstance(proxyObject.getClass().getClassLoader(),
                    proxyObject.getClass().getInterfaces(), interceptor);
                proxied = true;
                LOG.debug("Created proxy for {} with {} interceptor", targetObject.getClass(), interceptorClass);
            }
        }

        if (proxied) {
            _cache.put(targetObject, proxyObject); // if any proxy has been generated -> cache
        } else {
            _cache.put(targetObject, false); // if not annotated -> mark
        }

        return proxyObject != null ? proxyObject : targetObject;
    }

    /**
     * Collects all annotation's full qualified names. Just considers class and method annotations at the
     * moment.
     * @param targetObjectClass
     *            target object class to be proxied
     * @return a set of full qualified names of existing annotations in the target object without duplicates.
     */
    private static Set<String> collectAnnotations(Class<?> targetObjectClass) {
        Set<String> annotationClasses = new HashSet<>();
        for (Annotation a : targetObjectClass.getAnnotations()) {
            annotationClasses.add(a.annotationType().getCanonicalName());
        }
        for (Method m : targetObjectClass.getMethods()) {
            for (Annotation a : m.getAnnotations()) {
                annotationClasses.add(a.annotationType().getCanonicalName());
            }
        }
        return annotationClasses;
    }
}