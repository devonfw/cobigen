import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ${variables.component?cap_first}DataGridComponent } from './${variables.component}dataGrid/${variables.component}dataGrid.component';
import { InitialPageComponent } from './initial-page/initial-page.component';
import { AuthGuard } from './shared/security/auth-guard.service';

const appRoutes: Routes = [{
        path: 'login',
        component: LoginComponent,
    }, {
        path: 'home',
        component: HomeComponent,
        canActivate: [AuthGuard],
        children: [{
                path: '', redirectTo: '/home/initialPage', pathMatch: 'full', canActivate: [AuthGuard],
            }, {
                path: 'initialPage',
                component: InitialPageComponent,
                canActivate: [AuthGuard],
            }, {
                path: '${variables.component}dataGrid',
                component: ${variables.component?cap_first}DataGridComponent,
                canActivate: [AuthGuard],
            }]
    }, {
        path: '**',
        redirectTo: '/login',
        pathMatch: 'full' }
];

export const routing = RouterModule.forRoot(appRoutes)
