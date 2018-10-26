import { Injectable } from '@angular/core';
import {url} from '../../assets/serverPath';
/*
  Generated class for the BussinessOperator provider.

  See https://angular.io/guide/dependency-injection for more info on providers
  and Angular DI.
*/
@Injectable()
export class BusinessOperatorProvider {

  public serverPath = url;
  public restPath = url + 'services/rest/';


  constructor() { }

    /**
    * @returns The url to the login service.
    */
    login() {
        return this.restPath + 'login';
    }
    
    /**
    * @returns The url to the logout service.
    */
    logout() {
        return this.restPath + 'logout';
    }

    /**
    * @returns The url to the csrf token service.
    */
    getCsrf() {
        return this.restPath + 'security/v1/csrftoken';
    }

    /**
    * @returns The url to the ${variables.etoName?uncap_first} management service.
    */
    ${variables.etoName?uncap_first}Service(){
        return this.restPath + '${variables.component?uncap_first}/v1/${variables.etoName?uncap_first}/';
    }

}