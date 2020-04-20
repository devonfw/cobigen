import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './auth/login.component';
import { CoreModule } from './core/core.module';
import { LayoutModule } from './layout/layout.module';
import { AuthDataModule } from './auth/auth.module';
import { ${variables.etoName?cap_first}Module } from './${variables.etoName?lower_case}/${variables.etoName?lower_case}.module';

@NgModule({
  declarations: [AppComponent, LoginComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CoreModule,
    AuthDataModule,
    LayoutModule,
    ${variables.etoName?cap_first}Module,
    HttpClientModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
