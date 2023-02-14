package com.devonfw.cobigen.api.template.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.model.CobiGenModel;

/**
 * This is the central provider for any {@link CobiGenGenerator}.
 *
 * @see #getGenerator(String)
 */
public class CobiGenGeneratorProvider {

  private static final Logger LOG = LoggerFactory.getLogger(CobiGenGeneratorProvider.class);

  private static final CobiGenGeneratorProvider INSTANCE = new CobiGenGeneratorProvider();

  private final Map<String, CobiGenGenerator> generators;

  private CobiGenGeneratorProvider() {

    super();
    this.generators = new HashMap<>();
    ServiceLoader<CobiGenGenerator> loader = ServiceLoader.load(CobiGenGenerator.class);
    for (CobiGenGenerator generator : loader) {
      String simpleName = generator.getClass().getSimpleName();
      CobiGenGenerator duplicate = this.generators.put(simpleName, generator);
      if (duplicate != null) {
        LOG.warn("Duplicate Generator name {}: Replaced generator {} with {} assuming override.", simpleName,
            duplicate.getClass().getName(), generator.getClass().getName());
      }
    }
  }

  /**
   * @param simpleName the {@link Class#getSimpleName() simple name} of the {@link CobiGenGenerator} to invoke.
   * @param model the {@link CobiGenModel}.
   * @return the generated code.
   */
  public String generate(String simpleName, CobiGenModel model) {

    CobiGenGenerator generator = getGenerator(simpleName);
    try {
      return generator.generate(model);
    } catch (Throwable e) {
      throw new IllegalStateException(simpleName + " failed.", e);
    }
  }

  /**
   * @param simpleName the {@link Class#getSimpleName() simple name} of the {@link CobiGenGenerator} to invoke.
   * @param model the {@link CobiGenModel}.
   * @param code the {@link Appendable} where to {@link Appendable#append(CharSequence) write} the generated code to.
   */
  public void generate(String simpleName, CobiGenModel model, Appendable code) {

    CobiGenGenerator generator = getGenerator(simpleName);
    try {
      generator.generate(model, code);
    } catch (Throwable e) {
      throw new IllegalStateException(simpleName + " failed.", e);
    }
  }

  /**
   * @param simpleName the {@link Class#getSimpleName() simple name} of the requested {@link CobiGenGenerator}.
   * @return {@code true} if such {@link CobiGenGenerator} is registered and {@link #getGenerator(String) retrievable},
   *         {@code false} otherwise.
   */
  boolean hasGenerator(String simpleName) {

    return this.generators.containsKey(simpleName);
  }

  /**
   * @param simpleName the {@link Class#getSimpleName() simple name} of the requested {@link CobiGenGenerator}.
   * @return the requested {@link CobiGenGenerator}.
   * @throws IllegalArgumentException if no such generator exists.
   */
  public CobiGenGenerator getGenerator(String simpleName) {

    CobiGenGenerator generator = this.generators.get(simpleName);
    if (generator == null) {
      throw new IllegalArgumentException("Undefined generator " + simpleName);
    }
    return generator;
  }

  /**
   * @return the singleton instance.
   */
  public static CobiGenGeneratorProvider get() {

    return INSTANCE;
  }

}
