package ${variables.rootPackage}.${variables.component}.persistence.api;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};

import javax.persistence.Entity;

import net.sf.mmm.util.entity.api.PersistenceEntity;

/**
 * Data access object for ${variables.entityName} entities
 */
@Entity(name = "${variables.entityName}")
@javax.persistence.Table(name = "${variables.entityName}")
public class ${pojo.name} extends ${variables.domain?cap_first}PersistenceEntity implements PersistenceEntity<Long>, ${variables.entityName} {

  private static final long serialVersionUID = 1L;

}
