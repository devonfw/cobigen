import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from 'ng2-translate';
import { SecurityService } from './security/security.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class Material2AppAppComponent {

  constructor(private securityService: SecurityService,
              private router: Router,
              public translate: TranslateService) {
    this.securityService.checkCsrfToken();
    translate.setDefaultLang('en');
    translate.use('en');
  }

  navigateTo(dir) {
    this.router.navigate(['/' + dir]);
  }
}
