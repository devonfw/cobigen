import { BusinessOperatorProvider } from '../../../../providers/shared/business-operator'
import { Observable } from 'rxjs/Rx';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
// import { HTTP } from '@ionic-native/http';
/*
	Generated class for the ${variables.etoName}businessProvider provider.

	See https://angular.io/guide/dependency-injection for more info on providers
	and Angular DI.
*/
@Injectable()
export class ${variables.etoName}BusinessProvider {

	constructor(public http: HttpClient, public BO: BusinessOperatorProvider) {
	}

	getTableM(): Observable<any> {
		return this.http.post(this.BO.${variables.etoName}Service() + "search", {}, {});
	}

	Save(fullitem: any) {
		return this.http.post(this.BO.${variables.etoName}Service(), fullitem, {});
	}

	getItemId(searchitem: any): Observable<any> {
		return this.http.post(this.BO.${variables.etoName}Service() + "search", searchitem, {});
	}

	DeleteItem(id: any) {
		return this.http.delete(this.BO.${variables.etoName}Service() + id, {});
	}

	Filter(SinglePart: any) {
		return this.http.post(this.BO.${variables.etoName}Service() + "search", SinglePart, {})
	}

}
