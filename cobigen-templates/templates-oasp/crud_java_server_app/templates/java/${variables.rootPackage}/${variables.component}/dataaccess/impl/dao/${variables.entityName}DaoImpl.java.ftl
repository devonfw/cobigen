<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.dataaccess.impl.dao;

import java.util.List;

import ${variables.rootPackage}.general.common.api.constants.NamedQueries;
import ${variables.rootPackage}.general.dataaccess.base.dao.ApplicationDaoImpl;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.dao.${variables.entityName}Dao;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

import javax.inject.Named;

import com.mysema.query.alias.Alias;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.path.EntityPathBase;

/**
 * This is the implementation of {@link ${variables.entityName}Dao}.
 */
@Named
public class ${variables.entityName}DaoImpl extends ApplicationDaoImpl<${pojo.name}> implements ${variables.entityName}Dao {
	
	/**
	* The constructor.
	*/
	public ${variables.entityName}DaoImpl() {
	
		super();
	}
  
  @Override
  public Class<${pojo.name}> getEntityClass() {
  	return ${pojo.name}.class;
 	}
 	
 	@Override
  public PaginatedListTo<${variables.entityName}Entity> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria) {

    ${variables.entityName}Entity ${variables.entityName?lower_case} = Alias.alias(${variables.entityName}Entity.class);
    EntityPathBase<${variables.entityName}Entity> alias = Alias.$(${variables.entityName?lower_case});
    JPAQuery query = new JPAQuery(getEntityManager()).from(alias);

    <#list pojo.fields as attr>
    <#compress>
    <#assign newAttrType=attr.type?replace("[^<>,]+Entity","Long","r")>
    <#assign attrCapName=attr.name?cap_first>
    <#assign suffix="">
	<#assign persistenceIdGetterSuffix="">
	<#if attr.type?ends_with("Entity")>
		<#if attr.canonicalType?starts_with("java.util.List") || attr.canonicalType?starts_with("java.util.Set")>
		  <#assign suffix="Ids">
		  <#-- Handle the standard case. Due to no knowledge of the interface, we have no other possibility than guessing -->
		  <#-- Therefore remove (hopefully) plural 's' from attribute's name to attach it on the suffix -->
		  <#if attrCapName?ends_with("s")>
			 <#assign attrCapName=attrCapName?substring(0, attrCapName?length-1)>
		  </#if>
		<#else>
		  <#assign suffix="Id">
		</#if>
		
		<#if isEntityInComponent(attr.canonicalType, variables.component)>
			<#assign persistenceIdGetterSuffix="().getId"><#-- direct references for Entities in same component, so get id of the object reference -->
		<#else>
			<#assign persistenceIdGetterSuffix=suffix>
		</#if>
	</#if>
    </#compress>

    ${newAttrType} ${attr.name} = criteria.<#if attr.type=='boolean'>is${attrCapName}<#else>get${attrCapName}${suffix}</#if>();
    <#compress>
	<#if !equalsJavaPrimitive(attr.type)>if (${attr.name} != null) {</#if>
      query.where(Alias.$(${variables.entityName?lower_case}.<#if attr.type=='boolean'>is${attrCapName}<#else>get${attrCapName}${persistenceIdGetterSuffix}</#if>()).eq(${attr.name}));
    <#if !equalsJavaPrimitive(attr.type)>}</#if>
	</#compress>
	
    </#list>

    return findPaginated(criteria, query, alias);
  }

}