<#import '/variables.ftl' as class>
<#compress>
<#assign seen_properties=[]>
<#list class.properties as property>
  <#if seen_properties?seq_contains(property.type) == false>
    <#if property.type.module??>
      <#assign isEntity = property.type.module?ends_with("entity")>
      <#if isEntity> export { ${property.type.name?cap_first} } from './${property.type.name}'; </#if>
    </#if>
      
      <#assign seen_properties = seen_properties + [property.type.name]>
  </#if>
</#list>
</#compress>
