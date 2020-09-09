import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslocoService } from '@ngneat/transloco';

/* @export
 * @class ${variables.etoName?cap_first}DialogComponent
 */
@Component({
  selector: 'public-${variables.etoName?lower_case}-dialog',
  templateUrl: './${variables.etoName?lower_case}-dialog.component.html',
})
export class ${variables.etoName?cap_first}DialogComponent {

  title: string;
  items: any = {
  <#list pojo.fields as field>
    ${field.name?uncap_first}: '',
  </#list>
  };

  /* Creates an instance of ${variables.etoName?cap_first}DialogComponent.
   * @param {TranslateService} translate
   * @param {MatDialogRef<${variables.etoName?cap_first}DialogComponent>} dialogRef
   * @param {*} dialogData
   * @memberof ${variables.etoName?cap_first}DialogComponent
   */
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
      this.items = { ...dialogData };
    }
  }
}
