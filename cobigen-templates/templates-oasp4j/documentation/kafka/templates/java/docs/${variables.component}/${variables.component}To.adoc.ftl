<#compress>
  == ApiModelProperties of ${variables.toName?cap_first}

  Component Data
  [options="header"]
  |===
  |Name |General JavaDoc |Topic
  |${variables.component?cap_first}
  |<#if pojo.javaDoc??>${pojo.javaDoc.comment}<#else>No javaDoc available</#if>
  |<#if pojo.javaDoc.topic??>${pojo.javaDoc.topic}<#else>No topic defined</#if>
  |===
    
  === Fields
  <#assign declared=false>  
  <#if pojo.fields?has_content>
    <#list pojo.fields as field>
      <#if field.javaDoc??>
        <#if !declared>
          [options="header"]
          |===
          |Member name |javaDoc |Value |Required |Example |Datatype |Allows Empty Value
          <#assign declared=true>
        </#if>
        |${field.name}
        |${field.javaDoc.comment}
        |<#if field.javaDoc.value??>${field.javaDoc.value}<#else>No Information available</#if>
        |<#if field.javaDoc.required??>${field.javaDoc.required}<#else>No Information available</#if>
        |<#if field.javaDoc.example??>${field.javaDoc.example}<#else>No Information available</#if>
        |<#if field.javaDoc.datatype??>${field.javaDoc.datatype}<#else>No information available</#if>
        |<#if field.javaDoc.emptyvalue??>${field.javaDoc.emptyvalue}<#else>No information available</#if>
      </#if>
    </#list>
  </#if>
  <#if declared>
    |===
    <#assign declared=true>
  </#if>
</#compress>