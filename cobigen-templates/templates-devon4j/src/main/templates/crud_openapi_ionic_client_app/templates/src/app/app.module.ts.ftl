import { NgModule } from '@angular/core';
import {
  HTTP_INTERCEPTORS,
  HttpClient,
  HttpClientModule,
} from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';
import { IonicModule, IonicRouteStrategy } from '@ionic/angular';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
import { ComponentsModule } from './components/components.module';
import { AuthGuardService } from './services/authorization/auth-guard.service';
import { HttpinterceptorService } from './services/security/http-interceptor.service';
import { ${variables.etoName?cap_first}Detail } from './pages/${variables.etoName?lower_case}-detail/${variables.etoName?lower_case}-detail.page';
import { ${variables.etoName?cap_first}RestService } from './services/${variables.etoName?lower_case}-rest.service';
import { ${variables.etoName?cap_first}List } from './pages/${variables.etoName?lower_case}-list/${variables.etoName?lower_case}-list.page';
import { TranslocoRootModule } from './transloco-root.module';

@NgModule({
  declarations: [
    AppComponent,
    ${variables.etoName?cap_first}List,
    ${variables.etoName?cap_first}Detail,
  ],
  entryComponents: [
    ${variables.etoName?cap_first}Detail
  ],
  imports: [
    BrowserModule,
    IonicModule.forRoot(),
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ComponentsModule,
    TranslocoRootModule,
  ],

  providers: [
    AuthGuardService,
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpinterceptorService,
      multi: true,
    },
    ${variables.etoName?cap_first}RestService,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
