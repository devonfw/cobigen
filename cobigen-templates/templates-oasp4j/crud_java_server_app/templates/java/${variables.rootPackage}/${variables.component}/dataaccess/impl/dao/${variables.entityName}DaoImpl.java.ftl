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

    <#list pojo.fields as field>
    <#compress>
    <#assign newFieldType=field.type?replace("[^<>,]+Entity","Long","r")>
	<#if newFieldType?ends_with("Embeddable")><#assign newFieldType=newFieldType?replace("Embeddable","SearchCriteriaTo","r")></#if>
	<#assign newFieldType=newFieldType?replace("[^<>,]+Embeddable","SearchCriteriaTo","r")>
    <#assign fieldCapName=field.name?cap_first>
    </#compress>
    <#if !field.type?starts_with("List<") && !field.type?starts_with("Set<")>
        ${newFieldType} ${field.name} = criteria.<#if field.type=='boolean'>is${fieldCapName}()<#else>${CrudJavaServerAppFunctions.resolveIdGetter(field, false, "")}</#if>;
        <#compress>
    	<#if !JavaUtil.equalsJavaPrimitive(field.type)>if (${field.name} != null) {</#if>
          <#if field.type?ends_with("Entity") && newFieldType=='Long'>
              if(${variables.entityName?lower_case}.get${fieldCapName}() != null) {
                  query.where(Alias.$(${variables.entityName?lower_case}.get${fieldCapName}().getId()).eq(${field.name}));
              }
          <#else>
              query.where(Alias.$(${variables.entityName?lower_case}.<#if field.type=='boolean'>is${fieldCapName}()<#else>${CrudJavaServerAppFunctions.resolveIdGetter(field,false,"")}</#if>).eq(${field.name}));
          </#if>    
        <#if !JavaUtil.equalsJavaPrimitive(field.type)>}</#if>
    	</#compress>
    </#if>
    </#list>

    return findPaginated(criteria, query, alias);
  }

}