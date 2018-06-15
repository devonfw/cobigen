<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>
<#compress>
<#assign name = elemDoc["self::node()/@name"]>
<#assign connectors = doc["xmi:XMI/xmi:Extension/connectors/connector"]>
package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;

public interface ${name} extends ApplicationEntity {


<#list elemDoc["./ownedAttribute"] as attribute>
        <#if (attribute["@name"])??>
            <#if (attribute["type/@xmi:idref"]) == "EAJava_int">
    public Integer get${attribute["@name"]?cap_first}();
            <#else>    
    public ${(attribute["type/@xmi:idref"]?replace("EAJava_",""))?cap_first} get${attribute["@name"]?capitalize}();
            </#if>
            <#if (attribute["type/@xmi:idref"]) == "EAJava_int">
    public void set${attribute["@name"]?cap_first}(Integer ${attribute["@name"]});
            <#else>    
    public void set${attribute["@name"]?cap_first}(${(attribute["type/@xmi:idref"]?replace("EAJava_",""))?capitalize});
            </#if>
        </#if>
    </#list>

    <#-- Class connections/associations -->
    <#list connectors as connector>
        <#assign targetName = connector["target/model/@name"]> 
        <#assign sourceName = connector["source/model/@name"]> 
        <#-- I am source -->
        <#if (connector["source/model/@type"] == "Class") || (connector["source/model/@type"]=="Class")>
            <#-- If I am the source connector, check target's multiplicity -->
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
    public List<${className?cap_first}> get${className?uncap_first}s();
    
    public void set${className?cap_first}s(List<${className?cap_first}> ${className?uncap_first});
                </#if>
              </#if>
            </#if>
          </#if>
    </#list>

}
</#compress>
