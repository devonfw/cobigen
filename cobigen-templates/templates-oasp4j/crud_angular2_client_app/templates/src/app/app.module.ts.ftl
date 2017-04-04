import { BusinessOperations } from './BusinessOperations';
import { ${variables.component?cap_first}AddDialogComponent } from './components/${variables.component}AddDialog/${variables.component}AddDialog.component';
// modules
import { TranslateModule, TranslateLoader, TranslateStaticLoader } from 'ng2-translate/ng2-translate';
import { HttpModule, Http } from '@angular/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { MaterialModule } from '@angular/material';
import { Material2AppAppComponent } from './app.component';
import { routing } from './app.routing';
import { FormsModule } from '@angular/forms';
import 'hammerjs';
import { CovalentCoreModule } from '@covalent/core';

// components
import { HeaderComponent } from './components/header/header.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { ${variables.component?cap_first}DataGridComponent } from './components/${variables.component}DataGrid/${variables.component}DataGrid.component';

// services
import { ${variables.component?cap_first}DataGridService } from './components/${variables.component}DataGrid/${variables.component}DataGrid.service';
import { SecurityService } from './security/security.service';
import { HttpClient } from './security/httpClient.service';

export function translateFactory(http: Http) {
  return  new TranslateStaticLoader(http, '/assets/i18n', '.json');
}

@NgModule({
  imports: [
    BrowserModule,
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
    Material2AppAppComponent,
    HeaderComponent,
    LoginComponent,
    HomeComponent,
    ${variables.component?cap_first}DataGridComponent,
    ${variables.component?cap_first}AddDialogComponent
  ],
  entryComponents: [
    ${variables.component?cap_first}AddDialogComponent
  ],
  bootstrap: [
    Material2AppAppComponent
  ],
  providers: [
    ${variables.component?cap_first}DataGridService,
    SecurityService,
    HttpClient,
    BusinessOperations
  ],
})
export class AppModule { }
