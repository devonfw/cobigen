<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.dataaccess.api;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.general.dataaccess.api.ApplicationPersistenceEntity;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Data access object for ${variables.entityName} entities
 */
@Entity
@javax.dataaccess.Table(name = "${variables.entityName}")
public class ${pojo.name} extends ApplicationPersistenceEntity implements ${variables.entityName} {

  private static final long serialVersionUID = 1L;

<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references -->
   <#if !field.type?starts_with("List<") && !field.type?starts_with("Set<")>
      @Override
      @Transient
      public ${OaspUtil.getSimpleEntityTypeAsLongReference(field)} ${OaspUtil.resolveIdGetter(field, false,"")} {
    
      if (this.${field.name} == null) {
          return null;
        }
        return this.${field.name}.getId();
      }
    
      <#assign idVar = OaspUtil.resolveIdVariableName(classObject,field)>
      @Override
      public void ${OaspUtil.resolveIdSetter(field,false,"")}(${OaspUtil.getSimpleEntityTypeAsLongReference(field)} ${idVar}) {
    
        if (${idVar} == null) {
          this.${field.name} = null;
        } else {
          ${field.type} ${field.type?uncap_first} = new ${field.type}();
          ${field.type?uncap_first}.setId(${idVar});
          this.${field.name} = ${field.type?uncap_first};
        }
      }
    </#if>
</#if>
</#list>

}
