
import { HomePage } from '../pages/home/home';
import { AuthServiceProvider } from '../providers/security/auth-service';
import { Component, ViewChild } from '@angular/core';
import { Nav, Platform } from 'ionic-angular';
import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';
import { LoginPage } from '../pages/login/login';
import { ${variables.etoName?cap_first}List } from '../pages/${variables.etoName?lower_case}-list/${variables.etoName?lower_case}-list'
import { TranslateService } from '@ngx-translate/core';


@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  @ViewChild(Nav) nav: Nav;
  rootPage:any = LoginPage;
  pages:any;
  
  constructor(platform: Platform, statusBar: StatusBar, splashScreen: SplashScreen, private auth: AuthServiceProvider, private translate: TranslateService ) {
    platform.ready().then(() => {
      statusBar.styleDefault();
      splashScreen.hide();
      
      this.pages = [
        { title: 'Home', component: HomePage},
        { title :'${variables.etoName?lower_case}', component: ${variables.etoName?cap_first}List},
      ];
    });
    translate.setDefaultLang('en');
  }

  isAuthenticated(){
    return this.auth.getAuthenticated();
  }

  openPage(p){    
    this.nav.setRoot(p.component);
  }
}