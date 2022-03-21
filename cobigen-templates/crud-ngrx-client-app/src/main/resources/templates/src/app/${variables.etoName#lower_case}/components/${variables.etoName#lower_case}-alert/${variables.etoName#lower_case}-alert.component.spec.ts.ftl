import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ${variables.etoName?cap_first}AlertComponent } from './${variables.etoName?lower_case}-alert.component';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

describe('${variables.etoName?cap_first}AlertComponent', () => {
  let component: ${variables.etoName?cap_first}AlertComponent;
  let fixture: ComponentFixture<${variables.etoName?cap_first}AlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
     imports: [MatDialogModule, MatButtonModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
      ],
      declarations: [ ${variables.etoName?cap_first}AlertComponent ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(${variables.etoName?cap_first}AlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
