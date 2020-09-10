import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CoreModule } from '../core/core.module';
import { ${variables.etoName?cap_first}AlertComponent } from './${variables.etoName?lower_case}-alert/${variables.etoName?lower_case}-alert.component';
import { ${variables.etoName?cap_first}DialogComponent } from './${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';
import { ${variables.etoName?cap_first}GridComponent } from './${variables.etoName?lower_case}-grid/${variables.etoName?lower_case}-grid.component';
import { ${variables.etoName?cap_first}RoutingModule } from './${variables.etoName?lower_case}-routing.module';

@NgModule({
  imports: [ CommonModule, CoreModule, ${variables.etoName?cap_first}RoutingModule ],
  declarations: [
    ${variables.etoName?cap_first}GridComponent,
    ${variables.etoName?cap_first}DialogComponent,
    ${variables.etoName?cap_first}AlertComponent,
  ],
  providers: [],
})
export class ${variables.etoName?cap_first}Module { }
