package ${variables.rootPackage}.${variables.component}.dataaccess.api.repo;

import io.oasp.module.jpa.common.api.to.PaginatedListTo;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName?cap_first}SearchCriteriaTo;

public interface ${variables.entityName?cap_first}RepoCustom {

  PaginatedListTo find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria);

}