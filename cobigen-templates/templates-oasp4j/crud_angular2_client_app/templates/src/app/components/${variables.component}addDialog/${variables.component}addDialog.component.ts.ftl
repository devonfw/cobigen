import { MdDialogRef } from '@angular/material';
import { Component } from '@angular/core';


@Component({
  selector: '${variables.component}-add-dialog',
  templateUrl: './${variables.component}addDialog.component.html'
})

export class ${variables.component?cap_first}AddDialogComponent {
  cobigen_items = {
  <#list pojo.fields as field>
    <#if field?has_next>
  ${field.name}: '',
    <#else>
  ${field.name}: ''
   </#if>
  </#list>
  };

  title: string;

  constructor(public dialogRef: MdDialogRef<${variables.component?cap_first}AddDialogComponent>) { }
}
