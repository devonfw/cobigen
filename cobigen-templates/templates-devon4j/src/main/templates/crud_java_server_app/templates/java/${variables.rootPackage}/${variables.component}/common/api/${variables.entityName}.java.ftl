<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;
<#assign compositeIdVar = false>
<#list pojo.fields as field>
	<#if field.type?starts_with("Composite")>
		<#assign compositeIdVar = true>
		<#assign compositeIdTypeVar = field.type>
	</#if>
</#list>
<#if compositeIdVar = true>
import ${variables.rootPackage}.general.common.api.ApplicationComposedKeyEntity;
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>

public interface ${variables.entityName} extends <#if compositeIdVar = true>ApplicationComposedKeyEntity<${compositeIdTypeVar}><#else>ApplicationEntity</#if> {
	<@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion false true/>

}
