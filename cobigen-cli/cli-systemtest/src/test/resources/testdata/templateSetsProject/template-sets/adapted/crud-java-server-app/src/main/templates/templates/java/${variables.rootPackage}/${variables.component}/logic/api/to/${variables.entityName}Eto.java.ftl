<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import com.devonfw.module.basic.common.api.to.AbstractEto;
import com.devonfw.module.basic.common.api.entity.PersistenceEntity;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};

import java.util.List;
import java.util.Set;

<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null">
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>
/**   
 * Entity transport object of ${variables.entityName}
 */
public class ${variables.entityName}Eto <#if compositeIdTypeVar=="null"> extends <#if pojo.extendedType.canonicalName=="java.lang.Object" || pojo.extendedType.package!=pojo.package || pojo.extendedType.name == "ApplicationPersistenceEntity">AbstractEto<#else>${pojo.extendedType.name?replace("Entity","Eto")}</#if> </#if> implements ${variables.entityName} <#if compositeIdTypeVar!="null">, PersistenceEntity<${compositeIdTypeVar}></#if> { 


	private static final long serialVersionUID = 1L;

	<@generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion/>

	<@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion/>

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        <#if pojo.fields?has_content>
        	<#list pojo.fields as field>
        		<#if JavaUtil.equalsJavaPrimitive(classObject,field.name)>
					result = prime * result + ${JavaUtil.castJavaPrimitives(classObject,field.name)}.hashCode();
				<#elseif field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
          <#if !field.type?starts_with("List<") && !field.type?starts_with("Set<")>
  					<#assign idVar = DevonfwUtil.resolveIdVariableName(classObject,field)>
  					result = prime * result + ((this.${idVar} == null) ? 0 : this.${idVar}.hashCode());
  				</#if>
    		<#else>
					result = prime * result + ((this.${field.name} == null) ? 0 : this.${field.name}.hashCode());
        		</#if>
        	</#list>
        <#else>
        result = prime * result;
        </#if>
        return result;
    }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    // class check will be done by super type EntityTo!
    if (!super.equals(obj)) {
      return false;
    }
    ${variables.entityName}Eto other = (${variables.entityName}Eto) obj;
    <#list pojo.fields as field>
    <#if JavaUtil.equalsJavaPrimitive(classObject,field.name)>
		if(this.${field.name} != other.${field.name}) {
			return false;
		}
    <#elseif field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
      <#if !field.type?starts_with("List<") && !field.type?starts_with("Set<")>
    		<#assign idVar = DevonfwUtil.resolveIdVariableName(classObject,field)>
    		if (this.${idVar} == null) {
    		  if (other.${idVar} != null) {
    			return false;
    		  }
    		} else if(!this.${idVar}.equals(other.${idVar})){
    		  return false;
    		}
    </#if>
	<#else>
		if (this.${field.name} == null) {
		  if (other.${field.name} != null) {
			return false;
		  }
		} else if(!this.${field.name}.equals(other.${field.name})){
		  return false;
		}
    </#if>
    </#list>
    return true;
  }
}
