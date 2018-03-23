import { TranslateService } from '@ngx-translate/core';
import { Component } from '@angular/core';
import { AlertController, ModalController, IonicPage, NavController, NavParams, LoadingController } from 'ionic-angular';
import { ${variables.etoName}BusinessProvider } from './provider/${variables.etoName}-business/${variables.etoName}-business';
import { ${variables.etoName}operationsdialogComponent } from './Component/${variables.etoName}-operations/${variables.etoName}-operations-dialog/${variables.etoName}-operations-dialog'
import { ${variables.etoName}storeProvider } from './provider/${variables.etoName}store/${variables.etoName}store';

export interface ${variables.etoName}ListItem {
   <#list pojo.fields as field>
     ${field.name}:<#if (field.type=="long"||field.type=="int")> number <#else> ${field.type} </#if>,
     </#list>
}
     
@IonicPage()
@Component({
  selector: '${variables.etoName}-table',
  templateUrl: '${variables.etoName}.html',
})
export class ${variables.etoName}Page {

DeleteTranslations: any = {};
  interfaceuser : ${variables.etoName}ListItem = {<#list pojo.fields as field> ${field.name}:null,</#list> };
  DeleteButtonnames=["dismiss","confirm"];
  DeleteButtons=[
                 { text: "", handler: data => {  }},
                 { text: "", handler: data => {  }}
                ]
  Delete_and_Modified_Buttons_Disabled: boolean = true;
  Lastoperation: ${variables.etoName}ListItem[];
  tabletoshow: any = []
  FIRSTPAGINATIONTHRESHOLD = 15;
  NEXTELEMENTSTOLOAD = 10;
  InfiniteScrollingIndex: number = 0;
  currentIndex: number = -1;

  constructor(public navCtrl: NavController, public navParams: NavParams,
	public ${variables.etoName}Business: ${variables.etoName}BusinessProvider, public store: ${variables.etoName}storeProvider,
	public alertCtrl: AlertController, public translate: TranslateService,
	public modalCtrl: ModalController, public loadingCtrl: LoadingController
  ) {}

  reloadsamplePageTable(){

    this.Lastoperation = this.store.getList();
    this.tabletoshow = [];
    
    for (let i = 0; i < this.FIRSTPAGINATIONTHRESHOLD; i++) {
      if (this.Lastoperation[i]) {
        this.tabletoshow.push(this.Lastoperation[i]);
      }
    }
    
    this.InfiniteScrollingIndex = this.FIRSTPAGINATIONTHRESHOLD;
  }

  public getindex() {
    if(this.currentIndex == -1){
		  return;
    }
    return this.currentIndex;
  }

  ionViewWillEnter() {
	this.${variables.etoName}Business.getTableM().subscribe(
		(data: any) => {
        
      this.store.setList(data.result);
      this.Lastoperation = this.store.getList();
      for (let i = 0; i < this.FIRSTPAGINATIONTHRESHOLD; i++) {
        if (this.Lastoperation[i]){
              
          this.tabletoshow.push(this.Lastoperation[i]);
        }
      }
      this.InfiniteScrollingIndex = this.FIRSTPAGINATIONTHRESHOLD;
          
      }, (err) => {
          console.log(err);
      }
	  )
  }

  getTranslation(text: string): string {

    let value: string;
    this.translate.get(text).subscribe((res: string) => {
      value = res;
    });
    
    return value;
  }
  
  promptModifyClicked(index: number) { 
  
    if (!index && index != 0) {
      return;
    }
    let modal = this.modalCtrl.create(${variables.etoName}operationsdialogComponent, { dialog: "modify", edit:this.store.getList()[index]});
    this.enableUpdateDeleteOperations(index);
    modal.present();
    modal.onDidDismiss(() => this.reload${variables.etoName}PageTable());
      
  }

  // deletes the selected element
  DeleteConfirmed(index: number) {

	if (!index && index != 0) {
		return;
	}
    let cleanuser = this.interfaceuser;
    let search = this.tabletoshow[index]
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
                this.reload${variables.etoName}PageTable();
              }
            );            
          }
        )
      }
    )
  }
  
  DeleteConfirmForm(index: number) { 
    
	this.DeleteTranslations = this.getTranslation('${variables.component}.${variables.etoName}.operations.delete');
    for (let i in this.DeleteButtons){
		this.DeleteButtons[i].text=this.DeleteTranslations[this.DeleteButtonnames[i]];
    }
    let prompt = this.alertCtrl.create({
      title: this.DeleteTranslations.title, 
      message: this.DeleteTranslations.message,
      buttons:  [
          { text: this.DeleteButtons[0].text, handler: data => {  }}, 
          { text: this.DeleteButtons[1].text, handler: data => { this.DeleteConfirmed(index); } }
         ]
      });
      this.enableUpdateDeleteOperations(index);
      prompt.present();
    }
    

  doInfinite(infiniteScroll) {
    
	let MoreItems = this.InfiniteScrollingIndex + this.NEXTELEMENTSTOLOAD;
    setTimeout(() => {
		for (let i = this.InfiniteScrollingIndex; i < MoreItems; i++) {
			if (this.Lastoperation[i]) {
				this.tabletoshow.push(this.Lastoperation[i]);
			}
		}
		this.InfiniteScrollingIndex = MoreItems;
		infiniteScroll.complete();
	}, 500);
  }

  enableUpdateDeleteOperations(index) {
	if (this.currentIndex != index){
		this.currentIndex = index;
		this.Delete_and_Modified_Buttons_Disabled = false;
    }
    else{
		this.currentIndex = -1;
		this.Delete_and_Modified_Buttons_Disabled = true;
    }
  }
}
