<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './shared/security/auth-guard.service';
import { InitialPageComponent } from './initial-page/initial-page.component';
import { HomeComponent } from './home/home.component';
import { ${variables.component?cap_first}DataGridComponent } from './${variables.component}dataGrid/${variables.component}dataGrid.component';
