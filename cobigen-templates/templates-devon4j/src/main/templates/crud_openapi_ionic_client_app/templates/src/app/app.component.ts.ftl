import { Component } from '@angular/core';
import { Platform } from '@ionic/angular';
import { AuthService } from './services/security/auth.service';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';

import { Plugins, Capacitor } from '@capacitor/core';
const SplashScreen = Plugins.SplashScreen;
const StatusBar = Plugins.StatusBar;

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
})
export class AppComponent {
  rootPage: any;
  pages: any;

  constructor(
    private platform: Platform,
    private auth: AuthService,
    private translate: TranslateService,
    private router: Router,
  ) {
    this.initializeApp();

    platform.ready().then(() => {
      if (Capacitor.isPluginAvailable('SplashScreen')) {
        SplashScreen.hide().catch(() => {
          console.warn('Spashscreen not available');
        });
      }

      this.pages = [
        {
          title: 'Home',
          route: 'home',
        },
        {
          title: '${variables.etoName?cap_first}',
          route: '${variables.etoName?lower_case}',
        },
      ];
    });
    this.translate.setDefaultLang('en');
    this.translate.currentLang = 'en';
  }

  initializeApp() {
    this.platform.ready().then(() => {
      if (Capacitor.isPluginAvailable('SplashScreen')) {
        SplashScreen.hide().catch(() => {
          console.warn('Spashscreen not available');
        });
      }
    });
  }

  isAuthenticated() {
    return this.auth.getAuthenticated();
  }

  openPage(p: any) {
    this.router.navigate([p.route]);
  }
}