<#include '/functions.ftl'>
<h1 md-dialog-title>{{title}}</h1>
<form #dialogForm = "ngForm">
    <@getNG2Type_Add_Dialog/>
    
    <md-dialog-actions>
        <button type = "button" md-icon-button md-dialog-close>{{'buttons.cancel' | translate}}</button>
        <button type = "button" md-icon-button [disabled] = "!dialogForm.form.valid" (click)="dialogRef.close(item)"> {{'buttons.save' | translate}} </button>
    </md-dialog-actions>
</form>
