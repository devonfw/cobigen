import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CoreModule } from '../core/core.module';
import { TranslateModule } from '@ngx-translate/core';
import { StoreModule } from '@ngrx/store';
import { reducers } from './store/reducers/index';
import { EffectsModule } from '@ngrx/effects';
import { ${variables.etoName?cap_first}DialogComponent } from '../${variables.etoName?lower_case}/components/${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';
import { ${variables.etoName?cap_first}RoutingModule } from './${variables.etoName?lower_case}-routing.module';
import { ${variables.etoName?cap_first}AlertComponent } from './components/${variables.etoName?lower_case}-alert/${variables.etoName?lower_case}-alert.component';
import { ${variables.etoName?cap_first}GridComponent } from './components/${variables.etoName?lower_case}-grid/${variables.etoName?lower_case}-grid.component';
import { HomeModule } from '../home/home.module';
import { effects } from './store/effects';

@NgModule({
  imports: [
    CommonModule,
    CoreModule,
    TranslateModule,
    ${variables.etoName?cap_first}RoutingModule,
    HomeModule,
    StoreModule.forFeature('${variables.etoName?lower_case}reducer',reducers),
    EffectsModule.forFeature(effects),
  ],
  declarations: [
    ${variables.etoName?cap_first}GridComponent,
    ${variables.etoName?cap_first}DialogComponent,
    ${variables.etoName?cap_first}AlertComponent,
  ],
  entryComponents: [${variables.etoName?cap_first}DialogComponent, ${variables.etoName?cap_first}AlertComponent],
  providers: [],
})
export class ${variables.etoName?cap_first}Module { }
