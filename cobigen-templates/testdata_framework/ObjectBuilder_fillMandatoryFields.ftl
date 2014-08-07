<#include '/makros.ftl'>
package ${variables.rootPackage}.common.builders;

public class ${pojo.name}Builder {

	public ${pojo.name}Builder() {
		parameterToBeApplied = new LinkedList<P<${pojo.name}>>();
		fillMandatoryFields();
	}

    private void fillMandatoryFields() {
	    <#list pojo.attributes as attr>
		<#if (attr.annotations.javax_validation_constraints_NotNull)?has_content>
		<@callNotNullPropertyWithDefaultValue attr=attr/>
		
		</#if>
	    </#list>
    }
    
}