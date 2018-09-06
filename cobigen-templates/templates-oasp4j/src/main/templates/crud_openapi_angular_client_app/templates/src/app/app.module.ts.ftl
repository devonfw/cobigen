import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { CoreModule } from './core/core.module';
import { LayoutModule } from './layout/layout.module';
import { HomeModule } from './home/home.module';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { ${variables.etoName?cap_first}Module } from './${variables.etoName?lower_case}/${variables.etoName?lower_case}.module';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    LayoutModule,
    AppRoutingModule,
    CoreModule,
    HomeModule,
    ${variables.etoName?cap_first}Module,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
