import { HttpClient, HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { async, TestBed } from '@angular/core/testing';
import { CoreModule } from '../../core/core.module';
import { ${variables.etoName?cap_first}Module } from '../${variables.etoName?lower_case}.module';

import { ${variables.etoName?cap_first}Service } from '../services/${variables.etoName?lower_case}.service';
import { ${variables.etoName?cap_first}DialogComponent } from './${variables.etoName?lower_case}-dialog.component';
import { MatDialog } from '@angular/material/dialog';

describe('${variables.etoName?cap_first}DialogComponent', () => {
  let component: ${variables.etoName?cap_first}DialogComponent;
  let dialog: MatDialog;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [${variables.etoName?cap_first}Service, HttpClient],
      imports: [
        BrowserAnimationsModule,
	${variables.etoName?cap_first}Module,
        HttpClientModule,
        CoreModule,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    dialog = TestBed.get(MatDialog);
    component = dialog.open(${variables.etoName?cap_first}DialogComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
