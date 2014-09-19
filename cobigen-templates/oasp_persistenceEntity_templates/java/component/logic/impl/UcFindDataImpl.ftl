package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.base.Abstract${variables.entityName}Uc;
import ${variables.rootPackage}.${variables.component}.logic.base.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.persistence.api.${variables.entityName}Entity;

import java.util.List;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case implementation for searching, filtering and getting ${variables.entityName}s 
 */
@Named
public class UcFind${variables.entityName}Impl extends Abstract${variables.entityName}Uc implements UcFind${variables.entityName} {

	/** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(UcFind${variables.entityName}Impl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public ${variables.entityName}Eto get${variables.entityName}(Long id) {
        LOG.debug("Get ${variables.entityName} with id {} from database.", id);
        return getBeanMapper().map(this.${variables.entityName?uncap_first}Dao.findIfExists(id), ${variables.entityName}Eto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<${variables.entityName}Eto> getAll${variables.entityName}s() {
        LOG.debug("Get all ${variables.entityName}s from database.");
        List<${variables.entityName}Entity> ${variables.entityName?uncap_first}s = this.${variables.entityName?uncap_first}Dao.getAll${variables.entityName}s();
        return getBeanMapper().mapList(${variables.entityName?uncap_first}s, ${variables.entityName}Eto.class);
    }

}
