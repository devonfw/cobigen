import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CoreModule } from '../core/core.module';
import { TranslateModule } from '@ngx-translate/core';

import { ${variables.etoName?cap_first}Service } from './services/${variables.etoName?lower_case}.service';
import { ${variables.etoName?cap_first}GridComponent } from './${variables.etoName?lower_case}-grid/${variables.etoName?lower_case}-grid.component';
import { ${variables.etoName?cap_first}DialogComponent } from './${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';
import { ${variables.etoName?cap_first}AlertComponent } from './${variables.etoName?lower_case}-alert/${variables.etoName?lower_case}-alert.component';

@NgModule({
  imports: [
    CommonModule,
    CoreModule,
  ],
  declarations: [
    ${variables.etoName?cap_first}GridComponent,
    ${variables.etoName?cap_first}DialogComponent,
    ${variables.etoName?cap_first}AlertComponent,
  ],
  entryComponents: [
    ${variables.etoName?cap_first}DialogComponent,
    ${variables.etoName?cap_first}AlertComponent,
  ],
  providers: [
    ${variables.etoName?cap_first}Service,
  ],
})
export class ${variables.etoName?cap_first}Module { }
