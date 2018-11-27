<#include '/makros.ftl'>
package ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to;

import com.devonfw.module.basic.common.api.to.AbstractEto;
import ${variables.rootPackage}.${variables.component?lower_case}.common.api.${variables.entityName};

import java.util.List;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Entity transport object of ${variables.entityName}
 */
public class ${variables.entityName}Eto extends AbstractEto implements ${variables.entityName} {

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
	private ${propType} ${propName};
	</#if>
</#list>

<#list model.properties as property>
	<#if property.name != "id">
	<@definePropertyNameAndType property true/>
	public ${propType} <#if propType=='boolean'>is${propName?cap_first}<#else>get${propName?cap_first}</#if>() {
		return this.${propName};
	}
	
	public void set${propName?cap_first}(${propType} ${propName}) {
		this.${propName} = ${propName};
	}
	</#if>
</#list>

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      <#list model.properties as property>
        <#if (!property.isCollection) && (property.name != "id")>
        <@definePropertyNameAndType property true/>
        <#if JavaUtil.equalsJavaPrimitive(propType)>
    result = prime * result + (${JavaUtil.castJavaPrimitives(propType,propName)}.hashCode());
        <#else>
	  result = prime * result + ((this.${propName} == null) ? 0 : this.${propName}.hashCode());
	      </#if>
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
      <#if (!property.isCollection) && (property.name != "id")>
      <@definePropertyNameAndType property true/>
        <#if JavaUtil.equalsJavaPrimitive(propType)>
  if(this.${propName} != other.${propName}) {
    return false;
  }
        <#else>
	if (this.${propName} == null) {
		if (other.${propName} != null) {
		return false;
		}
	} else if(!this.${propName}.equals(other.${propName})){
		return false;
	}
	     </#if>
      </#if>
    </#list>
    return true;
  }
}

