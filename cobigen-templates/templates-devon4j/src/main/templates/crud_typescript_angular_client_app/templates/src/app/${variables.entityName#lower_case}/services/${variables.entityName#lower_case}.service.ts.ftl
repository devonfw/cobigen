<#import '/variables.ftl' as class>
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { environment } from '../../../environments/environment';
import { RequestQueryBuilder, CondOperator } from '@nestjsx/crud-request';
import { Observable } from 'rxjs';
import { Sort } from '../../core/interfaces/sort';

@Injectable()
export class ${variables.entityName?cap_first}Service {
  private urlService: string = environment.restServiceRoot +
  '${variables.entityName?lower_case}/${variables.entityName?lower_case}s/';

  constructor(private http: HttpClient) {}
  get${variables.entityName?cap_first}(
    pageSize: number,
    pageNumber: number,
    searchTerms: any,
    sort: Sort[],
  ): Observable<any> {
    let queryString: string = this.createQuery(pageSize, pageNumber, searchTerms, sort);
    return this.http.get<any>(this.urlService + '?' + queryString);
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
  
  createQuery(
    pageSize: number,
    pageNumber: number,
    searchTerms: any,
    sort: Sort[],
  ): string {
    
    let queryBuilder: RequestQueryBuilder = RequestQueryBuilder.create()
    .select([<#list class.properties as property>'${property.identifier}',</#list> ])
    .setLimit(pageSize)
    .setPage(pageNumber + 1);

    // Add sort only when requested
    if (sort.length > 0) {
      queryBuilder = this.addSort(queryBuilder, sort);
    }

    // the following filter is like a SQL WHERE property = 'value' condition
    <#list class.properties as property>
    if (searchTerms.${property.identifier}){
      queryBuilder.setFilter({field: '${property.identifier}', operator: CondOperator.EQUALS, value: searchTerms.${property.identifier},});
    }
    </#list>

    return queryBuilder.query();
  }
  
  addSort(queryBuilder: RequestQueryBuilder, sort: Sort[]): RequestQueryBuilder {
    const direction = sort[0].direction.toUpperCase();
    if (direction == 'ASC'){

      queryBuilder.sortBy({
        field: sort[0].property,
        order: 'ASC',
      })
    } else if (direction == 'DESC') {

      queryBuilder.sortBy({
        field: sort[0].property,
        order: 'DESC',
      })
    }
    return queryBuilder;
  }

}
