import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'public-${variables.entityName?lower_case}-alert',
  templateUrl: './${variables.entityName?lower_case}-alert.component.html',
  styleUrls: ['./${variables.entityName?lower_case}-alert.component.scss'],
})
export class ${variables.entityName?cap_first}AlertComponent implements OnInit {
  confirmDialog: boolean = false;
  message: string = '';
  title: string = '';
  cancelButton: string = 'Cancel';
  acceptButton: string = 'Delete';
  cancelButtonColor: string = '';
  constructor(
    public dialogRef: MatDialogRef<${variables.entityName?cap_first}AlertComponent>,
    @Inject(MAT_DIALOG_DATA) private dialogData: any,
  ) { }

  ngOnInit(): void {
    if (this.dialogData) {
      this.confirmDialog = this.dialogData.confirmDialog;
      this.message = this.dialogData.message;
      this.title = this.dialogData.title;
      this.cancelButton = this.dialogData.cancelButton;
      if (this.confirmDialog) {
        this.acceptButton = this.dialogData.acceptButton;
        this.cancelButtonColor = '';
      } else {
        this.cancelButtonColor = 'accent';
      }
    }
  }

}
