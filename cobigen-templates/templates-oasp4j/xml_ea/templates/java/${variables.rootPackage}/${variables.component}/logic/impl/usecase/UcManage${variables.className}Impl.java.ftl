package ${variables.rootPackage}.${variables.component}.logic.impl.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcManage${variables.className};
import ${variables.rootPackage}.${variables.component}.logic.base.usecase.Abstract${variables.className}Uc;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.className}Entity;

import java.util.Objects;

import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case implementation for modifying and deleting ${variables.className}s
 */
@Named
@UseCase
@Validated
@Transactional
public class UcManage${variables.className}Impl extends Abstract${variables.className}Uc implements UcManage${variables.className} {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(UcManage${variables.className}Impl.class);

  @Override
  public boolean delete${variables.className}(Long ${variables.className?uncap_first}Id) {

    ${variables.className}Entity ${variables.className?uncap_first} = get${variables.className}Dao().find(${variables.className?uncap_first}Id);
    get${variables.className}Dao().delete(${variables.className?uncap_first});
    LOG.debug("The ${variables.className?uncap_first} with id '{}' has been deleted.", ${variables.className?uncap_first}Id);
    return true;
  }

  @Override
  public ${variables.className}Eto save${variables.className}(${variables.className}Eto ${variables.className?uncap_first}) {

   Objects.requireNonNull(${variables.className?uncap_first}, "${variables.className?uncap_first}");

	 ${variables.className}Entity ${variables.className?uncap_first}Entity = getBeanMapper().map(${variables.className?uncap_first}, ${variables.className}Entity.class);

   //initialize, validate ${variables.className?uncap_first}Entity here if necessary
   ${variables.className}Entity resultEntity = get${variables.className}Dao().save(${variables.className?uncap_first}Entity);
   LOG.debug("${variables.className} with id '{}' has been created.",resultEntity.getId());
   return getBeanMapper().map(resultEntity, ${variables.className}Eto.class);
  }
}
