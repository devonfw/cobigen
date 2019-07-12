import { Action } from '@ngrx/store';
import { ${variables.etoName?cap_first}Model } from '../../models/${variables.etoName?lower_case}.model';
import { Update } from '@ngrx/entity';
import { HttpResponseModel } from '../../models/httpresponse.model';
import { SearchCriteriaDataModel } from '../../models/searchcriteriadata.model';

export enum ${variables.etoName}ActionTypes {
  LOAD_DATA = '[${variables.etoName?cap_first}] LoadData ',
  LOAD_DATA_SUCCESS = '[${variables.etoName?cap_first}] LoadDataSuccess ',
  LOAD_DATA_FAIL = '[${variables.etoName?cap_first}] LoadDataFail',
  SEARCH_DATA = '[${variables.etoName?cap_first}] SearchData',
  SEARCH_DATA_SUCCESS = '[${variables.etoName?cap_first}] SearchDataSuccess',
  CREATE_DATA = '[${variables.etoName?cap_first}] CreateData',
  CREATE_DATA_SUCCESS = '[${variables.etoName?cap_first}] CreateDataSuccess',
  CREATE_DATA_FAIL = '[${variables.etoName?cap_first}] CreateDataFail',
  DELETE_DATA = '[${variables.etoName?cap_first}] DeleteData',
  DELETE_DATA_SUCCESS = '[${variables.etoName?cap_first}] DeleteSuccess',
  DELETE_DATA_FAIL = '[${variables.etoName?cap_first}] DeleteDataFail',
  UPDATE_DATA = '[${variables.etoName?cap_first}] UpdateData',
  UPDATE_DATA_FAIL = '[${variables.etoName?cap_first}] UpdateDataFail',
  UPDATE_DATA_SUCCESS = '[${variables.etoName?cap_first}] UpdateDataSuccess',
}
/* @export
 * @class LoadData
 * @implements {Action}
 */
export class LoadData implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.LOAD_DATA =
    ${variables.etoName?cap_first}ActionTypes.LOAD_DATA;
  constructor(public payload: ${variables.etoName?cap_first}Model) {}
}

/* @export
 * @class LoadDataSuccess
 * @implements {Action}
 */
export class LoadDataSuccess implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.LOAD_DATA_SUCCESS =
    ${variables.etoName?cap_first}ActionTypes.LOAD_DATA_SUCCESS;
  constructor(public payload: HttpResponseModel) {}
}

/* @export
 * @class LoadDataFail
 * @implements {Action}
 */
export class LoadDataFail implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.LOAD_DATA_FAIL =
    ${variables.etoName?cap_first}ActionTypes.LOAD_DATA_FAIL;
  constructor(public payload: { error: Error }) {}
}

/* @export
 * @class CreateData
 * @implements {Action}
 */
export class CreateData implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.CREATE_DATA =
    ${variables.etoName?cap_first}ActionTypes.CREATE_DATA;
  constructor(public payload: SearchCriteriaDataModel) {}
}

/* @export
 * @class CreateDataSuccess
 * @implements {Action}
 */
export class CreateDataSuccess implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.CREATE_DATA_SUCCESS =
    ${variables.etoName?cap_first}ActionTypes.CREATE_DATA_SUCCESS;
  constructor(public payload: SearchCriteriaDataModel) {}
}

/* @export
 * @class CreateDataFail
 * @implements {Action}
 */
export class CreateDataFail implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.CREATE_DATA_FAIL =
    ${variables.etoName?cap_first}ActionTypes.CREATE_DATA_FAIL;
  constructor(public payload: { error: Error }) {}
}

/* @export
 * @class DeleteData
 * @implements {Action}
 */
export class DeleteData implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.DELETE_DATA =
    ${variables.etoName?cap_first}ActionTypes.DELETE_DATA;
  constructor(public payload: SearchCriteriaDataModel) {}
}

/* @export
 * @class DeleteDataSuccess
 * @implements {Action}
 */
export class DeleteDataSuccess implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.DELETE_DATA_SUCCESS =
    ${variables.etoName?cap_first}ActionTypes.DELETE_DATA_SUCCESS;
  constructor(public payload: SearchCriteriaDataModel) {}
}

/* @export
 * @class UpdateData
 * @implements {Action}
 */
export class UpdateData implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.UPDATE_DATA =
    ${variables.etoName?cap_first}ActionTypes.UPDATE_DATA;
  constructor(public payload: SearchCriteriaDataModel) {}
}

/* @export
 * @class UpdateDataSuccess
 * @implements {Action}
 */
export class UpdateDataSuccess implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.UPDATE_DATA_SUCCESS =
    ${variables.etoName?cap_first}ActionTypes.UPDATE_DATA_SUCCESS;
  constructor(public payload: { criteria: {}; data: Update<${variables.etoName?cap_first}Model> }) {}
}

/* @export
 * @class UpdateDataFail
 * @implements {Action}
 */
export class UpdateDataFail implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.UPDATE_DATA_FAIL =
    ${variables.etoName?cap_first}ActionTypes.UPDATE_DATA_FAIL;
  constructor(public payload: { error: Error }) {}
}

/* @export
 * @class DeleteDataFail
 * @implements {Action}
 */
export class DeleteDataFail implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.DELETE_DATA_FAIL =
    ${variables.etoName?cap_first}ActionTypes.DELETE_DATA_FAIL;
  constructor(public payload: { error: Error }) {}
}

/* @export
 * @class SearchData
 * @implements {Action}
 */
export class SearchData implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.SEARCH_DATA =
    ${variables.etoName?cap_first}ActionTypes.SEARCH_DATA;
  constructor(public payload: ${variables.etoName?cap_first}Model) {}
}

/* @export
 * @class SearchDataSuccess
 * @implements {Action}
 */
export class SearchDataSuccess implements Action {
  readonly type: ${variables.etoName?cap_first}ActionTypes.SEARCH_DATA_SUCCESS =
    ${variables.etoName?cap_first}ActionTypes.SEARCH_DATA_SUCCESS;
}

export type ${variables.etoName}Action =
  | CreateData
  | CreateDataSuccess
  | CreateDataFail
  | DeleteDataSuccess
  | DeleteData
  | DeleteDataFail
  | UpdateData
  | UpdateDataSuccess
  | UpdateDataFail
  | SearchData
  | LoadData
  | LoadDataSuccess
  | LoadDataFail
  | SearchDataSuccess;
