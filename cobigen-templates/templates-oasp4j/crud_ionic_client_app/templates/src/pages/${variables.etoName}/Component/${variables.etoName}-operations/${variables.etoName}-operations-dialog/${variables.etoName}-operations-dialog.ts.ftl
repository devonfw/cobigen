import { NavParams, Platform, ViewController } from 'ionic-angular';
import { TranslateService } from '@ngx-translate/core';
import { Component } from '@angular/core';
import { ${variables.etoName}storeProvider } from '../../../provider/${variables.etoName}store/${variables.etoName}store';
import { ${variables.etoName}BusinessProvider } from '../../../provider/${variables.etoName}-business/${variables.etoName}-business';
import { ${variables.etoName}Item} from '../${variables.etoName}-operations';

/**
 * Generated class for the ${variables.etoName}dialogComponent component.
 *
 * See https://angular.io/api/core/Component for more info on Angular
 * Components.
 */
@Component({
	selector: '${variables.etoName}dialog',
	templateUrl: '${variables.etoName}-operations-dialog.html'
})
export class ${variables.etoName}operationsdialogComponent {
	
	${variables.etoName}received : ${variables.etoName}Item;
	clean${variables.etoName} : any;
	translations = {title : "Dialog", message: "message" }
	dialogtype = "";
	disables : {filter : boolean } = {filter : true};

	constructor(
		public platform: Platform, public params: NavParams,
		public viewCtrl: ViewController, public translate: TranslateService,
		public ${variables.etoName}Business: ${variables.etoName}BusinessProvider, public store: ${variables.etoName}storeProvider,
	) {
		
		this.getTranslation("${variables.component}.${variables.etoName}.operations." + this.params.get('dialog'));
		this.dialogtype = this.params.get('dialog');
		this.${variables.etoName}received = this.params.get('edit');
		if(!this.${variables.etoName}received) this.${variables.etoName}received = { <#list pojo.fields as field> ${field.name}:null,</#list>};
		if(this.dialogtype == "filter") this.disables.filter = false;
		this.clean${variables.etoName} = {<#list pojo.fields as field> ${field.name}:null ,</#list> id:null};
		
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
		this.disables.filter = true;
	}

	AddorModify(){

		for(let i in this.clean${variables.etoName}){
			this.clean${variables.etoName}[i] = this.${variables.etoName}received[i];
		}

		if(this.clean${variables.etoName}.id!= null) this.${variables.etoName}received = this.clean${variables.etoName};
		
		this.${variables.etoName}Business.Save(this.${variables.etoName}received).subscribe(
			(data: any) => {
				this.${variables.etoName}Business.getTableM().subscribe(
					(data:any) => {
						this.store.setList(data.result);
					}
				);
				this.clean${variables.etoName}.id=null;
				this.dismiss();
			});
	}

	Search(){
		for (let i in this.${variables.etoName}received){
			if(this.${variables.etoName}received[i]=="") delete this.${variables.etoName}received[i]
		}
		if(!this. ${variables.etoName}received) return;
		this.${variables.etoName}Business.Filter(this.${variables.etoName}received).subscribe(
			(data : any) => {
				for (let i in data.result) {
					data.result[i].checkbox = false;
				}
				this.store.setList(data.result);
				this.dismiss();
			}
		)
	}

	clearSearch(){
		this.${variables.etoName}Business.getTableM().subscribe(
		 (data:any) => {
			 this.store.setList(data.result);
			}
		);
		this.dismiss();
	}

}