<div class="header-container">
  <mat-icon
    class="mat-icon-logo cursor-pointer devon-logo"
    svgIcon="logo"
  ></mat-icon>
  <button
    mat-icon-button
    class="menu-button"
    *ngIf="isLogged()"
    (click)="toggleSideNav()"
  >
    <mat-icon>menu</mat-icon>
  </button>
  <span class="cursor-pointer">{{ 'header.title' | translate }}</span>

  <span flex></span>

  <span class="menu-items-right">
    <a mat-icon-button [mat-menu-trigger-for]="menu">
      <mat-icon
        *ngIf="isCurrentLang('en')"
        class="flag-icon flag-icon-es"
      ></mat-icon>
      <mat-icon
        *ngIf="isCurrentLang('es')"
        class="flag-icon flag-icon-gb"
      ></mat-icon>
    </a>
    <mat-menu #menu="matMenu">
      <button
        mat-menu-item
        *ngIf="isCurrentLang('en')"
        (click)="toggleLanguage('en')"
      >
        <mat-icon class="flag-icon flag-icon-gb"></mat-icon>
        <span> English </span>
      </button>
      <button
        mat-menu-item
        *ngIf="isCurrentLang('es')"
        (click)="toggleLanguage('es')"
      >
        <mat-icon class="flag-icon flag-icon-es"></mat-icon>
        <span> Castellano </span>
      </button>
    </mat-menu>

    <button mat-icon-button *ngIf="isLogged()" (click)="logout()">
      <mat-icon>input</mat-icon>
    </button>
  </span>
</div>
