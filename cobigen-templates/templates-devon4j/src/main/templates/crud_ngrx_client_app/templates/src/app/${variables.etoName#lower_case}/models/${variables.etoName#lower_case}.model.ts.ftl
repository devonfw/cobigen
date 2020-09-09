/* @export
 * @interface ${variables.etoName?cap_first}Model
 */
export interface ${variables.etoName?cap_first}Model {
  <#list pojo.fields as field>
  ${field.name?uncap_first}?: ${JavaUtil.getAngularType(field.type)};
  </#list>
  id?: number;
  modificationCounter?: number;
  size?: number;
  page?: number;
  searchTerms?: any;
  sort?: any[];
}
