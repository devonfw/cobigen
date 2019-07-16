<h1 mat-dialog-title>{{title}}</h1>
<form (ngSubmit)="dialogRef.close(items)" #dialogForm="ngForm">
  <#list model.properties as property>
  <mat-form-field>
    <input matInput type="${OpenApiUtil.toJavaType(property, false)}" name = "${property.name?uncap_first}" [placeholder]= "'${variables.component}.${variables.etoName}.columns.${property.name?uncap_first}' | translate"
      [(ngModel)] = "items.${property.name?uncap_first}" required>
  </mat-form-field>
  </#list>
  <mat-dialog-actions>
    <button type="button" mat-button mat-dialog-close>{{'buttons.cancel' | translate}}</button>
    <button type="submit" mat-button [disabled]="!dialogForm.form.valid"> {{'buttons.save' | translate}} </button>
  </mat-dialog-actions>
</form>
