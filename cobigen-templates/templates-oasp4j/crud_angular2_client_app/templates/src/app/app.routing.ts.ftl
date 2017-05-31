import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { ${variables.component?cap_first}DataGridComponent } from './components/${variables.component}dataGrid/${variables.component}dataGrid.component';

const appRoutes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'home', component: HomeComponent },
    { path: '${variables.component}dataGrid', component: ${variables.component?cap_first}DataGridComponent },
    { path: '**', redirectTo: 'home' }
]

export const routing = RouterModule.forRoot(appRoutes)
