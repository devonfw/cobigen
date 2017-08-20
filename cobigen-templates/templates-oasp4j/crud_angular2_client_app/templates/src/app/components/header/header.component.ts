import { SecurityService } from '../../security/security.service';
import { DomSanitizer } from '@angular/platform-browser';
import { MdIconRegistry } from '@angular/material';
import { TranslateService } from 'ng2-translate/src/translate.service';
import { Router } from '@angular/router';
import { Component } from '@angular/core';

@Component({
    selector: 'header',
    templateUrl: './header.component.html'
})

export class HeaderComponent {

    constructor(private security: SecurityService,
                private iconReg: MdIconRegistry,
                private sanitizer: DomSanitizer,
                private translate: TranslateService,
                private router: Router) {
         iconReg.addSvgIcon('logo', sanitizer.bypassSecurityTrustResourceUrl('assets/img/Logo.svg'))
    }

    toggleLanguage(option) {
        this.translate.use(option);
    }

    logout() {
        this.security.logout();
    }

    isCurrentLang(lang) {
        return this.translate.currentLang !== lang;
    }
}
