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
    <#if attr.type?contains("Entity") && (attr.canonicalType?contains("java.util.List") || attr.canonicalType?contains("java.util.Set"))>
      <#assign suffix="Ids">
      <#-- Handle the standard case. Due to no knowledge of the interface, we have no other possibility than guessing -->
      <#-- Therefore remove (hopefully) plural 's' from attribute's name to attach it on the suffix -->
      <#if attrCapName?ends_with("s")>
         <#assign attrCapName=attrCapName?substring(0, attrCapName?length-1)>
      </#if>
    <#elseif attr.type?contains("Entity")>
      <#assign suffix="Id">
    </#if>
    </#compress>

    ${newAttrType} ${attr.name} = criteria.<#if attr.type=='boolean'>is${attrCapName}<#else>get${attrCapName}${suffix}</#if>();
    if (${attr.name} != null) {
      query.where(Alias.$(${variables.entityName?lower_case}.<#if attr.type=='boolean'>is${attrCapName}<#else>get${attrCapName}${suffix}</#if>()).eq(${attr.name}));
    }
    </#list>

    return findPaginated(criteria, query, alias);
  }

}