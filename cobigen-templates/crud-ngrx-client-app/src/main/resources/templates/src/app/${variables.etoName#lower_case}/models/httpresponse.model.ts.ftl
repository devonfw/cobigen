import { ${variables.etoName?cap_first}Model } from './${variables.etoName?lower_case}.model';
import { Pageable } from '../../shared/models/pageable';

/* @export
 * @interface HttpResponseModel
 */
export interface HttpResponseModel {
  content: ${variables.etoName?cap_first}Model[];
  pageable?: Pageable;
  totalElements: number;
}
