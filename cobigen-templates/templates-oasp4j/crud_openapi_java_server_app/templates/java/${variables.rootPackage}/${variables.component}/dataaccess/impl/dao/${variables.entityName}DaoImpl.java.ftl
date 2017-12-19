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
public class ${variables.entityName}DaoImpl extends ApplicationDaoImpl<${variables.entityName}Entity> implements ${variables.entityName}Dao {

	/**
	* The constructor.
	*/
	public ${variables.entityName}DaoImpl() {

		super();
	}

  @Override
  public Class<${variables.entityName}Entity> getEntityClass() {
  	return ${variables.entityName}Entity.class;
 	}

 	@Override
  public PaginatedListTo<${variables.entityName}Entity> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria) {

    ${variables.entityName}Entity ${variables.entityName?lower_case} = Alias.alias(${variables.entityName}Entity.class);
    EntityPathBase<${variables.entityName}Entity> alias = Alias.$(${variables.entityName?lower_case});
    JPAQuery query = new JPAQuery(getEntityManager()).from(alias);

    <#list model.properties as property>
    <#compress>
    <#assign newFieldType=OaspUtil.getOaspTypeFromOpenAPI(property, false)>
    <#assign fieldCapName=property.name?cap_first>
    </#compress>
    	<#if property.name != "id">
    		<#if !property.isCollection>
    ${newFieldType} ${property.name} = criteria.<#if property.type=='boolean'>is${fieldCapName}()<#else>get${fieldCapName}<#if property.isEntity>Id</#if>()</#if>;
        	<#compress>
    if (${property.name} != null) {
          	<#if property.isEntity && newFieldType=='Long'>
              if(${variables.entityName?lower_case}.get${fieldCapName}Id() != null) {
    query.where(Alias.$(${variables.entityName?lower_case}.get${fieldCapName}Id()).eq(criteria.get${fieldCapName}Id()));
              }
          	<#else>
    query.where(Alias.$(${variables.entityName?lower_case}.<#if property.type=='boolean'>is${fieldCapName}()<#else>get${fieldCapName}()</#if>).eq(${property.name}));
          	</#if>
    } 
    			</#compress>
    		</#if>
    	</#if>
    </#list>

    return findPaginated(criteria, query, alias);
  }

}