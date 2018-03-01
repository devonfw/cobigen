package com.capgemini.cobigen.impl.aop;

import java.lang.reflect.Method;

import com.capgemini.cobigen.api.annotation.ExceptionFacade;
import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.impl.exceptions.PluginProcessingException;
import com.capgemini.cobigen.impl.util.ExceptionUtil;

/**
 * This is the interceptor processing {@link ExceptionFacade} annotations. It wraps each return value with a
 * try catch block forwarding exceptions of type {@link CobiGenRuntimeException} and wrapping any other
 * exception into a {@link PluginProcessingException}.
 */
public class ExceptionFacadeInterceptor extends AbstractInterceptor {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // just skip if annotation is not available
        if (isActive(method, ExceptionFacade.class)) {
            return ExceptionUtil.invokeTarget(getTargetObject(), method, args);
        }

        try {
            Object result = ExceptionUtil.invokeTarget(getTargetObject(), method, args);
            // if internal API class, proxy again
            if (result.getClass().getPackage().getName().startsWith("com.capgemini.cobigen.api")) {
                result = ProxyFactory.getProxy(result);
            }
            return result;
        } catch (CobiGenRuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new PluginProcessingException(e);
        }
    }

}
