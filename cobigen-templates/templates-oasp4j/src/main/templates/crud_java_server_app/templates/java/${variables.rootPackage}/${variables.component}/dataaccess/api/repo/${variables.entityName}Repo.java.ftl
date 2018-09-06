package ${variables.rootPackage}.${variables.component}.dataaccess.api.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;

@Repository
public interface ${variables.entityName?cap_first}Repo extends JpaRepository<${variables.entityName}Entity, Long>,
                    QueryDslPredicateExecutor<${variables.entityName}Entity>, ${variables.entityName}RepoCustom {

  @Override
  <S extends ${variables.entityName}Entity> S save(S ${variables.entityName?lower_case});

  @Override
  Page<${variables.entityName?cap_first}Entity> findAll(Pageable pageable);

  <#list pojo.fields as field>
    Page<${variables.entityName?cap_first}Entity> findBy${field.name?cap_first}(${field.type} ${field.name}, Pageable pageable);
  </#list>

}