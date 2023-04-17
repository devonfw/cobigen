import { Pageable } from './pageable';

/** Interface used for searching ${variables.etoName?lower_case}s by criteria on the server.*/
export interface ${variables.etoName?cap_first}SearchCriteria {
  <#list pojo.fields as field>
  ${field.name}: ${JavaUtil.getAngularType(field.type)};
  </#list>
  pageable: Pageable;
}
