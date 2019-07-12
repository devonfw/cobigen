import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from '../core/security/auth-guard.service';
import { HomeComponent } from '../home/home.component';
import { InitialPageComponent } from '../home/initial-page/initial-page.component';
import { ${variables.etoName?cap_first}GridComponent } from './components/${variables.etoName?lower_case}-grid/${variables.etoName?lower_case}-grid.component';

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        redirectTo: '/home/initialPage',
        pathMatch: 'full',
        canActivate: [AuthGuard],
      },
      {
        path: 'initialPage',
        component: InitialPageComponent,
        canActivate: [AuthGuard],
      },
      {
        path: '${variables.etoName?uncap_first}',
        component: ${variables.etoName?cap_first}GridComponent,
        canActivate: [AuthGuard],
      },
    ],
  },

  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full',
  },
  {
    path: '**',
    redirectTo: '/login',
  },
];
@NgModule({
  exports: [RouterModule],
  imports: [RouterModule.forChild(routes)],
})
export class ${variables.etoName?cap_first}RoutingModule {}
