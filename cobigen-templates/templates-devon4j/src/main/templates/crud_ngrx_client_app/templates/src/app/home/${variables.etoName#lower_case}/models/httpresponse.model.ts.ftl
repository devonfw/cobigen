import { ${variables.etoName?cap_first}Model } from './${variables.etoName?lower_case}.model';

/* @export
 * @interface HttpResponseModel
 */
export interface HttpResponseModel {
  content: ${variables.etoName?cap_first}Model[];
  totalElements: number;
}
