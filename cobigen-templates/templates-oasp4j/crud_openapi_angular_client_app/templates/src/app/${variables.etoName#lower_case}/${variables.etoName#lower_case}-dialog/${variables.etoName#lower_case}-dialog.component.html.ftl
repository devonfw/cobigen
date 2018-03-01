<h1 mat-dialog-title>{{title}}</h1>
<form (ngSubmit)="dialogRef.close(items)" #dialogForm="ngForm">
  <#list model.properties as property>
  <mat-input-container>
    <input matInput type="${OaspUtil.getOaspTypeFromOpenAPI(property, false)}" name = "${property.name}" [placeholder]= "'${variables.component}.${variables.etoName}.columns.${property.name}' | translate" [(ngModel)] = "items.${property.name}" required>
  </mat-input-container>
  </#list>
  <mat-dialog-actions>
    <button type="button" mat-button mat-dialog-close>{{'buttons.cancel' | translate}}</button>
    <button type="submit" mat-button [disabled]="!dialogForm.form.valid"> {{'buttons.save' | translate}} </button>
  </mat-dialog-actions>
</form>
