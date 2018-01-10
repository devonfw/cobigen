import { TranslateService } from '@ngx-translate/core';
import { Component } from '@angular/core';
import { AlertController, IonicPage, NavController, NavParams, LoadingController } from 'ionic-angular';
import { ${variables.etoName}BusinessProvider } from './provider/${variables.etoName}-business/${variables.etoName}-business';
import { ${variables.etoName}storeProvider } from './provider/${variables.etoName}store/${variables.etoName}store';

export interface ${variables.etoName}CheckboxItem {
   <#list pojo.fields as field>
     ${field.name}:<#if (field.type=="long"||field.type=="int")> number <#else> ${field.type} </#if>,
     </#list>
     checkbox: boolean
 }
     
@IonicPage()
@Component({
  selector: '${variables.etoName}-table',
  templateUrl: '${variables.etoName}.html',
})
export class ${variables.etoName}Page {

  Delete_and_Modified_Buttons_Disabled: boolean = true;
  Lastoperation: ${variables.etoName}CheckboxItem[];
  tabletoshow: any = []
  FIRSTPAGINATIONTHRESHOLD = 15;
  NEXTELEMENTSTOLOAD = 10;
  InfiniteScrollingIndex: number = 0;

  constructor(public navCtrl: NavController, public navParams: NavParams,
    public ${variables.etoName}Business: ${variables.etoName}BusinessProvider, public store: ${variables.etoName}storeProvider,
    public alertCtrl: AlertController, public translate: TranslateService,
    public loadingCtrl: LoadingController
  ) {
  }

  NoMorethanOneCheckbox(p: any) {
    for (let i = 0; i < this.tabletoshow.length; i++) {
      if (p != i) {
        this.tabletoshow[i].checkbox = false;
      } else {
        this.tabletoshow[p].checkbox = !this.tabletoshow[p].checkbox;
        if (this.tabletoshow[p].checkbox) {
          this.Delete_and_Modified_Buttons_Disabled = false;
        }
        else {
          this.Delete_and_Modified_Buttons_Disabled = true;
        }
      }
    }
  }


  reload${variables.etoName}PageTable() {

    this.Lastoperation = this.store.getTable();
    this.Delete_and_Modified_Buttons_Disabled = true;
    this.tabletoshow = [];
    
    for (let i = 0; i < this.FIRSTPAGINATIONTHRESHOLD; i++) {
      if (this.Lastoperation[i]) {
      this.tabletoshow.push(this.Lastoperation[i]);
      this.tabletoshow[i].checkbox = false;
      }
      
    }
    
    this.InfiniteScrollingIndex = this.FIRSTPAGINATIONTHRESHOLD;
  }

  public getindex() {
    for (let i = 0; i < this.tabletoshow.length; i++) {
      if (this.tabletoshow[i].checkbox) {
        return i;
      }
    }
    return null;
  }

  ionViewWillEnter() {
    this.${variables.etoName}Business.getTableM().subscribe(
      (data: any) => {
        
        this.store.setTable(data.result);
        this.Lastoperation = this.store.getTable();
        for (let i = 0; i < this.FIRSTPAGINATIONTHRESHOLD; i++) {
          if (this.Lastoperation[i]) {
            
            this.tabletoshow.push(this.Lastoperation[i]);
            this.tabletoshow[i].checkbox = false;
          }
        }
        this.InfiniteScrollingIndex = this.FIRSTPAGINATIONTHRESHOLD;
        
      }, (err) => {
        console.log(err);
      }
      )
    }
    

  doInfinite(infiniteScroll) {
    
    let MoreItems = this.InfiniteScrollingIndex + this.NEXTELEMENTSTOLOAD;
    setTimeout(() => {
      for (let i = this.InfiniteScrollingIndex; i < MoreItems; i++) {
        if (this.Lastoperation[i]) {
          this.tabletoshow.push(this.Lastoperation[i]);
          this.tabletoshow[i].checkbox = false;
        }
      }
      this.InfiniteScrollingIndex = MoreItems;
      infiniteScroll.complete();
    }, 500);
  }

}
