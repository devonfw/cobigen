import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { AvailableLangs, TranslocoService } from '@ngneat/transloco';
import { Store } from '@ngrx/store';
import { logOutAction } from '../../auth/store/actions';
import { AuthService } from '../../core/security/auth.service';
import { AppState } from '../../${variables.etoName?lower_case}/store/reducers/index';

/* @export
 * @class HeaderComponent
 */
@Component({
  selector: 'public-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  currentLanguage: string;
  langs: AvailableLangs;
  @Input() sideNavOpened = false;
  @Output() toggle: EventEmitter<any> = new EventEmitter();

  /* Creates an instance of HeaderComponent.
   * @param {Router} router
   * @param {TranslateService} translate
   * @param {AuthService} auth
   * @param {Store<AppState>} store
   * @memberof HeaderComponent
   */
  constructor(
    public router: Router,
    private translocoService: TranslocoService,
    private auth: AuthService,
    private store: Store<AppState>,
  ) {
    this.langs = translocoService.getAvailableLangs();
    translocoService.langChanges$.subscribe(
      lang => (this.currentLanguage = lang),
    );
  }

  toggleSideNav() {
    this.sideNavOpened = !this.sideNavOpened;
    this.toggle.emit(this.sideNavOpened);
  }

  /* @param {string} option
   * @memberof HeaderComponent
   */
  toggleLanguage(option: string) {
    this.translocoService.setActiveLang(option);
  }

  /* @returns {boolean}
   * @memberof HeaderComponent
   */
  isLogged(): boolean {
    return this.auth.isLogged() || false;
  }

  logout() {
    this.store.dispatch(logOutAction());
  }
}
