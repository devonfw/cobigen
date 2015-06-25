<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import ${variables.rootPackage}.general.common.api.to.AbstractEto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};

import java.util.List;
import java.util.Set;

/**
 * Entity transport object of ${variables.entityName}
 */
public class ${variables.entityName}Eto extends <#if pojo.extendedType.canonicalName=="java.lang.Object" || pojo.extendedType.package!=pojo.package>AbstractEto<#else>${pojo.extendedType.name?replace("Entity","Eto")}</#if> implements ${variables.entityName} {

	private static final long serialVersionUID = 1L;

<#list pojo.fields as attr>
   	private ${attr.type?replace("[^<>,]+Entity","Long","r")} ${attr.name};
</#list>

<#list pojo.fields as attr>
  <#compress>
  <#assign newAttrType=attr.type?replace("[^<>,]+Entity","Long","r")>
	<#assign attrCapName=attr.name?cap_first>
	<#assign suffix="">
	<#if attr.type?contains("Entity") && (attr.canonicalType?contains("java.util.List") || attr.canonicalType?contains("java.util.Set"))>
	   <#assign suffix="Ids">
	   <#-- Handle the standard case. Due to no knowledge of the interface, we have no other possibility than guessing -->
	   <#-- Therefore remove (hopefully) plural 's' from attribute's name to attach it on the suffix -->
	   <#if attrCapName?ends_with("s")>
	     <#assign attrCapName=attrCapName?substring(0, attrCapName?length-1)>
	   </#if>
	<#elseif attr.type?contains("Entity")>
	   <#assign suffix="Id">
	</#if>
  </#compress>

	@Override
	public ${newAttrType} <#if attr.type=='boolean'>is${attrCapName}<#else>get${attrCapName}${suffix}</#if>() {
		return ${attr.name};
	}

	@Override
	public void set${attrCapName}${suffix}(${newAttrType} ${attr.name}) {
		this.${attr.name} = ${attr.name};
	}
</#list>

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        <#if pojo.fields?has_content>
        	<#list pojo.fields as attr>
        		<#if equalsJavaPrimitive(attr.type)>
        result = prime * result + <@boxJavaPrimitive attr.type attr.name/>.hashCode();
        		<#else>
        result = prime * result + ((this.${attr.name} == null) ? 0 : this.${attr.name}.hashCode());
        		</#if>
        	</#list>
        <#else>
        result = prime * result;
        </#if>
        return result;
    }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    // class check will be done by super type EntityTo!
    if (!super.equals(obj)) {
      return false;
    }
    ${variables.entityName}Eto other = (${variables.entityName}Eto) obj;
    <#list pojo.fields as attr>
    <#if equalsJavaPrimitive(attr.type)>
	if(this.${attr.name} != other.${attr.name}) {
		return false;
	}
    <#else>
    if (this.${attr.name} == null) {
      if (other.${attr.name} != null) {
        return false;
      }
    } else if(!this.${attr.name}.equals(other.${attr.name})){
      return false;
    }
    </#if>
    </#list>
    return true;
  }
}
