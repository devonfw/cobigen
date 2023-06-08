//anchor:${variables.component}kafka:override:anchorend
== Properties of ${variables.toName?cap_first}

Component Data
[options="header"]
|===
|Name |Description |Topic
|${variables.component?cap_first}
|<#if pojo.javaDoc??>${JavaDocumentationUtil.getJavaDocWithoutLink(pojo.javaDoc.comment)}<#else>No javaDoc available</#if>
|<#if pojo.javaDoc??><#if pojo.javaDoc.topic??>${JavaDocumentationUtil.getJavaDocWithoutLink(pojo.javaDoc.topic)}<#else>-</#if><#else>-</#if>
|===
    
=== Fields

[cols="1s,5"]
|===
|Body a|
<#if pojo.fields?size gt 0>${JavaDocumentationUtil.getJSON(classObject)}<#else>-</#if>
|Parameter list a|
[options='header']
!===
!Name !Description !Datatype !Required !Example
<#list pojo.fields as field>
  <#compress>
  !<#if field.name??>${field.name}<#else>-</#if>
  !<#if field.javaDoc??>${JavaDocumentationUtil.getJavaDocWithoutLink(field.javaDoc.comment)}<#else>-</#if>
  !<#if field.type??>${field.type}<#else>-</#if>
  !<#if field.javaDoc??><#if field.javaDoc.required??>${field.javaDoc.required}<#else>-</#if><#else>-</#if>
  !<#if field.javaDoc??><#if field.javaDoc.example??>${field.javaDoc.example}<#else>-</#if><#else>-</#if>
  </#compress>
</#list>
!===
|===