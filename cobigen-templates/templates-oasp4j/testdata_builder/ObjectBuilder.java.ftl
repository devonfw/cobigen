<#include '/makros.ftl'>
package ${variables.rootPackage}.common.builders;

import java.util.LinkedList;
import java.util.List;

import ${pojo.package}.${pojo.name};
import ${variables.rootPackage}.common.builders.P;

/**
 * Test data builder for ${pojo.name} generated with cobigen.
 */
public class ${pojo.name}Builder {

    private List<P<${pojo.name}>> parameterToBeApplied;

  /**
   * The constructor.
   */
    public ${pojo.name}Builder() {
		this.parameterToBeApplied = new LinkedList<>();
		fillMandatoryFields();
		fillMandatoryFields_custom();
	}

    <#list pojo.methodAccessibleFields as field>
    <#if field.name != "id" && field.name != "modificationCounter">
  /**
   * @param ${field.name} the ${field.name} to add.
   * @return the builder for fluent population of fields. 
   */
	public ${pojo.name}Builder ${field.name}(final ${field.type} ${field.name}) {
        this.parameterToBeApplied.add(new P<${pojo.name}>() {
            @Override
            public void apply(${pojo.name} target) {
                target.set${field.name?cap_first}(${field.name});
            }
        });
        return this;
    }
    </#if>
    </#list>

  /**
   * @return the populated ${pojo.name}. 
   */
    public ${pojo.name} createNew() {
        ${pojo.name} ${pojo.name?lower_case} = new ${pojo.name}();
        for (P<${pojo.name}> parameter : parameterToBeApplied) {
            parameter.apply(${pojo.name?lower_case});
        }
        return ${pojo.name?lower_case};
    }

}