package ${variables.rootPackage}.${variables.component}.dataaccess.api.repo;

import io.oasp.module.jpa.common.api.to.PaginatedListTo;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className?cap_first}SearchCriteriaTo;

public interface ${variables.className?cap_first}RepoCustom {

  PaginatedListTo find${variables.className}s(${variables.className}SearchCriteriaTo criteria);

}