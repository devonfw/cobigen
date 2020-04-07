package ${variables.rootPackage}.${variables.component}.service.impl.rest;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import ${variables.rootPackage}.${variables.component}.service.api.rest.${variables.component?cap_first}RestService;

import org.springframework.data.domain.Page;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

<#list pojo.fields as field>
		<#if field.name="id">
			<#assign compositeIdVar = true>
			<#assign compositeIdTypeVar = field.type>
		</#if>
	</#list>
<#if compositeIdVar = true>
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>

/**
 * The service implementation for REST calls in order to execute the logic of component {@link ${variables.component?cap_first}}.
 */
@Named("${variables.component?cap_first}RestService")
public class ${variables.component?cap_first}RestServiceImpl implements ${variables.component?cap_first}RestService{

  @Inject
  private ${variables.component?cap_first} ${variables.component?lower_case};

  @Override
  public ${variables.entityName}Eto get${variables.entityName}(<#if compositeIdVar = true> ${compositeIdTypeVar} <#else> long </#if> id) {
    return this.${variables.component?uncap_first}.find${variables.entityName}(id);
  }

  @Override
  public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case}) {
      return this.${variables.component?uncap_first}.save${variables.entityName}(${variables.entityName?lower_case});
  }

  @Override
  public void delete${variables.entityName}(<#if compositeIdVar = true> ${compositeIdTypeVar} <#else> long </#if> id) {
    this.${variables.component?uncap_first}.delete${variables.entityName}(id);
  }

  @Override
  public Page<${variables.entityName}Eto> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo searchCriteriaTo) {
    return this.${variables.component?uncap_first}.find${variables.entityName}s(searchCriteriaTo);
  }
}