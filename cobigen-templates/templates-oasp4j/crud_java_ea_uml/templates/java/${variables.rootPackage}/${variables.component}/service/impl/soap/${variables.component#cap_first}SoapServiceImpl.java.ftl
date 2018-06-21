package ${variables.rootPackage}.${variables.component}.service.impl.soap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jws.WebService;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.web.context.WebApplicationContext;

import ${variables.rootPackage}.general.common.api.to.PaginatedListToWrapper;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;

import ${variables.rootPackage}.general.service.api.soap.${variables.component?cap_first}Service;
import ${variables.rootPackage}.general.service.impl.config.WebApplicationContextLocator;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import io.oasp.module.jpa.common.api.to.PaginatedListTo;

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
   public ${variables.className}Eto get${variables.className}(@WebParam(name = "id") long id){
    return this.${variables.component?lower_case}.find${variables.className}(id);
   }
 
   @Override
   public ${variables.className}Eto save${variables.className}(${variables.className}Eto ${variables.className?lower_case}){
      Object o = ${variables.className?lower_case}.getId();
      if (o != null) {
        ElementNSImpl ele = (ElementNSImpl) o;
        Long id = Long.parseLong(ele.getTextContent());
        ${variables.className?lower_case}.setId(id);
      }
      return this.${variables.component?lower_case}.save${variables.className}(${variables.className?lower_case});
    }
   
  @Override
  public void delete${variables.className}(@WebParam(name = "id") long id){
    this.${variables.component?lower_case}.delete${variables.className}(id);
  }
  
  @Override
  public PaginatedListToWrapper<${variables.className}Eto> find${variables.className}sByPost(${variables.className}SearchCriteriaTo searchCriteriaTo){
    PaginatedListTo<${variables.className}Eto> actualResult = this.${variables.component?lower_case}.find${variables.className}Etos(searchCriteriaTo);
    PaginatedListToWrapper<${variables.className}Eto> wrapper = new PaginatedListToWrapper<>();
    wrapper.setResult(actualResult.getResult());
    wrapper.setPagination(actualResult.getPagination());
    return wrapper;
  }
  
}
