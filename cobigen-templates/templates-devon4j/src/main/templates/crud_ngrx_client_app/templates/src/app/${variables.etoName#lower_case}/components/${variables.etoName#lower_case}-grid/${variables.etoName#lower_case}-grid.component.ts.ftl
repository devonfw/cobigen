import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { TranslocoService } from '@ngneat/transloco';
import { Store } from '@ngrx/store';
import { untilDestroyed } from 'ngx-take-until-destroy';
import { Observable } from 'rxjs';
import { AuthService } from '../../../core/security/auth.service';
import { Pageable } from '../../../shared/models/pageable';
import { ${variables.etoName?cap_first}DialogComponent } from '../../components/${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';
import { ${variables.etoName?cap_first}Model } from '../../models/${variables.etoName?lower_case}.model';
import { SearchCriteriaDataModel } from '../../models/searchcriteriadata.model';
import { ${variables.etoName?cap_first}Service } from '../../services/${variables.etoName?lower_case}.service';
import * as fromStore from '../../store';
import * as ${variables.etoName?uncap_first}Actions from '../../store/actions/${variables.etoName?lower_case}.actions';
import { ${variables.etoName?cap_first}AlertComponent } from '../${variables.etoName?lower_case}-alert/${variables.etoName?lower_case}-alert.component';

/* @export
 * @class ${variables.etoName?cap_first}GridComponent
 * @implements {OnInit}
 */
@Component({
  selector: 'public-app-${variables.etoName?lower_case}-grid-display',
  templateUrl: './${variables.etoName?lower_case}-grid.component.html',
  styleUrls: ['./${variables.etoName?lower_case}-grid.component.scss'],
})
export class ${variables.etoName?cap_first}GridComponent implements OnInit, OnDestroy {
  private pageable: Pageable = {
    pageSize: 8,
    pageNumber: 0,
    sort: [
      {
        property: 'name',
        direction: 'ASC',
      },
    ],
  };

  private ${variables.etoName?lower_case}$: Observable<${variables.etoName?cap_first}Model[]>;
  private ${variables.etoName?lower_case}Total$: Observable<number>;
  private sorting: any[] = [];
  @ViewChild('pagingBar', { static: true }) pagingBar: MatPaginator;

  data: any = [];

  /* @type {ITdDataTableColumn[]}
   * @memberof ${variables.etoName?cap_first}GridComponent
   */
  columns: any[] = [
  <#list pojo.fields as field>
    {
      name: '${field.name?uncap_first}',
      label: '${variables.component?lower_case}.${variables.etoName?cap_first}.columns.${field.name?uncap_first}',
    },
  </#list>
  ];
  displayedColumns: string[] = [
    'select',
    <#list pojo.fields as field>
    '${field.name?uncap_first}',
    </#list>
    ];

  totalItems: number;
  pageSize = 8;
  pageSizes: number[] = [8, 16, 24];
  selectedRow: any;
  dialogRef: MatDialogRef<${variables.etoName?cap_first}DialogComponent>;
  selection: SelectionModel<any> = new SelectionModel<any>(false, []);

  /* @type {*}
   * @memberof ${variables.etoName?cap_first}GridComponent
   */
  searchTerms: any = {
  id: undefined,
  <#list pojo.fields as field>
  ${field.name?uncap_first}: undefined,
  </#list>
  modificationCounter: undefined,
  pageSize: undefined,
  pagination: undefined,
  searchTerms: undefined,
  };
  /* Creates an instance of ${variables.etoName?cap_first}GridComponent.
   * @param {Store<fromStore.AppState>} store
   * @param {TranslateService} translate
   * @param {MatDialog} dialog
   * @param {AuthService} authService
   * @param {Router} router
   * @param {${variables.etoName?cap_first}Service} dataGridService
   * @memberof ${variables.etoName?cap_first}GridComponent
   */
  constructor(
    private store: Store<fromStore.AppState>,
    private translocoService: TranslocoService,
    public dialog: MatDialog,
    public authService: AuthService,
    public router: Router,
    public dataGridService: ${variables.etoName?cap_first}Service,
  ) {}

  ngOnInit(): void {
    this.${variables.etoName?lower_case}$ = this.store.select<${variables.etoName?cap_first}Model[]>(
      fromStore.get${variables.etoName?cap_first}Array,
    );

    this.${variables.etoName?lower_case}Total$ = this.store.select<number>(
      fromStore.get${variables.etoName?cap_first}Total,
    );

    this.store.dispatch(
      ${variables.etoName?uncap_first}Actions.loadData({
        ${variables.etoName?uncap_first}Model: this.getSearchCriteria(),
      }),
    );
    this.get${variables.etoName?cap_first}();
  }

  ngOnDestroy(): void {
  	/* Method necessary to manage unsubcriptions,  it must not be deleted*/
  }

  get${variables.etoName?cap_first}(): void {
    this.${variables.etoName?lower_case}Total$.pipe(untilDestroyed(this)).subscribe(
      (res: number) => {
        this.totalItems = res;
      },
      (error: any) => {
        //
      },
    );

    this.${variables.etoName?lower_case}$.pipe(untilDestroyed(this)).subscribe(
      (res: ${variables.etoName?cap_first}Model[]) => {
        this.data = res;
      },
      (error: any) => {
        //
      },
    );
  }

  getSearchCriteria(): {} {
    return {
      size: this.pageable.pageSize,
      page: this.pageable.pageNumber,
      searchTerms: { ...this.searchTerms },
      sort: this.pageable.sort = this.sorting,
    };
  }
  /* @param {IPageChangeEvent} pagingEvent
   * @memberof ${variables.etoName?cap_first}GridComponent
   */
  page(pagingEvent: PageEvent): void {
    this.pageable = {
        pageSize: pagingEvent.pageSize,
        pageNumber: pagingEvent.pageIndex,
        sort: this.pageable.sort,
    };
    this.store.dispatch(
      ${variables.etoName?uncap_first}Actions.loadData({
        ${variables.etoName?uncap_first}Model: this.getSearchCriteria(),
      }),
    );
  }

  /* @param {ITdDataTableSortChangeEvent} sortEvent
   * @memberof ${variables.etoName?cap_first}GridComponent
   */
   
  sort(sortEvent: Sort): void {
    this.sorting = [];
    if (sortEvent.direction) {
      this.sorting.push({
       property: sortEvent.active.split('.').pop(),
       direction: '' + sortEvent.direction,
      });
	}
    this.store.dispatch(
      ${variables.etoName?uncap_first}Actions.loadData({
        ${variables.etoName?uncap_first}Model: this.getSearchCriteria(),
      }),
    );
  }

  checkboxLabel(row?: any): string {
    return ${r"`${
      this.selection.isSelected(row) ? 'deselect' : 'select'
    } row ${row.position + 1}`"};
  }

  openDialog(): void {
    this.dialogRef = this.dialog.open(${variables.etoName?cap_first}DialogComponent);

    this.dialogRef
      .afterClosed()
      .pipe(untilDestroyed(this))
      .subscribe((result: any) => {
        if (result) {
          const searchCriteriaDataModel: SearchCriteriaDataModel = {
            criteria: this.getSearchCriteria(),
            data: result,
          };
          this.store.dispatch(
            ${variables.etoName?uncap_first}Actions.createData({ searchCriteriaDataModel }),
          );
        }
      });
  }
  /* @param {*} e
   * @memberof ${variables.etoName?cap_first}GridComponent
   */
  selectEvent(row: any): void {
    this.selection.toggle(row);
    this.selection.isSelected(row)
      ? (this.selectedRow = row)
      : (this.selectedRow = undefined);
  }
  openEditDialog(): void {
    this.dialogRef = this.dialog.open(${variables.etoName?cap_first}DialogComponent, {
      data: this.selectedRow,
    });

    this.dialogRef
      .afterClosed()
      .pipe(untilDestroyed(this))
      .subscribe((result: any) => {
        if (result) {
          {
            this.selectedRow = undefined;
            const searchCriteriaDataModel: SearchCriteriaDataModel = {
              criteria: this.getSearchCriteria(),
              data: result,
            };
            this.store.dispatch(
              ${variables.etoName?uncap_first}Actions.updateData({ searchCriteriaDataModel }),
            );
          }
        }
      });
  }

  openConfirm(): void {
    const payload: any = {
      id: this.selectedRow.id,
      size: this.pageable.pageSize,
      page: this.pageable.pageNumber,
      searchTerms: { ...this.searchTerms },
      sort: this.pageable.sort = this.sorting,
    };
	this.dialog
      .open(${variables.etoName?cap_first}AlertComponent, {
        width: '400px',
        data: {
          message: this.translocoService.translate(
            '${variables.component?lower_case}.alert.message',
          ),
          title: this.translocoService.translate(
            '${variables.component?lower_case}.alert.title',
          ),
          cancelButton: this.translocoService.translate(
            '${variables.component?lower_case}.alert.cancelBtn',
          ),
          acceptButton: this.translocoService.translate(
            '${variables.component?lower_case}.alert.acceptBtn',
          ),
        },
      })
      .afterClosed()
      .pipe(untilDestroyed(this))
      .subscribe((accept: boolean) => {
        if (accept) {
          const searchCriteriaDataModel: SearchCriteriaDataModel = {
            criteria: this.getSearchCriteria(),
            data: payload,
          };
          this.store.dispatch(
            ${variables.etoName?uncap_first}Actions.deleteData({ searchCriteriaDataModel }),
          );
          this.selectedRow = undefined;
        }
      });
  }

  filter(): void {
    this.store.dispatch(
      ${variables.etoName?uncap_first}Actions.loadData({
        ${variables.etoName?uncap_first}Model: this.getSearchCriteria(),
      }),
    );
    this.pagingBar.firstPage();
  }

  /* @param {*} form
   * @memberof ${variables.etoName?cap_first}GridComponent
   */
  searchReset(form: any): void {
    form.reset();
    this.store.dispatch(
      ${variables.etoName?uncap_first}Actions.loadData({
        ${variables.etoName?uncap_first}Model: this.getSearchCriteria(),
      }),
    );
  }
}
