<#-- Copyright © Capgemini 2013. All rights reserved. -->
package ${variables.rootPackage}.core.${variables.component}.entity;

import java.io.Serializable;

/**
 * ${pojo.name} entity
 * @generated
 */
public class ${pojo.name} implements Serializable {

<#list pojo.attributes as attr>
	<#if attr.javaDoc[0]??>
    ${attr.javaDoc}
    </#if>
	private ${attr.type} ${attr.name};
</#list>

<#list pojo.attributes as attr>
	<#assign attrCapName=attr.name?cap_first>
	<#assign getter='get'+$attrCapName>
	<#assign getterBool='is'+$attrCapName>
	
	<#if doc["/doc/pojo/methods[name=$getter or name=$getterBool]"].javaDoc[0]??>
    ${doc["/doc/pojo/methods[name=$getter or name=$getterBool]"].javaDoc}
    </#if>
	public ${attr.type} <#if attr.type='boolean'>is${attrCapName}<#else>get${attrCapName}</#if>() {
		return ${attr.name};
	}
	
	<#assign setter='set'+$attrCapName>
	<#if doc["/doc/pojo/methods[name=$setter]"].javaDoc[0]??>
    ${doc["/doc/pojo/methods[name=$setter]"].javaDoc}
    </#if>
	public void set${attrCapName}(${attr.type} ${attr.name}) {
		this.${attr.name} = ${attr.name};
	}
</#list>
}
