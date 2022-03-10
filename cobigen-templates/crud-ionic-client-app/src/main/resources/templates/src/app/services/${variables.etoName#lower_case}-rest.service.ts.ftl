import { BusinessOperatorService } from './shared/business-operator.service';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ${variables.etoName?cap_first}SearchCriteria } from './interfaces/${variables.etoName?lower_case}-search-criteria';
import { ${variables.etoName?cap_first} } from './interfaces/${variables.etoName?lower_case}';

/**
  Generated class for the ${variables.etoName?cap_first}RestService service. Implements the REST service.

  See https://angular.io/guide/dependency-injection for more info on providers
  and Angular DI.
*/
@Injectable({ providedIn: 'root' })
export class ${variables.etoName?cap_first}RestService {
  constructor(public http: HttpClient, public BO: BusinessOperatorService) {}

  /**
   * @param  ${variables.etoName?lower_case} The item in the list.
   * @returns The found ${variables.etoName?lower_case} from the server.
   */
  get${variables.etoName?cap_first}(${variables.etoName?lower_case}: ${variables.etoName?cap_first}): Observable<any> {
    return this.http.post(
      this.BO.${variables.etoName?lower_case}Service() + 'search',
      ${variables.etoName?lower_case},
      {},
    );
  }

  /**
   * @param  ${variables.etoName?lower_case} The ${variables.etoName?lower_case} to save to the database.
   * @returns The result of the save operation.
   */
  save(${variables.etoName?lower_case}: ${variables.etoName?cap_first}) {
    return this.http.post(this.BO.${variables.etoName?lower_case}Service(), ${variables.etoName?lower_case}, {});
  }

  /**
   * @param  ${variables.etoName?lower_case}SearchCriteria Object used for searching ${variables.etoName?lower_case}s by a criteria on the server.
   * @returns The first data page on the server.
   */
  retrieveData(
    ${variables.etoName?lower_case}SearchCriteria: ${variables.etoName?cap_first}SearchCriteria,
  ): Observable<any> {
    return this.http.post(
      this.BO.${variables.etoName?lower_case}Service() + 'search',
      ${variables.etoName?lower_case}SearchCriteria,
    );
  }

  /**
   * @param  ${variables.etoName?lower_case}SearchCriteria Object used for searching ${variables.etoName?lower_case}s by a criteria on the server.
   * @returns A list of the found ${variables.etoName?lower_case}s on the server.
   */
  search(${variables.etoName?lower_case}SearchCriteria: ${variables.etoName?cap_first}SearchCriteria) {
    return this.http.post(
      this.BO.${variables.etoName?lower_case}Service() + 'search',
      ${variables.etoName?lower_case}SearchCriteria,
      {},
    );
  }

  /**
   * @param  id The id of the ${variables.etoName?lower_case} to delete.
   * @returns The result of the delete operation.
   */
  delete(id: number) {
    return this.http.delete(this.BO.${variables.etoName?lower_case}Service() + id, {});
  }
}
