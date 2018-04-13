export interface ${variables.etoName?cap_first} {
  <#list pojo.fields as field>
    ${field.name}:${JavaUtil.getAngularType(field.type)},
    </#list>
}