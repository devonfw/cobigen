import * as ${variables.etoName?uncap_first}State from './${variables.etoName?lower_case}.reducers';
import { createFeatureSelector, ActionReducerMap } from '@ngrx/store';

export * from '../effects';
/* @export
 * @interface AppState
 */
export interface AppState {
  ${variables.etoName?uncap_first}: ${variables.etoName?uncap_first}State.${variables.etoName?cap_first}State;
}
export const reducers: ActionReducerMap<AppState> = {
  ${variables.etoName?uncap_first}: ${variables.etoName?uncap_first}State.reducer,
};

export const getAppState: any = createFeatureSelector<AppState>(
  '${variables.etoName?lower_case}reducer',
);
