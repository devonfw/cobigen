import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CoreModule } from '../core/core.module';
import { TranslateModule } from '@ngx-translate/core';

import { ${variables.entityName?cap_first}Service } from './services/${variables.entityName?lower_case}.service';
import { ${variables.entityName?cap_first}GridComponent } from './${variables.entityName?lower_case}-grid/${variables.entityName?lower_case}-grid.component';
import { ${variables.entityName?cap_first}DialogComponent } from './${variables.entityName?lower_case}-dialog/${variables.entityName?lower_case}-dialog.component';
import { ${variables.entityName?cap_first}AlertComponent } from './${variables.entityName?lower_case}-alert/${variables.entityName?lower_case}-alert.component';

@NgModule({
  imports: [
    CommonModule,
    CoreModule,
    TranslateModule,
  ],
  declarations: [
    ${variables.entityName?cap_first}GridComponent,
    ${variables.entityName?cap_first}DialogComponent,
    ${variables.entityName?cap_first}AlertComponent,
  ],
  entryComponents: [
    ${variables.entityName?cap_first}DialogComponent,
    ${variables.entityName?cap_first}AlertComponent,
  ],
  providers: [
    ${variables.entityName?cap_first}Service,
  ],
})
export class ${variables.entityName?cap_first}Module { }
