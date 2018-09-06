<#include '/makros.ftl'>
<#include '/functions.ftl'>
package ${variables.rootPackage}.common.builders<#if variables.subPackage != "null">.${variables.subPackage}</#if>;

import java.util.LinkedList;
import java.util.List;

import ${pojo.package}.${pojo.name};
import ${variables.rootPackage}.common.builders.P;

<#list pojo.methodAccessibleFields as field>
  <#if field.canonicalType?has_content && !equalsJavaPrimitive(field.type)>
import ${getComponentType(field.canonicalType)};
  </#if>
</#list>

public class ${pojo.name}Builder {

    private List<P<${pojo.name}>> parameterToBeApplied;

    public ${pojo.name}Builder() {
		parameterToBeApplied = new LinkedList<P<${pojo.name}>>();
		fillMandatoryFields();
		fillMandatoryFields_custom();
	}

    <#list pojo.methodAccessibleFields as field>
    <#if field.name != "id" && field.name != "version">
	public ${pojo.name}Builder ${field.name}(final ${field.type} ${field.name}) {
        parameterToBeApplied.add(new P<${pojo.name}>() {
            @Override
            public void apply(${pojo.name} target) {
                target.set${field.name?cap_first}(${field.name});
            }
        });
        return this;
    }
    </#if>
    </#list>

    public ${pojo.name} createNew() {
        ${pojo.name} ${pojo.name?lower_case} = new ${pojo.name}();
        for (P<${pojo.name}> parameter : parameterToBeApplied) {
            parameter.apply(${pojo.name?lower_case});
        }
        return ${pojo.name?lower_case};
    }

}