<#include '/makros.ftl'>
package ${variables.rootPackage}.common.builders;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import ${pojo.package}.${pojo.name};

/**
 * Test data builder for ${pojo.name} generated with cobigen.
 */
public class ${pojo.name}Builder {

    private List<Consumer<${pojo.name}>> parameterToBeApplied;

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
        this.parameterToBeApplied.add(target -> target.set${field.name?cap_first}(${field.name}));

        return this;
    }
    </#if>
    </#list>

  /**
   * @return the populated ${pojo.name}.
   */
    public ${pojo.name} createNew() {
        ${pojo.name} ${pojo.name?lower_case} = new ${pojo.name}();
        for (Consumer<${pojo.name}> parameter : parameterToBeApplied) {
            parameter.accept(${pojo.name?lower_case});
        }
        return ${pojo.name?lower_case};
    }

}