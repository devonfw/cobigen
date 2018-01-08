import { TestBed, inject } from '@angular/core/testing';
import { ${variables.etoName?cap_first}Service } from './${variables.etoName?lower_case}.service';

describe('${variables.etoName?cap_first}Service', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [${variables.etoName?cap_first}Service]
    });
  });

  it('should be created', inject([${variables.etoName?cap_first}Service], (service: ${variables.etoName?cap_first}Service) => {
    expect(service).toBeTruthy();
  }));
});
