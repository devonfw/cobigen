import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CoreModule } from '../../core/core.module';
import { TranslateModule } from '@ngx-translate/core';
import { ${variables.entityName?cap_first}GridComponent } from './${variables.entityName?lower_case}-grid.component';
import { ${variables.entityName?cap_first}Service } from '../services/${variables.entityName?lower_case}.service';

describe('${variables.entityName?cap_first}GridComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, CoreModule, TranslateModule.forRoot()],
      declarations: [ ${variables.entityName?cap_first}GridComponent ],
      providers: [${variables.entityName?cap_first}Service],
    }).compileComponents();
  }));
  it('should create the app', async(() => {
    const fixture: ComponentFixture<
      ${variables.entityName?cap_first}GridComponent
    > = TestBed.createComponent(${variables.entityName?cap_first}GridComponent);
    const app: any = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));
});
