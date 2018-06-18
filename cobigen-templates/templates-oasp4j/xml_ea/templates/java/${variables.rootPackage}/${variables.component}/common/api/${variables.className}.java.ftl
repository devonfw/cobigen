<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>
<#include '/functions.ftl'>
<#compress>
<#assign name = elemDoc["self::node()/@name"]>

<#list connectors as connector>
    <#assign source = connector["source"]>
    <#assign target = connector["target"]> 
    ${OaspUtil.resolveConnectorsContent(source, target, name)}
</#list>

package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;

public interface ${name} extends ApplicationEntity {

${OaspUtil.generateConnectorsVariablesMethodsText(false)}

<#list elemDoc["./ownedAttribute"] as attribute>
  <#if (attribute["@name"])??>
    <#if (attribute["type/@xmi:idref"]) == "EAJava_int">
    public Integer get${attribute["@name"]?cap_first}();
    
    public void set${attribute["@name"]?cap_first}(Integer ${attribute["@name"]});
    <#else>    
    public ${(attribute["type/@xmi:idref"]?replace("EAJava_",""))?cap_first} get${attribute["@name"]?capitalize}();
    
    public void set${attribute["@name"]?cap_first}(${(attribute["type/@xmi:idref"]?replace("EAJava_",""))?capitalize});
    </#if>
  </#if>
</#list>

    <#-- Class connections/associations -->
   <#-- <#list connectors as connector>
        <#assign targetName = connector["target/model/@name"]> 
        <#assign sourceName = connector["source/model/@name"]> 
        <#if (connector["source/model/@type"] == "Class") || (connector["source/model/@type"]=="Class")>
            <#if ((sourceName) == '${variables.className}')>
              <#assign className=targetName>
              <#assign multiplicity=connector["target/type/@multiplicity"]>
            <#elseif ((targetName) == '${variables.className}')>
              <#assign className=sourceName>
              <#assign multiplicity=connector["source/type/@multiplicity"]>
            <#else>
              <#assign className="failure">
            </#if>
            <#if !(className=="failure")>
              <#if multiplicity?is_string>
                <#if (multiplicity == "1")>
    public ${className?cap_first} get${className?cap_first}();

    public void set${className?cap_first}(${className?cap_first} ${className?uncap_first});
                <#elseif (multiplicity == "*")>
    public List<${className?cap_first}> get${OaspUtil.removePlural(className?cap_first)}s();
    
    public void set${OaspUtil.removePlural(className?cap_first)}s(List<${className?cap_first}> ${OaspUtil.removePlural(className?uncap_first)}s);
                </#if>
              </#if>
            </#if>
          </#if>
    </#list> -->

}
</#compress>
