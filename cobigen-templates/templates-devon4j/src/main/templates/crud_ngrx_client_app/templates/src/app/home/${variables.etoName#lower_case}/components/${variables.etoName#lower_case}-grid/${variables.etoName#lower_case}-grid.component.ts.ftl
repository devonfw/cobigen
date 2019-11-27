import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ${variables.etoName?cap_first}Service } from '../../services/${variables.etoName?lower_case}.service';
import { AuthService } from '../../../../core/security/auth.service';

import { ${variables.etoName?cap_first}DialogComponent } from '../../components/${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';
import { Pageable } from '../../../../shared/models/pageable';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import * as fromStore from '../../store';
import { ${variables.etoName?cap_first}Model } from '../../models/${variables.etoName?lower_case}.model';
import * as ${variables.etoName?uncap_first}Actions from '../../store/actions/${variables.etoName?lower_case}.actions';
import { SelectionModel } from '@angular/cdk/collections';
import { ${variables.etoName?cap_first}AlertComponent } from '../${variables.etoName?lower_case}-alert/${variables.etoName?lower_case}-alert.component';
import { untilDestroyed } from 'ngx-take-until-destroy';
import { SearchCriteriaDataModel } from '../../models/searchcriteriadata.model';

@Component({
  selector: 'public-${variables.etoName?lower_case}-grid',
  templateUrl: './${variables.etoName?lower_case}-grid.component.html',
  styleUrls: ['./${variables.etoName?lower_case}-grid.component.scss'],
})
export class ${variables.etoName?cap_first}GridComponent implements OnInit {
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
  columns: any[] = [
  <#list pojo.fields as field>
    {
      name: '${field.name?uncap_first}',
      label: this.getTranslation('${variables.component?lower_case}.${variables.etoName?cap_first}.columns.${field.name?uncap_first}'),
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
  pageSizes: string[] = ['8', '16', '24'];
  selectedRow: any;
  dialogRef: MatDialogRef<${variables.etoName?cap_first}DialogComponent>;
  selection: SelectionModel<any> = new SelectionModel<any>(false, []);

  searchTerms: Object = {
  id: undefined,
  <#list pojo.fields as field>
  ${field.name?uncap_first}: undefined,
  </#list>
  modificationCounter: undefined,
  pageSize: undefined,
  pagination: undefined,
  searchTerms: undefined,
  };

  constructor(
    private store: Store<fromStore.AppState>,
    private translate: TranslateService,
    public dialog: MatDialog,
    public authService: AuthService,
    public router: Router,
  ) { }

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

  // tslint:disable-next-line: use-life-cycle-interface
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

  getTranslation(text: string): string {
    let value: string;
    this.translate
      .get(text)
      .pipe(untilDestroyed(this))
      .subscribe((res: string) => {
        value = res;
      });

	this.translate.onLangChange.pipe(untilDestroyed(this)).subscribe(() => {
      this.columns.forEach((column: any) => {
        if (text.endsWith(column.name)) {
          this.translate
            .get('${variables.component?lower_case}.${variables.etoName?cap_first}.columns.' + column.name)
            .pipe(untilDestroyed(this))
            .subscribe((res: string) => {
              column.label = res;
            });
        }
      });
    });

    return value;
  }

  getSearchCriteria(): {} {
    return {
      size: this.pageable.pageSize,
      page: this.pageable.pageNumber,
      searchTerms: { ...this.searchTerms },
      sort: this.pageable.sort = this.sorting,
    };
  }

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
    return 'row ' + row.id;
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
  selectEvent(row: any): void {
    this.selection.toggle(row);
    this.selection.isSelected(row) ? (this.selectedRow = row) : (this.selectedRow = undefined);
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
          message: this.getTranslation('${variables.component?lower_case}.alert.message'),
          title: this.getTranslation('${variables.component?lower_case}.alert.title'),
          cancelButton: this.getTranslation(
            '${variables.component?lower_case}.alert.cancelBtn',
          ),
          acceptButton: this.getTranslation(
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

  searchReset(form: any): void {
    form.reset();
    this.store.dispatch(
      ${variables.etoName?uncap_first}Actions.loadData({
        ${variables.etoName?uncap_first}Model: this.getSearchCriteria(),
      }),
    );
  }
}
