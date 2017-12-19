package ${variables.rootPackage}.${variables.component}.logic.api.to;

import ${variables.rootPackage}.general.common.api.to.AbstractEto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};

import java.util.List;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
/**
 * Entity transport object of ${variables.entityName}
 */
public class ${variables.entityName}Eto extends AbstractEto implements ${variables.entityName} {

	private static final long serialVersionUID = 1L;
<#list model.properties as property>
  <#if property.name != "id">
		<#if !property.isCollection>
			<#if property.description??>
	/**
	* ${property.description}
	*/
			</#if>
			<#if property.constraints.maximum??>
	@Max(${property.constraints.maximum})
			</#if>
			<#if property.constraints.minimum??>
	@Min(${property.constraints.minimum})
			</#if>
			<#if property.required>
	${property.required?c}
	@NotNull
			</#if>
			<#if property.constraints.maxLength?? && !property.constraints.minLength??>
	@Size(max = ${property.constraints.maxLength})
			<#elseif !property.constraints.maxLength?? && property.constraints.minLength??>
	@Size(min = ${property.constraints.minLength})
			<#elseif property.constraints.maxLength?? && property.constraints.minLength??>
	@Size(max = ${property.constraints.maxLength}, min = ${property.constraints.minLength})	
			</#if>
	private ${OaspUtil.getOaspTypeFromOpenAPI(property, false)} ${property.name};
		</#if>
	</#if>
</#list>
<#list model.relationShips as rs>
	<#if rs.type != "manytomany" && rs.type != "onetomany">
    private Long ${rs.entity?uncap_first}Id;
	</#if>
</#list>

<#list model.properties as property>
	<#if property.name != "id">
		<#if !property.isCollection && !property.isEntity>
	public ${OaspUtil.getOaspTypeFromOpenAPI(property false)} get${property.name?cap_first}() {
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
	@Override
	public void set${property.name?cap_first}Id(${OaspUtil.getOaspTypeFromOpenAPI(property, false)} ${property.name}Id) {
		this.${property.name}Id = ${property.name}Id;
	}
	
	@Override
	public ${OaspUtil.getOaspTypeFromOpenAPI(property, false)} get${property.name?cap_first}Id() {
        return this.${property.name}Id;
	}
	<#else>
	
	</#if>
</#list>

<#list model.relationShips as rs>
	<#if rs.type != "manytomany" && rs.type != "onetomany">
	@Override
    public void set${rs.entity}Id(Long ${rs.entity?uncap_first}Id) {
      this.${rs.entity?uncap_first}Id = ${rs.entity?uncap_first}Id;
	}

	@Override
	public Long get${rs.entity}Id() {
		return this.${rs.entity?uncap_first}Id;
	}
	</#if>
</#list>

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      <#list model.properties as property>
        <#if !property.isCollection>
	  result = prime * result +((this.${property.name} == null) ? 0 : this.${property.name}.hashCode());
  		</#if>
      </#list>
      <#list model.relationShips as rs>
	    <#if rs.type != "manytomany" && rs.type != "onetomany">
	  result = prime * result +((this.${rs.entity?uncap_first}Id == null) ? 0 : this.${rs.entity?uncap_first}Id.hashCode());
		</#if>
	  </#list>
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
    <#list model.properties as property>
      <#if !property.isCollection>
	if (this.${property.name} == null) {
		if (other.${property.name} != null) {
		return false;
		}
	} else if(!this.${property.name}.equals(other.${property.name})){
		return false;
	}
      </#if>
    </#list>
	<#list model.relationShips as rs>
	  <#if rs.type != "manytomany" && rs.type != "onetomany">
	if (this.${rs.entity?uncap_first}Id == null) {
		if (other.${rs.entity?uncap_first}Id != null) {
		return false;
		}
	} else if(!this.${rs.entity?uncap_first}Id.equals(other.${rs.entity?uncap_first}Id)){
		return false;
	}
	  </#if>
	</#list>
    return true;
  }
}

