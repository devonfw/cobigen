== ApiModelProperties of ${variables.toName?cap_first}

Component Data
[options="header"]
|===
|Name |Description |Consumes |Produces
|${variables.component?cap_first}
|<#if pojo.javaDoc??>${JavaUtil.getJavaDocWithoutLink(pojo.javaDoc.comment)}<#else>No javaDoc available</#if>
|<#if pojo.javaDoc.topic??>${JavaUtil.getJavaDocWithoutLink(pojo.javaDoc.topic)}<#else>No topic defined</#if>
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
      |${JavaUtil.getJavaDocWithoutLink(field.javaDoc.comment)}
      |<#if field.javaDoc.value??>${JavaUtil.getJavaDocWithoutLink(field.javaDoc.value)}<#else>-</#if>
      |<#if field.javaDoc.required??>${JavaUtil.getJavaDocWithoutLink(field.javaDoc.required)}<#else>-</#if>
      |<#if field.javaDoc.example??>${JavaUtil.getJavaDocWithoutLink(field.javaDoc.example)}<#else>-</#if>
      |<#if field.javaDoc.datatype??>${JavaUtil.getJavaDocWithoutLink(field.javaDoc.datatype)}<#else>-</#if>
      |<#if field.javaDoc.emptyvalue??>${JavaUtil.getJavaDocWithoutLink(field.javaDoc.emptyvalue)}<#else>-</#if>
      </#compress>
    </#if>
  </#list>
</#if>
<#if declared>
|===
<#assign declared=true>
</#if>