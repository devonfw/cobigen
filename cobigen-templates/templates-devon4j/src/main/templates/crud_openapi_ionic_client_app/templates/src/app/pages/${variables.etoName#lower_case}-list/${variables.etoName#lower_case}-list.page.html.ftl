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
                <ion-col class="crop">{{'${variables.component?lower_case}.${variables.etoName?lower_case}.${field.name}'| transloco}}</ion-col></#list>
              </ion-row>
            </ion-grid>
          </ion-label>
        </div>
      </div>
    </span>
  </header>
</ion-header>
<ion-content class="ion-padding">
  <ion-refresher slot="fixed" (ionRefresh)="doRefresh($event)">
    <ion-refresher-content></ion-refresher-content>
  </ion-refresher>
  <ion-list #slidingList>
    <ion-item-sliding *ngFor="let ${variables.etoName?lower_case} of ${variables.etoName?lower_case}s; let i = index">
      <ion-item
        [class.selected]="i === selectedItemIndex"
        (click)="enableUpdateDeleteOperations(i)"
        tappable
      >
        <ion-grid>
          <ion-row class="grid-margin">
          <#list model.properties as field>
            <ion-col>{{${variables.etoName?lower_case}.${field.name}}}</ion-col>
          </#list>
          </ion-row>
        </ion-grid>
      </ion-item>
      <ion-item-options
        side="end"
        (ionSwipe)="setSelectedItemIndex(i); deleteSelected${variables.etoName?cap_first}()"
      >
        <ion-item-option
          expandable
          color="danger"
          (click)="setSelectedItemIndex(i); deleteSelected${variables.etoName?cap_first}()"
          tappable
        >
          <ion-icon size="large" name="trash"></ion-icon>
        </ion-item-option>
      </ion-item-options>
      <ion-item-options
        side="start"
        (ionSwipe)="setSelectedItemIndex(i); updateSelected${variables.etoName?cap_first}()"
      >
        <ion-item-option
          expandable
          (click)="setSelectedItemIndex(i); updateSelected${variables.etoName?cap_first}()"
          color="secondary"
          tappable
        >
          <ion-icon size="large" name="brush"></ion-icon>
        </ion-item-option>
      </ion-item-options>
    </ion-item-sliding>
  </ion-list>
  <ion-infinite-scroll
    *ngIf="infiniteScrollEnabled"
    (ionInfinite)="doInfinite($event)"
  >
    <ion-infinite-scroll-content></ion-infinite-scroll-content>
  </ion-infinite-scroll>
  <ion-fab vertical="bottom" horizontal="end" slot="fixed">
    <ion-fab-button color="primary">
      <ion-icon name="arrow-up-outline"></ion-icon>
    </ion-fab-button>
    <ion-fab-list side="top">
      <ion-fab-button
        color="primary"
        class="fabButton fab-button-size"
        (click)="create${variables.etoName?cap_first}()"
      >
        <ion-icon name="add-circle"></ion-icon>
      </ion-fab-button>

      <ion-fab-button
        color="primary"
        (click)="updateSelected${variables.etoName?cap_first}()"
        [disabled]="deleteModifiedButtonsDisabled"
      >
        <ion-icon name="brush"></ion-icon>
      </ion-fab-button>

      <ion-fab-button
        color="primary"
        (click)="deleteSelected${variables.etoName?cap_first}()"
        [disabled]="deleteModifiedButtonsDisabled"
      >
        <ion-icon name="trash"></ion-icon>
      </ion-fab-button>

      <ion-fab-button color="primary" (click)="search${variables.etoName?cap_first}s()">
        <ion-icon name="search"></ion-icon>
      </ion-fab-button>
    </ion-fab-list>
  </ion-fab>
</ion-content>
