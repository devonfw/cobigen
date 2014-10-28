package ${variables.rootPackage}.${variables.component}.persistence.api;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.general.persistence.api.ApplicationPersistenceEntity;

import javax.persistence.Entity;

/**
 * Data access object for ${variables.entityName} entities
 */
@Entity(name = "${variables.entityName}")
@javax.persistence.Table(name = "${variables.entityName}")
public class ${pojo.name} extends ApplicationPersistenceEntity implements ${variables.entityName} {

  private static final long serialVersionUID = 1L;

}
