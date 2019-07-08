import { Injectable } from '@angular/core';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { of, Observable } from 'rxjs';
import { map, switchMap, catchError } from 'rxjs/operators';
import { AuthService } from '../../../core/security/auth.service';
import { ${variables.etoName?cap_first}Service } from '../../../${variables.etoName?lower_case}/services/${variables.etoName?lower_case}.service';
import { ${variables.etoName?cap_first}Model } from '../../models/${variables.etoName?lower_case}.model';
import {
  ${variables.etoName?cap_first}ActionTypes,
} from '../actions/${variables.etoName?lower_case}.actions';
import { Action } from '@ngrx/store';
import { Update } from '@ngrx/entity';
import { HttpResponseModel } from '../../models/httpresponse.model';
import { SearchCriteriaDataModel } from '../../models/searchcriteriadata.model';

/* @export
 * @class ${variables.etoName}Effects
 */
@Injectable()
export class ${variables.etoName?cap_first}Effects {
  /* @type {Observable<Action>}
   * @memberof ${variables.etoName}Effects
   */
  @Effect()
  loadData: Observable<Action> = this.actions.pipe(
    ofType(${variables.etoName?cap_first}ActionTypes.LOAD_DATA),
    map((action: LoadData) => action.payload),
    switchMap((payload: any) => {
      return this.${variables.etoName?lower_case}service
        .get${variables.etoName?cap_first}(
          payload.size,
          payload.page,
          payload.searchTerms,
          payload.sort,
        )
        .pipe(
          map(
            (${variables.etoName?lower_case}Res: HttpResponseModel) => new LoadDataSuccess(${variables.etoName?lower_case}Res),
          ),
          catchError((error: Error) => of(new LoadDataFail({ error: error }))),
        );
    }),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName}Effects
   */
  @Effect()
  addData: Observable<Action> = this.actions.pipe(
    ofType(${variables.etoName?cap_first}ActionTypes.CREATE_DATA),
    map((action: CreateData) => action.payload),
    switchMap((payload: SearchCriteriaDataModel) => {
      return this.${variables.etoName?lower_case}service.save${variables.etoName?cap_first}(payload.data).pipe(
        map((adddata: ${variables.etoName?cap_first}Model) => new CreateDataSuccess({
          criteria: payload.criteria,
          data: adddata,
        })),
        catchError((error: Error) => of(new CreateDataFail({ error: error }))),
      );
    }),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName?cap_first}Effects
   */
  @Effect()
  addDataSuccess: Observable<Action> = this.actions.pipe(
    ofType(${variables.etoName?cap_first}ActionTypes.CREATE_DATA_SUCCESS),
    map((action: CreateDataSuccess) => new LoadData(action.payload.criteria)),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName}Effects
   */
  @Effect()
  deleteData: Observable<Action> = this.actions.pipe(
    ofType(${variables.etoName}ActionTypes.DELETE_DATA),
    map((action: DeleteData) => action.payload),
    switchMap((payload: SearchCriteriaDataModel) => {
      return this.${variables.etoName?lower_case}service.delete${variables.etoName?cap_first}(payload.data.id).pipe(
        map(() => new DeleteDataSuccess(payload)),
        catchError((error: Error) => of(new DeleteDataFail({ error: error }))),
      );
    }),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName?cap_first}Effects
   */
  @Effect()
  deleteDataSuccess: Observable<Action> = this.actions.pipe(
    ofType(${variables.etoName?cap_first}ActionTypes.DELETE_DATA_SUCCESS),
    map((action: DeleteDataSuccess) => new LoadData(action.payload.criteria)),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName}Effects
   */
  @Effect()
  editData: Observable<Action> = this.actions.pipe(
    ofType(${variables.etoName?cap_first}ActionTypes.UPDATE_DATA),
    map((action: UpdateData) => action.payload),
    switchMap((payload: SearchCriteriaDataModel) => {
      return this.${variables.etoName?lower_case}service.edit${variables.etoName?cap_first}(payload.data).pipe(
        map((editdata: ${variables.etoName?cap_first}Model) => {
          const update: Update<${variables.etoName?cap_first}Model> = {
            id: editdata.id,
            changes: {
              <#list pojo.fields as field>
              ${field.name?uncap_first}: editdata.${field.name?uncap_first},
              </#list>
            },
          };

          return new UpdateDataSuccess({
            criteria: payload.criteria,
            data: update,
          });
        }),
        catchError((error: Error) => of(new UpdateDataFail({ error: error }))),
      );
    }),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName?cap_first}Effects
   */
  @Effect()
  editDataSuccess: Observable<Action> = this.actions.pipe(
    ofType(${variables.etoName?cap_first}ActionTypes.UPDATE_DATA_SUCCESS),
    map((action: UpdateDataSuccess) => new LoadData(action.payload.criteria)),
  );

  /* Creates an instance of ${variables.etoName}Effects.
   * @param {Actions} actions
   * @param {AuthService} authservice
   * @param {${variables.etoName}Service} ${variables.etoName?lower_case}service
   * @memberof ${variables.etoName}Effects
   */
  constructor(
    private actions: Actions,
    public authservice: AuthService,
    private ${variables.etoName?lower_case}service: ${variables.etoName?cap_first}Service,
  ) {}
}
