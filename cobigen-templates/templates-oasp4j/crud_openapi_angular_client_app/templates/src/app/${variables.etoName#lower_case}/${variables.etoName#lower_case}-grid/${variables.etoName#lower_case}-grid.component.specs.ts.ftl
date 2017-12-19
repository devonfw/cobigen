import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ${variables.etoName?cap_first}GridComponent } from './${variables.etoName?lower_case}-grid.component';

describe('${variables.etoName?cap_first}GridComponent', () => {
  let component: ${variables.etoName?cap_first}GridComponent;
  let fixture: ComponentFixture<SampleDataGridComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ${variables.etoName?cap_first}GridComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(${variables.etoName?cap_first}GridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
