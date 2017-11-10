<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["/packagedElement/@name"]>

public class ${variables.className} {

    <#list elemDoc["/packagedElement/ownedAttribute"] as attributes>
        <#if (attributes.name)?has_content>
    ${attributes.visibility} ${attributes.xmi:idref?replace("EAJava_","")} ${attributes.name};
        </#if>
    </#list>

    <#list elemDoc["/packagedElement/ownedOperation"] as operations>
        <#if (operations.name)?has_content>
    ${operations.visibility} ${operations.name}()
        </#if>
    </#list>
}

</#compress>