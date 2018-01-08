import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ${variables.etoName?cap_first}DataGridService } from './services/${variables.etoName?lower_case}.service';
import { ${variables.etoName?cap_first}GridComponent } from './${variables.etoName?lower_case}-grid/${variables.etoName?lower_case}-grid.component';
import { ${variables.etoName?cap_first}DialogComponent } from './${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';
import { CoreModule } from '../core/core.module';

@NgModule({
  imports: [
    CommonModule,
    CoreModule,
  ],
  declarations: [
    ${variables.etoName?cap_first}GridComponent,
    ${variables.etoName?cap_first}DialogComponent
  ],
  entryComponents: [
    ${variables.etoName?cap_first}DialogComponent
  ],
  providers: [
    ${variables.etoName?cap_first}DataGridService
  ]
})
export class ${variables.etoName?cap_first}Module { }
