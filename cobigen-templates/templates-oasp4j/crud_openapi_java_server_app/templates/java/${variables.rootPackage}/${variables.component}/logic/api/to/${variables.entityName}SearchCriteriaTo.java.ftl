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
	private ${OaspUtil.getOaspTypeFromOpenAPI(property, false)} ${property.name};
		</#if>
	</#if>
</#list>
<#list model.relationShips as rs>
	<#if rs.type != "manytomany" && rs.type != "onetomany">
    private Long ${rs.entity?uncap_first}Id;
	</#if>
</#list>

  /**
  * The constructor.
  */
  public ${variables.entityName}SearchCriteriaTo() {

    super();
  }

<#list model.properties as property>
	<#if property.name != "id">
		<#if !property.isCollection && !property.isEntity>
	public ${OaspUtil.getOaspTypeFromOpenAPI(property, false)} get${property.name?cap_first}() {
		return this.${property.name};
	}
	
	public void set${property.name?cap_first}(${OaspUtil.getOaspTypeFromOpenAPI(property, false)} ${property.name}) {
		this.${property.name} = ${property.name};
	}
		</#if>
	</#if>
</#list>

<#list model.properties as property>
	<#if !property.isCollection && property.isEntity>
	public void set${property.name?cap_first}Id(${OaspUtil.getOaspTypeFromOpenAPI(property, false)} ${property.name}Id) {
		this.${property.name}Id = ${property.name}Id;
	}
	
	public ${OaspUtil.getOaspTypeFromOpenAPI(property, false)} get${property.name?cap_first}Id() {
        return this.${property.name}Id;
	}
	<#else>
	
	</#if>
</#list>

<#list model.relationShips as rs>
	<#if rs.type != "manytomany" && rs.type != "onetomany">
    public void set${rs.entity}Id(Long ${rs.entity?uncap_first}Id) {
      this.${rs.entity?uncap_first}Id = ${rs.entity?uncap_first}Id;
	}

	public Long get${rs.entity}Id() {
		return this.${rs.entity?uncap_first}Id;
	}
	</#if>
</#list>

}
