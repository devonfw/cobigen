<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import ${variables.rootPackage}.general.common.api.to.AbstractCto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};

import java.util.List;
import java.util.Set;

/**
 * Composite transport object of ${variables.entityName}
 */
public class ${variables.entityName}Cto extends AbstractCto {

	private static final long serialVersionUID = 1L;

	private ${variables.entityName}Eto ${variables.entityName?uncap_first};

<#list pojo.attributes as attr>
<#if attr.type?contains("Entity")>
   	private ${attr.type?replace("Entity","Eto")} ${attr.name};
</#if>
</#list>

	public ${variables.entityName}Eto get${variables.entityName}() {
		return ${variables.entityName?uncap_first};
	}

	public void set${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first}) {
		this.${variables.entityName?uncap_first} = ${variables.entityName?uncap_first};
	}

<#list pojo.attributes as attr>
<#if attr.type?contains("Entity")>
	<#assign attrCapName=attr.name?cap_first>

	public ${attr.type?replace("Entity","Eto")} <#if attr.type='boolean'>is${attrCapName}<#else>get${attrCapName}</#if>() {
		return ${attr.name};
	}

	public void set${attrCapName}(${attr.type?replace("Entity","Eto")} ${attr.name}) {
		this.${attr.name} = ${attr.name};
	}
</#if>
</#list>

}
