import * as fromMyReducers from './${variables.etoName?lower_case}.reducers';
import * as froasptempletesaction from '../actions/${variables.etoName?lower_case}.actions';
import { SearchCriteriaDataModel } from '../../models/searchcriteriadata.model';
import { Update } from '@ngrx/entity';
import { ${variables.etoName?cap_first}Model } from '../../models/${variables.etoName?lower_case}.model';

const TEST_ID = 0;
const detailsdata: SearchCriteriaDataModel = {
  criteria: {},
  data: {
      id: TEST_ID,
	 <#list pojo.fields as field>
	   <#if JavaUtil.getAngularType(field.type) == 'number'>
	     ${field.name?uncap_first}: 1,  
	   <#else>
	    ${field.name?uncap_first}: 'TEST${field.name?upper_case}',	 
	    </#if>      
	  </#list>

  },
};

describe('${variables.etoName?cap_first}ReducersTestCase', () => {
  describe('Add Action  Reducer', () => {
    it('should return the default state', () => {
      const { initialState } = fromMyReducers;
      const action: any = {} as any;
      const state: any = fromMyReducers.reducer(undefined, action);
      expect(state).toBe(initialState);
    });

    it('should add the New Details in array', () => {
      const { initialState } = fromMyReducers;
      const previousState: any = { ...initialState };
      const action: any = froasptempletesaction.createDataSuccess({
        searchCriteriaDataModel: detailsdata,
      });
      const state: fromMyReducers.${variables.etoName?cap_first}State = fromMyReducers.reducer(
        previousState,
        action,
      );

      expect(state.entities[TEST_ID]).toEqual(detailsdata.data);
    });
  });
  describe('Edit Action Reducer ', () => {
    it('should return the default state', () => {
      const { initialState } = fromMyReducers;
      const action: any = {} as any;
      const state: any = fromMyReducers.reducer(undefined, action);
      expect(state).toBe(initialState);
    });
    it('should Edit Details in array', () => {
      // Add entity
      const { initialState } = fromMyReducers;
      const action: any = froasptempletesaction.createDataSuccess({
        searchCriteriaDataModel: detailsdata,
      });
      const state: fromMyReducers.${variables.etoName?cap_first}State = fromMyReducers.reducer(
        { ...initialState },
        action,
      );

      // Defiene changes
      const update: Update<${variables.etoName?cap_first}Model> = {
        id: TEST_ID,
        changes: {
              id: TEST_ID,
         <#list pojo.fields as field>
         <#if JavaUtil.getAngularType(field.type) == 'number'>
           ${field.name?uncap_first}: 12,  
         <#else>
            ${field.name?uncap_first}: 'TEST${field.name?upper_case}2',   
          </#if>      
        </#list>
        },
      };

      const edit: any = {
        criteria: {},
        data: update,
      };

      // Update added entity
      const afterAddState: fromMyReducers.${variables.etoName?cap_first}State = { ...state };
      const actionUpdate: any = froasptempletesaction.updateDataSuccess(edit);
      const stateUpdated: fromMyReducers.${variables.etoName?cap_first}State = fromMyReducers.reducer(
        afterAddState,
        actionUpdate,
      );

      expect(stateUpdated.entities[TEST_ID].name).toEqual(update.changes.name);
      expect(stateUpdated.entities[TEST_ID].surname).toEqual(
        update.changes.surname,
      );
      expect(stateUpdated.entities[TEST_ID].email).toEqual(
        update.changes.email,
      );
    });
  });
  describe('Remove Action Reducer ', () => {
    it('should return the default state', () => {
      const { initialState } = fromMyReducers;
      const action: any = {} as any;
      const state: any = fromMyReducers.reducer(undefined, action);
      expect(state).toBe(initialState);
    });
    it('should Remove the Details from array', () => {
      // Add entity
      const { initialState } = fromMyReducers;
      const action: any = froasptempletesaction.createDataSuccess({
        searchCriteriaDataModel: detailsdata,
      });
      const state: fromMyReducers.${variables.etoName?cap_first}State = fromMyReducers.reducer(
        { ...initialState },
        action,
      );

      // Delete added entity
      const textMessage: any = 'delete Data Success';
      const afterAddState: any = { ...state };
      const actionDelete: any = froasptempletesaction.deleteDataSuccess({
        searchCriteriaDataModel: detailsdata,
      });
      const stateDeleted: any = fromMyReducers.reducer(
        afterAddState,
        actionDelete,
      );

      expect(stateDeleted.textMessage).toEqual(textMessage);
    });
  });
});
