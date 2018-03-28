import { HttpinterceptorProvider } from '../providers/security/httpinterceptor';
import { AuthServiceProvider } from '../providers/security/auth-service';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { ErrorHandler, NgModule } from '@angular/core';
import { IonicApp, IonicErrorHandler, IonicModule } from 'ionic-angular';
import { SplashScreen } from '@ionic-native/splash-screen';
import { StatusBar } from '@ionic-native/status-bar';
import { MyApp } from './app.component';
import { LoginPage } from '../pages/Login/Login';
import { LoginProvider } from '../providers/login/loginProvider';
import { TranslateLoader,TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HeaderComponent } from '../components/header/header';
import { HomePage } from '../pages/home/home';
import { BusinessOperatorProvider } from '../providers/shared/business-operator';
import { ${variables.etoName}operationsdialogComponent } from '../pages/${variables.etoName}/Component/${variables.etoName}-operations/${variables.etoName}-operations-dialog/${variables.etoName}-operations-dialog'
import {${variables.etoName}storeProvider} from '../pages/${variables.etoName}/provider/${variables.etoName}store/${variables.etoName}store';
import {${variables.etoName}BusinessProvider} from '../pages/${variables.etoName}/provider/${variables.etoName}-business/${variables.etoName}-business';
import { ${variables.etoName}Page } from '../pages/${variables.etoName}/${variables.etoName}';
import {${variables.etoName}OperationsComponent} from '../pages/${variables.etoName}/Component/${variables.etoName}-operations/${variables.etoName}-operations';


export function translateFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    LoginPage,
    HeaderComponent,
    ${variables.etoName}Page,
    ${variables.etoName}OperationsComponent,
    ${variables.etoName}operationsdialogComponent
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
    ${variables.etoName}Page,
    ${variables.etoName}operationsdialogComponent
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
    ${variables.etoName}BusinessProvider,
    ${variables.etoName}storeProvider,
    
  ]
})
export class AppModule {}


