export interface ${variables.etoName?cap_first} {
    id?: number;
    modificationCounter?: number;
    revision?: number;
  <#list pojo.fields as field>
    ${field.name}:${JavaUtil.getAngularType(field.type)},
    </#list>
}