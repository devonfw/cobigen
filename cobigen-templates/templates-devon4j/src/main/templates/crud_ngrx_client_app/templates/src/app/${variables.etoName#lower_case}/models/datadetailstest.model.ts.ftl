/* @export
 * @class TestDataDetails
 */
export class TestDataDetails {
  <#list pojo.fields as field>
  ${field.name?uncap_first}?: ${JavaUtil.getAngularType(field.type)};
  </#list>
  id?: number;
  pageSize?: number = 8;
  pageSizes?: number[] = [8, 16, 24];
  selectedRow?: any;
  size?: number;
  page?: number;
  searchTerms?: any;
  sort?: any[];
}
export const generateUser: any = (): TestDataDetails => {
  return {
    id: 0,
    <#list pojo.fields as field>
    ${field.name?uncap_first}: <#if JavaUtil.getAngularType(field.type) == 'number'>20 <#else>'${field.name?uncap_first}'</#if>,
    </#list>
	pageSize: 8,
	pageSizes: [8, 16, 24],
	selectedRow: undefined,
	size: 8,
	page: 0,
	searchTerms: undefined,
	sort: undefined,
  };
};
