<#list pojo.fields as field>
  <#if field.name="testField">
    ${field.name}
  </#if>
</#list>