<#list pojo.fields as field>
  <#if field.name="testField">
    {name: '${field.name}', type: '${field.type}'}
  </#if>
</#list>