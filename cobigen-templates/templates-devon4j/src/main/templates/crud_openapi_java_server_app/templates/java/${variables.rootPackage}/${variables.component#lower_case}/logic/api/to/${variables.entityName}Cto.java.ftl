package ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to;

import com.devonfw.module.basic.common.api.to.AbstractCto;
import ${variables.rootPackage}.${variables.component?lower_case}.common.api.${variables.entityName};

import java.util.List;
import java.util.Set;

/**
 * Composite transport object of ${variables.entityName}
 */
public class ${variables.entityName}Cto extends AbstractCto {

	private static final long serialVersionUID = 1L;

	private ${variables.entityName}Eto ${variables.entityName?uncap_first};

<#list model.properties as property>
   <#if property.isEntity && property.sameComponent>
    <#if property.isCollection>
   	private List<${property.type}Eto> ${property.type?uncap_first}s;
    <#else>
    private ${property.type}Eto ${property.type?uncap_first};
    </#if>
   </#if>   
</#list>
	public ${variables.entityName}Eto get${variables.entityName}() {
		return ${variables.entityName?uncap_first};
	}

	public void set${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first}) {
		this.${variables.entityName?uncap_first} = ${variables.entityName?uncap_first};
	}
<#list model.properties as property>
   <#if property.isEntity && property.sameComponent>
	<#assign fieldCapName=property.name?cap_first>
	<#assign newType = property.type?replace("Entity|Embeddable","","r")>
	
	public ${newType}Eto <#if property.type='boolean'>is${fieldCapName}<#else>get${fieldCapName}</#if>() {
		return ${property.name};
	}

	public void set${fieldCapName}(${newType}Eto ${property.name}) {
		this.${property.name} = ${property.name};
	}
</#if>
</#list>
}
