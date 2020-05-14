import { Update } from '@ngrx/entity';
import { createAction, props, union } from '@ngrx/store';
import { HttpResponseModel } from '../../models/httpresponse.model';
import { ${variables.etoName?cap_first}Model } from '../../models/${variables.etoName?lower_case}.model';
import { SearchCriteriaDataModel } from '../../models/searchcriteriadata.model';


export const loadData = createAction(
  '[${variables.etoName?cap_first}] LoadData',
  props<{ ${variables.etoName?uncap_first}Model: ${variables.etoName?cap_first}Model }>(),
);

export const loadDataSuccess = createAction(
  '[${variables.etoName?cap_first}] LoadDataSuccess',
  props<{ httpResponseModel: HttpResponseModel }>(),
);

export const loadDataFail = createAction(
  '[${variables.etoName?cap_first}] LoadDataFail',
  props<{ error: Error }>(),
);

export const createData = createAction(
  '[${variables.etoName?cap_first}] CreateData',
  props<{ searchCriteriaDataModel: SearchCriteriaDataModel }>(),
);

export const createDataSuccess = createAction(
  '[${variables.etoName?cap_first}] CreateDataSuccess',
  props<{ searchCriteriaDataModel: SearchCriteriaDataModel }>(),
);

export const createDataFail = createAction(
  '[${variables.etoName?cap_first}] CreateDataFail',
  props<{ error: Error }>(),
);

export const deleteData = createAction(
  '[${variables.etoName?cap_first}] DeleteData',
  props<{ searchCriteriaDataModel: SearchCriteriaDataModel }>(),
);

export const deleteDataSuccess = createAction(
  '[${variables.etoName?cap_first}] DeleteSuccess',
  props<{ searchCriteriaDataModel: SearchCriteriaDataModel }>(),
);

export const updateData = createAction(
  '[${variables.etoName?cap_first}] UpdateData',
  props<{ searchCriteriaDataModel: SearchCriteriaDataModel }>(),
);

export const updateDataSuccess = createAction(
  '[${variables.etoName?cap_first}] UpdateDataSuccess',
  props<{ criteria: {}; data: Update<${variables.etoName?cap_first}Model> }>(),
);

export const updateDataFail = createAction(
  '[${variables.etoName?cap_first}] UpdateDataFail',
  props<{ error: Error }>(),
);

export const deleteDataFail = createAction(
  '[${variables.etoName?cap_first}] DeleteDataFail',
  props<{ error: Error }>(),
);

export const searchData = createAction(
  '[${variables.etoName?cap_first}] SearchData',
  props<{ ${variables.etoName?uncap_first}Model: ${variables.etoName?cap_first}Model }>(),
);

export const searchDataSuccess = createAction('[${variables.etoName?cap_first}] SearchDataSuccess');

const all = union({
  loadData,
  loadDataSuccess,
  loadDataFail,
  createData,
  createDataSuccess,
  createDataFail,
  deleteData,
  deleteDataSuccess,
  deleteDataFail,
  updateData,
  updateDataSuccess,
  updateDataFail,
  searchData,
  searchDataSuccess,
});

export type ${variables.etoName?cap_first}Actions = typeof all;
