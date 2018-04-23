import { TranslateService } from '@ngx-translate/core';
import { Component, Input} from '@angular/core';
import { AlertController, ModalController, NavController, NavParams, LoadingController } from 'ionic-angular';
import { ${variables.etoName?cap_first}Rest } from '../../providers/${variables.etoName?uncap_first}-rest';
import { ${variables.etoName?cap_first}Detail } from '../${variables.etoName?uncap_first}-detail/${variables.etoName?uncap_first}-detail'
import { ${variables.etoName?cap_first} } from '../../providers/interfaces/${variables.etoName?uncap_first}'
import { Pagination } from '../../providers/interfaces/pagination'
import { ${variables.etoName?cap_first}SearchCriteria } from '../../providers/interfaces/${variables.etoName?uncap_first}-search-criteria';

    
@Component({
  selector: '${variables.etoName?uncap_first}-list',
  templateUrl: '${variables.etoName?uncap_first}-list.html',
})
export class ${variables.etoName?cap_first}List {

  deleteTranslations: any = {};
  pagination: Pagination = { size:15, page:1, total:false };
  ${variables.etoName?uncap_first}SearchCriteria : ${variables.etoName?cap_first}SearchCriteria = { <#list pojo.fields as field> ${field.name}:null,</#list> pagination : this.pagination };
  ${variables.etoName?uncap_first}ListItem : ${variables.etoName?cap_first} = {<#list pojo.fields as field> ${field.name}:null,</#list> };
  deleteButtonNames=["dismiss","confirm"];
  deleteButtons=[
                { text: "", handler: data => {  }},
                { text: "", handler: data => {  }}
                ]
  @Input() deleteModifiedButtonsDisabled: boolean = true;
  @Input() infiniteScrollEnabled = true;

  listToShow: ${variables.etoName?cap_first}[] = []
  currentIndex: number = -1;

  constructor(public navCtrl: NavController, public navParams: NavParams,
  public ${variables.etoName?uncap_first}Rest: ${variables.etoName?cap_first}Rest, public alertCtrl: AlertController, 
  public translate: TranslateService, public modalCtrl: ModalController, public loadingCtrl: LoadingController
  ) {}

  ionViewWillEnter() {
  
    let loading = this.loadingCtrl.create({
      content: 'Please wait...'
    });
    loading.present();
    this.${variables.etoName?uncap_first}SearchCriteria.pagination.page = 1;
    this.${variables.etoName?uncap_first}Rest.retrieveData(this.${variables.etoName?uncap_first}SearchCriteria).subscribe(
      (data: any) => {
        
        this.listToShow = this.listToShow.concat(data.result);
        loading.dismiss();
      }, (err) => {
        console.log(err);
      }
    )
  }
  
  public setListToShow(list:any){
    this.listToShow = list;
  }

  public getListToShow(){
    return this.listToShow;
  }

  public getCurrentIndex() {
  
    if(this.currentIndex <= -1){
      return;
    }
    return this.currentIndex;
  }

  public setCurrentIndex(index: number) {
    this.currentIndex = index;
  }

  doRefresh(refresher) {  
  
    setTimeout(() => {
      this.reload${variables.etoName?cap_first}ListTable();
      refresher.complete();
    }, 500);
  }
  
  reload${variables.etoName?cap_first}ListTable(){
    
    this.listToShow = [];
    this.${variables.etoName?uncap_first}SearchCriteria.pagination.page = 1;
    this.deleteModifiedButtonsDisabled = true;
    this.currentIndex = -1;
    this.${variables.etoName?uncap_first}Rest.retrieveData(this.${variables.etoName?uncap_first}SearchCriteria).subscribe(
      (data: any) => {      
        this.listToShow = this.listToShow.concat(data.result);      
        this.infiniteScrollEnabled = true;
      }, (err) => {
            console.log(err);
      }
    );
  }
  
  getTranslation(text: string): string {

    let value: string;
    value = this.translate.instant(text);
    return value;
  }
  
  promptCreateClicked() {

    let modal = this.modalCtrl.create(${variables.etoName?cap_first}Detail, { dialog: "add", edit: null });
    modal.present();
    modal.onDidDismiss(() => this.reload${variables.etoName?cap_first}ListTable());
  }

  promptSearchClicked() {
  
    this.deleteModifiedButtonsDisabled = true;
    this.currentIndex = -1;
    let modal = this.modalCtrl.create(${variables.etoName?cap_first}Detail, { dialog: "filter", edit: null });
    modal.present();
    modal.onDidDismiss(data => {
      if (data == null) return;
      else {
          this.infiniteScrollEnabled = true;
          this.${variables.etoName?uncap_first}SearchCriteria = data[0];
          this.listToShow = data[1].result;          
      }
    });
  }
  promptUpdateClicked() { 
  
    if (!this.currentIndex && this.currentIndex != 0) {
      return;
    }
    let modal = this.modalCtrl.create(${variables.etoName?cap_first}Detail, { dialog: "modify", edit:this.listToShow[this.currentIndex]});
    modal.present();
    modal.onDidDismiss(data => {
      if(data == null) this.reload${variables.etoName?cap_first}ListTable();
      else this.listToShow.splice(this.currentIndex, 1, data);
    });
      
  }
  
  showDeleteAlert() { 
    
  this.deleteTranslations = this.getTranslation('${variables.component}.${variables.etoName?uncap_first}.operations.delete');
    for (let i in this.deleteButtons){
    this.deleteButtons[i].text=this.deleteTranslations[this.deleteButtonNames[i]];
    }
    let prompt = this.alertCtrl.create({
      title: this.deleteTranslations.title, 
      message: this.deleteTranslations.message,
      buttons:  [
          { text: this.deleteButtons[0].text, handler: data => {  }}, 
          { text: this.deleteButtons[1].text, handler: data => { this.delete(); } }
         ]
      });
      prompt.present();
    }

  delete() {

    if (!this.currentIndex && this.currentIndex != 0) {
      return;
    }
    let cleanItem = this.${variables.etoName?uncap_first}ListItem;
    let search = this.listToShow[this.currentIndex]
    for(let i in cleanItem){
      cleanItem[i] = search[i];
    }
    this.${variables.etoName?uncap_first}Rest.getItemId(cleanItem).subscribe(
    (idResponse: any) => {
      this.${variables.etoName?uncap_first}Rest.delete(idResponse.result[0].id).subscribe(
        (deleteresponse) => {      
            this.listToShow.splice(this.currentIndex, 1);
            this.currentIndex = -1;
            this.deleteModifiedButtonsDisabled = true;
            }, (err) => {
              console.log(err);
          }
        )
      }
    )
  } 

  doInfinite(infiniteScroll) {

    if (this.${variables.etoName?uncap_first}SearchCriteria.pagination.page <= 0) this.infiniteScrollEnabled = false;
    else {
      this.${variables.etoName?uncap_first}SearchCriteria.pagination.page = this.${variables.etoName?uncap_first}SearchCriteria.pagination.page + 1;

      setTimeout(() => {
        this.${variables.etoName?uncap_first}Rest.retrieveData(this.${variables.etoName?uncap_first}SearchCriteria).subscribe(
          (data: any) => {
              if (data.result.length == 0 && this.${variables.etoName?uncap_first}SearchCriteria.pagination.page > 1){
                this.${variables.etoName?uncap_first}SearchCriteria.pagination.page = this.${variables.etoName?uncap_first}SearchCriteria.pagination.page - 1;
                this.infiniteScrollEnabled = false;
              }
              else{
                this.listToShow = this.listToShow.concat(data.result);
              }
              infiniteScroll.complete();
            }, (err) => {
                console.log(err);
            }
        )    
      }, 500);
    }
  }

  enableUpdateDeleteOperations(index) {
  
    if (this.currentIndex != index){
      this.currentIndex = index;
      this.deleteModifiedButtonsDisabled = false;
    }
    else{
      this.currentIndex = -1;
      this.deleteModifiedButtonsDisabled = true;
    }
  }
  
}
