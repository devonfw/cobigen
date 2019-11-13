import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { CoreModule } from '../core/core.module';
import { HeaderComponent } from './header/header.component';
import { NavBarComponent } from './nav-bar/nav-bar.component';

/* @export
 * @class LayoutModule
 */
@NgModule({
  imports: [CommonModule, CoreModule, TranslateModule, RouterModule],
  providers: [],
  declarations: [NavBarComponent, HeaderComponent],
  exports: [NavBarComponent, HeaderComponent],
})
export class LayoutModule {}
