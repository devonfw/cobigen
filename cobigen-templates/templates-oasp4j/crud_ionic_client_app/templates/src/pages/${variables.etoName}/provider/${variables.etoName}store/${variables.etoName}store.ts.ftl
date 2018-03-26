import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

/*
	Generated class for the ${variables.etoName}storeProvider provider.

	See https://angular.io/guide/dependency-injection for more info on providers
	and Angular DI.
*/



@Injectable()
export class ${variables.etoName}storeProvider {

	
	List : any;


	constructor(public http: HttpClient) {
		this.List = [{<#list pojo.fields as field> ${field.name}:null,</#list>}];
	}

	setList(list:any){
		this.List = list;
	}

	getList() : any {
		return this.List;
	}
}
