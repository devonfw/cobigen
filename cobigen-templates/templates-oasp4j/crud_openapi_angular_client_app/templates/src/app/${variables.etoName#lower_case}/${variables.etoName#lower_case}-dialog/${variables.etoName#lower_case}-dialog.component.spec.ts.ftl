import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ${variables.etoName?cap_first}DialogComponent } from './${variables.etoName?lower_case}-dialog.component';

describe('${variables.etoName?cap_first}DialogComponent', () => {
  let component: ${variables.etoName?cap_first}DialogComponent;
  let fixture: ComponentFixture<${variables.etoName?cap_first}DialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ${variables.etoName?cap_first}DialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(${variables.etoName?cap_first}DialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
