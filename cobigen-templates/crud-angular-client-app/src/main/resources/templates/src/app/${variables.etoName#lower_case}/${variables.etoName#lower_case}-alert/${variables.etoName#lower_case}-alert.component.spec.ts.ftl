import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { ${variables.etoName?cap_first}AlertComponent } from './${variables.etoName?lower_case}-alert.component';

describe('${variables.etoName?cap_first}AlertComponent', () => {
  let component: ${variables.etoName?cap_first}AlertComponent;
  let fixture: ComponentFixture<${variables.etoName?cap_first}AlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ${variables.etoName?cap_first}AlertComponent ],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
      ]
    })
    .compileComponents();
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
