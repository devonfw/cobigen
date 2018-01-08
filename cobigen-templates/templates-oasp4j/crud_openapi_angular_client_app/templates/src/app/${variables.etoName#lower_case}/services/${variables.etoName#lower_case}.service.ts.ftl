import { TranslateService } from '@ngx-translate/core';
import { ITdDataTableColumn } from '@covalent/core';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { environment } from '../../../environments/environment';

@Injectable()
export class SampleDataDataGridService {
  private urlService = environment.restServiceRoot + '${variables.component?lower_case}/v1/${variables.etoName?lower_case}/';

  constructor(private http: HttpClient) {}
  
  
  get${variables.etoName?cap_first}(size: number, page: number, searchTerms, sort: any[]) {
    const pageData = {
      pagination: {
        size: size,
        page: page,
        total: 1
      },
    <#list model.properties as property>
      ${property.name}: searchTerms.${property.name}<#if property?has_next>,</#if>
    </#list>
      sort: sort
    };

    return this.http.post<any>(this.urlService + 'search', pageData);
  }

  save${variables.etoName?cap_first}(data) {
    const obj = {
      id: data.id,
    <#list model.properties as property>
      ${property.name}: data.${property.name}<#if property?has_next>,</#if>
    </#list>
    };
    return this.http.post(this.urlService, obj);
  }

  delete${variables.etoName?cap_first}(id) {
    return this.http.delete(this.urlService + id);
  }

  search${variables.etoName?cap_first}(criteria) {
    return this.http.post(this.urlService + 'search', { criteria: criteria });
  }

}
