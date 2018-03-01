<!--
  Generated template for the TablePage page.

  See http://ionicframework.com/docs/components/#navigation for more info on
  Ionic pages and navigation.
-->
<ion-header>
  <layoutheader Title="${variables.etoName}"></layoutheader>
</ion-header>


<ion-content padding>

  <ion-grid>
    <ion-row *ngFor="let p of tabletoshow; let i = index" [(ngModel)]="checkboxes" ngDefaultControl>
      <ion-col>
        <ion-checkbox (click)="NoMorethanOneCheckbox(i)" checked="{{p.checkbox}}"></ion-checkbox>
      </ion-col>
      <#list pojo.fields as field>
        <ion-col>{{p.${field.name}}}</ion-col>
        </#list>
    </ion-row>
    <ion-infinite-scroll (ionInfinite)="doInfinite($event)">
      <ion-infinite-scroll-content></ion-infinite-scroll-content>
    </ion-infinite-scroll>
  </ion-grid>
  <ion-fab bottom right>
    <button ion-fab color="blue">
      <ion-icon name="arrow-dropright"></ion-icon>
    </button>
    <ion-fab-list side="top">
      <sample-operations [isDisabled]="Delete_and_Modified_Buttons_Disabled">
      </sample-operations>
    </ion-fab-list>
  </ion-fab>

</ion-content>
