<#include '/makros.ftl'>
package ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to;

import com.devonfw.module.basic.common.api.query.StringSearchConfigTo;
import ${variables.rootPackage}.general.common.api.to.AbstractSearchCriteriaTo;
import java.util.List;
import java.util.Set;

import java.math.BigDecimal;

/**
 * {@link SearchCriteriaTo} to find instances of {@link ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName}}s.
 */
public class ${variables.entityName}SearchCriteriaTo extends AbstractSearchCriteriaTo {

  private static final long serialVersionUID = 1L;
  
<#list model.properties as property>
	<#if property.name != "id" && !property.isCollection>
	 <@definePropertyNameAndType property/>
	private ${propType} ${propName};
	</#if>
</#list>

<#list model.properties as property>
        <@definePropertyNameAndType property/>
        <#if propType = "String"> 
	private StringSearchConfigTo ${propName}Option;
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

<#list model.properties as property>
        <@definePropertyNameAndType property/>
        <#if propType = "String"> 
	public StringSearchConfigTo get${propName?cap_first}Option() {
		return this.${propName}Option;
	}
	
	public void set${propName?cap_first}Option(StringSearchConfigTo ${propName}Option) {
		this.${propName}Option = ${propName}Option;
	}
	</#if>
</#list>

}
