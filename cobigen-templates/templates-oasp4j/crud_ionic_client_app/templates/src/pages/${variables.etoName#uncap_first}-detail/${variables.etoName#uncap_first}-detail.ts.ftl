import { NavParams, ViewController } from 'ionic-angular';
import { TranslateService } from '@ngx-translate/core';
import { Component } from '@angular/core';
import { ${variables.etoName?cap_first}Rest } from '../../providers/${variables.etoName?uncap_first}-rest';
import { ${variables.etoName?cap_first} } from '../../providers/interfaces/${variables.etoName?uncap_first}';
import { ${variables.etoName?cap_first}SearchCriteria } from '../../providers/interfaces/${variables.etoName?uncap_first}-search-criteria';
import { Pagination } from '../../providers/interfaces/pagination'
import { PaginatedListTo } from '../../providers/interfaces/paginated-list-to';
/**
 * Generated class for the ${variables.etoName?cap_first}Detail component.
 *
 * See https://angular.io/api/core/Component for more info on Angular
 * Components.
 */
@Component({
  selector: '${variables.etoName?uncap_first}-detail',
  templateUrl: '${variables.etoName?uncap_first}-detail.html'
})
export class ${variables.etoName?cap_first}Detail {
  
  pagination: Pagination = { size:15, page:1, total:false };
  ${variables.etoName?uncap_first}SearchCriteria : ${variables.etoName?cap_first}SearchCriteria = { <#list pojo.fields as field> ${field.name}:null,</#list> pagination : this.pagination };

  ${variables.etoName?uncap_first}Received : ${variables.etoName?cap_first};
  clean${variables.etoName?cap_first} : ${variables.etoName?cap_first} = { <#list pojo.fields as field> ${field.name}:null,</#list> id:null, modificationCounter:null, revision:null };
  
  translations = {title : "Dialog", message: "message" }
  dialogType = "";
  filterActive : boolean = true;

  constructor(
    public params: NavParams, 
    public viewCtrl: ViewController, 
    public translate: TranslateService, 
    public ${variables.etoName?uncap_first}Rest: ${variables.etoName?cap_first}Rest
  ) {
    
    this.getTranslation("${variables.component}.${variables.etoName?uncap_first}.operations." + this.params.get('dialog'));
    this.dialogType = this.params.get('dialog');
    this.${variables.etoName?uncap_first}Received = this.params.get('edit');
    if(!this.${variables.etoName?uncap_first}Received) this.${variables.etoName?uncap_first}Received = { <#list pojo.fields as field> ${field.name}:null,</#list>};
    if(this.dialogType == "filter") this.filterActive = false;
  }

  getTranslation(dialog:string){
    this.translations = this.translate.instant(dialog);
  }

  dismiss(data: [${variables.etoName?cap_first}SearchCriteria, PaginatedListTo<${variables.etoName?cap_first}>]) {
    this.viewCtrl.dismiss(data);
    this.filterActive = true;
  }

  addOrModify(){

    this.clean${variables.etoName?cap_first}.id=null; 
    for(let i in this.clean${variables.etoName?cap_first}){
      this.clean${variables.etoName?cap_first}[i] = this.${variables.etoName?uncap_first}Received[i];
    }

    this.${variables.etoName?uncap_first}Rest.save(this.${variables.etoName?uncap_first}Received).subscribe(
      (data: ${variables.etoName?cap_first}) => {  
        this.viewCtrl.dismiss(data);
      });
  }

  search(){
    for (let i in this.${variables.etoName?uncap_first}Received){
      if(this.${variables.etoName?uncap_first}Received[i]=="") delete this.${variables.etoName?uncap_first}Received[i]
      else this.${variables.etoName?uncap_first}SearchCriteria[i] = this.${variables.etoName?uncap_first}Received[i];
    }
    if(!this.${variables.etoName?uncap_first}SearchCriteria) return;
    this.${variables.etoName?uncap_first}Rest.search(this.${variables.etoName?uncap_first}SearchCriteria).subscribe(
      (data: PaginatedListTo<${variables.etoName?cap_first}>) => {
        let dataArray : [${variables.etoName?cap_first}SearchCriteria, PaginatedListTo<${variables.etoName?cap_first}>];
        dataArray = [this.${variables.etoName?uncap_first}SearchCriteria, data];
        this.dismiss(dataArray);
        this.${variables.etoName?uncap_first}SearchCriteria = { <#list pojo.fields as field> ${field.name}:null,</#list> pagination : this.pagination };
      }
    )
  }

  clearSearch(){
    this.${variables.etoName?uncap_first}SearchCriteria.pagination.page = 1;
    this.${variables.etoName?uncap_first}Rest.retrieveData(this.${variables.etoName?uncap_first}SearchCriteria).subscribe(
     (data: PaginatedListTo<${variables.etoName?cap_first}>) => {        
        let dataArray : [${variables.etoName?cap_first}SearchCriteria, PaginatedListTo<${variables.etoName?cap_first}>];
        dataArray = [this.${variables.etoName?uncap_first}SearchCriteria, data];
        this.dismiss(dataArray);
      }
    );
  }

}