<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import ${variables.rootPackage}.general.common.api.to.AbstractCto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.className};

import java.util.List;
import java.util.Set;

/**
 * Composite transport object of ${variables.className}
 */
public class ${variables.className}Cto extends AbstractCto {

	private static final long serialVersionUID = 1L;

	private ${variables.className}Eto ${variables.className?uncap_first};

<#list elemDoc["self::node()/ownedAttribute"] as field>
<#assign fieldType=field["type/@xmi:idref"]?replace("EAJava_","")>
<#if fieldType?contains("Entity")>
   	private ${fieldType?replace("Entity|Embeddable","Eto","r")} ${field["@name"]};
</#if>
</#list>

	public ${variables.className}Eto get${variables.className}() {
		return ${variables.className?uncap_first};
	}

	public void set${variables.className}(${variables.className}Eto ${variables.className?uncap_first}) {
		this.${variables.className?uncap_first} = ${variables.className?uncap_first};
	}

<#list elemDoc["self::node()/ownedAttribute"] as field>
<#assign fieldType=field["type/@xmi:idref"]?replace("EAJava_","")>
<#if fieldType?contains("Entity")>
	<#assign fieldCapName=field["@name"]?cap_first>
	<#assign newType = fieldType?replace("Entity|Embeddable","Eto","r")>
	
	public ${newType} <#if fieldType='boolean'>is${fieldCapName}<#else>get${fieldCapName}</#if>() {
		return ${field["@name"]};
	}

	public void set${fieldCapName}(${newType} ${field["@name"]}) {
		this.${field["@name"]} = ${field["@name"]};
	}
</#if>
</#list>

}
