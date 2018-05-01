import { BusinessOperatorProvider } from '../providers/shared/business-operator'
import { Observable } from 'rxjs/Rx';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ${variables.etoName?cap_first}SearchCriteria } from './interfaces/${variables.etoName?uncap_first}-search-criteria';
import { ${variables.etoName?cap_first} } from './interfaces/${variables.etoName?uncap_first}';
// import { HTTP } from '@ionic-native/http';
/*
  Generated class for the ${variables.etoName?cap_first}Rest provider.

  See https://angular.io/guide/dependency-injection for more info on providers
  and Angular DI.
*/
@Injectable()
export class ${variables.etoName?cap_first}Rest {

  constructor(public http: HttpClient, public BO: BusinessOperatorProvider) {
  }

  get${variables.etoName?cap_first}(${variables.etoName?uncap_first}: ${variables.etoName?cap_first}): Observable<any> {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service() + "search", ${variables.etoName?uncap_first}, {});
  }

  save(${variables.etoName?uncap_first}: ${variables.etoName?cap_first}) {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service(), ${variables.etoName?uncap_first}, {});
  }

  retrieveData(${variables.etoName?uncap_first}SearchCriteria : ${variables.etoName?cap_first}SearchCriteria): Observable<any> {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service() + "search", ${variables.etoName?uncap_first}SearchCriteria);
  }

  search(${variables.etoName?uncap_first}SearchCriteria: ${variables.etoName?cap_first}SearchCriteria) {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service() + "search", ${variables.etoName?uncap_first}SearchCriteria, {})
  }

  delete(id: number) {
    return this.http.delete(this.BO.${variables.etoName?uncap_first}Service() + id, {});
  }

}
