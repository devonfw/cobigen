<header></header>

<md-sidenav-layout style="height:91vh">

    <md-sidenav *ngIf="this.router.location.path() !== '/login'" #sidenav style= "width:25%" mode="side" opened="true" class="app-sidenav">
      <span class="app-toolbar-filler"></span>

      <md-nav-list list-items>
          <a id="home" md-list-item (click)="navigateTo('home')">
            <md-icon md-list-avatar>home</md-icon>
            <h3 md-line> {{'${variables.component}datagrid.navHome' | translate}} </h3>
            <p md-line> {{'${variables.component}datagrid.navHomeSub' | translate}} </p>
          </a>
        
          <a id="${variables.component}dataGrid" md-list-item (click)="navigateTo('${variables.component}dataGrid')">
            <md-icon md-list-avatar>grid_on</md-icon>
            <h3 md-line> {{'${variables.component}datagrid.navData' | translate}} </h3>
            <p md-line> {{'${variables.component}datagrid.navDataSub' | translate}} </p>
          </a>
      </md-nav-list>
    </md-sidenav>

    <router-outlet></router-outlet>

</md-sidenav-layout>
