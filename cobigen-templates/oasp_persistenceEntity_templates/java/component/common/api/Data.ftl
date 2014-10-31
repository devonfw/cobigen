<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;

public interface ${variables.entityName} extends ApplicationEntity {

<#list pojo.attributes as attr>
   	${attr.type} get${attr.name?cap_first}();

   	void set${attr.name?cap_first}(${attr.type} ${attr.name});
</#list>

}