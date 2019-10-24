import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

/* @export
 * @class NavBarComponent
 */
@Component({
  selector: 'public-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss'],
})
export class NavBarComponent {
  sideNavOpened = false;
  isMobile: any;
  /* Creates an instance of NavBarComponent.
   * @param {Router} router
   * @memberof NavBarComponent
   */
  constructor(private router: Router, private breakpoint: BreakpointObserver) {
    this.breakpoint.observe(Breakpoints.Handset).subscribe((data: any) => {
      this.isMobile = data.matches;
    });
  }

  /* @param {string} route
   * @memberof NavBarComponent
   */
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  /* @param {boolean} value
   * @memberof NavBarComponent
   */
  onToggle(value: boolean): void {
    this.sideNavOpened = value;
  }

  close(): void {
    this.sideNavOpened = false;
  }
}
