package ${variables.rootPackage}.${variables.component}.dataaccess.api;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import ${variables.rootPackage}.${variables.component}.dataaccess.api.repo.${variables.entityName?cap_first}Repo;

import ${variables.rootPackage}.${variables.component}.dataaccess.api.repo.${variables.entityName?cap_first}RepoImpl;

@Component
public class ${variables.entityName}EntityRegistrationBean {

  @Inject
  private ${variables.entityName?cap_first}Repo ${variables.entityName?lower_case}Repo;
  
  @Inject
  private ${variables.entityName?cap_first}RepoImpl ${variables.entityName?lower_case}RepoImpl;

  /**
   * The constructor.
   */
  public ${variables.entityName}EntityRegistrationBean() {}

  /**
   * @return ${variables.entityName?lower_case}Repo
   */
  public ${variables.entityName?cap_first}Repo get${variables.entityName?cap_first}Repo() {

    return this.${variables.entityName?lower_case}Repo;
  }

  /**
   * @param ${variables.entityName?lower_case}Repo the ${variables.entityName?lower_case}Repo to set
   */
  public void set${variables.entityName?cap_first}Repo(${variables.entityName?cap_first}Repo ${variables.entityName?lower_case}Repo) {

    this.${variables.entityName?lower_case}Repo = ${variables.entityName?lower_case}Repo;
  }
  
  /**
   * @return ${variables.entityName?lower_case}RepoImpl
   */
  public ${variables.entityName?cap_first}RepoImpl get${variables.entityName?cap_first}RepoImpl() {

    return this.${variables.entityName?lower_case}RepoImpl;
  }

  /**
   * @param ${variables.entityName?lower_case}RepoImpl the ${variables.entityName?lower_case}RepoImpl to set
   */
  public void set${variables.entityName?cap_first}RepoImpl(${variables.entityName?cap_first}RepoImpl ${variables.entityName?lower_case}RepoImpl) {

    this.${variables.entityName?lower_case}RepoImpl = ${variables.entityName?lower_case}RepoImpl;
  }

}