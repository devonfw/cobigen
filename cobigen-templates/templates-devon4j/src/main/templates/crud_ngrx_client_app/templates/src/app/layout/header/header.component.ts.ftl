import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { logOutAction } from '../../auth/store/actions';
import { AuthService } from '../../core/security/auth.service';
import { AppState } from '../../home/${variables.etoName?lower_case}/store/reducers/index';

/* @export
 * @class HeaderComponent
 */
@Component({
  selector: 'public-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
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
    private translate: TranslateService,
    private auth: AuthService,
    private store: Store<AppState>,
  ) {}

  toggleSideNav(): void {
    this.sideNavOpened = !this.sideNavOpened;
    this.toggle.emit(this.sideNavOpened);
  }

  /* @param {string} option
   * @memberof HeaderComponent
   */
  toggleLanguage(option: string): void {
    this.translate.use(option);
  }

  /* @param {string} lang
   * @returns {boolean}
   * @memberof HeaderComponent
   */
  isCurrentLang(lang: string): boolean {
    return this.translate.currentLang !== lang;
  }

  /* @returns {boolean}
   * @memberof HeaderComponent
   */
  isLogged(): boolean {
    return this.auth.isLogged() || false;
  }
  logout(): void {
    this.store.dispatch(logOutAction());
  }
}
