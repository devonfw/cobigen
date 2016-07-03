package ${variables.rootPackage}.core.${variables.component}.to;

import java.io.Serializable;

/**
 * ${pojo.name} entity
 * @generated
 */
public class ${pojo.name}To implements Serializable {

	/**
     *
     */
    private static final long serialVersionUID = 1L;

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
