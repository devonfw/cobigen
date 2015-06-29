package ${variables.rootPackage}.general.common.base;

import io.oasp.module.beanmapping.common.api.BeanMapper;

import javax.inject.Inject;

/**
 * This abstract class wraps advanced functionality according dozer mappings
 */
public abstract class AbstractBeanMapperSupport {

  /** @see #getBeanMapper() */
  private BeanMapper beanMapper;

  /**
   * @param beanMapper is the {@link BeanMapper} to {@link Inject}
   */
  @Inject
  public void setBeanMapper(BeanMapper beanMapper) {

    this.beanMapper = beanMapper;
  }

  /**
   * @return the {@link BeanMapper} instance.
   */
  protected BeanMapper getBeanMapper() {

    return this.beanMapper;
  }

}
