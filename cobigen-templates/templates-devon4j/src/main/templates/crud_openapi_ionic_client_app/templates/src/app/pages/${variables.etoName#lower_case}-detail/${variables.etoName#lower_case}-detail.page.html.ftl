<!-- Generated template for the ${variables.etoName}Detail component -->
<ion-header>
  <ion-toolbar color="primary" >
    <ion-title align=center>
      {{translations.title}}
    </ion-title>
    
    <ion-buttons slot="end">
      <ion-button (tap)="dismiss()">
        <ion-text color="primary" showWhen="ios">Cancel</ion-text>
        <ion-icon name="md-close" showWhen="android,windows"></ion-icon>
      </ion-button>
    </ion-buttons>
  </ion-toolbar>
</ion-header>

<ion-content>
  <div class="detail-message" padding >{{translations.message}}</div>
  <form class="form-content">
  
    <#list model.properties as field> 
    <ion-item lines="inset" class="formItem">
      <ion-label>{{'${variables.component?lower_case}.${variables.etoName?lower_case}.${field.name}'| translate}}</ion-label>
      <ion-input  ${JavaUtil.getAngularType(field.type)} [(ngModel)]="${variables.etoName?lower_case}Received.${field.name}" name="${field.name}"></ion-input>
    </ion-item>
    </#list>
     
  </form>
</ion-content>
<ion-footer>
  <ion-button class="buttonForm" [hidden]=!filterActive (tap)="addOrModify()" expand="full">
    {{'${variables.component?lower_case}.${variables.etoName?lower_case}.commonbuttons.send' | translate}}
  </ion-button>
  <ion-button class="buttonForm" [hidden]=filterActive (tap)="search()" expand="full">
    {{'${variables.component?lower_case}.${variables.etoName?lower_case}.commonbuttons.send' | translate}}
  </ion-button>
  <ion-button class="buttonForm" [hidden]=filterActive (tap)="clearSearch()" expand="block">
    {{'${variables.component?lower_case}.${variables.etoName?lower_case}.operations.filter.clear' | translate}}
  </ion-button>
</ion-footer>
