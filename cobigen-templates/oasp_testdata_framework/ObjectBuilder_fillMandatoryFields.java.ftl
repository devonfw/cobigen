<#include '/makros.ftl'>
package ${variables.rootPackage}.${variables.component}.common.builders;

public class ${pojo.name}Builder {

	/**
	 * Fills all mandatory fields by default. (will be overwritten on re-generation)
	 */
    private void fillMandatoryFields() {
	    <#list pojo.attributes as attr>
		<#if (attr.annotations.javax_validation_constraints_NotNull)?has_content>
		<@callNotNullPropertyWithDefaultValue attr=attr/>
		
		</#if>
	    </#list>
    }
    
}