package ${variables.rootPackage}.${variables.component}.service.impl.soap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jws.WebService;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.web.context.WebApplicationContext;

import ${variables.rootPackage}.general.common.api.to.PaginatedListToWrapper;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;

import ${variables.rootPackage}.general.service.api.soap.${variables.component?cap_first}Service;
import ${variables.rootPackage}.general.service.impl.config.WebApplicationContextLocator;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import org.springframework.data.domain.Page;
<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null">
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>
/**
 * @author riraman
 *
 */
@Named("${variables.component?cap_first}SoapService")
@WebService(endpointInterface = "${variables.rootPackage}.${variables.component}.service.api.soap.${variables.component?cap_first}SoapService")
public class ${variables.component?cap_first}SoapServiceImpl implements ${variables.component?cap_first}SoapService {

  @Inject
  private ${variables.component?cap_first} ${variables.component?lower_case};

    public ${variables.component?cap_first}SoapServiceImpl() {
      AutowiredAnnotationBeanPostProcessor aabpp = new AutowiredAnnotationBeanPostProcessor();
      WebApplicationContext currentContext = WebApplicationContextLocator.getCurrentWebApplicationContext();
      if (currentContext != null) {
        aabpp.setBeanFactory(currentContext.getAutowireCapableBeanFactory());
        aabpp.processInjection(this);
      }
    }

   @Override
   public ${variables.entityName}Eto get${variables.entityName}(@WebParam(name = "id") <#if compositeIdTypeVar!="null">${compositeIdTypeVar}<#else>long</#if> id){
    return this.${variables.component?lower_case}.find${variables.entityName}(id);
   }
 
   @Override
   public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case}){
      Object o = ${variables.entityName?lower_case}.getId();
      if (o != null) {
        ElementNSImpl ele = (ElementNSImpl) o;
        Long id = Long.parseLong(ele.getTextContent());
        ${variables.entityName?lower_case}.setId(id);
      }
      return this.${variables.component?lower_case}.save${variables.entityName}(${variables.entityName?lower_case});
    }
   
  @Override
  public void delete${variables.entityName}(@WebParam(name = "id") <#if compositeIdTypeVar!="null">${compositeIdTypeVar}<#else>long</#if> id){
    this.${variables.component?lower_case}.delete${variables.entityName}(id);
  }
  
  @Override
  public PaginatedListToWrapper<${variables.entityName}Eto> find${variables.entityName}sByPost(${variables.entityName}SearchCriteriaTo searchCriteriaTo){
    Page<${variables.entityName}Eto> actualResult = this.${variables.component?lower_case}.find${variables.entityName}Etos(searchCriteriaTo);
    PaginatedListToWrapper<${variables.entityName}Eto> wrapper = new PaginatedListToWrapper<>();
    wrapper.setResult(actualResult.getContent());
    wrapper.setPagination(actualResult.getPageable());
    return wrapper;
  }
  
}
