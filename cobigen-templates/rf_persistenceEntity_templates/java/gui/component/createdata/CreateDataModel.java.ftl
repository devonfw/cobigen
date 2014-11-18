package ${variables.rootPackage}.gui.${variables.component}.create${pojo.name?lower_case};

import java.io.Serializable;

/**
 * model class for create${pojo.name?lower_case}
 * @generated
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

	<#if doc["/doc/pojo/methods[name=$getter or name=$getterBool]"].javaDoc[0]??>
    /**${doc["/doc/pojo/methods[name=$getter or name=$getterBool]"].javaDoc}*/
    </#if>
	public ${attr.type} <#if attr.type='boolean'>is${attrCapName}<#else>get${attrCapName}</#if>() {
		return ${attr.name};
	}

	<#assign setter='set'+$attrCapName>
	<#if doc["/doc/pojo/methods[name=$setter]"].javaDoc[0]??>
    /**${doc["/doc/pojo/methods[name=$setter]"].javaDoc}*/
    </#if>
	public void set${attrCapName}(${attr.type} ${attr.name}) {
		this.${attr.name} = ${attr.name};
	}
</#list>
}
