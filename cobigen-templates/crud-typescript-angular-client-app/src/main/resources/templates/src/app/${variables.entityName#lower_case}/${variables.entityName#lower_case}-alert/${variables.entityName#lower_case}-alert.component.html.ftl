<h2 mat-dialog-title>{{title}}</h2>
<mat-dialog-content>{{message}}</mat-dialog-content>
<mat-dialog-actions align="end">
  <button mat-button mat-dialog-close [color]="confirmDialog ? null : 'accent'">{{cancelButton | uppercase}}</button>
  <button mat-button [mat-dialog-close]="true" color="accent" *ngIf="confirmDialog">{{acceptButton | uppercase}}</button>
</mat-dialog-actions>
