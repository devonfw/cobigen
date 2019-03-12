import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SampledataList } from './pages/${variables.etoName?lower_case}-list/${variables.etoName?lower_case}-list.page';
import { AuthGuardService } from './services/authorization/auth-guard.service';

const routes: Routes = [
  { path: '', loadChildren: './pages/login/login.module#LoginPageModule', pathMatch: 'full' },
  { path: 'home', loadChildren: './pages/home/home.module#HomePageModule', pathMatch: 'full', canActivate: [AuthGuardService]},
  { path: '${variables.etoName?lower_case}', component: ${variables.etoName?cap_first}List, pathMatch: 'full', canActivate: [AuthGuardService]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
