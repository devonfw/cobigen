<#compress>
<#assign seen_properties=[]>
<#list model.properties as property>
  <#if seen_properties?seq_contains(property.type) == false>
      <#if property.isEntity> export { ${property.type?cap_first} } from './${property.type}'; </#if>
      <#assign seen_properties = seen_properties + [property.type]>
  </#if>
</#list>
</#compress>
