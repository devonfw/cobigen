<#import '/variables.ftl' as class>
<h1 mat-dialog-title>{{title}}</h1>
<form (ngSubmit)="dialogRef.close(items)" #dialogForm="ngForm">
  <#list class.properties as property>
  <mat-form-field>
    <input matInput type="${property.type.name}" name = "${property.identifier?uncap_first}" [placeholder]= "'${variables.component}.${variables.entityName}.columns.${property.identifier?uncap_first}' | translate"
      [(ngModel)] = "items.${property.identifier?uncap_first}" required>
  </mat-form-field>
  </#list>
  <mat-dialog-actions>
    <button type="button" mat-button mat-dialog-close>{{'buttons.cancel' | translate}}</button>
    <button type="submit" mat-button [disabled]="!dialogForm.form.valid"> {{'buttons.save' | translate}} </button>
  </mat-dialog-actions>
</form>
