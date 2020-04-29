<div class="home-container-outer">
  <div class="home-container-inner">
    <mat-toolbar class="public-header-container" color="primary">
      <public-header
        (toggle)="onToggle($event)"
        [sideNavOpened]="sideNavOpened"
      ></public-header>
    </mat-toolbar>
    <div class="sidenav-container-outer">
      <div class="sidenav-container-inner">
        <mat-sidenav-container>
          <mat-sidenav
            [disableClose]="false"
            [mode]="isMobile ? 'over' : 'side'"
            [opened]="!isMobile || sideNavOpened"
            #sidenav
          >
            <mat-nav-list>
              <!-- Sidenav links -->
              <a
                id="home"
                mat-list-item
                [routerLink]="['./initial']"
                (click)="close()"
              >
                <mat-icon matListAvatar>home</mat-icon>
                <h3 matLine>{{ 'home' | transloco }}</h3>
                <p matLine class="desc">{{ 'description' | transloco }}</p>
              </a>
              <a
                id="${variables.etoName?uncap_first}"
                mat-list-item
                [routerLink]="['./${variables.etoName?uncap_first}']"
                (click)="close()"
              >
                <mat-icon matListAvatar>grid_on</mat-icon>
                <h3 matLine>
                  {{ '${variables.component?lower_case}.${variables.etoName?cap_first}.navData' | transloco }}
                </h3>
                <p matLine class="desc">
                  {{ '${variables.component?lower_case}.${variables.etoName?cap_first}.navDataSub' | transloco }}
                </p>
              </a>
            </mat-nav-list>
          </mat-sidenav>
          <mat-sidenav-content>
            <div class="content-container-outer">
              <div class="content-container-inner">
                <router-outlet></router-outlet>
              </div>
              <mat-toolbar class="public-footer">
                <span>devonfw Application</span>
                <span>devonfw</span>
              </mat-toolbar>
            </div>
          </mat-sidenav-content>
        </mat-sidenav-container>
      </div>
    </div>
  </div>
</div>
