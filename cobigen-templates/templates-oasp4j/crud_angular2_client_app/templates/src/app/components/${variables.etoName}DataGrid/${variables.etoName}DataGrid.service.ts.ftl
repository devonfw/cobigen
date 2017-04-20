import { Injectable } from '@angular/core'
import { HttpClient } from '../../security/httpClient.service';
import { BusinessOperations } from '../../BusinessOperations';

@Injectable()
export class ${variables.etoName?cap_first}DataGridService {

    constructor(private BO: BusinessOperations, private http: HttpClient) {
    }

    getData(size:number, page: number, searchTerms, sort: any[]) {
      
      let cobigen_pageData = {
        pagination: {
          size: size,
          page: page,
          total: 1
        },
        <#list pojo.fields as field>
        ${field.name}: searchTerms.${field.name},
        </#list>
        sort: sort
      }
      return this.http.post(this.BO.post${variables.etoName}Search(), cobigen_pageData)
                      .map(res => res.json());
    }

    saveData(data) {
      let cobigen_obj = {
        id: data.id,
        <#list pojo.fields as field>
          <#if field?has_next>
        ${field.name}: data.${field.name},
          <#else>
        ${field.name}: data.${field.name}
          </#if>
        </#list>
      };

      return this.http.post(this.BO.post${variables.etoName}(),  cobigen_obj )
                      .map(res => res.json());
    }

    deleteData(id) {
      return this.http.delete(this.BO.delete${variables.etoName}() + id)
    }

    searchData(criteria) {
      return this.http.post(this.BO.post${variables.etoName}Search(), { criteria: criteria })
                      .map(res => res.json());
    }

}
