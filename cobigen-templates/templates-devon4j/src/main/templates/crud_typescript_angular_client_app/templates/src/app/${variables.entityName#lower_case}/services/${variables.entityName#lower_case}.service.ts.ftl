<#import '/variables.ftl' as class>
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Sort } from '../../core/interfaces/sort';

@Injectable()
export class ${variables.entityName?cap_first}Service {
  private urlService: string = environment.restServiceRoot +
  '${variables.entityName?lower_case}/';

  constructor(private http: HttpClient) {}
  get${variables.entityName?cap_first}(
    searchTerms: any,
    sort: Sort[],
  ): Observable<any> {
    const where: any = {
    <#list class.properties as property>
      ${property.identifier}: searchTerms.${property.identifier},
    </#list>
    };
    let order: any;

    if (sort.length > 0) {
      order = {
        [sort[0].property]: sort[0].direction.toUpperCase()
      };
    }

    let params = new HttpParams().set("order",JSON.stringify(order)).set("where",JSON.stringify(where));

    return this.http.get<any>(this.urlService, {params: params});
  }

  save${variables.entityName?cap_first}(data: any): Observable<Object> {
    const obj: any = {
      id: data.id,
      modificationCounter: data.modificationCounter,
    <#list class.properties as property>
      ${property.identifier}: data.${property.identifier},
    </#list>
    };
    return this.http.post(this.urlService, obj);
  }

  delete${variables.entityName?cap_first}(id: number): Observable<Object> {
    return this.http.delete(this.urlService + id);
  }

  search${variables.entityName?cap_first}(criteria: any): Observable<Object> {
    return this.http.post(this.urlService + 'search', {
      criteria: criteria,
    });
  }

}
