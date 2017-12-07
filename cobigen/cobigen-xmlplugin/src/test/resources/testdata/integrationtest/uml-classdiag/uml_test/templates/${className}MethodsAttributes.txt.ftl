<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["./@name"]>

<#if (elemDoc["./ownedAttribute/@name"])?has_content>
    ${elemDoc["./ownedAttribute/@visibility"]} ${elemDoc["./ownedAttribute/type/@xmi:idref"]?replace("EAJava_","")} ${elemDoc["./ownedAttribute/@name"]}
</#if>

<#if (elemDoc["./ownedOperation/@name"])?has_content>
    ${elemDoc["./ownedOperation/@visibility"]} ${elemDoc["./ownedOperation/@name"]}
</#if>

</#compress>