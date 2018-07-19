import { TranslateService } from '@ngx-translate/core';
import { Component, Input} from '@angular/core';
import { AlertController, ModalController, NavController, NavParams, LoadingController } from 'ionic-angular';
import { ${variables.etoName?cap_first}Rest } from '../../providers/${variables.etoName?uncap_first}-rest';
import { ${variables.etoName?cap_first}Detail } from '../${variables.etoName?uncap_first}-detail/${variables.etoName?uncap_first}-detail'
import { ${variables.etoName?cap_first} } from '../../providers/interfaces/${variables.etoName?uncap_first}'
import { Pagination } from '../../providers/interfaces/pagination'
import { ${variables.etoName?cap_first}SearchCriteria } from '../../providers/interfaces/${variables.etoName?uncap_first}-search-criteria';
import { PaginatedListTo } from '../../providers/interfaces/paginated-list-to';

    
@Component({
  selector: '${variables.etoName?uncap_first}-list',
  templateUrl: '${variables.etoName?uncap_first}-list.html',
})
export class ${variables.etoName?cap_first}List {
  /** Contains the strings for the deletion prompt */
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

  ${variables.etoName?uncap_first}s: ${variables.etoName?cap_first}[] = []
  selectedItemIndex: number = -1;

  constructor(public navCtrl: NavController, public navParams: NavParams,
  public ${variables.etoName?uncap_first}Rest: ${variables.etoName?cap_first}Rest, public alertCtrl: AlertController, 
  public translate: TranslateService, public modalCtrl: ModalController, public loadingCtrl: LoadingController
  ) {}

  /**
   * Runs when the page is about to enter and become the active page.
   */
  private ionViewWillEnter() {
  
    let loading = this.loadingCtrl.create({
      content: 'Please wait...'
    });
    loading.present();
    this.${variables.etoName?uncap_first}SearchCriteria.pagination.page = 1;
    this.${variables.etoName?uncap_first}Rest.retrieveData(this.${variables.etoName?uncap_first}SearchCriteria).subscribe(
      (data: PaginatedListTo<${variables.etoName?cap_first}>) => {
        
        this.${variables.etoName?uncap_first}s = this.${variables.etoName?uncap_first}s.concat(data.result);
        loading.dismiss();
	}, 
	(err) => {
        loading.dismiss();
        console.log(err);
      }
    )
  }
  
  /**
   * Get the selected item index.
   * @returns The current selected item index.
   */
  public getSelectedItemIndex():number {
  
    if(this.selectedItemIndex <= -1){
      return;
    }
    return this.selectedItemIndex;
  }

  /**
   * Set the selected item index.
   * @param  index The item index you want to set.
   */
  public setSelectedItemIndex(index: number) {
    this.selectedItemIndex = index;
    this.deleteModifiedButtonsDisabled = false;
  }

  /**
   * Executed after a pull-to-refresh event. It reloads the ${variables.etoName?uncap_first} list.
   * @param  refresher Pull-to-refresh event.
   */
  public doRefresh(refresher) {  
  
    setTimeout(() => {
      this.reload${variables.etoName?cap_first}List();
      refresher.complete();
    }, 500);
  }
  
  /**
   * Reloads the ${variables.etoName?uncap_first} list, retrieving the first page.
   */
  private reload${variables.etoName?cap_first}List(){
    
    this.${variables.etoName?uncap_first}s = [];
    this.${variables.etoName?uncap_first}SearchCriteria.pagination.page = 1;
    this.deleteModifiedButtonsDisabled = true;
    this.selectedItemIndex = -1;
    this.${variables.etoName?uncap_first}Rest.retrieveData(this.${variables.etoName?uncap_first}SearchCriteria).subscribe(
      (data: PaginatedListTo<${variables.etoName?cap_first}>) => {      
        this.${variables.etoName?uncap_first}s = this.${variables.etoName?uncap_first}s.concat(data.result);      
        this.infiniteScrollEnabled = true;
      }, 
      (err) => {
        console.log(err);
      }
    );
  }
  
  /**
   * Translates a string to the current language.
   * @param  text The string to be translated.
   * @returns The translated string.
   */
  private getTranslation(text: string): string {

    let value: string;
    value = this.translate.instant(text);
    return value;
  }
  
  /**
   * Presents the create dialog to the user and creates a new ${variables.etoName?uncap_first} if the data is correctly defined.
   */
  public create${variables.etoName?cap_first}() {

    let modal = this.modalCtrl.create(${variables.etoName?cap_first}Detail, { dialog: "add", edit: null });
    modal.present();
    modal.onDidDismiss(() => this.reload${variables.etoName?cap_first}List());
  }

