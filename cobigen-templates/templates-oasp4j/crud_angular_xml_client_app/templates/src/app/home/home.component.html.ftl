<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>
<td-layout-nav>
  <div td-toolbar-content flex>
    <app-header></app-header>
  </div>
  <td-layout-manage-list opened="true" mode="side" sidenavWidth="350px">

    <md-nav-list td-sidenav-content>
      <a id="home" md-list-item [routerLink]="['./initialPage']">
        <md-icon md-list-avatar>home</md-icon>
        <h3 md-line> {{'${variables.component}datagrid.navHome' | translate}} </h3>
        <p md-line> {{'${variables.component}datagrid.navHomeSub' | translate}} </p>
      </a>

      <a id="${variables.etoName}" md-list-item [routerLink]="['./${variables.component}dataGrid']">
        <md-icon md-list-avatar>grid_on</md-icon>
        <h3 md-line> {{'${variables.component}datagrid.navData' | translate}} </h3>
        <p md-line> {{'${variables.component}datagrid.navDataSub' | translate}} </p>
      </a>
    </md-nav-list>

    <router-outlet></router-outlet>

  </td-layout-manage-list>
</td-layout-nav>
