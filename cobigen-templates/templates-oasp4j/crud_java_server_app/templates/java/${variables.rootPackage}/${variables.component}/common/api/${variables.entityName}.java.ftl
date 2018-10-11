<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;

public interface ${variables.entityName} extends ApplicationEntity {

	<@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion false true/>

}
