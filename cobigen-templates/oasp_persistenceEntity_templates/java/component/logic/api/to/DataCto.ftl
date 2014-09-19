<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import ${variables.rootPackage}.general.logic.base.AbstractEto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};

/**
 * Composite transport object of ${variables.entityName}
 */
public class ${variables.entityName}Cto extends AbstractCto {

	private static final long serialVersionUID = 1L;

<#list pojo.attributes as attr>
	<#if attr.javaDoc[0]??>
    ${attr.javaDoc}
    </#if>
    <#if attr.type?ends_with("Entity")>
   	private ${attr.type?replace("Entity","")}Eto ${attr.name};
   	<#else>
   	private ${attr.type} ${attr.name};
    </#if>
</#list>

<#list pojo.attributes as attr>
	<#assign attrCapName=attr.name?cap_first>
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <#if attr.type?ends_with("Entity")>${attr.type?replace("Entity","")}Eto<#else>${attr.type}</#if> <#if attr.type='boolean'>is${attrCapName}<#else>get${attrCapName}</#if>() {
		return ${attr.name};
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set${attrCapName}(<#if attr.type?ends_with("Entity")>${attr.type?replace("Entity","")}Eto<#else>${attr.type}</#if> ${attr.name}) {
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
