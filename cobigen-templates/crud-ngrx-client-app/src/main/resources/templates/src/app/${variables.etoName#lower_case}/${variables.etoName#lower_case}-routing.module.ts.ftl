import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ${variables.etoName?cap_first}GridComponent } from './components/${variables.etoName?lower_case}-grid/${variables.etoName?lower_case}-grid.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: ${variables.etoName?cap_first}GridComponent,
  },
];
@NgModule({
  exports: [RouterModule],
  imports: [RouterModule.forChild(routes)],
})
export class ${variables.etoName?cap_first}RoutingModule {}
