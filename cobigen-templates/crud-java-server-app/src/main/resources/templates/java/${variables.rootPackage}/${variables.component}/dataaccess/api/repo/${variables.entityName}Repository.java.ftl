<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.dataaccess.api.repo;

import static com.querydsl.core.alias.Alias.$;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.querydsl.jpa.impl.JPAQuery;
import java.util.Iterator;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import com.devonfw.module.jpa.dataaccess.api.QueryUtil;
import com.devonfw.module.jpa.dataaccess.api.data.DefaultRepository;

<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null"> 
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>

/**
 * {@link DefaultRepository} for {@link ${variables.entityName}Entity}
  */
public interface ${variables.entityName}Repository extends <#if compositeIdTypeVar!="null"> GenericRepository<${variables.entityName}Entity, ${compositeIdTypeVar}><#else>DefaultRepository<${variables.entityName}Entity></#if> {

  /**
   * @param criteria the {@link ${variables.entityName}SearchCriteriaTo} with the criteria to search.
   * @return the {@link Page} of the {@link ${variables.entityName}Entity} objects that matched the search.
   * If no pageable is set, it will return a unique page with all the objects that matched the search.
   */
  default Page<${variables.entityName}Entity> findByCriteria(${variables.entityName}SearchCriteriaTo criteria) {

    ${variables.entityName}Entity alias = newDslAlias();
    JPAQuery<${variables.entityName}Entity> query = newDslQuery(alias);

    <#list pojo.fields as field>
      <#compress>
        <#assign newFieldType=JavaUtil.boxJavaPrimitives(classObject,field.name)>
        <#assign newFieldType=newFieldType?replace("[^<>,]+Entity","Long","r")>
        <#if newFieldType?ends_with("Embeddable")><#assign newFieldType=newFieldType?replace("Embeddable","SearchCriteriaTo","r")></#if>
        <#assign newFieldType=newFieldType?replace("[^<>,]+Embeddable","SearchCriteriaTo","r")>
        <#assign fieldCapName=field.name?cap_first>
      </#compress>
        <#if !JavaUtil.isCollection(classObject, field.name)>
          <#compress>
          <#if field.type?ends_with("Entity") && newFieldType=='Long'>
              ${newFieldType} ${field.name} = criteria.${DevonfwUtil.resolveIdGetter(field,false,"")};
              if(${field.name} != null) {
                  query.where($(alias.get${fieldCapName}().getId()).eq(${field.name}));
              }
              
          <#elseif field.type="String">
              String ${field.name} = criteria.${DevonfwUtil.resolveIdGetter(field,false,"")};
              if (${field.name} != null && !${field.name}.isEmpty()) {
                QueryUtil.get().whereString(query, $(alias.get${field.name?cap_first}()), ${field.name}, criteria.get${field.name?cap_first}Option());
              }
              
          <#else>
              ${newFieldType} ${field.name} = criteria.${DevonfwUtil.resolveIdGetter(field,false,"")};
              if (${field.name} != null) {
                query.where($(alias.<#if field.type=='boolean'>is${fieldCapName}()<#else>${DevonfwUtil.resolveIdGetter(field, true, pojo.package)}</#if>).eq(${field.name}));
              }
              
          </#if> 
      </#compress>
    </#if>
    </#list>
    if (criteria.getPageable() == null) {
      criteria.setPageable(PageRequest.of(0, Integer.MAX_VALUE));
    } else {
      addOrderBy(query, alias, criteria.getPageable().getSort());
    }
    
    return QueryUtil.get().findPaginated(criteria.getPageable(), query, true);
  }
  
  /**
   * Add sorting to the given query on the given alias
   * 
   * @param query to add sorting to
   * @param alias to retrieve columns from for sorting
   * @param sort specification of sorting
   */
  public default void addOrderBy(JPAQuery<${variables.entityName}Entity> query, ${variables.entityName}Entity alias, Sort sort) {
    if (sort != null && sort.isSorted()) {
      Iterator<Order> it = sort.iterator();
      while (it.hasNext()) {
        Order next = it.next();
        switch(next.getProperty()) {
        <#list pojo.fields as field>
          <#if !JavaUtil.isCollection(classObject, field.name)>
          case "${field.name}":
            if (next.isAscending()) {
                query.orderBy($(alias.<#if field.type=='boolean'>is${fieldCapName}()<#else>get${field.name?cap_first}()<#if field.name=="id">.toString()</#if></#if><#if field.type?ends_with("Entity") >.getId().toString()</#if>).asc());
            } else {
                query.orderBy($(alias.<#if field.type=='boolean'>is${fieldCapName}()<#else>get${field.name?cap_first}()<#if field.name=="id">.toString()</#if></#if><#if field.type?ends_with("Entity")>.getId().toString()</#if>).desc());
            }   
          break;
          </#if>
        </#list>
          default:
            throw new IllegalArgumentException("Sorted by the unknown property '"+next.getProperty()+"'");
        }
      }
    }
}

}