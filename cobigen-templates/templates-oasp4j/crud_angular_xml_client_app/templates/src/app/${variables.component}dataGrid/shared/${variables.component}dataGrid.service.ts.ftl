<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>

import { TranslateService } from '@ngx-translate/core';
import { ITdDataTableColumn } from '@covalent/core';
import { Injectable } from '@angular/core'
import { HttpClient } from '../../shared/security/httpClient.service';
import { BusinessOperations } from '../../BusinessOperations';

@Injectable()
export class ${variables.component?cap_first}DataGridService {

    constructor(private BO: BusinessOperations,
                private http: HttpClient) {
    }

    getData(size: number, page: number, searchTerms, sort: any[]) {
      let pageData = {
        pagination: {
          size: size,
          page: page,
          total: 1
        },
        <#list elemDoc["self::node()/ownedAttribute"] as field>
        ${field["@name"]}: searchTerms.${field["@name"]},
        </#list>
        sort: sort
      }
      return this.http.post(this.BO.post${variables.etoName?cap_first}Search(), pageData)
                      .map(res => res.json());
    }

    saveData(data) {
      let obj = {
        id: data.id,
        <#list elemDoc["self::node()/ownedAttribute"] as field>
          <#if field?has_next>
        ${field["@name"]}: data.${field["@name"]},
          <#else>
        ${field["@name"]}: data.${field["@name"]}
         </#if>
        </#list>
      };

      return this.http.post(this.BO.post${variables.etoName?cap_first}(),  obj ).map(res => res.json());
    }

    deleteData(id) {
      return this.http.delete(this.BO.delete${variables.etoName?cap_first}() + id)
    }

    searchData(criteria) {
      return this.http.post(this.BO.post${variables.etoName?cap_first}Search(), { criteria: criteria }).map(res => res.json());
    }

}
