<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>
// Components
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ${variables.component?cap_first}DataGridComponent } from './${variables.component}dataGrid/${variables.component}dataGrid.component';

import {
    ${variables.component?cap_first}AddDialogComponent
} from './${variables.component}dataGrid/${variables.component}addDialog/${variables.component}addDialog.component';
import { HeaderComponent } from './header/header.component';
import { InitialPageComponent } from './initial-page/initial-page.component';

// modules
import { routing } from './app.routing';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { CovalentModule } from './shared/covalent.module';
import { HttpModule, Http} from '@angular/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { MaterialModule } from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import 'hammerjs';

// services
import { BusinessOperations } from './BusinessOperations';
import { ${variables.component?cap_first}DataGridService } from './${variables.component}dataGrid/shared/${variables.component}dataGrid.service';
import { HttpClient } from './shared/security/httpClient.service';
import { LoginService } from './login/shared/login.service';
import { AuthService } from './shared/security/auth.service';
import { HeaderService } from './header/shared/header.service';
import { AuthGuard } from './shared/security/auth-guard.service';

export function translateFactory(http: Http) {
    return  new TranslateHttpLoader(http);
}
@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        CovalentModule,
        FormsModule,
        HttpModule,
        routing,
        MaterialModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: translateFactory,
                deps: [Http]
            }
        })
    ],
    declarations: [
        AppComponent,
        LoginComponent,
        HomeComponent,
        ${variables.component?cap_first}DataGridComponent,
        ${variables.component?cap_first}AddDialogComponent,
        HeaderComponent,
        InitialPageComponent
    ],
    entryComponents: [
        ${variables.component?cap_first}AddDialogComponent
    ],
    bootstrap: [
        AppComponent
    ],
    providers: [
        ${variables.component?cap_first}DataGridService,
        HttpClient,
        AuthGuard,
        LoginService,
        HeaderService,
        AuthService,
        BusinessOperations
    ]
})
export class AppModule {}
