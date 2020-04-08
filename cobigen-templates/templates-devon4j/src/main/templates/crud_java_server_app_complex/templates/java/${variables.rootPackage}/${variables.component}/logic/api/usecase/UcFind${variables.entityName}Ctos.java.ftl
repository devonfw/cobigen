package ${variables.rootPackage}.${variables.component}.logic.api.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Cto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
<#assign compositeIdVar = false>
<#list pojo.fields as field>
	<#if field.type?starts_with("Composite")>
		<#assign compositeIdVar = true>
		<#assign compositeIdTypeVar = field.type>
	</#if>
</#list>
<#if compositeIdVar = true>
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>

public interface UcFind${variables.entityName} {

  /**
   * Returns a composite ${variables.entityName} by its id 'id'.
   *
   * @param id The id 'id' of the ${variables.entityName}.
   * @return The {@link ${variables.entityName}Cto} with id 'id'
   */
  ${variables.entityName}Cto find${variables.entityName}Cto(<#if compositeIdVar = true>${compositeIdTypeVar}<#else>long</#if> id);
  
  /**
   * Returns a paginated list of composite ${variables.entityName}s matching the search criteria.
   *
   * @param criteria the {@link ${variables.entityName}SearchCriteriaTo}.
   * @return the {@link List} of matching {@link ${variables.entityName}Cto}s.
   */
  Page<${variables.entityName}Cto> find${variables.entityName}Ctos(${variables.entityName}SearchCriteriaTo criteria);

}
