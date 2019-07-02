package ${variables.rootPackage}.${variables.component}.logic.api;

import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcManage${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Cto;

/**
 * Interface for ${variables.component?cap_first} component.
 */
public interface ${variables.component?cap_first} extends UcFind${variables.entityName} {

}
