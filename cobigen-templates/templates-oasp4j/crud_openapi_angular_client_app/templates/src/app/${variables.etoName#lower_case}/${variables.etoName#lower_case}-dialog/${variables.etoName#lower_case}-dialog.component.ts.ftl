import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-${variables.etoName?lower_case}-dialog',
  templateUrl: './${variables.etoName?lower_case}-dialog.component.html'
})
export class ${variables.etoName?cap_first}DialogComponent implements OnInit {

  title: string;
  items = {
  <#list model.properties as property>
    ${property.name}: ''<#if property?has_next>,</#if>
  </#list>
  };

  ngOnInit() {
  }

  constructor(public dialogRef: MatDialogRef<${variables.etoName?cap_first}DialogComponent >,
              private translate: TranslateService,
              @Inject(MAT_DIALOG_DATA) dialogData: any) {
    if (!dialogData) {
      this.title = this.getTranslation('${variables.component}.addTitle');
    } else {
      this.title = this.getTranslation('${variables.component}.editTitle');
      this.items = dialogData;
    }
  }

  getTranslation(text: string): string {
    let value: string;
    this.translate.get(text).subscribe((res) => {
      value = res;
    });
    return value;
  }

}
