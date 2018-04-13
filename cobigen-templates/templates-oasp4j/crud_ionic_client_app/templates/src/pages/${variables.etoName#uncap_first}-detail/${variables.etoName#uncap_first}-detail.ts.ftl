import { NavParams, Platform, ViewController } from 'ionic-angular';
import { TranslateService } from '@ngx-translate/core';
import { Component } from '@angular/core';
import { ${variables.etoName?cap_first}Rest } from '../../providers/${variables.etoName?uncap_first}-rest';
import { ${variables.etoName?cap_first} } from '../../providers/interfaces/${variables.etoName?uncap_first}';
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
  
  ${variables.etoName?uncap_first}Received : ${variables.etoName?cap_first};
  clean${variables.etoName?cap_first} : any;
  translations = {title : "Dialog", message: "message" }
  dialogType = "";
  disableds : {filter : boolean } = {filter : true};

  constructor(
    public platform: Platform, public params: NavParams,
    public viewCtrl: ViewController, public translate: TranslateService,
    public ${variables.etoName?uncap_first}Rest: ${variables.etoName?cap_first}Rest
  ) {
    
    this.getTranslation("${variables.component}.${variables.etoName?uncap_first}.operations." + this.params.get('dialog'));
    this.dialogType = this.params.get('dialog');
    this.${variables.etoName?uncap_first}Received = this.params.get('edit');
    if(!this.${variables.etoName?uncap_first}Received) this.${variables.etoName?uncap_first}Received = { <#list pojo.fields as field> ${field.name}:null,</#list>};
    if(this.dialogType == "filter") this.disableds.filter = false;
    this.clean${variables.etoName?cap_first} = {<#list pojo.fields as field> ${field.name}:null ,</#list> id:null};
    
  }

  getTranslation(dialog:string){
    this.translate.get(dialog).subscribe(
      (data:any) => {
        this.translations = data;
      }
    )
  }

  dismiss() {
    this.viewCtrl.dismiss();
    this.disableds.filter = true;
  }

  AddorModify(){

    for(let i in this.clean${variables.etoName?cap_first}){
      this.clean${variables.etoName?cap_first}[i] = this.${variables.etoName?uncap_first}Received[i];
    }

    if(this.clean${variables.etoName?cap_first}.id!= null) this.${variables.etoName?uncap_first}Received = this.clean${variables.etoName?cap_first};
    
    this.${variables.etoName?uncap_first}Rest.Save(this.${variables.etoName?uncap_first}Received).subscribe(
      (data: any) => {
        this.${variables.etoName?uncap_first}Rest.retrieveData().subscribe(
          (data:any) => {
            this.${variables.etoName?uncap_first}Rest.setList(data.result);
          }
        );
        this.clean${variables.etoName?cap_first}.id=null;
        this.dismiss();
      });
  }

  Search(){
    for (let i in this.${variables.etoName?uncap_first}Received){
      if(this.${variables.etoName?uncap_first}Received[i]=="") delete this.${variables.etoName?uncap_first}Received[i]
    }
    if(!this. ${variables.etoName?uncap_first}Received) return;
    this.${variables.etoName?uncap_first}Rest.Filter(this.${variables.etoName?uncap_first}Received).subscribe(
      (data : any) => {
        this.${variables.etoName?uncap_first}Rest.setList(data.result);
        this.dismiss();
      }
    )
  }

  clearSearch(){
    this.${variables.etoName?uncap_first}Rest.retrieveData().subscribe(
     (data:any) => {
       this.${variables.etoName?uncap_first}Rest.setList(data.result);
      }
    );
    this.dismiss();
  }

}