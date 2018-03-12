package ${variables.rootPackage}.${variables.component}.logic.api.to;

import io.oasp.module.jpa.common.api.to.SearchCriteriaTo;
import java.util.List;
import java.util.Set;

/**
 * This is the {@link SearchCriteriaTo search criteria} {@link net.sf.mmm.util.transferobject.api.TransferObject TO}
 * used to find {@link ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName}}s.
 *
 */
public class ${variables.entityName}SearchCriteriaTo extends SearchCriteriaTo {

  private static final long serialVersionUID = 1L;
  
<#list model.properties as property>
	<#if property.name != "id">
		<#if !property.isCollection>
		  <#if property.isEntity>
	private Long ${rs.entity?uncap_first}Id;
		  <#else>
	private ${OpenApiUtil.toJavaType(property, false)} ${property.name};
	    </#if>
		</#if>
	</#if>
</#list>
<#-- <#list model.relationShips as rs>
	<#if rs.type != "manytomany" && rs.type != "onetomany">
    private Long ${rs.entity?uncap_first}Id;
	</#if>
</#list>-->

  /**
  * The constructor.
  */
  public ${variables.entityName}SearchCriteriaTo() {

    super();
  }

<#list model.properties as property>
	<#if property.name != "id">
		<#if !property.isCollection && !property.isEntity>
		  <#if property.isEntity>
  public void set${property.name?cap_first}Id(${OpenApiUtil.toJavaType(property, false)} ${property.name}Id) {
    this.${property.name}Id = ${property.name}Id;
  }
  
  public ${OpenApiUtil.toJavaType(property, false)} get${property.name?cap_first}Id() {
        return this.${property.name}Id;
  }		  
		  <#else>
	public ${OpenApiUtil.toJavaType(property, false)} get${property.name?cap_first}() {
		return this.${property.name};
	}
	
	public void set${property.name?cap_first}(${OpenApiUtil.toJavaType(property, false)} ${property.name}) {
		this.${property.name} = ${property.name};
	}
	   </#if>
		</#if>
	</#if>
</#list>

<#-- <#list model.relationShips as rs>
	<#if rs.type != "manytomany" && rs.type != "onetomany">
  public void set${rs.entity}Id(Long ${rs.entity?uncap_first}Id) {
      this.${rs.entity?uncap_first}Id = ${rs.entity?uncap_first}Id;
	}

	public Long get${rs.entity}Id() {
		return this.${rs.entity?uncap_first}Id;
	}
	</#if>
</#list>-->

}
