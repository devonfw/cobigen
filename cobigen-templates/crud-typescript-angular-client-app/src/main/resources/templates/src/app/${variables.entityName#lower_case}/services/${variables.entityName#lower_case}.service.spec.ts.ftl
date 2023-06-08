import { TestBed, inject } from '@angular/core/testing';
import { ${variables.entityName?cap_first}Service } from './${variables.entityName?lower_case}.service';

import { CoreModule } from '../../core/core.module';

describe('SidenavSharedService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ${variables.entityName?cap_first}Service,
      ],
      imports: [
        CoreModule,
      ],
    });
  });

  it('should create', inject([${variables.entityName?cap_first}Service], (service: ${variables.entityName?cap_first}Service) => {
    expect(service).toBeTruthy();
  }));
});
