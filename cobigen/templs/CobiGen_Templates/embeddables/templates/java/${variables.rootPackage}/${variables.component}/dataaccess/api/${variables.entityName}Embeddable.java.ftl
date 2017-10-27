<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.dataaccess.api;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};

import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * Data access object for ${variables.entityName} entities
 */
@Embeddable
public class ${pojo.name} implements ${variables.entityName} {

  private static final long serialVersionUID = 1L;

<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references -->
  @Override
  @Transient
  public ${OaspUtil.getSimpleEntityTypeAsLongReference(field)} ${OaspUtil.resolveIdGetter(field,false,variables.component)} {

  if (this.${field.name} == null) {
      return null;
    }
    return this.${field.name}.getId();
  }

  <#assign idVar = OaspUtil.resolveIdVariableName(classObject,field)>
  @Override
  public void ${OaspUtil.resolveIdSetter(field,false,variables.component)}(${OaspUtil.getSimpleEntityTypeAsLongReference(field)} ${idVar}) {

    if (${idVar} == null) {
      this.${field.name} = null;
    } else {
      ${field.type} ${field.type?uncap_first} = new ${field.type}();
      ${field.type?uncap_first}.setId(${idVar});
      this.${field.name} = ${field.type?uncap_first};
    }
  }
</#if>
</#list>

}
