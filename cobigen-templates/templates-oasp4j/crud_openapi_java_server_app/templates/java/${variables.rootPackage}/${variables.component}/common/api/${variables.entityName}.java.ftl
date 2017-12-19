package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;
import java.util.List;
import java.util.Set;

public interface ${variables.entityName} extends ApplicationEntity {

<#list model.properties as property>
	<#if property.name != "id" && !property.isCollection>
	public void set${property.name?cap_first}(${OaspUtil.getOaspTypeFromOpenAPI(property, false)} ${property.name});
	
	public ${OaspUtil.getOaspTypeFromOpenAPI(property, false)} <#if property.type == "boolean">is<#else>get</#if>${property.name?cap_first}();
	</#if>
</#list>
<#list model.relationShips as rs>
	<#if rs.type != "manytomany" && rs.type != "onetomany">
  public void set${rs.entity}Id(Long ${rs.entity?uncap_first}Id);

  public Long get${rs.entity}Id();
  </#if>
</#list>

}
