<#include '/makros.ftl'>
package ${variables.rootPackage}.${variables.component?lower_case}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;
import java.util.List;
import java.util.Set;

import java.math.BigDecimal;

public interface ${variables.entityName} extends ApplicationEntity {

<#list model.properties as property>
	<#if property.name != "id" && !property.isCollection>
	<@definePropertyNameAndType property true/>
	public ${propType} <#if propType == "boolean">is<#else>get</#if>${propName?cap_first}();
	
	public void set${propName?cap_first}(${propType} ${propName});
	</#if>
</#list>

}
