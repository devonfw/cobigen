<td-layout-nav>
  <public-header (toggle)="onToggle($event)" [sideNavOpened]="sideNavOpened" td-toolbar-content flex></public-header>
  <td-layout-manage-list
    [opened]="(media.registerQuery('gt-sm') | async) || sideNavOpened"
    [mode]="(media.registerQuery('gt-sm') | async) ? 'side' : 'over'"
    [sidenavWidth]="(media.registerQuery('gt-xs') | async) ? '280px' : '100%'">
    <div td-sidenav-content>
      <mat-nav-list td-sidenav-content>
        <!-- Sidenav links -->
        <a id="home" mat-list-item [routerLink]="['./initialPage']" (click)="close()">
          <mat-icon matListAvatar>home</mat-icon>
          <h3 matLine>{{ 'home' | translate }}</h3>
          <p matLine>{{ 'description' | translate }}</p>
        </a>
        <a id="${variables.etoName?uncap_first}" mat-list-item [routerLink]="['./${variables.etoName?uncap_first}']" (click)="close()">
          <mat-icon matListAvatar>grid_on</mat-icon>
          <h3 matLine>{{ '${variables.component?lower_case}.${variables.etoName?cap_first}.navData' | translate }}</h3>
          <p matLine>{{ '${variables.component?lower_case}.${variables.etoName?cap_first}.navDataSub' | translate }}</p>
        </a>
      </mat-nav-list>
    </div>
    <router-outlet></router-outlet>
    <td-layout-footer-inner>
      <div layout="row" layout-align="center center" flex>
        <span>devonfw Application</span>
        <span flex></span>
        <span>devonfw</span>
      </div>
    </td-layout-footer-inner>
  </td-layout-manage-list>
</td-layout-nav>
