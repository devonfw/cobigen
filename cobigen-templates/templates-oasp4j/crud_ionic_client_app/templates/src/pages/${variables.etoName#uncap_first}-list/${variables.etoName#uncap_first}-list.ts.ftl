import { TranslateService } from '@ngx-translate/core';
import { Component, Input} from '@angular/core';
import { AlertController, ModalController, IonicPage, NavController, NavParams, LoadingController } from 'ionic-angular';
import { ${variables.etoName?cap_first}Rest } from '../../providers/${variables.etoName?uncap_first}-rest';
import { ${variables.etoName?cap_first}Detail } from '../${variables.etoName?uncap_first}-detail/${variables.etoName?uncap_first}-detail'
import { ${variables.etoName?cap_first} } from '../../providers/interfaces/${variables.etoName?uncap_first}'
import { Pagination } from '../../providers/interfaces/pagination'


    
@Component({
  selector: '${variables.etoName?uncap_first}-list',
  templateUrl: '${variables.etoName?uncap_first}-list.html',
})
export class ${variables.etoName?cap_first}List {

deleteTranslations: any = {};
  ${variables.etoName?uncap_first}ListItem : ${variables.etoName?cap_first} = {<#list pojo.fields as field> ${field.name}:null,</#list> };
  pagination: Pagination = {size:15, page:1, total:false};
  deleteButtonNames=["dismiss","confirm"];
  deleteButtons=[
                { text: "", handler: data => {  }},
                { text: "", handler: data => {  }}
                ]
  @Input() deleteModifiedButtonsEnabled: boolean = true;
  updatedList: ${variables.etoName?cap_first}[];
  listToShow: any = []
  infiniteScrollingIndex: number = 0;
  currentIndex: number = -1;

  constructor(public navCtrl: NavController, public navParams: NavParams,
  public ${variables.etoName?uncap_first}Rest: ${variables.etoName?cap_first}Rest, public alertCtrl: AlertController, 
  public translate: TranslateService, public modalCtrl: ModalController, public loadingCtrl: LoadingController
  ) {}

  reload${variables.etoName?cap_first}ListTable(){
  

    this.updatedList = this.${variables.etoName?uncap_first}Rest.getList();
    this.listToShow = [];
    
    for (let i = 0; i < this.pagination.size; i++) {
      if (this.updatedList[i]) {
        this.listToShow.push(this.updatedList[i]);
      }
    }
    
    this.infiniteScrollingIndex = this.pagination.size;
  }

  public getCurrentIndex() {
    if(this.currentIndex == -1){
      return;
    }
    return this.currentIndex;
  }

  public setCurrentIndex(index: number) {
    this.currentIndex = index;
  }

  ionViewWillEnter() {
  this.${variables.etoName?uncap_first}Rest.retrieveData().subscribe(
    (data: any) => {
        
      this.${variables.etoName?uncap_first}Rest.setList(data.result);
      this.updatedList = this.${variables.etoName?uncap_first}Rest.getList();
      for (let i = 0; i < this.pagination.size; i++) {
        if (this.updatedList[i]){
              
          this.listToShow.push(this.updatedList[i]);
        }
      }
      this.infiniteScrollingIndex = this.pagination.size;
          
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
  
  //Add Operation
  promptAddClicked() {

    let modal = this.modalCtrl.create(${variables.etoName?cap_first}Detail, { dialog: "add", edit: null });
    modal.present();
    modal.onDidDismiss(() => 
    this.reload${variables.etoName?cap_first}ListTable()
    );
  }

  //Search operation
  promptFilterClicked() {
    this.deleteModifiedButtonsEnabled = true;
    let modal = this.modalCtrl.create(${variables.etoName?cap_first}Detail, { dialog: "filter", edit: null });
    modal.present();
    modal.onDidDismiss(() => this.reload${variables.etoName?cap_first}ListTable());
  }
  promptModifyClicked() { 
  
    if (!this.currentIndex && this.currentIndex != 0) {
      return;
    }
    let modal = this.modalCtrl.create(${variables.etoName?cap_first}Detail, { dialog: "modify", edit:this.${variables.etoName?uncap_first}Rest.getList()[this.currentIndex]});
    this.enableUpdateDeleteOperations(this.currentIndex);
    modal.present();
    modal.onDidDismiss(() => this.reload${variables.etoName?cap_first}ListTable());
      
  }

  // deletes the selected item
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
            
      this.${variables.etoName?uncap_first}Rest.retrieveData().subscribe(
              (data:any) => {
        this.${variables.etoName?uncap_first}Rest.setList(data.result);
                this.reload${variables.etoName?cap_first}ListTable();
              }
            );
          }
        )
      }
    )
    this.enableUpdateDeleteOperations(this.currentIndex);
  }
  
  showDeleteForm() { 
    
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
    

  doInfinite(infiniteScroll) {
    
  let moreItems = this.infiniteScrollingIndex + (this.pagination.page*this.pagination.size);
    setTimeout(() => {
    for (let i = this.infiniteScrollingIndex; i < moreItems; i++) {
      if (this.updatedList[i]) {
        this.listToShow.push(this.updatedList[i]);
        this.infiniteScrollingIndex++;
      }
    }
    infiniteScroll.complete();
  }, 500);
  }

  enableUpdateDeleteOperations(index) {
  if (this.currentIndex != index){
    this.currentIndex = index;
    this.deleteModifiedButtonsEnabled = false;
    }
    else{
    this.currentIndex = -1;
    this.deleteModifiedButtonsEnabled = true;
    }
  }
}
