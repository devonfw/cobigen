<#macro definePropertyNameAndType property simpleType=false>
<#compress>
<#if property.isEntity>
 <#if property.isCollection>
  <#assign propType="List<Long>">
  <#assign propName=property.name?uncap_first + "Ids">
 <#else>
  <#assign propType="Long">
  <#assign propName=property.name?uncap_first + "Id">
 </#if>
<#else>
  <#assign propType=OpenApiUtil.toJavaType(property simpleType)>
  <#assign propName=property.name?uncap_first>
</#if>
</#compress>
</#macro>