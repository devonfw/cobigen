<h2 mat-dialog-title>{{title}}</h2>
<mat-dialog-content>{{message}}</mat-dialog-content>
<mat-dialog-actions align="end">
  <button mat-button mat-dialog-close color="accent">
  	{{cancelButton | uppercase}}
	</button>
  <button mat-button [mat-dialog-close]="true" color="accent">
  {{acceptButton | uppercase}}
  </button>
</mat-dialog-actions>
