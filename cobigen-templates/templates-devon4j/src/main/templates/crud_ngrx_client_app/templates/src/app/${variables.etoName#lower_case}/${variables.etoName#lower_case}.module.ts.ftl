import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { CoreModule } from '../core/core.module';
import { ${variables.etoName?cap_first}DialogComponent } from '../${variables.etoName?lower_case}/components/${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';
import { ${variables.etoName?cap_first}AlertComponent } from './components/${variables.etoName?lower_case}-alert/${variables.etoName?lower_case}-alert.component';
import { ${variables.etoName?cap_first}GridComponent } from './components/${variables.etoName?lower_case}-grid/${variables.etoName?lower_case}-grid.component';
import { ${variables.etoName?cap_first}RoutingModule } from './${variables.etoName?lower_case}-routing.module';
import { ${variables.etoName?cap_first}Effects } from './store/effects/${variables.etoName?lower_case}.effects';
import { reducers } from './store/reducers/index';

/* @export
 * @class ${variables.etoName?cap_first}Module
 */
@NgModule({
  imports: [
    CommonModule,
    CoreModule,

    ${variables.etoName?cap_first}RoutingModule,
    StoreModule.forFeature('${variables.etoName?lower_case}reducer', reducers),
    EffectsModule.forFeature([${variables.etoName?cap_first}Effects]),
  ],
  declarations: [
    ${variables.etoName?cap_first}GridComponent,
    ${variables.etoName?cap_first}DialogComponent,
    ${variables.etoName?cap_first}AlertComponent,
  ],
  providers: [],
})
export class ${variables.etoName?cap_first}Module { }
