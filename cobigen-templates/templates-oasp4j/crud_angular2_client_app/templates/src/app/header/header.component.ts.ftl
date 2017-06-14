import { Component } from '@angular/core';
import { MdIconRegistry } from '@angular/material';
import { DomSanitizer } from '@angular/platform-browser';
import { AuthService } from '../shared/security/auth.service';
import { HeaderService } from './shared/header.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})

export class HeaderComponent {

    constructor (
        private translate: TranslateService,
        private iconReg: MdIconRegistry,
        private auth: AuthService,
        private sanitizer: DomSanitizer,
        private headerService: HeaderService) {
            iconReg.addSvgIcon('logo', sanitizer.bypassSecurityTrustResourceUrl('assets/img/Logo.svg'))
    }

    toggleLanguage(option) {
        this.translate.use(option);
    }

    isCurrentLang(lang) {
        return this.translate.currentLang !== lang;
    }

    logout() {
        this.headerService.logout();
    }

}
