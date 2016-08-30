package com.capgemini.cobigen.impl.proxy;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * A factory for creating Proxy objects.
 */
public class ProxyFactory {

    /**
     * Creates a new proxy for a given target object and registers the handlers.
     *
     * @param targetObject
     *            the target object
     * @param handlers
     *            the handlers
     * @return the proxyied targetObject
     */
    public static Object getProxy(Object targetObject, List<AbstractHandler> handlers) {
        Object proxyObject = null;
        if (handlers.size() > 0) {
            proxyObject = targetObject;
            for (int i = 0; i < handlers.size(); i++) {
                handlers.get(i).setTargetObject(proxyObject);
                proxyObject = Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
                    targetObject.getClass().getInterfaces(), handlers.get(i));
            }
            return proxyObject;
        } else {
            return targetObject;
        }
    }
}