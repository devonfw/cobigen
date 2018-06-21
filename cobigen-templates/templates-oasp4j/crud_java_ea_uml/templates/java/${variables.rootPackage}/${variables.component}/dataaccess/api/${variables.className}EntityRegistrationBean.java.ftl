package ${variables.rootPackage}.${variables.component}.dataaccess.api;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import ${variables.rootPackage}.${variables.component}.dataaccess.api.repo.${variables.className?cap_first}Repo;

import ${variables.rootPackage}.${variables.component}.dataaccess.api.repo.${variables.className?cap_first}RepoImpl;

@Component
public class ${variables.className}EntityRegistrationBean {

  @Inject
  private ${variables.className?cap_first}Repo ${variables.className?lower_case}Repo;
  
  @Inject
  private ${variables.className?cap_first}RepoImpl ${variables.className?lower_case}RepoImpl;

  /**
   * The constructor.
   */
  public ${variables.className}EntityRegistrationBean() {}

  /**
   * @return ${variables.className?lower_case}Repo
   */
  public ${variables.className?cap_first}Repo get${variables.className?cap_first}Repo() {

    return this.${variables.className?lower_case}Repo;
  }

  /**
   * @param ${variables.className?lower_case}Repo the ${variables.className?lower_case}Repo to set
   */
  public void set${variables.className?cap_first}Repo(${variables.className?cap_first}Repo ${variables.className?lower_case}Repo) {

    this.${variables.className?lower_case}Repo = ${variables.className?lower_case}Repo;
  }
  
  /**
   * @return ${variables.className?lower_case}RepoImpl
   */
  public ${variables.className?cap_first}RepoImpl get${variables.className?cap_first}RepoImpl() {

    return this.${variables.className?lower_case}RepoImpl;
  }

  /**
   * @param ${variables.className?lower_case}RepoImpl the ${variables.className?lower_case}RepoImpl to set
   */
  public void set${variables.className?cap_first}RepoImpl(${variables.className?cap_first}RepoImpl ${variables.className?lower_case}RepoImpl) {

    this.${variables.className?lower_case}RepoImpl = ${variables.className?lower_case}RepoImpl;
  }

}