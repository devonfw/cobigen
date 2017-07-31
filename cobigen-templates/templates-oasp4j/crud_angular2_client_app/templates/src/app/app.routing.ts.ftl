import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { ${variables.etoName?cap_first}DataGridComponent } from './components/${variables.etoName}DataGrid/${variables.etoName}DataGrid.component';

const appRoutes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'home', component: HomeComponent },
    { path: '${variables.etoName}DataGrid', component: ${variables.etoName?cap_first}DataGridComponent },
    { path: '**', redirectTo: 'home' }
]

export const routing = RouterModule.forRoot(appRoutes)
