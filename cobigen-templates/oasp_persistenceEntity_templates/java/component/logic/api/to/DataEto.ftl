<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import ${variables.rootPackage}.general.logic.base.AbstractEto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};

import java.util.List;
import java.util.Set;

/**
 * Entity transport object of ${variables.entityName}
 */
public class ${variables.entityName}Eto extends AbstractEto implements ${variables.entityName} {

	private static final long serialVersionUID = 1L;

<#list pojo.attributes as attr>
   	private ${attr.type?replace("[^<>,]+Entity","Long","r")} ${attr.name};
</#list>

<#list pojo.attributes as attr>
	<#assign attrCapName=attr.name?cap_first>
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ${attr.type?replace("[^<>,]+Entity","Long","r")} <#if attr.type='boolean'>is${attrCapName}<#else>get${attrCapName}<#if attr.type?contains("Entity")>Id</#if></#if>() {
		return ${attr.name};
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set${attrCapName}<#if attr.type?contains("Entity")>Id</#if>(${attr.type?replace("[^<>,]+Entity","Long","r")} ${attr.name}) {
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
        <#if pojo.attributes?has_content>
        	<#list pojo.attributes as attr>
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
    // class check will be done by super type EntityTo!
    if (!super.equals(obj)) {
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
