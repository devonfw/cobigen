import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ${variables.entityName?cap_first}AlertComponent } from './${variables.entityName?lower_case}-alert.component';

describe('${variables.entityName?cap_first}AlertComponent', () => {
  let component: ${variables.entityName?cap_first}AlertComponent;
  let fixture: ComponentFixture<${variables.entityName?cap_first}AlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ${variables.entityName?cap_first}AlertComponent ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(${variables.entityName?cap_first}AlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
