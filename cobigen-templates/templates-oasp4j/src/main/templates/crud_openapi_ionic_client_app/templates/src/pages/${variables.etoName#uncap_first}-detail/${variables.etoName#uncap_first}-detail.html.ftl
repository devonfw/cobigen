<!-- Generated template for the ${variables.etoName}Detail component -->
<ion-header>
  <ion-toolbar color="primary" >
    <ion-title align=center>
      {{translations.title}}
    </ion-title>
    
    <ion-buttons start>
      <button ion-button (click)="dismiss()">
        <span ion-text color="primary" showWhen="ios">Cancel</span>
        <ion-icon name="md-close" showWhen="android,windows"></ion-icon>
      </button>
    </ion-buttons>
  </ion-toolbar>
</ion-header>

<ion-content >
  
  <div align=center style="font-size: 16px" padding >{{translations.message}}</div>
  <form>
  
    <#list model.properties as field> 
    <ion-item class="formItem">
      <ion-label>{{'${variables.component?uncap_first}.${variables.etoName?uncap_first}.${field.name}'| translate}}</ion-label>
      <ion-input  ${JavaUtil.getAngularType(field.type)} [(ngModel)]="${variables.etoName?uncap_first}Received.${field.name}" name="${field.name}"></ion-input>
    </ion-item>
    </#list>
     
  </form>

</ion-content>

<ion-footer>
  
  <button ion-button class="buttonForm" [hidden]=!filterActive (click)="addOrModify()" full>
    {{'${variables.component?uncap_first}.${variables.etoName?uncap_first}.commonbuttons.send' | translate}}
  </button>
  <button ion-button class="buttonForm" [hidden]=filterActive (click)="search()" full >
    {{'${variables.component?uncap_first}.${variables.etoName?uncap_first}.commonbuttons.send' | translate}}
  </button>
  <button ion-button class="buttonForm" [hidden]=filterActive (click)="clearSearch()" block>
    {{'${variables.component?uncap_first}.${variables.etoName?uncap_first}.operations.filter.clear' | translate}}
  </button>


</ion-footer>
