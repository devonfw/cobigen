<#include '/makros.ftl'>
package ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to;

import io.oasp.module.jpa.common.api.to.SearchCriteriaTo;
import java.util.List;
import java.util.Set;

import java.math.BigDecimal;

/**
 * This is the {@link SearchCriteriaTo search criteria} {@link net.sf.mmm.util.transferobject.api.TransferObject TO}
 * used to find {@link ${variables.rootPackage}.${variables.component?lower_case}.common.api.${variables.entityName}}s.
 *
 */
public class ${variables.entityName}SearchCriteriaTo extends SearchCriteriaTo {

  private static final long serialVersionUID = 1L;
  
<#list model.properties as property>
	<#if property.name != "id" && !property.isCollection>
	 <@definePropertyNameAndType property/>
	private ${propType} ${propName};
	</#if>
</#list>

  /**
  * The constructor.
  */
  public ${variables.entityName}SearchCriteriaTo() {

    super();
  }

<#list model.properties as property>
	<#if property.name != "id" && !property.isCollection>
		<@definePropertyNameAndType property/>
	public ${propType} get${propName?cap_first}() {
		return this.${propName};
	}
	
	public void set${propName?cap_first}(${propType} ${propName}) {
		this.${propName} = ${propName};
	}
	</#if>
</#list>

}
