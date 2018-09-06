<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import ${variables.rootPackage}.general.common.api.to.AbstractCto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
<#list pojo.fields as field>
<#if field.type?contains("Entity")>
import ${variables.rootPackage}.${field.type?replace("Set<","","r")?replace("Entity|Embeddable","managment","r")?replace(">","","r")?uncap_first}.logic.api.to.${field.type?replace("Set<","","r")?replace("Entity|Embeddable","Cto","r")?replace(">","","r")};
</#if>
</#list> 


import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Composite transport object of ${variables.entityName}
 */
public class ${variables.entityName}Cto extends AbstractCto {

	private static final long serialVersionUID = 1L;

	private ${variables.entityName}Eto ${variables.entityName?uncap_first};

<#list pojo.fields as field>
<#if field.type?contains("Entity") && field.type?starts_with("Set<")>
   	private ${field.type?replace("Entity|Embeddable","Cto","r")} ${field.name} = new HashSet<>();
<#elseif field.type?contains("Entity")>
    private ${field.type?replace("Entity|Embeddable","Cto","r")} ${field.name};
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
	<#assign newType = field.type?replace("Entity|Embeddable","Cto","r")>
	
	public ${newType} <#if field.type='boolean'>is${fieldCapName}<#else>get${fieldCapName}</#if>() {
		return ${field.name};
	}

	public void set${fieldCapName}(${newType} ${field.name}) {
		this.${field.name} = ${field.name};
	}
</#if>
</#list>


 @Override
    public int hashCode() {
       
        return this.${variables.entityName?uncap_first}.hashCode();
    }

  @Override
  public boolean equals(Object obj) {

  return this.${variables.entityName?uncap_first}.equals(obj);
  }

}
