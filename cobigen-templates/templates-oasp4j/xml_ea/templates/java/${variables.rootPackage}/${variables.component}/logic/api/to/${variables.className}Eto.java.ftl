<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#include '/functions.ftl'>
<#assign name = elemDoc["self::node()/@name"]>
<#assign connectors = doc["xmi:XMI/xmi:Extension/connectors/connector"]>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import ${variables.rootPackage}.general.common.api.to.AbstractEto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.className};

import java.util.List;
import java.util.Set;

<#-- Class connections/associations -->
<#list connectors as connector>
    <#assign source = connector["source"]>
    <#assign target = connector["target"]> 
    <#-- We store the information of the connectors of this class to a variable -->
    ${OaspUtil.resolveConnectorsContent(source, target, name)}
</#list>

/**
 * Entity transport object of ${variables.className}
 */
public class ${variables.className}Eto extends <#if variables.className?contains("Entity")>${variables.className?replace("Entity","Eto")}<#else>AbstractEto</#if> implements ${variables.className} {

	private static final long serialVersionUID = 1L;

  <#-- Generates all the attributes defined for the class on the UML -->
	<@generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion/>

  <#-- Generates all the connnected classes -->
  <#-- For generating the variables and methods of all the connected classes to this class -->
    ${OaspUtil.generateConnectorsVariablesMethodsText()}

  <#-- Generates all the getters and setters of each attribute defined for the class on the UML -->
	<@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion/>

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        <#if elemDoc["self::node()/ownedAttribute"]?has_content>
        	<#list elemDoc["self::node()/ownedAttribute"] as field>
            <#assign fieldType=field["type/@xmi:idref"]?replace("EAJava_","")>
        		<#if JavaUtil.equalsJavaPrimitive(classObject,field["@name"])>
					result = prime * result + ${JavaUtil.castJavaPrimitives(classObject,field["@name"])}.hashCode();
				<#elseif fieldType?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
          <#if !fieldType?starts_with("List<") && !fieldType?starts_with("Set<")>
  					<#assign idVar = OaspUtil.resolveIdVariableName(classObject,field)>
  					result = prime * result + ((this.${idVar} == null) ? 0 : this.${idVar}.hashCode());
  				</#if>
    		<#else>
					result = prime * result + ((this.${field["@name"]} == null) ? 0 : this.${field["@name"]}.hashCode());
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
    ${variables.className}Eto other = (${variables.className}Eto) obj;
    <#list elemDoc["self::node()/ownedAttribute"] as field>
    <#if JavaUtil.equalsJavaPrimitive(classObject,field["@name"])>
		if(this.${field["@name"]} != other.${field["@name"]}) {
			return false;
		}
    <#elseif fieldType?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
      <#if !fieldType?starts_with("List<") && !fieldType?starts_with("Set<")>
    		<#assign idVar = OaspUtil.resolveIdVariableName(classObject,field)>
    		if (this.${idVar} == null) {
    		  if (other.${idVar} != null) {
    			return false;
    		  }
    		} else if(!this.${idVar}.equals(other.${idVar})){
    		  return false;
    		}
    </#if>
	<#else>
		if (this.${field["@name"]} == null) {
		  if (other.${field["@name"]} != null) {
			return false;
		  }
		} else if(!this.${field["@name"]}.equals(other.${field["@name"]})){
		  return false;
		}
    </#if>
    </#list>
    return true;
  }
}
