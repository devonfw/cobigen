import { BusinessOperatorProvider } from '../providers/shared/business-operator'
import { Observable } from 'rxjs/Rx';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
// import { HTTP } from '@ionic-native/http';
/*
  Generated class for the ${variables.etoName?cap_first}Rest provider.

  See https://angular.io/guide/dependency-injection for more info on providers
  and Angular DI.
*/
@Injectable()
export class ${variables.etoName?cap_first}Rest {

  list : any;

  constructor(public http: HttpClient, public BO: BusinessOperatorProvider) {
    this.list = [{<#list pojo.fields as field> ${field.name}:null,</#list>}];
  }

  retrieveData(): Observable<any> {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service() + "search", {}, {});
  }

  Save(fullitem: any) {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service(), fullitem, {});
  }

  getItemId(searchitem: any): Observable<any> {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service() + "search", searchitem, {});
  }

  DeleteItem(id: any) {
    return this.http.delete(this.BO.${variables.etoName?uncap_first}Service() + id, {});
  }

  Filter(SinglePart: any) {
    return this.http.post(this.BO.${variables.etoName?uncap_first}Service() + "search", SinglePart, {})
  }

  setList(list:any){
    this.list = list;
  }

  getList() : any {
    return this.list;
  }
}
