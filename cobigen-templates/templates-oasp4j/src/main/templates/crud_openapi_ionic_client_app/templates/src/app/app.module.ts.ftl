import { HttpinterceptorProvider } from '../providers/security/httpinterceptor';
import { AuthServiceProvider } from '../providers/security/auth-service';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { ErrorHandler, NgModule } from '@angular/core';
import { IonicApp, IonicErrorHandler, IonicModule } from 'ionic-angular';
import { SplashScreen } from '@ionic-native/splash-screen';
import { StatusBar } from '@ionic-native/status-bar';
import { MyApp } from './app.component';
import { LoginPage } from '../pages/login/login';
import { LoginProvider } from '../providers/login/loginProvider';
import { TranslateModule,TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HeaderComponent } from '../components/header/header';
import { HomePage } from '../pages/home/home';
import { BusinessOperatorProvider } from '../providers/shared/business-operator';
import { ${variables.etoName?cap_first}Detail } from '../pages/${variables.etoName?uncap_first}-detail/${variables.etoName?uncap_first}-detail';
import { ${variables.etoName?cap_first}Rest } from '../providers/${variables.etoName?uncap_first}-rest';
import { ${variables.etoName?cap_first}List } from '../pages/${variables.etoName?uncap_first}-list/${variables.etoName?uncap_first}-list';

export function translateFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    LoginPage,
    HeaderComponent,
    ${variables.etoName?cap_first}List,
    ${variables.etoName?cap_first}Detail,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    IonicModule.forRoot(MyApp),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: translateFactory,
        deps: [HttpClient]
      }
    })
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    HomePage,
    LoginPage,
    ${variables.etoName?cap_first}List,
    ${variables.etoName?cap_first}Detail
  ],
  providers: [

    TranslateModule,
    StatusBar,
    SplashScreen,
    {provide: ErrorHandler, useClass: IonicErrorHandler},
    BusinessOperatorProvider,
    HttpClient,
    LoginProvider,
    AuthServiceProvider,
    {provide: HTTP_INTERCEPTORS,
      useClass: HttpinterceptorProvider,
      multi: true},
    ${variables.etoName?cap_first}Rest,
    
  ]
})
export class AppModule {}


