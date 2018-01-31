<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;
import java.util.Set;

public interface ${variables.entityName} extends ApplicationEntity {

	<@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion false true/>

}
