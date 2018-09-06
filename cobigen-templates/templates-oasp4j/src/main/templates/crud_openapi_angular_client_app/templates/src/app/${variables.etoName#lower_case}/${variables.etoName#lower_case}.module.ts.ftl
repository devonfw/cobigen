import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CoreModule } from '../core/core.module';
import { TranslateModule } from '@ngx-translate/core';

import { ${variables.etoName?cap_first}Service } from './services/${variables.etoName?lower_case}.service';
import { ${variables.etoName?cap_first}GridComponent } from './${variables.etoName?lower_case}-grid/${variables.etoName?lower_case}-grid.component';
import { ${variables.etoName?cap_first}DialogComponent } from './${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';

@NgModule({
  imports: [
    CommonModule,
    CoreModule,
    TranslateModule,
  ],
  declarations: [
    ${variables.etoName?cap_first}GridComponent,
    ${variables.etoName?cap_first}DialogComponent,
  ],
  entryComponents: [
    ${variables.etoName?cap_first}DialogComponent,
  ],
  providers: [
    ${variables.etoName?cap_first}Service,
  ],
})
export class ${variables.etoName?cap_first}Module { }
