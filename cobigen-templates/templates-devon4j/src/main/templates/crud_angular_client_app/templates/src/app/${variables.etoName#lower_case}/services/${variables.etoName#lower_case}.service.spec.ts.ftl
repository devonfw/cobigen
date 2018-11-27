import { TestBed, inject } from '@angular/core/testing';
import { ${variables.etoName?cap_first}Service } from './${variables.etoName?lower_case}.service';

import { CoreModule } from '../../core/core.module';

describe('SidenavSharedService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ${variables.etoName?cap_first}Service,
      ],
      imports: [
        CoreModule,
      ],
    });
  });

  it('should create', inject([${variables.etoName?cap_first}Service], (service: ${variables.etoName?cap_first}Service) => {
    expect(service).toBeTruthy();
  }));
});
