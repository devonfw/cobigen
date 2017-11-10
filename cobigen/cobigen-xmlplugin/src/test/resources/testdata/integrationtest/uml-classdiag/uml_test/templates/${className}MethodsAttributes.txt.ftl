<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["/packagedElement/@name"]>

<#if (elemDoc["/packagedElement/ownedAttribute/@name"])?has_content>
    ${elemDoc["/packagedElement/ownedAttribute/@visibility"]} ${elemDoc["/packagedElement/ownedAttribute/type/@xmi:idref"]?replace("EAJava_","")} ${elemDoc["/packagedElement/ownedAttribute/@name"]}
</#if>

<#if (elemDoc["/packagedElement/ownedOperation/@name"])?has_content>
    ${elemDoc["/packagedElement/ownedOperation/@visibility"]} ${elemDoc["/packagedElement/ownedOperation/@name"]}
</#if>

</#compress>