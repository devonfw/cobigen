<#include '/makros.ftl'>
package ${variables.rootPackage}.${variables.component?lower_case}.dataaccess.api;

import ${variables.rootPackage}.${variables.component?lower_case}.common.api.${variables.entityName};
import ${variables.rootPackage}.general.dataaccess.api.ApplicationPersistenceEntity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Transient;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

<#if model.description??>
/**
 * ${model.description}
 */
 <#else>
 /**
 * Data access object for ${variables.entityName} entities
 */
 </#if>
@Entity
@javax.persistence.Table(name = "${variables.entityName}")
public class ${variables.entityName?cap_first}Entity extends ApplicationPersistenceEntity implements ${variables.entityName} {

  private static final long serialVersionUID = 1L;

<#list model.properties as property>
	<#if property.name != "id">
		<#if property.description??>
	/** ${property.description}	*/
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
	<@definePropertyNameAndType property true/>
	<#if property.isEntity && property.sameComponent>
	 <#if !property.isCollection>
	private ${property.type}Entity ${property.name};
	 <#else>
	private List<${property.type}Entity> ${property.name}; 
   </#if>	
	<#else>
	private ${propType} ${propName};
	</#if>
	</#if>
</#list>

<#list model.properties as property>
	<#if property.name != "id">
	 <@definePropertyNameAndType property true/>
	 <#if property.isEntity && property.sameComponent>
	   <#if !property.isCollection>
  
  public ${property.type?cap_first}Entity get${property.name?cap_first}() {
    return this.${property.name};
  }
  
  public void set${property.name?cap_first}(${property.type}Entity ${propName}) {
    this.${property.name} = ${property.name};
  }
    <#else>
  public void set${property.name?cap_first}(List<${property.type}Entity> ${property.name}) {
    this.${property.name} = ${property.name};
  }
  
  public List<${property.type?cap_first}Entity> get${property.name?cap_first}() {
    return this.${property.name};
  }
    </#if>
	 <#else>
	public ${propType} <#if propType == "boolean">is<#else>get</#if>${propName?cap_first}() {
	  return this.${propName};
	}
	
	public void set${propName?cap_first}(${propType} ${propName}) {
	  this.${propName} = ${propName};
	}
	</#if>
	</#if>
	
	<#if !property.isCollection && property.sameComponent && property.isEntity>
  @Override
  @Transient
  public ${propType} get${propName?cap_first}() {
    
    if (this.${property.name?uncap_first} == null) {
      return null;
    }
    return this.${property.name?uncap_first}.getId();
  }
  
  @Override
  @Transient
  public void set${propName?cap_first}(${propType} ${propName?uncap_first}) {

    if (${propName?uncap_first} == null) {
      this.${property.name?uncap_first} = null;
    } else {
      ${property.type?cap_first}Entity ${property.name?uncap_first} = new ${property.type?cap_first}Entity();
      ${property.name?uncap_first}.setId(${propName?uncap_first});
      this.${property.type?uncap_first} = ${property.type?uncap_first};
    }
  }
  </#if>
</#list>

}
