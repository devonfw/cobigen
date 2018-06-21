<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>

import { Component, Inject } from '@angular/core';
import { MdDialogRef, MD_DIALOG_DATA } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-${variables.component}-add-dialog',
  templateUrl: './${variables.component}addDialog.component.html'
})

export class ${variables.component?cap_first}AddDialogComponent {
  items: any;
  title: string;

  constructor(public dialogRef: MdDialogRef<${variables.component?cap_first}AddDialogComponent>,
              private translate: TranslateService,
              @Inject(MD_DIALOG_DATA) dialogData: any) {
                if (!dialogData) {
                  this.title = this.getTranslation('${variables.component}datagrid.addTitle');
                  this.items = {
                  <#list elemDoc["self::node()/ownedAttribute"] as field>
                      <#if field?has_next>
                    ${field["@name"]}: '',
                      <#else>
                    ${field["@name"]}: ''
                     </#if>
                    </#list>
                  };
                } else {
                  this.title = this.getTranslation('${variables.component}datagrid.editTitle');
                  this.items = dialogData;
                }
  }

  getTranslation(text: string): string {
    let value: string;
    this.translate.get(text).subscribe( (res) => {
        value = res;
    });
    return value;
  }
}
