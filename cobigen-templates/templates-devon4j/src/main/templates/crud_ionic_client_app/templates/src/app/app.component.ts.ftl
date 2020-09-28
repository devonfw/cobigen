import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Capacitor, Plugins } from '@capacitor/core';
import { Platform } from '@ionic/angular';
import { AuthService } from './services/security/auth.service';

const SplashScreen = Plugins.SplashScreen;

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
