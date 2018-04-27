<#compress>
  :toc:

  == ApiModelProperties of ${variables.toName?cap_first}

  Component Data
  [options="header"]
  |===
  |Name |JavaDoc
  |${variables.component?cap_first}
  |<#if pojo.javaDoc??>${pojo.javaDoc.comment}<#else>No javaDoc available</#if>
  |===
    
  === Fields
    
  [options="header"]
  |===
  |Member name |javaDoc |Value |Required |Example |Datatype |Allows Empty Value
  <#if pojo.fields?has_content>
    <#list pojo.fields as field>
      <#if field.annotations.io_swagger_annotations_ApiModelProperty??>
        |${field.name}
        |<#if field.javaDoc??>${field.javaDoc.comment}<#else>No JavaDoc available</#if>
        |<#if field.annotations.io_swagger_annotations_ApiModelProperty.value??>${field.annotations.io_swagger_annotations_ApiModelProperty.value}<#else>No Information available</#if>
        |<#if field.annotations.io_swagger_annotations_ApiModelProperty.required??>${field.annotations.io_swagger_annotations_ApiModelProperty.required}<#else>No Information available</#if>
        |<#if field.annotations.io_swagger_annotations_ApiModelProperty.example??>${field.annotations.io_swagger_annotations_ApiModelProperty.example}<#else>No Information available</#if>
        |<#if field.annotations.io_swagger_annotations_ApiModelProperty.dataType??>${field.annotations.io_swagger_annotations_ApiModelProperty.dataType}<#else>No information available</#if>
        |<#if field.annotations.io_swagger_annotations_ApiModelProperty.allowEmptyValue??>${field.annotations.io_swagger_annotations_ApiModelProperty.allowEmptyValue}<#else>No information available</#if>
      </#if>
    </#list>
  </#if>
  |===
</#compress>