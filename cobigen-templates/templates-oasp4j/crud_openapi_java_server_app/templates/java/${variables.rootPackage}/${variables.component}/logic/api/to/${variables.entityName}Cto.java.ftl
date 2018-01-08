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

<#list model.relationShips as rs>
   <#if rs.sameComponent>
   	private <#if rs.type == "manytomany" || rs.type == "onetomany">List<${rs.entity}Eto><#else>${rs.entity}Eto</#if> ${rs.entity?uncap_first}<#if rs.type == "manytomany" || rs.type == "onetomany">s</#if>;

   </#if>   
</#list>
	public ${variables.entityName} get${variables.entityName}() {
		return ${variables.entityName?uncap_first};
	}

	public void set${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first}) {
		this.${variables.entityName?uncap_first} = ${variables.entityName?uncap_first};
	}

<#list model.relationShips as rs>
	<#if rs.sameComponent>
	public <#if rs.type == "manytomany" || rs.type == "onetomany">List<${rs.entity}Eto> get${rs.entity}s<#else>${rs.entity}Eto get${rs.entity}</#if>() {
		return this.${rs.entity?uncap_first}<#if rs.type == "manytomany" || rs.type == "onetomany">s</#if>;
	}
	
	public void <#if rs.type == "manytomany" || rs.type == "onetomany">set${rs.entity}s(List<${rs.entity}Eto> ${rs.entity?uncap_first}s<#else> set${rs.entity}(${rs.entity}Eto ${rs.entity?uncap_first}</#if>) {
		this.${rs.entity?uncap_first}<#if rs.type == "manytomany" || rs.type == "onetomany">s</#if> = ${rs.entity?uncap_first}<#if rs.type == "manytomany" || rs.type == "onetomany">s</#if>;
	}
	</#if>
</#list>

}
