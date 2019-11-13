import { Injectable } from '@angular/core';

import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of, Observable } from 'rxjs';
import { map, switchMap, catchError } from 'rxjs/operators';
import { AuthService } from '../../../../core/security/auth.service';
import { ${variables.etoName?cap_first}Service } from '../../../${variables.etoName?lower_case}/services/${variables.etoName?lower_case}.service';
import { ${variables.etoName?cap_first}Model } from '../../models/${variables.etoName?lower_case}.model';
import * as ${variables.etoName?lower_case}Actions from '../actions/${variables.etoName?lower_case}.actions';
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
  loadData: Observable<Action> = createEffect(() =>
    this.actions.pipe(
      ofType(${variables.etoName?lower_case}Actions.loadData),
      map((action) => action.${variables.etoName?uncap_first}Model),
      switchMap((payload: ${variables.etoName?cap_first}Model) => {
      return this.${variables.etoName?lower_case}service
      .get${variables.etoName?cap_first}(
      payload.size,
      payload.page,
      payload.searchTerms,
      payload.sort,
      )
          .pipe(
            map((httpResponseModel: HttpResponseModel) =>
          	  ${variables.etoName?lower_case}Actions.loadDataSuccess({httpResponseModel}),
          	),
          	catchError((error: Error) =>
          	  of(${variables.etoName?lower_case}Actions.loadDataFail({ error: error }))
          	),
          );
    	}),
      ),
    );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName}Effects
   */
  addData: Observable<Action> = createEffect(() =>
  	this.actions.pipe(
  	  ofType(${variables.etoName?lower_case}Actions.createData),
	  map((action) => action.searchCriteriaDataModel),
	  switchMap((searchCriteriaDataModel: SearchCriteriaDataModel) => {
	    return this.${variables.etoName?lower_case}service
	      .save${variables.etoName?cap_first}(searchCriteriaDataModel.data)
	      .pipe(
	        map((data: ${variables.etoName?cap_first}Model) => {
	          const criteriaDataModel: SearchCriteriaDataModel = {
                criteria: searchCriteriaDataModel.criteria,
                data: data,
              };
              return ${variables.etoName?lower_case}Actions.createDataSuccess({
                searchCriteriaDataModel: criteriaDataModel,
              });
	        }),
	        catchError((error: Error) =>
	          of(${variables.etoName?lower_case}Actions.createDataFail({ error: error }))
	        ),
	    );
	  }),
	),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName?cap_first}Effects
   */
  addDataSuccess: Observable<Action> = createEffect(() =>
  	this.actions.pipe(
   	  ofType(${variables.etoName?lower_case}Actions.createDataSuccess),
      map((action) => ${variables.etoName?lower_case}Actions.loadData({
      	${variables.etoName?uncap_first}Model: action.searchCriteriaDataModel.criteria,
      }),
      ),
    ),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName}Effects
   */
  deleteData: Observable<Action> = createEffect(() =>
    this.actions.pipe(
      ofType(${variables.etoName?lower_case}Actions.deleteData),
      map(action => action.searchCriteriaDataModel),
      switchMap((searchCriteriaDataModel: SearchCriteriaDataModel) => {
        return this.${variables.etoName?lower_case}service
          .delete${variables.etoName?cap_first}(searchCriteriaDataModel.data.id)
          .pipe(
            map(() =>
              ${variables.etoName?lower_case}Actions.deleteDataSuccess({ searchCriteriaDataModel }),
            ),
            catchError((error: Error) =>
              of(${variables.etoName?lower_case}Actions.deleteDataFail({ error: error })),
            ),
    	  );
      }),
    ),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName?cap_first}Effects
   */
  deleteDataSuccess: Observable<Action> = createEffect(() =>
    this.actions.pipe(
      ofType(${variables.etoName?lower_case}Actions.deleteDataSuccess),
      map(action =>
        ${variables.etoName?lower_case}Actions.loadData({
          ${variables.etoName?uncap_first}Model: action.searchCriteriaDataModel.data,
        }),
      ),
    ),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName}Effects
   */
  editData: Observable<Action> = createEffect(() =>
    this.actions.pipe(
      ofType(${variables.etoName?lower_case}Actions.updateData),
      map(action => action.searchCriteriaDataModel),
      switchMap((searchCriteriaDataModel: SearchCriteriaDataModel) => {
        return this.${variables.etoName?lower_case}service
          .edit${variables.etoName?cap_first}(searchCriteriaDataModel.data)
          .pipe(
            map((editdata: ${variables.etoName?cap_first}Model) => {
            const update: Update<${variables.etoName?cap_first}Model> = {
              id: editdata.id,
              changes: {
                <#list pojo.fields as field>
                ${field.name?uncap_first}: editdata.${field.name?uncap_first},
                </#list>
              },
            };
            return ${variables.etoName?lower_case}Actions.updateDataSuccess({
              criteria: searchCriteriaDataModel.criteria,
              data: update,
            });
          }),
          catchError((error: Error) =>
            of(${variables.etoName?lower_case}Actions.updateDataFail({ error: error })),
          ),
        );
      }),
    ),
  );

  /* @type {Observable<Action>}
   * @memberof ${variables.etoName?cap_first}Effects
   */
  editDataSuccess: Observable<Action> = createEffect(() =>
    this.actions.pipe(
      ofType(${variables.etoName?lower_case}Actions.updateDataSuccess),
      map(action =>
        ${variables.etoName?lower_case}Actions.loadData({
          ${variables.etoName?uncap_first}Model: action.criteria,
        }),
      ),
    ),
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
