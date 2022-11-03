<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import com.devonfw.module.basic.common.api.to.AbstractCto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};

import java.util.List;
import java.util.Set;

/**
 * Composite transport object of ${variables.entityName}
 */
public class ${variables.entityName}Cto extends AbstractCto {

	private static final long serialVersionUID = 1L;

	private ${variables.entityName}Eto ${variables.entityName?uncap_first};

<#list pojo.fields as field>
<#if field.type?contains("Entity")>
   	private ${field.type?replace("Entity|Embeddable","Eto","r")} ${field.name};
</#if>
</#list>

	public ${variables.entityName}Eto get${variables.entityName}() {
		return ${variables.entityName?uncap_first};
	}

	public void set${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first}) {
		this.${variables.entityName?uncap_first} = ${variables.entityName?uncap_first};
	}

<#list pojo.fields as field>
<#if field.type?contains("Entity")>
	<#assign fieldCapName=field.name?cap_first>
	<#assign newType = field.type?replace("Entity|Embeddable","Eto","r")>
	
	public ${newType} <#if field.type='boolean'>is${fieldCapName}<#else>get${fieldCapName}</#if>() {
		return ${field.name};
	}

	public void set${fieldCapName}(${newType} ${field.name}) {
		this.${field.name} = ${field.name};
	}
</#if>
</#list>

}
