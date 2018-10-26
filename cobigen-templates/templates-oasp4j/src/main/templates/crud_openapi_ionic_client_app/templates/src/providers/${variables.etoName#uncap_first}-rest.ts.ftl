import { BusinessOperatorProvider } from '../providers/shared/business-operator'
import { Observable } from 'rxjs/Rx';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ${variables.etoName?cap_first}SearchCriteria } from './interfaces/${variables.etoName?uncap_first}-search-criteria';
import { ${variables.etoName?cap_first} } from './interfaces/${variables.etoName?uncap_first}';
// import { HTTP } from '@ionic-native/http';
/**
  Generated class for the ${variables.etoName?cap_first}Rest provider. Implements the REST service.

  See https://angular.io/guide/dependency-injection for more info on providers
  and Angular DI.
*/
@Injectable()
export class ${variables.etoName?cap_first}Rest {

  constructor(public http: HttpClient, public BO: BusinessOperatorProvider) {
  }

  /**
  * @param  ${variables.etoName?uncap_first} The item in the list.
  * @returns The found ${variables.etoName?uncap_first} from the server.
  */
  get${variables.etoName?cap_first}(${variables.etoName?uncap_first}: ${variables.etoName?cap_first}): Observable<any> {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service() + "search", ${variables.etoName?uncap_first}, {});
  }

  /**
  * @param  ${variables.etoName?uncap_first} The ${variables.etoName?uncap_first} to save to the database.
  * @returns The result of the save operation.
  */
  save(${variables.etoName?uncap_first}: ${variables.etoName?cap_first}) {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service(), ${variables.etoName?uncap_first}, {});
  }

  /**
  * @param  ${variables.etoName?uncap_first}SearchCriteria Object used for searching ${variables.etoName?uncap_first}s by a criteria on the server.
  * @returns The first data page on the server.
  */
  retrieveData(${variables.etoName?uncap_first}SearchCriteria : ${variables.etoName?cap_first}SearchCriteria): Observable<any> {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service() + "search", ${variables.etoName?uncap_first}SearchCriteria);
  }

  /**
  * @param  ${variables.etoName?uncap_first}SearchCriteria Object used for searching ${variables.etoName?uncap_first}s by a criteria on the server.
  * @returns A list of the found ${variables.etoName?uncap_first}s on the server.
  */
  search(${variables.etoName?uncap_first}SearchCriteria: ${variables.etoName?cap_first}SearchCriteria) {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service() + "search", ${variables.etoName?uncap_first}SearchCriteria, {})
  }

  /**
  * @param  id The id of the ${variables.etoName?uncap_first} to delete.
  * @returns The result of the delete operation.
  */
  delete(id: number) {
    return this.http.delete(this.BO.${variables.etoName?uncap_first}Service() + id, {});
  }

}
