import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslocoService } from '@ngneat/transloco';

@Component({
  selector: 'public-${variables.etoName?lower_case}-dialog',
  templateUrl: './${variables.etoName?lower_case}-dialog.component.html',
})
export class ${variables.etoName?cap_first}DialogComponent {

  title: string;
  items: any = {
  <#list model.properties as property>
    ${property.name?uncap_first}: '',
  </#list>
  };

  constructor(
    private translocoService: TranslocoService,
    public dialogRef: MatDialogRef<${variables.etoName?cap_first}DialogComponent>,
    @Inject(MAT_DIALOG_DATA) dialogData: any,
  ) {
    if (!dialogData) {
      this.title = this.translocoService.translate(
        '${variables.component}.addTitle',
      );
    } else {
      this.title = this.translocoService.translate(
        '${variables.component}.editTitle',
      );
       <#list model.properties as property>
        this.items.${property.name?uncap_first} = dialogData.${property.name?uncap_first};
      </#list>
      this.items.id = dialogData.id;
      this.items.modificationCounter = dialogData.modificationCounter;
    }
  }

  /* getTranslation(text: string): string {
    let value: string;
    this.translocoService.get(text).subscribe((res: any) => {
      value = res;
    });
    return value;
  } */
}
