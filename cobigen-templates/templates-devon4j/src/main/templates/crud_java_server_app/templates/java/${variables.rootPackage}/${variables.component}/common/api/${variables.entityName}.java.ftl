<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;

<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null"> 
import ${variables.rootPackage}.general.common.api.ApplicationComposedKeyEntity;
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>

public interface ${variables.entityName} extends <#if compositeIdTypeVar != "null">ApplicationComposedKeyEntity<${compositeIdTypeVar}><#else>ApplicationEntity</#if> {

	<@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion false true/>

}
