import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  routerReducer,
  StoreRouterConnectingModule,
  DefaultRouterStateSerializer,
} from '@ngrx/router-store';
import { StoreModule } from '@ngrx/store';
import { AuthGuard } from './core/security/auth-guard.service';
import { NavBarComponent } from './layout/nav-bar/nav-bar.component';
const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/login',
  },
  {
    path: 'login',
    loadChildren: () =>
      import('./auth/auth.module').then(m => m.AuthDataModule),
  },
  {
    path: 'home',
    canActivate: [AuthGuard],
    component: NavBarComponent,
    children: [
      {
        path: '${variables.etoName?uncap_first}',
        canActivate: [AuthGuard],
        loadChildren: () =>
          import('./${variables.etoName?lower_case}/${variables.etoName?lower_case}.module').then(
            m => m.${variables.etoName?cap_first}Module,
          ),
      },
      {
        path: 'initial',
        canActivate: [AuthGuard],
        loadChildren: () =>
          import('./home/initial-page/initial-page.module').then(
            m => m.InitialPageModule,
          ),
      },
    ],
  },
  {
    path: '**',
    redirectTo: '/login',
  },
];

/* @export
 * @class AppRoutingModule
 */
@NgModule({
  exports: [RouterModule],
  imports: [
    CommonModule,
    StoreModule.forRoot(
      {
        router: routerReducer,
      },
      {
        runtimeChecks: {
          strictStateImmutability: true,
          strictActionImmutability: true,
        },
      },
    ),
    RouterModule.forRoot(routes),
    StoreRouterConnectingModule.forRoot({
      serializer: DefaultRouterStateSerializer,
    }),
  ],
})

export class AppRoutingModule {}
