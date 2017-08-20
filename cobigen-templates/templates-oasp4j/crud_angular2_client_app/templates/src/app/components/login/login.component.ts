import { TranslateService } from 'ng2-translate/src/translate.service';
import { TdDialogService } from '@covalent/core/dialogs/services/dialog.service';
import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { SecurityService } from '../../security/security.service';

@Component({
    templateUrl: './login.component.html'
})

export class LoginComponent {

    constructor (private translate: TranslateService, private _dialogService: TdDialogService, private router: Router, private securityService: SecurityService) {
    }

    getTranslation(text: string): string {
        let value: string;
        this.translate.get(text).subscribe( (res) => {
            value = res;
        });
        return value;
    }

    login(login) {
        this.securityService.login(login.value.username, login.value.password)
                            .subscribe(() => {
                                this.router.navigate(['/home']);
                            }, (error) => {
                                login.reset();
                                this._dialogService.openAlert({
                                    message: this.getTranslation('login.errorMsg'),
                                    title: this.getTranslation('header.error')
                                })
                            });
    }
}
