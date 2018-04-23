import { Pagination } from "./pagination";

export interface ${variables.etoName?cap_first}SearchCriteria {
  <#list pojo.fields as field>
    ${field.name}:${JavaUtil.getAngularType(field.type)},</#list>
    pagination: Pagination,
}