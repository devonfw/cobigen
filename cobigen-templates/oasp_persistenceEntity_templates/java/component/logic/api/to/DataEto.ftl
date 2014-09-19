<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import ${variables.rootPackage}.general.logic.base.AbstractEto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};

/**
 * Entity transport object of ${variables.entityName}
 */
public class ${variables.entityName}Eto extends AbstractEto implements ${variables.entityName} {

	private static final long serialVersionUID = 1L;

<#list pojo.attributes as attr>
	<#if attr.javaDoc[0]??>
    ${attr.javaDoc}
    </#if>
	private ${attr.type} ${attr.name};
</#list>

<#list pojo.attributes as attr>
	<#assign attrCapName=attr.name?cap_first>
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ${attr.type} <#if attr.type='boolean'>is${attrCapName}<#else>get${attrCapName}</#if>() {
		return ${attr.name};
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set${attrCapName}(${attr.type} ${attr.name}) {
		this.${attr.name} = ${attr.name};
	}
</#list>

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        <#list pojo.attributes as attr>
        <#if equalsJavaPrimitive(attr.type)>
        result = prime * result + (int) this.${attr.name};
        <#else>
        result = prime * result + ((this.${attr.name} == null) ? 0 : this.${attr.name}.hashCode());
        </#if>
        </#list>
        return result;
    }
    
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ${variables.entityName}Eto other = (${variables.entityName}Eto) obj;
    <#list pojo.attributes as attr>
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
