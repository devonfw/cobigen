<md-toolbar color="primary">

    <md-icon class="md-icon-logo" svgIcon="logo"></md-icon>

    <span>{{'header.title' | translate}}</span>

    <span class="app-toolbar-filler"></span>

    <button md-icon-button [md-menu-trigger-for]="menu">
      <md-icon>language</md-icon>
    </button>

    <md-menu #menu="mdMenu">
      <span class="flag-icon flag-icon-gb" *ngIf = "isCurrentLang('en')" (click)="toggleLanguage('en')" style="margin-bottom:10px"></span>
      <span class="flag-icon flag-icon-es" *ngIf = "isCurrentLang('es')" (click)="toggleLanguage('es')"></span>
    </md-menu>

    <button md-icon-button *ngIf="this.router.location.path() !== '/login'" (click) = "logout()">
      <md-icon>exit_to_app</md-icon>
    </button>
    
</md-toolbar>