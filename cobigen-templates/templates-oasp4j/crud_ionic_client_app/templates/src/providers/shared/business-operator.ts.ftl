
import { Injectable } from '@angular/core';
import {url} from '../../assets/serverPath';
/*
  Generated class for the BussinessOperatorProvider provider.

  See https://angular.io/guide/dependency-injection for more info on providers
  and Angular DI.
*/
@Injectable()
export class BusinessOperatorProvider {

  public serverPath = url;
  public restPath = url + 'services/rest/';


  constructor() { }

  login() {
    return this.restPath + 'login';
  }
  logout() {
    return this.restPath + 'logout';
  }
  getCsrf() {
    return this.restPath + 'security/v1/csrftoken';
  }

  ${variables.etoName}Service(){
    return this.restPath + '${variables.component}/v1/${variables.etoName?uncap_first}/';
  }

}