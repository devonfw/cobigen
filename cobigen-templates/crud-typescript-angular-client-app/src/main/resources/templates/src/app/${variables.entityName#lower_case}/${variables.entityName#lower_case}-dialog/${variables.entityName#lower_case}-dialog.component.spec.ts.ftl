import { HttpClient, HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { async, TestBed } from '@angular/core/testing';
import { CoreModule } from '../../core/core.module';
import { ${variables.entityName?cap_first}Module } from '../${variables.entityName?lower_case}.module';

import { ${variables.entityName?cap_first}Service } from '../services/${variables.entityName?lower_case}.service';
import { ${variables.entityName?cap_first}DialogComponent } from './${variables.entityName?lower_case}-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';

describe('${variables.entityName?cap_first}DialogComponent', () => {
  let component: ${variables.entityName?cap_first}DialogComponent;
  let dialog: MatDialog;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [${variables.entityName?cap_first}Service, HttpClient],
      imports: [
        BrowserAnimationsModule,
        TranslateModule.forRoot(),
        ${variables.entityName?cap_first}Module,
        HttpClientModule,
        CoreModule,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    dialog = TestBed.get(MatDialog);
    component = dialog.open(${variables.entityName?cap_first}DialogComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