  /**
   * Presents the search dialog to the user and sets to the list all the found ${variables.etoName?uncap_first}s.
   */
  public search${variables.etoName?cap_first}s() {
  
    this.deleteModifiedButtonsDisabled = true;
    this.selectedItemIndex = -1;
    let modal = this.modalCtrl.create(${variables.etoName?cap_first}Detail, { dialog: "filter", edit: null });
    modal.present();
    modal.onDidDismiss(data => {
      if (data == null) return;
      else {
          this.infiniteScrollEnabled = true;
          this.${variables.etoName?uncap_first}SearchCriteria = data[0];
          this.${variables.etoName?uncap_first}s = data[1].result;          
      }
    });
  }

  /**
   * Presents the modify dialog and updates the selected ${variables.etoName?uncap_first}.
   */
  public updateSelected${variables.etoName?cap_first}() { 
  
    if (!this.selectedItemIndex && this.selectedItemIndex != 0) {
      return;
    }
    let cleanItem = this.${variables.etoName?uncap_first}ListItem;
    for (let i in cleanItem){
      cleanItem[i] = this.${variables.etoName?uncap_first}s[this.selectedItemIndex][i]; 
    }
    let modal = this.modalCtrl.create(${variables.etoName?cap_first}Detail, { dialog: "modify", edit:this.${variables.etoName?uncap_first}s[this.selectedItemIndex]});
    modal.present();
    modal.onDidDismiss(data => {
      if(data == null) this.reload${variables.etoName?cap_first}List();
      else{
        for (let i in cleanItem){
          if (data[i] != cleanItem[i]) {
            data.modificationCounter++;
            break;
          }
        }
        this.${variables.etoName?uncap_first}s.splice(this.selectedItemIndex, 1, data);
      } 
    });
      
  }
  
  /**
   * Presents a promt to the user to warn him about the deletion.
   */
  public deleteSelected${variables.etoName?cap_first}() { 
    
    this.deleteTranslations = this.getTranslation('${variables.component?uncap_first}.${variables.etoName?uncap_first}.operations.delete');
    for (let i in this.deleteButtons){
      this.deleteButtons[i].text=this.deleteTranslations[this.deleteButtonNames[i]];
    }
    let prompt = this.alertCtrl.create({
      title: this.deleteTranslations.title, 
      message: this.deleteTranslations.message,
      buttons:  [
          { text: this.deleteButtons[0].text, handler: data => {  }}, 
          { text: this.deleteButtons[1].text, handler: data => { this.confirmDeletion(); } }
         ]
      });
      prompt.present();
    }

  /**
   * Removes the current selected item.
   */  
  private confirmDeletion() {

    if (!this.selectedItemIndex && this.selectedItemIndex != 0) {
      return;
    }    
    let search = this.${variables.etoName?uncap_first}s[this.selectedItemIndex]
    
    this.${variables.etoName?uncap_first}Rest.delete(search.id).subscribe(
      (deleteresponse) => {      
        this.${variables.etoName?uncap_first}s.splice(this.selectedItemIndex, 1);
        this.selectedItemIndex = -1;
        this.deleteModifiedButtonsDisabled = true;
      }, 
      (err) => {
        console.log(err);
      }
    );
  } 

  /**
   * Executed after the user reaches the end of the last page. It tries to retrieve the next data page.
   * @param  infiniteScroll Infinite scroll event.
   */
  public doInfinite(infiniteScroll) {

    if (this.${variables.etoName?uncap_first}SearchCriteria.pagination.page <= 0) this.infiniteScrollEnabled = false;
    else {
      this.${variables.etoName?uncap_first}SearchCriteria.pagination.page = this.${variables.etoName?uncap_first}SearchCriteria.pagination.page + 1;

      setTimeout(() => {
        this.${variables.etoName?uncap_first}Rest.retrieveData(this.${variables.etoName?uncap_first}SearchCriteria).subscribe(
          (data: PaginatedListTo<${variables.etoName?cap_first}>) => {
              if (data.result.length == 0 && this.${variables.etoName?uncap_first}SearchCriteria.pagination.page > 1){
                this.${variables.etoName?uncap_first}SearchCriteria.pagination.page = this.${variables.etoName?uncap_first}SearchCriteria.pagination.page - 1;
                this.infiniteScrollEnabled = false;
              }
              else{
                this.${variables.etoName?uncap_first}s = this.${variables.etoName?uncap_first}s.concat(data.result);
              }
              infiniteScroll.complete();
            }, 
            (err) => {
              console.log(err);
            }
        )    
      }, 500);
    }
  }

  /**
   * Enables the update and delete buttons for the selected ${variables.etoName?uncap_first}.
   * @param  index The index of the selected ${variables.etoName?uncap_first} that will be allowed to be updated or deleted.
   */
  public enableUpdateDeleteOperations(index: number) {
  
    if (this.selectedItemIndex != index){
      this.selectedItemIndex = index;
      this.deleteModifiedButtonsDisabled = false;
    }
    else{
      this.selectedItemIndex = -1;
      this.deleteModifiedButtonsDisabled = true;
    }
  }
  
}
