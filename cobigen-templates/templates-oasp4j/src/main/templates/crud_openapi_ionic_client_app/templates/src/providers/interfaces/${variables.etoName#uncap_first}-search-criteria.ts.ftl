import { Pagination } from "./pagination";

/** Interface used for searching ${variables.etoName?uncap_first}s by criteria on the server.*/
export interface ${variables.etoName?cap_first}SearchCriteria {
  <#list model.properties as field>
    ${field.name}:${JavaUtil.getAngularType(field.type)},
    </#list>
    pagination: Pagination,
}