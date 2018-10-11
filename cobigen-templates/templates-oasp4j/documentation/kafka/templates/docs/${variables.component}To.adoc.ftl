//anchor:${variables.component}kafka:override:anchorend
== Properties of ${variables.toName?cap_first}

Component Data
[options="header"]
|===
|Name |Description |Consumes |Produces
|${variables.component?cap_first}
|<#if pojo.javaDoc??>${JavaDocumentationUtil.getJavaDocWithoutLink(pojo.javaDoc.comment)}<#else>No javaDoc available</#if>
|<#if pojo.javaDoc.topic??>${JavaDocumentationUtil.getJavaDocWithoutLink(pojo.javaDoc.topic)}<#else>No topic defined</#if>
|===
    
=== Fields
<#assign declared=false>  
<#if pojo.fields?has_content>
  <#list pojo.fields as field>
    <#if field.javaDoc??>
      <#compress>
      <#if !declared>
        [options="header"]
        |===
        |Member name |javaDoc |Value |Required |Example |Datatype |Allows Empty Value
        <#assign declared=true>
      </#if>
      |${field.name}
      |${JavaDocumentationUtil.getJavaDocWithoutLink(field.javaDoc.comment)}
      |<#if field.javaDoc.value??>${JavaDocumentationUtil.getJavaDocWithoutLink(field.javaDoc.value)}<#else>-</#if>
      |<#if field.javaDoc.required??>${JavaDocumentationUtil.getJavaDocWithoutLink(field.javaDoc.required)}<#else>-</#if>
      |<#if field.javaDoc.example??>${JavaDocumentationUtil.getJavaDocWithoutLink(field.javaDoc.example)}<#else>-</#if>
      |<#if field.javaDoc.datatype??>${JavaDocumentationUtil.getJavaDocWithoutLink(field.javaDoc.datatype)}<#else>-</#if>
      |<#if field.javaDoc.emptyvalue??>${JavaDocumentationUtil.getJavaDocWithoutLink(field.javaDoc.emptyvalue)}<#else>-</#if>
      </#compress>
    </#if>
  </#list>
</#if>
<#if declared>
|===
<#assign declared=true>
</#if>