export interface ${variables.etoName}Item {
  <#list pojo.fields as field>
    ${field.name}:<#if (field.type=="long"||field.type=="int")> number <#else> ${field.type} </#if>,
    </#list>
}
