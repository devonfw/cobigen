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
        <#if (connector["source/model/@type"] == "Class")>
            <#-- If I am the source connector, check target's multiplicity -->
            <#if ((sourceName) == '${variables.className}')>
                <#if (connector["target/type/@multiplicity"] )?is_string>
                    <#if (connector["target/type/@multiplicity"] == "1")>
    <#-- I want one (multiplicity == 1) -->

    public ${targetName?cap_first} get${targetName?cap_first}();

    public void set${targetName?cap_first}(${targetName?cap_first} ${targetName?uncap_first});
                    <#elseif (connector["target/type/@multiplicity"] == "*")>
    public List<${targetName?cap_first}> get${targetName?uncap_first}s();
    
    public void set${targetName?uncap_first}s(List<${${targetName?cap_first}>);
                    </#if>
                </#if>
            </#if>
        </#if>
        <#-- I am target -->
        <#if (connector["target/model/@type"] == "Class")>
            <#-- If I am the target connector, check sources' multiplicity -->
            <#if ((targetName) == '${variables.className}')>
                <#if (connector["source/type/@multiplicity"] )?is_string>
                    <#if (connector["source/type/@multiplicity"] == "1")>
    <#-- I want one (multiplicity == 1) -->
    public ${sourceName?cap_first} get${sourceName?cap_first}();

    public void set${sourceName?cap_first}(${sourceName?cap_first} ${sourceName?uncap_first});
                    <#elseif (connector["target/type/@multiplicity"] == "*")>
    public List<${sourceName?cap_first}> get${sourceName?uncap_first}s();
    
    public void set${sourceName?uncap_first}s(List<${${sourceName?cap_first}>);
                    </#if>
                </#if>
            </#if>
        </#if>
    </#list>

}
</#compress>
