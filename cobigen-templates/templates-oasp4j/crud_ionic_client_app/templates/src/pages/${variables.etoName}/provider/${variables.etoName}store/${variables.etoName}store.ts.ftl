import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

/*
  Generated class for the ${variables.etoName}storeProvider provider.

  See https://angular.io/guide/dependency-injection for more info on providers
  and Angular DI.
*/



@Injectable()
export class ${variables.etoName}storeProvider {

  
  
  
  Table : any;


  constructor(public http: HttpClient) {
    this.Table = [{<#list pojo.fields as field> ${field.name}:null,</#list>}];
  }

  setTable(table:any){
    this.Table = table;
  }

  getTable() : any {
    return this.Table;
  }
}
