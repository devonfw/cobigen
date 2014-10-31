package ${variables.rootPackage}.${variables.component}.persistence.api.dao;

import java.util.List;
import ${variables.rootPackage}.general.persistence.api.dao.ApplicationDao;
import ${variables.rootPackage}.${variables.component}.persistence.api.${pojo.name};

/**
 * Data access interface for ${variables.entityName} entities
 */
public interface ${variables.entityName}Dao extends ApplicationDao<${pojo.name}> {

}
