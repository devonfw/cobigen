<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.dataaccess.api;

import static com.querydsl.core.alias.Alias.$;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.querydsl.jpa.impl.JPAQuery;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.${variables.component}.common.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.dataaccess.api.QueryUtil;
import io.oasp.module.jpa.dataaccess.api.data.DefaultRepository;


/**
 * {@link DefaultRepository} for {@link ${variables.entityName}Entity}
  */
public interface ${variables.entityName}Repository extends DefaultRepository<${variables.entityName}Entity> {

  /**
   * @param criteria the {@link ${variables.entityName}SearchCriteriaTo} with the criteria to search.
   * @param pageRequest {@link Pageable} implementation used to set page properties like page size
   * @return the {@link Page} of the {@link ${variables.entityName}Entity} objects that matched the search.
   */
  default Page<${variables.entityName}Entity> findByCriteria(${variables.entityName}SearchCriteriaTo criteria) {

    ${variables.entityName}Entity alias = newDslAlias();
    JPAQuery<${variables.entityName}Entity> query = newDslQuery(alias);

    <#list pojo.fields as field>
      <#compress>
        <#assign newFieldType=field.type?replace("[^<>,]+Entity","Long","r")>
        <#if newFieldType?ends_with("Embeddable")><#assign newFieldType=newFieldType?replace("Embeddable","SearchCriteriaTo","r")></#if>
          <#assign newFieldType=newFieldType?replace("[^<>,]+Embeddable","SearchCriteriaTo","r")>
          <#assign fieldCapName=field.name?cap_first>
      </#compress>
        <#if !field.type?starts_with("List<") && !field.type?starts_with("Set<")>
          <#compress>          
              <#if field.type?ends_with("Entity") && newFieldType=='Long'>
                <#-- Cast the attribute to String for creating the query --> 
                String ${field.name} = ${newFieldType}.toString(criteria.${OaspUtil.resolveIdGetter(field,false,"")});
                if ((${field.name} != null)) {
                  QueryUtil.get().whereString(query, $(${newFieldType}.toString(alias.get${field.name?cap_first}Id())), ${field.name}, criteria.get${field.name?cap_first}Option());
                }                                    
              <#elseif field.type == 'int'>
                <#-- Cast the attribute to String for creating the query --> 
                String ${field.name} = Integer.toString(criteria.<#if field.type=='boolean'>is${fieldCapName}()<#else>${OaspUtil.resolveIdGetter(field,false,"")}</#if>);
                if (${field.name} != null) {
                  QueryUtil.get().whereString(query, $(Integer.toString(alias.get${field.name?cap_first}())), ${field.name}, criteria.get${field.name?cap_first}Option());
                }
              <#elseif field.type != 'String'>
              <#else>
                String ${field.name} = criteria.${OaspUtil.resolveIdGetter(field,false,"")};
                if ((${field.name} != null) && !${field.name}.isEmpty()) {
                  QueryUtil.get().whereString(query, $(alias.get${field.name?cap_first}()), ${field.name}, criteria.get${field.name?cap_first}Option());
                }
              </#if>        
      </#compress>
    </#if>
    </#list>
    return QueryUtil.get().findPaginated(criteria.getPageable(), query, false);
  }

}