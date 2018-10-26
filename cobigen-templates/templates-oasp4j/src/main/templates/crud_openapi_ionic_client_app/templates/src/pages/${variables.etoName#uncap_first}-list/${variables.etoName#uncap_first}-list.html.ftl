<!--
  Generated template for the list page.

  See http://ionicframework.com/docs/components/#navigation for more info on
  Ionic pages and navigation.
-->
<ion-header>
  <layoutheader Title="${variables.etoName}"></layoutheader>
  <header class="header-attributes">
    <span class="header-span item item-block item-md">
      <div class="item-inner">
        <div class="input-wrapper">
          <ion-label class="label label-md">
            <ion-grid class="header-grid">
              <ion-row> <#list model.properties as field>
                <ion-col class="crop">{{'${variables.component?uncap_first}.${variables.etoName?uncap_first}.${field.name}'| translate}}</ion-col></#list>
              </ion-row>
            </ion-grid>
          </ion-label>
        </div>
      </div>
    </span>
  </header>
</ion-header>
<ion-content padding>
  <ion-refresher (ionRefresh)="doRefresh($event)">
      <ion-refresher-content></ion-refresher-content>
  </ion-refresher>
  <ion-list>
    <ion-item-sliding *ngFor="let ${variables.etoName?uncap_first} of ${variables.etoName?uncap_first}s; let i = index">
      <ion-item [class.selected]="i === selectedItemIndex" (click)="enableUpdateDeleteOperations(i)" >
        <ion-grid>
          <ion-row>
          <#list model.properties as field>
            <ion-col>{{${variables.etoName?uncap_first}.${field.name}}}</ion-col>
          </#list>
          </ion-row>
        </ion-grid>
      </ion-item>
      <ion-item-options icon-start (ionSwipe)="setSelectedItemIndex(i);deleteSelected${variables.etoName?cap_first}()">
        <button color="danger" ion-button expandable (click)="setSelectedItemIndex(i);deleteSelected${variables.etoName?cap_first}()">
          <ion-icon name="trash"></ion-icon>
        </button>
      </ion-item-options>
      <ion-item-options side="left" (ionSwipe)="setSelectedItemIndex(i);updateSelected${variables.etoName?cap_first}()">
        <button ion-button color="secondary" ion-button expandable (click)="setSelectedItemIndex(i);updateSelected${variables.etoName?cap_first}()">
          <ion-icon name="brush"></ion-icon>
        </button>
      </ion-item-options>
    </ion-item-sliding>
  </ion-list>
    <ion-infinite-scroll *ngIf="infiniteScrollEnabled" (ionInfinite)="doInfinite($event)">
      <ion-infinite-scroll-content></ion-infinite-scroll-content>
    </ion-infinite-scroll>
  <ion-fab bottom right>
    <button ion-fab color="blue">
      <ion-icon name="arrow-dropright"></ion-icon>
    </button>
    <ion-fab-list side="top">
      <button ion-fab class="fabButton" (click)="create${variables.etoName?cap_first}()" >
        <ion-icon name="add-circle"></ion-icon>
      </button>
      
      <button ion-fab (click)="updateSelected${variables.etoName?cap_first}()" [disabled] = deleteModifiedButtonsDisabled  > 
        <ion-icon name="brush"></ion-icon>
      </button>
    
      <button ion-fab (click)="deleteSelected${variables.etoName?cap_first}()" [disabled] = deleteModifiedButtonsDisabled >
        <ion-icon name="trash"></ion-icon>
      </button>
      
      <button ion-fab (click)="search${variables.etoName?cap_first}s()" >
        <ion-icon name="search"></ion-icon>
      </button>
    </ion-fab-list>
  </ion-fab>


</ion-content>
