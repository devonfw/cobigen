package com.devonfw.cobigen.impl.aop;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.impl.generator.CobiGenImpl;
import com.devonfw.cobigen.impl.generator.ConfigurationInterpreterImpl;
import com.devonfw.cobigen.impl.generator.GenerationProcessorImpl;
import com.devonfw.cobigen.impl.generator.InputInterpreterImpl;
import com.devonfw.cobigen.impl.generator.InputResolverImpl;
import com.devonfw.cobigen.impl.generator.MatcherEvaluatorImpl;
import com.devonfw.cobigen.impl.generator.TriggerMatchingEvaluatorImpl;

/**
 * AOP factory with a specific simple scope to not introduce any heavy weight frameworks and make separation of concerns
 * a little bit easier. Will just serve singletons.
 */
public class BeanFactory {

  /** Pre-configure known beans to be injectable to prevent from performance issues */
  private static final List<Class<?>> KNOWN_BEANS = List.of(ConfigurationInterpreterImpl.class,
      GenerationProcessorImpl.class, InputInterpreterImpl.class, MatcherEvaluatorImpl.class, InputResolverImpl.class,
      TriggerMatchingEvaluatorImpl.class, CobiGenImpl.class);

  /** All Beans are singletons and therefore registered on their class name */
  private Map<String, Object> registry = new HashMap<>();

  /**
   * Creates or returns a new bean matching the interface given as a parameter. The returned instance is always a
   * singleton.
   *
   * @param <T> super type of the returned instance
   * @param interfaze to get a bean instance for
   * @return the singleton instance implementing the given interface
   */
  @SuppressWarnings("unchecked")
  public <T> T createBean(Class<T> interfaze) {

    // check registry for existing instance
    if (this.registry.containsKey(interfaze.getCanonicalName())) {
      return (T) this.registry.get(interfaze.getCanonicalName());
    }

    // else create one
    return newInstance(interfaze);
  }

  /**
   * Enforces the creation of a new instance just of the bean for the given interface. Objects for field initialization
   * will be retrieved from the registry. If there is already a bean instance registered for the given interface, this
   * bean will be overwritten by the newly created one. This method should just be used if a new object instance has to
   * be enforced.
   *
   * @param <T> type of the interface
   * @param interfaze to be instantiated
   * @return the newly created instance
   */
  @SuppressWarnings("unchecked")
  public <T> T newInstance(Class<T> interfaze) {

    try {
      for (Class<?> c : KNOWN_BEANS) {
        if (interfaze.isAssignableFrom(c)) {
          Object newInstance = c.newInstance();
          this.registry.put(interfaze.getCanonicalName(), ProxyFactory.getProxy(newInstance));
          initializeAndSetFields(newInstance);
          return (T) this.registry.get(interfaze.getCanonicalName());
        }
      }
    } catch (InstantiationException | IllegalAccessException e) {
      throw new CobiGenRuntimeException("Failure on initialization of class " + interfaze.getCanonicalName(), e);
    }
    throw new CobiGenRuntimeException("Could find bean implementation for interface " + interfaze.getCanonicalName()
        + " as no class of such has been registered at AopFactory#KNOWN_BEANS");
  }

  /**
   * Sets the fields annotated with {@link Inject} of the new instance. On demand creates new beans.
   *
   * @param newInstance to be initialized
   */
  private void initializeAndSetFields(Object newInstance) {

    for (Field field : newInstance.getClass().getDeclaredFields()) {
      if (field.getAnnotationsByType(Inject.class).length > 0) {
        Class<?> fieldType = field.getType();
        Object fieldInstance = createBean(fieldType);

        boolean accessible = field.isAccessible();
        if (!accessible) {
          field.setAccessible(true);
        }

        try {
          field.set(newInstance, fieldInstance);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          throw new CobiGenRuntimeException(
              "Failure on setting field " + field.getName() + " of class " + newInstance.getClass().getCanonicalName(),
              e);
        }
        // restore config
        field.setAccessible(accessible);
      }
    }
  }

  /**
   * Add a bean manually. The bean will just be registered on its own type.
   *
   * @param bean to be added
   */
  public void addManuallyInitializedBean(Object bean) {

    this.registry.put(bean.getClass().getCanonicalName(), bean);
  }

}
