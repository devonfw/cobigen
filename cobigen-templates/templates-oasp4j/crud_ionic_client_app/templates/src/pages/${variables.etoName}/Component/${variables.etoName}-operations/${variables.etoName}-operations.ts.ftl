import { AlertController, ModalController } from 'ionic-angular';
import { Component, Input } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ${variables.etoName}Page } from '../../${variables.etoName}';
import { ${variables.etoName}operationsdialogComponent } from './${variables.etoName}-operations-dialog/${variables.etoName}-operations-dialog'
import { ${variables.etoName}storeProvider } from '../../provider/${variables.etoName}store/${variables.etoName}store';
import { ${variables.etoName}BusinessProvider } from '../../provider/${variables.etoName}-business/${variables.etoName}-business';
/**
 * Generated class for the ${variables.etoName}OperationsComponent component.
 *
 * See https://angular.io/api/core/Component for more info on Angular
 * Components.
 */

export interface ${variables.etoName}Item {
	 <#list pojo.fields as field>
		 ${field.name}:<#if (field.type=="long"||field.type=="int")> number <#else> ${field.type} </#if>,
		 </#list>
 }

@Component({
	selector: '${variables.etoName}-operations',
	templateUrl: '${variables.etoName}-operations.html'
})
export class ${variables.etoName}OperationsComponent {

	DeleteTranslations: any = {};
	interfaceuser : ${variables.etoName}Item = {<#list pojo.fields as field> ${field.name}:null,</#list> };
	tabletoshow: any;
	DeleteButtonnames=["dismiss","confirm"];
	DeleteButtons=[
		{ text: "", handler: data => {	}}, 
		{ text: "", handler: data => { this.DeleteConfirmed(); } }
	]

	@Input() isDisabled: boolean = true;

	constructor(public translate: TranslateService, public alertCtrl: AlertController,
		public ${variables.etoName}Business: ${variables.etoName}BusinessProvider, public ${variables.etoName}Page: ${variables.etoName}Page,
		public modalCtrl: ModalController, public store: ${variables.etoName}storeProvider) {
	}


	getTranslation(text: string): string {

		let value: string;
		this.translate.get(text).subscribe((res: string) => {
			value = res;
		});
		this.translate.onLangChange.subscribe(
			() => {
			 for (let i in this.DeleteButtons){
					this.translate.get("${variables.component}.${variables.etoName}.operations.delete."+this.DeleteButtonnames[i]).subscribe(
						(data:any) => {
							this.DeleteButtons[i].text = data;
						}
					)
			}
		}
		);
		return value;
	}

	promptFilterClicked() {
		this.isDisabled = true;
		let modal = this.modalCtrl.create(${variables.etoName}operationsdialogComponent, { dialog: "filter", edit: null });
		modal.present();
		modal.onDidDismiss(() => this.${variables.etoName}Page.reload${variables.etoName}PageTable());
	}

	//Add Operation
	promptAddClicked() {

		let modal = this.modalCtrl.create(${variables.etoName}operationsdialogComponent, { dialog: "add", edit: null });
		modal.present();
		modal.onDidDismiss(() => 
		this.${variables.etoName}Page.reload${variables.etoName}PageTable()
		);
	}
	
	// deletes the selected element
	DeleteConfirmed() {
		let index = this.${variables.etoName}Page.getindex();
		if (!index && index != 0) {
			return;
		}
		let cleanuser = this.interfaceuser;
		let search = this.${variables.etoName}Page.tabletoshow[index]
		for(let i in cleanuser){
			cleanuser[i] = search[i];
		}
		this.${variables.etoName}Business.getItemId(cleanuser).subscribe(
			(Idresponse: any) => {
				this.${variables.etoName}Business.DeleteItem(Idresponse.result[0].id).subscribe(
					(deleteresponse) => {
						
						this.${variables.etoName}Business.getTableM().subscribe(
							(data:any) => {
								this.store.setList(data.result);
								this.${variables.etoName}Page.reload${variables.etoName}PageTable();
							}
						);
					}
				)
			}
		)
	}

	DeleteConfirmForm() { 
		
		this.DeleteTranslations = this.getTranslation('${variables.component}.${variables.etoName}.operations.delete');
		for (let i in this.DeleteButtons){
			this.DeleteButtons[i].text=this.DeleteTranslations[this.DeleteButtonnames[i]];
		}
		let prompt = this.alertCtrl.create({
			title: this.DeleteTranslations.title, 
			message: this.DeleteTranslations.message,
			buttons: this.DeleteButtons
		});
		prompt.present();
		 
	}

	promptModifyClicked() { 
		let index = this.${variables.etoName}Page.getindex();
		if (!index && index != 0) {
			return;
		}
		let modal = this.modalCtrl.create(${variables.etoName}operationsdialogComponent, { dialog: "modify", edit:this.store.getList()[index]});
		modal.present();
		modal.onDidDismiss(() => this.${variables.etoName}Page.reload${variables.etoName}PageTable());
		
	}
}