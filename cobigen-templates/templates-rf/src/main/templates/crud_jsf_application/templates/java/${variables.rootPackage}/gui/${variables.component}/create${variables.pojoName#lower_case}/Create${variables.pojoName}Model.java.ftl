package ${variables.rootPackage}.gui.${variables.component}.create${pojo.name?lower_case};

import java.io.Serializable;

/**
 * Model class for create${pojo.name?lower_case}.
 */
public class Create${pojo.name}Model implements Serializable {

<#list pojo.fields as attr>
	<#if attr.javaDoc[0]??>
    ${attr.javaDoc}
    </#if>
	private ${attr.type} ${attr.name};
</#list>

<#list pojo.fields as attr>
	<#assign attrCapName=attr.name?cap_first>
	<#assign getter='get'+$attrCapName>
	<#assign getterBool='is'+$attrCapName>

	<#if doc["/doc/pojo/methods[name=$getter or name=$getterBool]"].javaDoc.comment??>
    /**${doc["/doc/pojo/methods[name=$getter or name=$getterBool]"].javaDoc.comment}*/
    </#if>
	public ${attr.type} <#if attr.type='boolean'>is${attrCapName}<#else>get${attrCapName}</#if>() {
		return ${attr.name};
	}

	<#assign setter='set'+$attrCapName>
	<#if doc["/doc/pojo/methods[name=$setter]"].javaDoc.comment??>
    /**${doc["/doc/pojo/methods[name=$setter]"].javaDoc.comment}*/
    </#if>
	public void set${attrCapName}(${attr.type} ${attr.name}) {
		this.${attr.name} = ${attr.name};
	}
</#list>
}
