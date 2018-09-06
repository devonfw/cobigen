<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.dataaccess.api.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.mysema.query.jpa.impl.JPAQuery;

import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName?cap_first}Entity;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName?cap_first}SearchCriteriaTo;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.Q${variables.entityName?cap_first}Entity;

import io.oasp.module.jpa.common.api.to.OrderByTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;
import io.oasp.module.jpa.common.api.to.PaginationResultTo;

public class ${variables.entityName?cap_first}RepoImpl implements ${variables.entityName?cap_first}RepoCustom {

  @PersistenceContext
  private EntityManager entityManager;

  private Q${variables.entityName}Entity ${variables.entityName?lower_case};

  public ${variables.entityName?cap_first}RepoImpl(){
    setQ${variables.entityName}Entity(Q${variables.entityName}Entity.tableEntity);
  }
   
  @Override
  public PaginatedListTo find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria) {

    
    JPAQuery query = new JPAQuery(getEntityManager()).from(getQ${variables.entityName}Entity());

    <#list pojo.fields as field>
        <#compress>
        <#assign newFieldType=field.type?replace("[^<>,]+Entity","Long","r")>
      <#if newFieldType?ends_with("Embeddable")><#assign newFieldType=newFieldType?replace("Embeddable","SearchCriteriaTo","r")></#if>
      <#assign newFieldType=newFieldType?replace("[^<>,]+Embeddable","SearchCriteriaTo","r")>
        <#assign fieldCapName=field.name?cap_first>
        </#compress>
        <#if !field.type?starts_with("List<") && !field.type?starts_with("Set<")>
        ${newFieldType} ${field.name} = criteria.<#if field.type=='boolean'>is${fieldCapName}()<#else>${OaspUtil.resolveIdGetter(field,false,"")}</#if>;
        <#compress>
      <#if !JavaUtil.equalsJavaPrimitive(field.type)>if (${field.name} != null) {</#if>
          <#if field.type?ends_with("Entity") && newFieldType=='Long'>
              if(${variables.entityName?lower_case}.get${fieldCapName}() != null) {
                  query.where(getQ${variables.entityName}Entity().${field.name}.eq(${field.name});
              }
          <#else>
              query.where(getQ${variables.entityName}Entity().<#if field.type=='boolean'>is${fieldCapName}()<#else>${field.name}</#if>.eq(${field.name}));
          </#if>
        <#if !JavaUtil.equalsJavaPrimitive(field.type)>}</#if>
      </#compress>
    </#if>
    </#list>
    Integer timeout = criteria.getSearchTimeout();
    if (timeout != null) {
      query.setHint("javax.persistence.query.timeout", timeout.intValue());
    }
    addOrderBy(query, criteria.getSort());
    List<${variables.entityName}Entity> ${variables.entityName?lower_case}s = query.list(getQ${variables.entityName}Entity());

    PaginationResultTo resultPaginated = new PaginationResultTo(criteria.getPagination(), new Long(query.clone().count()));
    return new PaginatedListTo<>(${variables.entityName?lower_case}s, resultPaginated);

  }

  private void addOrderBy(JPAQuery query, List<OrderByTo> sort) {

    if (sort != null && !sort.isEmpty()) {
      for (OrderByTo sortEntry : sort) {
      <#list pojo.fields as field>
        if (sortEntry.getName().equals("${field.name}")) {
          if (sortEntry.getDirection().isAsc()) {
            query.orderBy(getQTableEntity().${field.name}.asc());
          } else {
            query.orderBy(getQTableEntity().${field.name}.desc());
          }
        }
      </#list>
      }
    }
  }
  
  private EntityManager getEntityManager() {

    return this.entityManager;
  }

  private Q${variables.entityName}Entity getQ${variables.entityName}Entity() {

    return this.${variables.entityName?lower_case};
  }

  private void setQ${variables.entityName}Entity(Q${variables.entityName}Entity ${variables.entityName?lower_case}) {

    this.${variables.entityName?lower_case} = ${variables.entityName?lower_case};
  }

}
