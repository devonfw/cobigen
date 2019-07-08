import {
  EntityState,
  EntityAdapter,
  createEntityAdapter,
  Update,
} from '@ngrx/entity';
import { ${variables.etoName?cap_first}Model } from '../../models/${variables.etoName?lower_case}.model';
import { HttpResponseModel } from '../../models/httpresponse.model';
import {
  ${variables.etoName?cap_first}ActionTypes,
  ${variables.etoName?cap_first}Action,
  LoadDataSuccess,
  CreateDataSuccess,
  UpdateDataSuccess,
  DeleteDataSuccess,
} from '../actions/${variables.etoName?lower_case}.actions';

/* @export
 * @interface SampleDataState
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

/* @export
 * @param {${variables.etoName?cap_first}State} [state=initialState]
 * @param {${variables.etoName?cap_first}Action} action
 * @returns {${variables.etoName?cap_first}State}
 */
export function reducer(
  state: ${variables.etoName?cap_first}State = initialState,
  action: ${variables.etoName?cap_first}Action,
): ${variables.etoName?cap_first}State {
  switch (action.type) {
    case ${variables.etoName?cap_first}ActionTypes.LOAD_DATA: {
      return { ...state, loading: true };
    }

    case ${variables.etoName?cap_first}ActionTypes.LOAD_DATA_SUCCESS: {
      const response: HttpResponseModel = (<LoadDataSuccess>action).payload;
      const data: ${variables.etoName?cap_first}Model[] = response.content;

      state = {
        ...state,
        loading: false,
        loaded: true,
        totalElements: response.totalElements,
      };
      return adapter.addAll(data, state);
    }

    case ${variables.etoName?cap_first}ActionTypes.LOAD_DATA_FAIL: {
      return { ...state, loading: false, loaded: false };
    }

    case ${variables.etoName?cap_first}ActionTypes.CREATE_DATA: {
      return { ...state };
    }

    case ${variables.etoName?cap_first}ActionTypes.CREATE_DATA_SUCCESS: {
      const data: ${variables.etoName?cap_first}Model = (<CreateDataSuccess>action).payload.data;
      state = {
        ...state,
        loading: false,
        loaded: false,
      };
      return adapter.addOne(data, state);
    }

    case ${variables.etoName?cap_first}ActionTypes.CREATE_DATA_FAIL: {
      return { ...state, textMessage: 'Add Data Fail' };
    }

    case ${variables.etoName?cap_first}ActionTypes.UPDATE_DATA: {
      return { ...state };
    }

    case ${variables.etoName?cap_first}ActionTypes.UPDATE_DATA_SUCCESS: {
      const data: Update<${variables.etoName?cap_first}Model> = (<UpdateDataSuccess>action).payload
        .data;
      state = {
        ...state,
        textMessage: 'Edit Data Success',
      };
      return adapter.updateOne(data, state);
    }

    case ${variables.etoName?cap_first}ActionTypes.UPDATE_DATA_FAIL: {
      return { ...state, textMessage: 'Edit Data Fail' };
    }
    case ${variables.etoName?cap_first}ActionTypes.DELETE_DATA: {
      return { ...state };
    }
    case ${variables.etoName?cap_first}ActionTypes.DELETE_DATA_SUCCESS: {
      const dataId: number = (<DeleteDataSuccess>action).payload.data.id;
      state = {
        ...state,
        textMessage: 'delete Data Success',
        loading: false,
        loaded: true,
      };

      return adapter.removeOne(dataId, state);
    }
    case ${variables.etoName?cap_first}ActionTypes.DELETE_DATA_FAIL: {
      return { ...state, textMessage: 'delete Data Fail' };
    }
    default: {
      return state;
    }
  }
}

export const get${variables.etoName?cap_first}Total: any = (state: ${variables.etoName?cap_first}State) =>
  state.totalElements;
export const get${variables.etoName?cap_first}Loading: any = (state: ${variables.etoName?cap_first}State) =>
  state.loading;
export const get${variables.etoName?cap_first}Loaded: any = (state: ${variables.etoName?cap_first}State) =>
  state.loaded;
