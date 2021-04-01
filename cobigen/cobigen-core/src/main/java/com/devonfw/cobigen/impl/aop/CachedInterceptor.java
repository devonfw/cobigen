package com.devonfw.cobigen.impl.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.annotation.Cached;
import com.devonfw.cobigen.impl.util.ExceptionUtil;

/**
 * The {@link CachedInterceptor} enables caching of several requests on the same method. Therefore, the cache
 * relies on the assumption, that the input objects will be hold in memory as long as they are referenced. Due
 * to the fact, that this cache is just utilizing a {@link WeakHashMap}, it will automatically discard entries
 * which are collected by the GC. This class serves as an interceptor for the {@link Cached @Cached}
 * annotation.
 */
public class CachedInterceptor extends AbstractInterceptor {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(CachedInterceptor.class);

    /** Mapping of input object to method name to method result */
    private Map<Object, Map<Method, Object>> _cache = new HashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // just skip if annotation is not available
        if (!isActive(method, Cached.class)
            && !isActive(getTargetObject().getClass().getMethod(method.getName(), method.getParameterTypes()),
                Cached.class)) {
            return ExceptionUtil.invokeTarget(getTargetObject(), method, args);
        }

        // Ask cache
        // int keyHash = UUID.randomUUID().toString().hashCode();// Arrays.toString(args).hashCode()
        int keyHash;
        if (args.length > 1) {
            keyHash = Objects.hash(args[0], args[1]);
        } else if (args.length > 0) {
            keyHash = args[0].hashCode();
        } else {
            keyHash = "default".hashCode();
        }

        Object returnValue = askCache(keyHash, method);
        if (returnValue != null) {
            return returnValue;
        }

        // execute target implementation
        returnValue = ExceptionUtil.invokeTarget(getTargetObject(), method, args);

        // persist cache
        persistCache(keyHash, method, returnValue);

        return returnValue;
    }

    /**
     * Asks the cache for an already available value for the given input and the given Method
     * @param paramHash
     *            hash value of the parameters to serve as a key for caching
     * @param method
     *            {@link Method} to be called
     * @return the cached return value or {@code null} if the cache does not contain this value.
     */
    private Object askCache(int paramHash, Method method) {
        if (_cache.containsKey(paramHash)) {
            if (_cache.get(paramHash).containsKey(method)) {
                LOG.debug("Value for method {}#{} retrieved from cache.", method.getClass().getName(),
                    method.getName());
                return _cache.get(paramHash).get(method);
            }
        }
        return null;
    }

    /**
     * Persists the given return value for the given method call with the given input parameter in the cache.
     * @param paramHash
     *            hash value of the parameters to serve as a key for caching
     * @param method
     *            {@link Method} to be called
     * @param returnValue
     *            to be cached
     */
    private void persistCache(int paramHash, Method method, Object returnValue) {
        if (!_cache.containsKey(paramHash)) {
            // setting initial size to 4 as memory optimization due to the fact, that the cache is currently
            // just used for ConfigurationInterpreter, which just has four public methods.
            _cache.put(paramHash, new HashMap<Method, Object>(4));
        }
        _cache.get(paramHash).put(method, returnValue);
    }
}
