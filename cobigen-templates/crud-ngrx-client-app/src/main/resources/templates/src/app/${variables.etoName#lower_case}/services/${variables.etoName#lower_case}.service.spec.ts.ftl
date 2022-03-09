import { TestBed, inject } from '@angular/core/testing';
import { ${variables.etoName?cap_first}Service } from './${variables.etoName?lower_case}.service';

import { CoreModule } from '../../core/core.module';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

describe('SidenavSharedService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [],
      imports: [
        RouterTestingModule,
        CoreModule,
        BrowserModule,
        HttpClientModule,
      ],
    });
  });

  it('should create', inject(
    [${variables.etoName?cap_first}Service],
    (service: ${variables.etoName?cap_first}Service) => {
      expect(service).toBeTruthy();
    },
  ));
});
