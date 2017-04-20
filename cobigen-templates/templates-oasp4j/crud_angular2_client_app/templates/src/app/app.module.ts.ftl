import { BusinessOperations } from './BusinessOperations';
import { ${variables.etoName?cap_first}AddDialogComponent } from './components/${variables.etoName}AddDialog/${variables.etoName}AddDialog.component';

import { TranslateModule, TranslateLoader, TranslateStaticLoader } from 'ng2-translate/ng2-translate';
import { HttpModule, Http } from '@angular/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { MaterialModule } from '@angular/material';
import { AppComponent } from './app.component';
import { routing } from './app.routing';
import { FormsModule } from '@angular/forms';
import 'hammerjs';
import { CovalentCoreModule } from '@covalent/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { HeaderComponent } from './components/header/header.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { ${variables.etoName?cap_first}DataGridComponent } from './components/${variables.etoName}DataGrid/${variables.etoName}DataGrid.component';

import { ${variables.etoName?cap_first}DataGridService } from './components/${variables.etoName}DataGrid/${variables.etoName}DataGrid.service';
import { SecurityService } from './security/security.service';
import { HttpClient } from './security/httpClient.service';

export function translateFactory(http: Http) {
  return  new TranslateStaticLoader(http, '/assets/i18n', '.json');
}

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    CovalentCoreModule.forRoot(),
    FormsModule,
    HttpModule,
    routing,
    MaterialModule.forRoot(),
    TranslateModule.forRoot({
      provide: TranslateLoader,
      useFactory: translateFactory,
      deps: [Http]
    })
  ],
  declarations: [
    AppComponent,
    HeaderComponent,
    LoginComponent,
    HomeComponent,
    ${variables.etoName?cap_first}DataGridComponent,
    ${variables.etoName?cap_first}AddDialogComponent
  ],
  entryComponents: [
    ${variables.etoName?cap_first}AddDialogComponent
  ],
  bootstrap: [
    AppComponent
  ],
  providers: [
    ${variables.etoName?cap_first}DataGridService,
    SecurityService,
    HttpClient,
    BusinessOperations
  ],
})
export class AppModule { }
