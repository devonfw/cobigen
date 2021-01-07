package com.devonfw.cobigen.impl.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.devonfw.cobigen.api.annotation.Cached;
import com.devonfw.cobigen.api.annotation.ExceptionFacade;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.google.common.collect.Maps;

/**
 * A factory for creating Proxy objects.
 */
public final class ProxyFactory {

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
                return proxyObject;
            }
        }

        // create proxy if not cached
        proxyObject = targetObject;
        for (String annotationClass : collectAnnotations(targetObject.getClass())) {
            Class<? extends AbstractInterceptor> interceptorClass = annotationToInterceptorMap.get(annotationClass);
            if (interceptorClass != null) {
                AbstractInterceptor interceptor;
                try {
                    interceptor = interceptorClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new CobiGenRuntimeException(
                        "Unable to instantiate class " + interceptorClass.getCanonicalName());
                }
                interceptor.setTargetObject(targetObject);
                proxyObject = (T) Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
                    targetObject.getClass().getInterfaces(), interceptor);
            }
        }

        // save proxy object
        if (proxyObject != targetObject) {
            _cache.put(targetObject, proxyObject);// if any proxy has been generated -> cache
        } else {
            _cache.put(targetObject, false);// if not annotated -> mark
        }

        return proxyObject;
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