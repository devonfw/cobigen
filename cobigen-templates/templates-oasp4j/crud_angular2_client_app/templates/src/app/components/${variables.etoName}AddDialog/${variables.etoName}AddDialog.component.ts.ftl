import { MdDialogRef } from '@angular/material';
import { Component } from '@angular/core';


@Component({
  selector: '${variables.etoName}-add-dialog',
  templateUrl: './${variables.etoName?cap_first}AddDialog.component.html'
})

export class ${variables.etoName?cap_first}AddDialogComponent {
  cobigen_item = {
  <#list pojo.fields as field>
    <#if field?has_next>
  ${field.name}: '',
    <#else>
  ${field.name}: ''
   </#if>
  </#list>
  };

  title: string;

  constructor(public dialogRef: MdDialogRef<${variables.etoName?cap_first}AddDialogComponent>) { }
}
