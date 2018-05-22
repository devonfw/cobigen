/** The ${variables.etoName?uncap_first} class from the server. */
export interface ${variables.etoName?cap_first} {
    id?: number;
    modificationCounter?: number;
    revision?: number;
  <#list pojo.fields as field>
    ${field.name}:${JavaUtil.getAngularType(field.type)},
    </#list>
}