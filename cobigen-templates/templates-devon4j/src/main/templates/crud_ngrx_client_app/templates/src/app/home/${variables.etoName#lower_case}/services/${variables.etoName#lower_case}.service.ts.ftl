import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import { SearchCriteria } from '../../../shared/models/search-criteria';
import { Sort } from '../../../shared/models/sort';
import { ${variables.etoName?cap_first}Model } from '../models/${variables.etoName?lower_case}.model';


@Injectable({
  providedIn: 'root',
})
export class ${variables.etoName?cap_first}Service {
  private urlService: string = environment.restServiceRoot +
  '${variables.component?lower_case}/v1/${variables.etoName?lower_case}/';

  constructor(private http: HttpClient) {}
  get${variables.etoName?cap_first}(
    size: number,
    page: number,
    searchTerms: any,
    sort: Sort[],
  ): Observable<{ content: ${variables.etoName?cap_first}Model[] }> {
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
    return this.http.post<{ content: ${variables.etoName?cap_first}Model[] }>(this.urlService + 'search', searchCriteria);
  }

  save${variables.etoName?cap_first}(data: any): Observable<Object> {
    const obj: any = {
      id: data.id,
    <#list pojo.fields as field>
      ${field.name?uncap_first}: data.${field.name?uncap_first},
    </#list>
    };
    return this.http.post(this.urlService, obj);
  }

  edit${variables.etoName?cap_first}(data: any): Observable<Object> {
    const obj: any = {
      id: data.id,
      modificationCounter: data.modificationCounter,
    <#list pojo.fields as field>
      ${field.name?uncap_first}: data.${field.name?uncap_first},
    </#list>
    };

    return this.http.post(this.urlService, obj);
  }

  search${variables.etoName?cap_first}(criteria: any): Observable<Object> {
    return this.http.post(this.urlService + 'search', {
      criteria: criteria,
    });
  }

  delete${variables.etoName?cap_first}(id: number): Observable<Object> {
    return this.http.delete(this.urlService + id);
  }
}
