<#import '/variables.ftl' as class>
import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'public-${variables.entityName?lower_case}-dialog',
  templateUrl: './${variables.entityName?lower_case}-dialog.component.html',
})
export class ${variables.entityName?cap_first}DialogComponent {

  title: string;
  items: any = {
  <#list class.properties as property>
    ${property.identifier?uncap_first}: '',
  </#list>
  };

  constructor(
    private translate: TranslateService,
    public dialogRef: MatDialogRef<${variables.entityName?cap_first}DialogComponent>,
    @Inject(MAT_DIALOG_DATA) dialogData: any,
  ) {
    if (!dialogData) {
      this.title = this.getTranslation('${variables.component}.addTitle');
    } else {
      this.title = this.getTranslation('${variables.component}.editTitle');
      <#list class.properties as property>
        this.items.${property.identifier?uncap_first} = dialogData.${property.identifier?uncap_first};
      </#list>
      this.items.id = dialogData.id;
      this.items.modificationCounter = dialogData.modificationCounter;
    }
  }

  getTranslation(text: string): string {
    let value: string;
    this.translate.get(text).subscribe((res: any) => {
      value = res;
    });
    return value;
  }
}
