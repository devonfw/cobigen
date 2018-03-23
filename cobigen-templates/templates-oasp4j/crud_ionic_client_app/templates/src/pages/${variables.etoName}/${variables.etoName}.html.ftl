<!--
  Generated template for the TablePage page.

  See http://ionicframework.com/docs/components/#navigation for more info on
  Ionic pages and navigation.
-->
<ion-header>
  <layoutheader Title="${variables.etoName}"></layoutheader>
</ion-header>
<ion-content padding>
  <ion-list>
	<ion-item-sliding *ngFor="let p of tabletoshow; let i = index">
      <ion-item [class.selected]="i === currentIndex" (click)="enableUpdateDeleteOperations(i)" >
		  <ion-grid>
		      <ion-row>
		      <#list pojo.fields as field>
		        <ion-col>{{p.${field.name}}}</ion-col>
		      </#list>
		    </ion-row>
		  </ion-grid>
      </ion-item>
      <ion-item-options icon-start (ionSwipe)="DeleteConfirmForm(i)">
        <button color="danger" ion-button expandable (click)="DeleteConfirmForm(i)">
          <ion-icon name="trash"></ion-icon>
        </button>
      </ion-item-options>
      <ion-item-options side="left" (ionSwipe)="promptModifyClicked(i)">
        <button ion-button color="secondary" ion-button expandable (click)="promptModifyClicked(i)">
          <ion-icon name="brush"></ion-icon>
        </button>
      </ion-item-options>
    </ion-item-sliding>
  </ion-list>
    <ion-infinite-scroll (ionInfinite)="doInfinite($event)">
      <ion-infinite-scroll-content></ion-infinite-scroll-content>
    </ion-infinite-scroll>
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
