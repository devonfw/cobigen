import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { SearchCriteria } from '../../core/interfaces/search-criteria';
import { Sort } from '../../core/interfaces/sort';

@Injectable()
export class ${variables.etoName?cap_first}Service {
  private urlService: string = environment.restServiceRoot +
  '${variables.component?lower_case}/v1/${variables.etoName?lower_case}/';

  constructor(private http: HttpClient) {}
  get${variables.etoName?cap_first}(
    size: number,
    page: number,
    searchTerms: any,
    sort: Sort[],
  ): Observable<any> {
    const searchCriteria: SearchCriteria = {
      pageable: {
        pageSize: size,
        pageNumber: page,
        sort: sort,
      },
    <#list model.properties as property>
      ${property.name}: searchTerms.${property.name},
    </#list>
    };
    return this.http.post<any>(this.urlService + 'search', searchCriteria);
  }

  save${variables.etoName?cap_first}(data: any): Observable<Object> {
    const obj: any = {
      id: data.id,
      modificationCounter: data.modificationCounter,
    <#list model.properties as property>
      ${property.name}: data.${property.name},
    </#list>
    };
    return this.http.post(this.urlService, obj);
  }

  delete${variables.etoName?cap_first}(id: number): Observable<Object> {
    return this.http.delete(this.urlService + id);
  }

  search${variables.etoName?cap_first}(criteria: any): Observable<Object> {
    return this.http.post(this.urlService + 'search', {
      criteria: criteria,
    });
  }

}
