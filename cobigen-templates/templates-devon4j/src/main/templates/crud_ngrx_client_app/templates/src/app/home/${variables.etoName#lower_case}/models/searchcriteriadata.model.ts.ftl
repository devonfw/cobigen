import { ${variables.etoName?cap_first}Model } from './${variables.etoName?lower_case}.model';

/* @export
 * @interface SearchCriteriaDataModel
 */
export interface SearchCriteriaDataModel {
  criteria: {};
  data: ${variables.etoName?cap_first}Model;
}
