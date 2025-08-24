package com.devonfw.cobigen.api.template.out;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mmm.base.text.CaseHelper;

/**
 * Factory for {@link CobiGenOutput}.
 */
public final class CobiGenOutputFactory {

  private static final Logger LOG = LoggerFactory.getLogger(CobiGenOutputFactory.class);

  private static final CobiGenOutputFactory INSTANCE = new CobiGenOutputFactory();

  private final Map<String, CobiGenOutputTypeFactory> typeFactories;

  private CobiGenOutputFactory() {

    super();
    this.typeFactories = new HashMap<>();
    ServiceLoader<CobiGenOutputTypeFactory> loader = ServiceLoader.load(CobiGenOutputTypeFactory.class);
    for (CobiGenOutputTypeFactory factory : loader) {
      String type = factory.getType();
      CobiGenOutputTypeFactory duplicate = this.typeFactories.put(type, factory);
      if (duplicate != null) {
        LOG.warn("Duplicate CobiGenOutputTypeFactory for {}: replacing {} with {}", type,
            duplicate.getClass().getName(), factory.getClass().getName());
      }
    }
    if (this.typeFactories.isEmpty()) {
      LOG.error("No CobiGenOutputTypeFactory was registered - check plugins.");
    } else {
      LOG.debug("Registered {} instance(s) of CobiGenOutputTypeFactory: {}", this.typeFactories.size(),
          this.typeFactories);
    }
  }

  /**
   * @param filename the filename of the output to generate.
   * @return the {@link CobiGenOutput}.
   */
  public CobiGenOutput create(String filename) {

    CobiGenOutputTypeFactory factory = null;
    int lastDot = filename.lastIndexOf('.');
    String extension = "";
    if (lastDot > 0) {
      extension = CaseHelper.toLowerCase(filename.substring(lastDot + 1));
      factory = this.typeFactories.get(extension);
      if (factory == null) {
        for (CobiGenOutputTypeFactory typeFactory : this.typeFactories.values()) {
          if (typeFactory.isResponsible(extension)) {
            factory = typeFactory;
            break;
          }
        }
      }
    }
    if (factory == null) {
      LOG.info("No output factory found for file {} with extension {} - factories registered for extensions {}",
          filename, extension, this.typeFactories.keySet());
      return null;
    }
    CobiGenOutput output = factory.create(filename);
    LOG.debug("Created output of {} for file {} via factory {}", output.getClass(), filename, factory.getClass());
    return output;
  }

  /**
   * @return the singleton instance.
   */
  public static CobiGenOutputFactory get() {

    return INSTANCE;
  }

}
