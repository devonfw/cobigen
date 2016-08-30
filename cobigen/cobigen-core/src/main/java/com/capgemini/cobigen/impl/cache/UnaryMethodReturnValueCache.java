package com.capgemini.cobigen.impl.cache;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.impl.proxy.AbstractHandler;

/**
 * The {@link UnaryMethodReturnValueCache} enables caching of several requests on the same method. Therefore,
 * the cache relies on the assumption, that the input objects will be hold in memory as long as they are
 * referenced. Due to the fact, that this cache is just utilizing a {@link WeakHashMap}, it will automatically
 * discard entries which are collected by the GC. This class serves as an interceptor for the
 * {@link Cached @Cached} annotation.
 */
public class UnaryMethodReturnValueCache extends AbstractHandler {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(UnaryMethodReturnValueCache.class);

    /** Mapping of input object to method name to method result */
    private Map<Object, Map<Method, Object>> _cache = new WeakHashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // Ask cache
        Object returnValue = askCache(args[0], method);
        if (returnValue != null) {
            return returnValue;
        }

        // execute target implementation
        returnValue = method.invoke(getTargetObject(), args);

        // persist cache
        persistCache(args[0], method, returnValue);

        return returnValue;
    }

    /**
     * Asks the cache for an already available value for the given input and the given Method
     * @param input
     *            object of the method call
     * @param method
     *            {@link Method} to be called
     * @return the cached return value or {@code null} if the cache does not contain this value.
     */
    private Object askCache(Object input, Method method) {
        if (_cache.containsKey(input)) {
            if (_cache.get(input).containsKey(method)) {
                LOG.debug("Value for method {}#{} retrieved from cache.", method.getClass().getName(),
                    method.getName());
                return _cache.get(input).get(method);
            }
        }
        return null;
    }

    /**
     * Persists the given return value for the given method call with the given input parameter in the cache.
     * @param input
     *            object of the method call
     * @param method
     *            {@link Method} to be called
     * @param returnValue
     *            to be cached
     */
    private void persistCache(Object input, Method method, Object returnValue) {
        if (!_cache.containsKey(input)) {
            // setting initial size to 4 as memory optimization due to the fact, that the cache is currently
            // just used for ConfigurationInterpreter, which just has four public methods.
            _cache.put(input, new HashMap<Method, Object>(4));
        }
        _cache.get(input).put(method, returnValue);
    }
}
