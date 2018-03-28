<!-- Generated template for the ${variables.etoName}dialogComponent component -->
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
  
    <#list pojo.fields as field>
    <ion-item>
      <ion-label>{{'${variables.component}.${variables.etoName}.${field.name}'| translate}}</ion-label>
      <ion-input  ${JavaUtil.getAngularType(field.type)} [(ngModel)]="${variables.etoName}received.${field.name}" name="${field.name}"></ion-input>
    </ion-item>
    </#list>
     
  </form>

</ion-content>

<ion-footer>
  
  <button ion-button [hidden]=!disables.filter (click)="AddorModify()" full>
    {{'${variables.component}.${variables.etoName}.commonbuttons.send' | translate}}
  </button>
  <button ion-button [hidden]=disables.filter (click)="Search()" full >
    {{'${variables.component}.${variables.etoName}.commonbuttons.send' | translate}}
  </button>
  <button ion-button [hidden]=disables.filter (click)="clearSearch()" block>
    {{'${variables.component}.${variables.etoName}.operations.filter.clear' | translate}}
  </button>


</ion-footer>