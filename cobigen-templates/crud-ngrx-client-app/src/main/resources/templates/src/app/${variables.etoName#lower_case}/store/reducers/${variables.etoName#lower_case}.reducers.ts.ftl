import {
  createEntityAdapter,
  Dictionary,
  EntityAdapter,
  EntityState,
  Update,
} from '@ngrx/entity';
import { createReducer, on } from '@ngrx/store';
import { Action, ActionReducer } from '@ngrx/store/src/models';
import { HttpResponseModel } from '../../models/httpresponse.model';
import { ${variables.etoName?cap_first}Model } from '../../models/${variables.etoName?lower_case}.model';
import * as ${variables.etoName?uncap_first}Actions from '../actions/${variables.etoName?lower_case}.actions';


/* @export
 * @interface ${variables.etoName?cap_first}State
 */
export interface ${variables.etoName?cap_first}State extends EntityState<${variables.etoName?cap_first}Model> {
  loaded: boolean;
  loading: boolean;
  totalElements: number;
  textMessage: string;
}

export const adapter: EntityAdapter<${variables.etoName?cap_first}Model> = createEntityAdapter<
  ${variables.etoName?cap_first}Model
>();

export const initialState: ${variables.etoName?cap_first}State = adapter.getInitialState({
  loaded: false,
  loading: false,
  totalElements: 0,
  textMessage: undefined,
});


const ${variables.etoName?uncap_first}Reducer: ActionReducer<
  {
    textMessage: string;
    loaded: boolean;
    loading: boolean;
    totalElements: number;
    ids: string[] | number[];
    entities: Dictionary<${variables.etoName?cap_first}Model>;
  },
  Action
> = createReducer(
  initialState,
  on(${variables.etoName?uncap_first}Actions.loadData, (state: ${variables.etoName?cap_first}State) => ({
    ...state,
    loading: true,
  })),
  on(${variables.etoName?uncap_first}Actions.loadDataSuccess, (state: ${variables.etoName?cap_first}State, action) => {
    const response: HttpResponseModel = action.httpResponseModel;
    const data: ${variables.etoName?cap_first}Model[] = response.content;

    state = {
      ...state,
      loading: false,
      loaded: true,
      totalElements: response.totalElements,
    };
    return adapter.setAll(data, state);
  }),
  on(${variables.etoName?uncap_first}Actions.loadDataFail, (state: ${variables.etoName?cap_first}State) => ({
    ...state,
    loading: false,
    loaded: false,
  })),
  on(${variables.etoName?uncap_first}Actions.createData, (state: ${variables.etoName?cap_first}State) => ({ ...state })),
  on(${variables.etoName?uncap_first}Actions.createDataSuccess, (state: ${variables.etoName?cap_first}State, action) => {
    const data: ${variables.etoName?cap_first}Model = action.searchCriteriaDataModel.data;
    state = {
      ...state,
      loading: false,
      loaded: false,
    };
    return adapter.addOne(data, state);
  }),
  on(${variables.etoName?uncap_first}Actions.createDataFail, (state: ${variables.etoName?cap_first}State) => ({
    ...state,
    textMessage: 'Add Data Fail',
  })),
  on(${variables.etoName?uncap_first}Actions.updateData, (state: ${variables.etoName?cap_first}State) => ({ ...state })),
  on(${variables.etoName?uncap_first}Actions.updateDataSuccess, (state: ${variables.etoName?cap_first}State, action) => {
    const data: Update<${variables.etoName?cap_first}Model> = action.data;
    state = {
      ...state,
      textMessage: 'Edit Data Success',
    };
    return adapter.updateOne(data, state);
  }),
  on(${variables.etoName?uncap_first}Actions.updateDataFail, (state: ${variables.etoName?cap_first}State) => ({
    ...state,
    textMessage: 'Edit Data Fail',
  })),
  on(${variables.etoName?uncap_first}Actions.deleteData, (state: ${variables.etoName?cap_first}State) => ({ ...state })),
  on(${variables.etoName?uncap_first}Actions.deleteDataSuccess, (state: ${variables.etoName?cap_first}State, action) => {
    const dataId: number = action.searchCriteriaDataModel.data.id;
    state = {
      ...state,
      textMessage: 'delete Data Success',
      loading: false,
      loaded: true,
    };

    return adapter.removeOne(dataId, state);
  }),
  on(${variables.etoName?uncap_first}Actions.deleteDataFail, (state: ${variables.etoName?cap_first}State) => ({
    ...state,
    textMessage: 'delete Data Fail',
  })),
);

/* @export
 * @param {${variables.etoName?cap_first}State} [state=initialState]
 * @param {${variables.etoName?cap_first}Action} action
 * @returns {${variables.etoName?cap_first}State}
 */
export function reducer(
  state: ${variables.etoName?cap_first}State = initialState,
  action: Action,
): ${variables.etoName?cap_first}State {
  return ${variables.etoName?uncap_first}Reducer(state, action);
}

export const get${variables.etoName?cap_first}Total: any = (state: ${variables.etoName?cap_first}State) =>
  state.totalElements;
export const get${variables.etoName?cap_first}Loading: any = (state: ${variables.etoName?cap_first}State) =>
  state.loading;
export const get${variables.etoName?cap_first}Loaded: any = (state: ${variables.etoName?cap_first}State) =>
  state.loaded;
