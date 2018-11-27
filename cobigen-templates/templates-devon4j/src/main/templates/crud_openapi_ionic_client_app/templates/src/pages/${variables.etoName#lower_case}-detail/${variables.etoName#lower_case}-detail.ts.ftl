import { NavParams, ViewController } from 'ionic-angular';
import { TranslateService } from '@ngx-translate/core';
import { Component } from '@angular/core';
import { ${variables.etoName?cap_first}Rest } from '../../providers/${variables.etoName?lower_case}-rest';
import { ${variables.etoName?cap_first} } from '../../providers/interfaces/${variables.etoName?lower_case}';
import { ${variables.etoName?cap_first}SearchCriteria } from '../../providers/interfaces/${variables.etoName?lower_case}-search-criteria';
import { Pageable } from '../../providers/interfaces/pageable';
import { PaginatedListTo } from '../../providers/interfaces/paginated-list-to';
/**
 * Generated class for the ${variables.etoName?cap_first}Detail component.
 *
 * See https://angular.io/api/core/Component for more info on Angular
 * Components.
 */
@Component({
  selector: '${variables.etoName?lower_case}-detail',
  templateUrl: '${variables.etoName?lower_case}-detail.html'
})
export class ${variables.etoName?cap_first}Detail {
  
  pageable: Pageable = {
    pageSize: 15,
    pageNumber: 0,
    sort: [
      {
        property: '${model.properties[0].name!}',
        direction: 'ASC',
      },
    ],
  };
  ${variables.etoName?lower_case}SearchCriteria : ${variables.etoName?cap_first}SearchCriteria = { <#list model.properties as field> ${field.name}:null,</#list> pageable : this.pageable };

  ${variables.etoName?lower_case}Received : ${variables.etoName?cap_first};
  clean${variables.etoName?cap_first} : ${variables.etoName?cap_first} = { <#list model.properties as field> ${field.name}:null,</#list> id:null, modificationCounter:null, revision:null };
  
  translations = {title : "Dialog", message: "message" }
  dialogType = "";

  /** If filterActive is true, then the dialog will be of type search. */
  filterActive : boolean = true;

  constructor(
    public params: NavParams, 
    public viewCtrl: ViewController, 
    public translate: TranslateService, 
    public ${variables.etoName?lower_case}Rest: ${variables.etoName?cap_first}Rest
  ) {
    
    this.getTranslation("${variables.component?lower_case}.${variables.etoName?lower_case}.operations." + this.params.get('dialog'));
    this.dialogType = this.params.get('dialog');
    this.${variables.etoName?lower_case}Received = this.params.get('edit');
    if(!this.${variables.etoName?lower_case}Received) this.${variables.etoName?lower_case}Received = { <#list model.properties as field> ${field.name}:null,</#list>};
    if(this.dialogType == "filter") this.filterActive = false;
  }

  /**
   * Translates the passed dialog to the current language
   * @param  dialog - The passed dialog
   */
  private getTranslation(dialog:string){
    this.translations = this.translate.instant(dialog);
  }

  /**
   * Dismisses the current opened dialog and returns the result data to it's creator.
   * @param  data - Tuple containing all the objects which the server returns .
   */
  private dismiss(data: [${variables.etoName?cap_first}SearchCriteria, PaginatedListTo<${variables.etoName?cap_first}>]) {
    this.viewCtrl.dismiss(data);
    this.filterActive = true;
  }

  /**
   * Creates the add and modify dialog and returns the result data to it's creator. 
   */
  public addOrModify(){

    this.clean${variables.etoName?cap_first}.id=null; 
    for(let i in this.clean${variables.etoName?cap_first}){
      this.clean${variables.etoName?cap_first}[i] = this.${variables.etoName?lower_case}Received[i];
    }

    this.${variables.etoName?lower_case}Rest.save(this.${variables.etoName?lower_case}Received).subscribe(
      (data: ${variables.etoName?cap_first}) => {  
        this.viewCtrl.dismiss(data);
      });
  }

  /**
   * Creates the search dialog. 
   */
  public search(){
    for (let i in this.${variables.etoName?lower_case}Received){
      if(this.${variables.etoName?lower_case}Received[i]=="") delete this.${variables.etoName?lower_case}Received[i]
      else this.${variables.etoName?lower_case}SearchCriteria[i] = this.${variables.etoName?lower_case}Received[i];
    }
    if(!this.${variables.etoName?lower_case}SearchCriteria) return;
    this.${variables.etoName?lower_case}Rest.search(this.${variables.etoName?lower_case}SearchCriteria).subscribe(
      (data: PaginatedListTo<${variables.etoName?cap_first}>) => {
        let dataArray : [${variables.etoName?cap_first}SearchCriteria, PaginatedListTo<${variables.etoName?cap_first}>];
        dataArray = [this.${variables.etoName?lower_case}SearchCriteria, data];
        this.dismiss(dataArray);
        this.${variables.etoName?lower_case}SearchCriteria = { <#list model.properties as field> ${field.name}:null,</#list> pageable : this.pageable };
      }
    )
  }

  /**
   * Clears all the search filters and returns the first data page. 
   */
  clearSearch(){
    this.${variables.etoName?lower_case}SearchCriteria.pageable.pageNumber = 0;
    this.${variables.etoName?lower_case}Rest.retrieveData(this.${variables.etoName?lower_case}SearchCriteria).subscribe(
     (data: PaginatedListTo<${variables.etoName?cap_first}>) => {        
        let dataArray : [${variables.etoName?cap_first}SearchCriteria, PaginatedListTo<${variables.etoName?cap_first}>];
        dataArray = [this.${variables.etoName?lower_case}SearchCriteria, data];
        this.dismiss(dataArray);
      }
    );
  }

}
