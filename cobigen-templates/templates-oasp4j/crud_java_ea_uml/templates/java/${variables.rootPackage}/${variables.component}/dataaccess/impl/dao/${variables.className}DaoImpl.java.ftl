<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#assign name = elemDoc["self::node()/@name"]>
package ${variables.rootPackage}.${variables.component}.dataaccess.impl.dao;

import java.util.List;

import ${variables.rootPackage}.general.common.api.constants.NamedQueries;
import ${variables.rootPackage}.general.dataaccess.base.dao.ApplicationDaoImpl;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.dao.${variables.className}Dao;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.className}Entity;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;

import io.oasp.module.jpa.common.api.to.PaginatedListTo;

import javax.inject.Named;

import com.mysema.query.alias.Alias;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.path.EntityPathBase;

/**
 * This is the implementation of {@link ${variables.className}Dao}.
 */
@Named
public class ${variables.className}DaoImpl extends ApplicationDaoImpl<${variables.className}Entity> implements ${variables.className}Dao {

	/**
	* The constructor.
	*/
	public ${variables.className}DaoImpl() {

		super();
	}

  @Override
  public Class<${variables.className}Entity> getEntityClass() {
  	return ${variables.className}Entity.class;
 	}

 	@Override
  public PaginatedListTo<${variables.className}Entity> find${OaspUtil.removePlural(variables.className)}s(${variables.className}SearchCriteriaTo criteria) {

    ${variables.className}Entity ${variables.className?lower_case} = Alias.alias(${variables.className}Entity.class);
    EntityPathBase<${variables.className}Entity> alias = Alias.$(${variables.className?lower_case});
    JPAQuery query = new JPAQuery(getEntityManager()).from(alias);

    <#list elemDoc["self::node()/ownedAttribute"] as field>
    <#compress>
    <#-- sholzer, 29.05.2017, #259: the newFiledType is now never a Java primitive. The field["type/@xmi:idref"]?replace("EAJava_","")?replace.. value is processed by the JavaUtil.boxJavaPrimitives(String) method that wraps primitives into their objects -->
    <#assign newFieldType=JavaUtil.boxJavaPrimitives(field["type/@xmi:idref"]?replace("EAJava_",""))>
	<#if newFieldType?ends_with("Embeddable")><#assign newFieldType=newFieldType?replace("Embeddable","SearchCriteriaTo","r")></#if>
	<#assign newFieldType=newFieldType?replace("[^<>,]+Embeddable","SearchCriteriaTo","r")>
    <#assign fieldCapName=field["@name"]?cap_first>
    </#compress>
    <#if !field["type/@xmi:idref"]?replace("EAJava_","")?starts_with("List<") && !field["type/@xmi:idref"]?replace("EAJava_","")?starts_with("Set<")>
        ${newFieldType} ${field["@name"]} = criteria.<#if field["type/@xmi:idref"]?replace("EAJava_","")=='boolean'>is${fieldCapName}()<#else>${OaspUtil.resolveIdGetter(field, false, "")}</#if>;
        <#compress>
        <#-- sholzer, 29.05.2017, #259: if clause not needed anymore since newFieldType is never a primitive. -->
    	if (${field["@name"]} != null) {
          <#if field["type/@xmi:idref"]?replace("EAJava_","")?ends_with("Entity") && newFieldType=='Long'>
              if(${variables.className?lower_case}.get${fieldCapName}() != null) {
                  query.where(Alias.$(${variables.className?lower_case}.get${fieldCapName}().getId()).eq(${field["@name"]}));
              }
          <#else>
              query.where(Alias.$(${variables.className?lower_case}.<#if field["type/@xmi:idref"]?replace("EAJava_","")=='boolean'>is${fieldCapName}()<#else>${OaspUtil.resolveIdGetter(field,false,"")}</#if>).eq(${field["@name"]}));
          </#if>  
        <#-- sholzer, 29.05.2017, #259: as above -->  
        }
    	</#compress>
    </#if>
    </#list>

    return findPaginated(criteria, query, alias);
  }

}
