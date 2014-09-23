package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.base.Abstract${variables.entityName}Uc;
import ${variables.rootPackage}.${variables.component}.persistence.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.logic.base.UcManage${variables.entityName};

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case implementation for modifying and deleting ${variables.entityName}s
 */
@Named
public class UcManage${variables.entityName}Impl extends Abstract${variables.entityName}Uc implements UcManage${variables.entityName} {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(UcManage${variables.entityName}Impl.class);

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean delete${variables.entityName}(Long ${variables.entityName?uncap_first}Id) {

    ${variables.entityName}Entity ${variables.entityName?uncap_first} = get${variables.entityName}Dao().find(${variables.entityName?uncap_first}Id);
    get${variables.entityName}Dao().delete(${variables.entityName?uncap_first});
    LOG.debug("The ${variables.entityName?uncap_first} with id '{}' has been deleted.", ${variables.entityName?uncap_first}Id);
    return true;
  }
  
    /**
   * {@inheritDoc}
   */
  @Override
  public ${variables.entityName}Eto create${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first}) {

	${variables.entityName}Entity ${variables.entityName?uncap_first}Entity = getBeanMapper().map(${variables.entityName?uncap_first}, ${variables.entityName}Entity.class);
    get${variables.entityName}Dao().save(${variables.entityName?uncap_first}Entity);
    LOG.debug("${variables.entityName} with id '{}' has been created.", ${variables.entityName?uncap_first}Entity.getId());
    return getBeanMapper().map(${variables.entityName?uncap_first}Entity, ${variables.entityName}Eto.class);
  }
}
