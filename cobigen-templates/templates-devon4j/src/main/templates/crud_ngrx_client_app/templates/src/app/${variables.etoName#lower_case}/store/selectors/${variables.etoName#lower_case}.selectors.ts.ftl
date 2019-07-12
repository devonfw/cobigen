import { createSelector } from '@ngrx/store';
import { getAppState } from '../reducers';
import { AppState } from '../reducers/index';
import * as from${variables.etoName?cap_first} from '../reducers/${variables.etoName?lower_case}.reducers';

export const {
  selectAll,
  selectEntities,
  selectIds,
  selectTotal,
} = from${variables.etoName?cap_first}.adapter.getSelectors();

export const get${variables.etoName?cap_first}State: any = createSelector(
  getAppState,
  (state: AppState) => state.${variables.etoName?uncap_first},
);

export const get${variables.etoName?cap_first}Array: any = createSelector(
  get${variables.etoName?cap_first}State,
  selectAll,
);

export const get${variables.etoName?cap_first}Total: any = createSelector(
  get${variables.etoName?cap_first}State,
  from${variables.etoName?cap_first}.get${variables.etoName?cap_first}Total,
);

export const getDataLoading: any = createSelector(
  get${variables.etoName?cap_first}State,
  from${variables.etoName?cap_first}.get${variables.etoName?cap_first}Loading,
);

export const getDataLoaded: any = createSelector(
  get${variables.etoName?cap_first}State,
  from${variables.etoName?cap_first}.get${variables.etoName?cap_first}Loaded,
);
