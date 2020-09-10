import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SearchCriteria } from '../../shared/models/search-criteria';
import { HttpResponseModel } from '../models/httpresponse.model';
import { ${variables.etoName?cap_first}Model } from '../models/${variables.etoName?lower_case}.model';


@Injectable({
  providedIn: 'root',
})
export class ${variables.etoName?cap_first}Service {
  private urlService: string = 
    environment.restServiceRoot +  '${variables.component?lower_case}/v1/${variables.etoName?lower_case}/';

  /* Creates an instance of ${variables.etoName?cap_first}Service.
   * @param {HttpClient} http
   * @param {Router} router
   * @memberof ${variables.etoName?cap_first}Service
   */
  constructor(private http: HttpClient, public router: Router) {}

  /* @param {number} size
   * @param {number} page
   * @param {*} searchTerms
   * @param {any[]} sort
   * @returns {Observable<SampleDataModel[]>}
   * @memberof SampleDataService
   */
   get${variables.etoName?cap_first}(
    size: number,
    page: number,
    searchTerms: any,
    sort: any[],
  ): Observable<HttpResponseModel> {
    const searchCriteria: SearchCriteria = {
      pageable: {
        pageSize: size,
        pageNumber: page,
        sort: sort,
      },
    <#list pojo.fields as field>
      ${field.name?uncap_first}: searchTerms.${field.name?uncap_first},
    </#list>
    };

    return this.http.post<HttpResponseModel>(
      this.urlService + 'search',
      searchCriteria,
    );
  }
  

  /* @param {*} data
   * @returns {Observable<Object>}
   * @memberof ${variables.etoName?cap_first}Service
   */
  save${variables.etoName?cap_first}(data: ${variables.etoName?cap_first}Model): Observable<Object> {
    const obj: ${variables.etoName?cap_first}Model = {
      id: data.id,
    <#list pojo.fields as field>
      ${field.name?uncap_first}: data.${field.name?uncap_first},
    </#list>
    };
    return this.http.post<${variables.etoName?cap_first}Model>(this.urlService, obj);
  }

  /* @param {*} data
   * @returns {Observable<Object>}
   * @memberof ${variables.etoName?cap_first}Service
   */
   edit${variables.etoName?cap_first}(data: ${variables.etoName?cap_first}Model): Observable<${variables.etoName?cap_first}Model> {
    const obj: ${variables.etoName?cap_first}Model = {
      id: data.id,
      modificationCounter: data.modificationCounter,
    <#list pojo.fields as field>
      ${field.name?uncap_first}: data.${field.name?uncap_first},
    </#list>
    };

    return this.http.post<${variables.etoName?cap_first}Model>(this.urlService, obj);
  }

  /* @param {*} criteria
   * @returns {Observable<Object>}
   * @memberof ${variables.etoName?cap_first}Service
   */
  search${variables.etoName?cap_first}(criteria: any): Observable<Object> {
    return this.http.post<HttpResponseModel>(this.urlService + 'search', {
      criteria: criteria,
    });
  }

  /* @param {number} id
   * @returns {Observable<Object>}
   * @memberof ${variables.etoName?cap_first}Service
   */
  delete${variables.etoName?cap_first}(id: number): Observable<Object> {
    return this.http.delete(this.urlService + id);
  }
}
