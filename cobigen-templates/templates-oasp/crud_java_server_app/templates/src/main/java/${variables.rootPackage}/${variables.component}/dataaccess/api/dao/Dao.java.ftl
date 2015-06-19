package ${variables.rootPackage}.${variables.component}.dataaccess.api.dao;

import java.util.List;
import ${variables.rootPackage}.general.dataaccess.api.dao.ApplicationDao;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${pojo.name};

/**
 * Data access interface for ${variables.entityName} entities
 */
public interface ${variables.entityName}Dao extends ApplicationDao<${pojo.name}> {

}
