package ${variables.rootPackage}.${variables.component}.persistence.api.dao;

import java.util.List;

import ${variables.rootPackage}.general.persistence.api.dao.AbstractDao;
import ${variables.rootPackage}.${variables.component}.persistence.api.${pojo.name};

/**
 * @generated
 */
public interface ${variables.entityName}Dao extends AbstractDao<${pojo.name}, Long> {

    /**
     * Creates a new ${pojo.name} or updates an existing ${pojo.name} 
     *
     * @param ${variables.entityName?uncap_first} existing {@link ${pojo.name}}
     */
    void save(${pojo.name} ${variables.entityName?uncap_first});
  
    /**
     * Deletes the given ${pojo.name}
     *
     * @param ${variables.entityName?uncap_first} existing {@link ${pojo.name}}
     */
    void delete(${pojo.name} ${variables.entityName?uncap_first});
    
    /**
     * Returns a list of all existing ${variables.entityName}s.
     * 
     * @return {@link List} of all existing {@link ${pojo.name}}s.
     */
    List<${pojo.name}> getAll${variables.entityName}s();

}
