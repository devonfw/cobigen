<#include '/makros.ftl'>
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

/**
 * {@link DefaultRepository} for {@link ${variables.entityName}Entity}
  */
public interface ${variables.entityName}Repository extends DefaultRepository<${variables.entityName}Entity> {

  /**
   * @param criteria the {@link ${variables.entityName}SearchCriteriaTo} with the criteria to search.
   * @return the {@link Page} of the {@link ${variables.entityName}Entity} objects that matched the search.
   * If no pageable is set, it will return a unique page with all the objects that matched the search.
   */
  default Page<${variables.entityName}Entity> findByCriteria(${variables.entityName}SearchCriteriaTo criteria) {

    ${variables.entityName}Entity alias = newDslAlias();
    JPAQuery<${variables.entityName}Entity> query = newDslQuery(alias);

    <#list model.properties as property>
      <#compress>
        <@definePropertyNameAndType property true/>
        <#if property.type?ends_with("Embeddable")><#assign propType=property.type?replace("Embeddable","SearchCriteriaTo","r")></#if>
        <#assign propType=propType?replace("[^<>,]+Embeddable","SearchCriteriaTo","r")>
        <#assign fieldCapName=propName?cap_first>
      </#compress>
        <#if !property.isCollection>
          <#compress>
          <#if property.sameComponent && property.isEntity>
              ${propType?cap_first} ${propName} = criteria.get${fieldCapName}();
              if(${propName} != null) {
                  query.where($(alias.get${property.type}().getId()).eq(${propName}));
              }
              
          <#elseif propType="String">
              String ${propName} = criteria.get${fieldCapName}();
              if (${propName} != null && !${propName}.isEmpty()) {
                QueryUtil.get().whereString(query, $(alias.get${fieldCapName}()), ${propName}, criteria.get${fieldCapName}Option());
              }
              
          <#else>
              ${propType?cap_first} ${propName} = criteria.get${fieldCapName}();
              if (${propName} != null) {
                query.where($(alias.<#if propType=='boolean'>is${fieldCapName}()<#else>get${fieldCapName}()</#if>).eq(${propName}));
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
        <#list model.properties as property>
	  <@definePropertyNameAndType property true/>
          <#if !property.isCollection>
	     <#if property.sameComponent && property.isEntity>
          case "${propName}":
            if (next.isAscending()) {
                query.orderBy($(alias.get${property.name?cap_first}().getId()).asc());
            } else {
                query.orderBy($(alias.get${property.name?cap_first}().getId()).desc());
            }   
          break;
	     <#else>
	  case "${propName}":
            if (next.isAscending()) {
                query.orderBy($(alias.<#if propType=='boolean'>is${fieldCapName}()<#else>get${propName?cap_first}()</#if>).asc());
            } else {
                query.orderBy($(alias.<#if propType=='boolean'>is${fieldCapName}()<#else>get${propName?cap_first}()</#if><#if property.sameComponent && property.isEntity>.getId()</#if>).desc());
            }   
          break;
	      </#if>
          </#if>
        </#list>
          default:
            throw new IllegalArgumentException("Sorted by the unknown property '"+next.getProperty()+"'");
        }
      }
    }
}

}