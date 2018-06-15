<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
package ${variables.rootPackage}.${variables.component}.dataaccess.api.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.className}Entity;

@Repository
public interface ${variables.className?cap_first}Repo extends JpaRepository<${variables.className}Entity, Long>,
                    QueryDslPredicateExecutor<${variables.className}Entity>, ${variables.className}RepoCustom {

  @Override
  <S extends ${variables.className}Entity> S save(S ${variables.className?lower_case});

  @Override
  Page<${variables.className?cap_first}Entity> findAll(Pageable pageable);

  <#list elemDoc["self::node()/ownedAttribute"] as field>
    Page<${variables.className?cap_first}Entity> findBy${field["@name"]?cap_first}(${field["type/@xmi:idref"]} ${field["@name"]}, Pageable pageable);
  </#list>

}