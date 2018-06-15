<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>

<#include '/functions.ftl'>
<h1 md-dialog-title>{{title}}</h1>
<form (ngSubmit)="dialogRef.close(items)" #dialogForm = "ngForm">

    <@getNG2Type_Add_Dialog/>

    <md-dialog-actions>
        <button type="button" md-button md-dialog-close>{{'buttons.cancel' | translate}}</button>
        <button type="submit" md-button [disabled]="!dialogForm.form.valid"> {{'buttons.save' | translate}} </button>
    </md-dialog-actions>
</form>
