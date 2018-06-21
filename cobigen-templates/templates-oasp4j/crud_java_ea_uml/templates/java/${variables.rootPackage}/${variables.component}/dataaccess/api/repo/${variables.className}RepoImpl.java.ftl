<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
package ${variables.rootPackage}.${variables.component}.dataaccess.api.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.mysema.query.jpa.impl.JPAQuery;

import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.className?cap_first}Entity;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className?cap_first}SearchCriteriaTo;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.Q${variables.className?cap_first}Entity;

import io.oasp.module.jpa.common.api.to.OrderByTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;
import io.oasp.module.jpa.common.api.to.PaginationResultTo;

public class ${variables.className?cap_first}RepoImpl implements ${variables.className?cap_first}RepoCustom {

  @PersistenceContext
  private EntityManager entityManager;

  private Q${variables.className}Entity ${variables.className?lower_case};

  public ${variables.className?cap_first}RepoImpl(){
    setQ${variables.className}Entity(Q${variables.className}Entity.tableEntity);
  }
   
  @Override
  public PaginatedListTo find${variables.className}s(${variables.className}SearchCriteriaTo criteria) {

    
    JPAQuery query = new JPAQuery(getEntityManager()).from(getQ${variables.className}Entity());

    <#list elemDoc["self::node()/ownedAttribute"] as field>
        <#compress>
        <#assign fieldTypeUML = field["type/@xmi:idref"]?replace("EAJava_","")>
        <#assign newFieldType=JavaUtil.boxJavaPrimitives(fieldTypeUML)>
      <#if newFieldType?ends_with("Embeddable")><#assign newFieldType=newFieldType?replace("Embeddable","SearchCriteriaTo","r")></#if>
      <#assign newFieldType=newFieldType?replace("[^<>,]+Embeddable","SearchCriteriaTo","r")>
        <#assign fieldCapName=field["@name"]?cap_first>
        </#compress>
        <#if !fieldTypeUML?starts_with("List<") && !fieldTypeUML?starts_with("Set<")>
        ${newFieldType} ${field["@name"]} = criteria.<#if fieldTypeUML=='boolean'>is${fieldCapName}()<#else>${OaspUtil.resolveIdGetter(field,false,"")}</#if>;
        <#compress>
      <#if !JavaUtil.equalsJavaPrimitive(fieldTypeUML)>if (${field["@name"]} != null) {</#if>
          <#if fieldTypeUML?ends_with("Entity") && newFieldType=='Long'>
              if(${variables.className?lower_case}.get${fieldCapName}() != null) {
                  query.where(getQ${variables.className}Entity().${field["@name"]}.eq(${field["@name"]});
              }
          <#else>
              query.where(getQ${variables.className}Entity().<#if fieldTypeUML=='boolean'>is${fieldCapName}()<#else>${field["@name"]}</#if>.eq(${field["@name"]}));
          </#if>
        <#if !JavaUtil.equalsJavaPrimitive(fieldTypeUML)>}</#if>
      </#compress>
    </#if>
    </#list>
    Integer timeout = criteria.getSearchTimeout();
    if (timeout != null) {
      query.setHint("javax.persistence.query.timeout", timeout.intValue());
    }
    addOrderBy(query, criteria.getSort());
    List<${variables.className}Entity> ${variables.className?lower_case}s = query.list(getQ${variables.className}Entity());

    PaginationResultTo resultPaginated = new PaginationResultTo(criteria.getPagination(), new Long(query.clone().count()));
    return new PaginatedListTo<>(${variables.className?lower_case}s, resultPaginated);

  }

  private void addOrderBy(JPAQuery query, List<OrderByTo> sort) {

    if (sort != null && !sort.isEmpty()) {
      for (OrderByTo sortEntry : sort) {
      <#list elemDoc["self::node()/ownedAttribute"] as field>
        if (sortEntry.getName().equals("${field["@name"]}")) {
          if (sortEntry.getDirection().isAsc()) {
            query.orderBy(getQTableEntity().${field["@name"]}.asc());
          } else {
            query.orderBy(getQTableEntity().${field["@name"]}.desc());
          }
        }
      </#list>
      }
    }
  }
  
  private EntityManager getEntityManager() {

    return this.entityManager;
  }

  private Q${variables.className}Entity getQ${variables.className}Entity() {

    return this.${variables.className?lower_case};
  }

  private void setQ${variables.className}Entity(Q${variables.className}Entity ${variables.className?lower_case}) {

    this.${variables.className?lower_case} = ${variables.className?lower_case};
  }

}
</#compress>