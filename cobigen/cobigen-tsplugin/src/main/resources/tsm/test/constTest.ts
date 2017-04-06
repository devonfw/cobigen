import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { SampledatamanagementDataGridComponent } from './components/sampledatamanagementdataGrid/sampledatamanagementdataGrid.component';

const appRoutes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'home', component: HomeComponent },
    { path: 'sampledatamanagementdataGrid', component: SampledatamanagementDataGridComponent },
    { path: '**', redirectTo: 'home' }
]

export const routing = RouterModule.forRoot(appRoutes)