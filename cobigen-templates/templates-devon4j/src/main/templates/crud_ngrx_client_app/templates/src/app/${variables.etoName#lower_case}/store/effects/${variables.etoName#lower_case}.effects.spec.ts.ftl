import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Action } from '@ngrx/store';
import { cold, hot } from 'jasmine-marbles';
import { Observable, of } from 'rxjs';
import {
  generateUser,
  generateUserUpdate,
} from '../../models/datadetailstest.model';
import { ${variables.etoName?cap_first}Service } from '../../services/${variables.etoName?lower_case}.service';
import * as ${variables.etoName?uncap_first}Actions from '../actions/${variables.etoName?lower_case}.actions';
import { ${variables.etoName?cap_first}Effects } from './${variables.etoName?lower_case}.effects';

describe('${variables.etoName?cap_first}ffects', () => {
  let actions$: Observable<Action>;
  let effects: ${variables.etoName?cap_first}Effects;
  let ${variables.etoName?uncap_first}Service: any;
  let save${variables.etoName?cap_first}Spy: any;
  let get${variables.etoName?cap_first}Spy: any;
  let edit${variables.etoName?cap_first}Spy: any;
  let delete${variables.etoName?cap_first}Spy: any;

  beforeEach(() => {
    ${variables.etoName?uncap_first}Service = jasmine.createSpyObj('${variables.etoName?cap_first}Service', [
      'save${variables.etoName?cap_first}',
      'get${variables.etoName?cap_first}',
      'edit${variables.etoName?cap_first}',
      'delete${variables.etoName?cap_first}',
    ]);
    save${variables.etoName?cap_first}Spy = ${variables.etoName?uncap_first}Service.save${variables.etoName?cap_first}.and.returnValue(
      of(generateUser()),
    );
    get${variables.etoName?cap_first}Spy = ${variables.etoName?uncap_first}Service.get${variables.etoName?cap_first}.and.returnValue(
      of({ content: [generateUser()], totalElements: 1 }),
    );
    edit${variables.etoName?cap_first}Spy = ${variables.etoName?uncap_first}Service.edit${variables.etoName?cap_first}.and.returnValue(
      of(generateUser()),
    );
    delete${variables.etoName?cap_first}Spy = ${variables.etoName?uncap_first}Service.delete${variables.etoName?cap_first}.and.returnValue(
      of(generateUser()),
    );

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        ${variables.etoName?cap_first}Effects,
        {
          provide: ${variables.etoName?cap_first}Service,
          useValue: ${variables.etoName?uncap_first}Service,
        },
        provideMockActions(() => actions$),
      ],
    });

    effects = TestBed.inject<${variables.etoName?cap_first}Effects>(${variables.etoName?cap_first}Effects);
  });

  describe('${variables.etoName?cap_first} Effects', () => {
    describe('loadData', () => {
      it('Success', () => {
        const action = ${variables.etoName?uncap_first}Actions.loadData({
          ${variables.etoName?uncap_first}Model: generateUser(),
        });
        const completion = ${variables.etoName?uncap_first}Actions.loadDataSuccess({
          httpResponseModel: { content: [generateUser()], totalElements: 1 },
        });

        actions$ = hot('-a-', {
          a: action,
        });
        const response = cold('-b', {
          b: {
            content: [generateUser()],
            totalElements: 1,
          },
        });
        const expected = cold('--c', { c: completion });

        get${variables.etoName?cap_first}Spy.and.returnValue(response);
        expect(effects.loadData$).toBeObservable(expected);
      });
    });

    describe('createData', () => {
      it('Success', () => {
        const action = ${variables.etoName?uncap_first}Actions.createData({
          searchCriteriaDataModel: { criteria: {}, data: generateUser() },
        });
        const completion = ${variables.etoName?uncap_first}Actions.createDataSuccess({
          searchCriteriaDataModel: { criteria: {}, data: generateUser() },
        });

        actions$ = hot('-a-', {
          a: action,
        });
        const response = cold('-b', { b: generateUser() });
        const expected = cold('--c', { c: completion });

        save${variables.etoName?cap_first}Spy.and.returnValue(response);
        expect(effects.createData$).toBeObservable(expected);
      });
    });

    describe('updateData', () => {
      it('Success', () => {
        const action = ${variables.etoName?uncap_first}Actions.updateData({
          searchCriteriaDataModel: { criteria: {}, data: generateUser() },
        });
        const completion = ${variables.etoName?uncap_first}Actions.updateDataSuccess({
          criteria: {},
          data: generateUserUpdate(),
        });

        actions$ = hot('-a-', {
          a: action,
        });
        const response = cold('-b', { b: generateUser() });
        const expected = cold('--c', { c: completion });

        edit${variables.etoName?cap_first}Spy.and.returnValue(response);
        expect(effects.updateData$).toBeObservable(expected);
      });
    });

    describe('deleteData', () => {
      it('Success', () => {
        const action = ${variables.etoName?uncap_first}Actions.deleteData({
          searchCriteriaDataModel: { criteria: {}, data: generateUser() },
        });
        const completion = ${variables.etoName?uncap_first}Actions.deleteDataSuccess({
          searchCriteriaDataModel: { criteria: {}, data: generateUser() },
        });

        actions$ = hot('-a-', {
          a: action,
        });
        const response = cold('-b', { b: generateUser() });
        const expected = cold('--c', { c: completion });

        delete${variables.etoName?cap_first}Spy.and.returnValue(response);
        expect(effects.deleteData$).toBeObservable(expected);
      });
    });
  });
});
