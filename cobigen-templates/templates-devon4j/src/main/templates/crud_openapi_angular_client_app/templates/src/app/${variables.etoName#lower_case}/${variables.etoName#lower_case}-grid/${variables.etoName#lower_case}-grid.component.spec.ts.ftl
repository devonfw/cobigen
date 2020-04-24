import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CoreModule } from '../../core/core.module';
import { TranslateModule } from '@ngx-translate/core';

import { ${variables.etoName?cap_first}Service } from '../services/${variables.etoName?lower_case}.service';

import { ${variables.etoName?cap_first}GridComponent } from './${variables.etoName?lower_case}-grid.component';
describe('${variables.etoName?cap_first}GridComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, CoreModule, TranslateModule.forRoot()],
      declarations: [ ${variables.etoName?cap_first}GridComponent ],
      providers: [${variables.etoName?cap_first}Service],
    }).compileComponents();
  }));
  it('should create the app', async(() => {
    const fixture: ComponentFixture<
      ${variables.etoName?cap_first}GridComponent
    > = TestBed.createComponent(${variables.etoName?cap_first}GridComponent);
    const app: any = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));
});
