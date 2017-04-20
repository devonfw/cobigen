<header></header>

<md-sidenav-container style="height:91vh">

    <md-sidenav *ngIf="this.router.location.path() !== '/login'" #sidenav style= "width:25%" mode="side" opened="true" class="app-sidenav">
      <span class="app-toolbar-filler"></span>

      <md-nav-list list-items>
          <a id="home" md-list-item (click)="navigateTo('home')">
            <md-icon md-list-avatar>home</md-icon>
            <h3 md-line> {{'${variables.etoName}DataGrid.navHome' | translate}} </h3>
            <p md-line> {{'${variables.etoName}DataGrid.navHomeSub' | translate}} </p>
          </a>
        
          <a id="${variables.etoName}DataGrid" md-list-item (click)="navigateTo('${variables.etoName}DataGrid')">
            <md-icon md-list-avatar>grid_on</md-icon>
            <h3 md-line> {{'${variables.etoName}DataGrid.navData' | translate}} </h3>
            <p md-line> {{'${variables.etoName}DataGrid.navDataSub' | translate}} </p>
          </a>
      </md-nav-list>
    </md-sidenav>

    <router-outlet></router-outlet>

</md-sidenav-container>
