import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'public-${variables.etoName?lower_case}-alert',
  templateUrl: './${variables.etoName?lower_case}-alert.component.html',
  styleUrls: ['./${variables.etoName?lower_case}-alert.component.scss'],
})
export class ${variables.etoName?cap_first}AlertComponent implements OnInit {
  message = '';
  title = '';
  cancelButton = 'Cancel';
  acceptButton = 'Delete';
  constructor(
    public dialogRef: MatDialogRef<${variables.etoName?cap_first}AlertComponent>,
    @Inject(MAT_DIALOG_DATA) private dialogData: any,
  ) { }

  ngOnInit(): void {
    if (this.dialogData) {
      this.message = this.dialogData.message;
      this.title = this.dialogData.title;
      this.cancelButton = this.dialogData.cancelButton;
      this.acceptButton = this.dialogData.acceptButton;
    }
  }

}
