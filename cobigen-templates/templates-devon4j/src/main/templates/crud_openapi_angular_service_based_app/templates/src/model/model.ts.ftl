<#compress>
<#list model.properties as property>
    <#if property.isEntity> export { ${property.type?cap_first} } from './${property.type}'; </#if>
</#list>
</#compress>
