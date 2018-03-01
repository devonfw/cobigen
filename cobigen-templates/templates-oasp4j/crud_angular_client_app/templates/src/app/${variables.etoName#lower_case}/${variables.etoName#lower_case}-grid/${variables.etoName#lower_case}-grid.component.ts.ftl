import { Component, OnInit, ViewChild } from '@angular/core';
import { ITdDataTableColumn, 
         TdDataTableColumnComponent,
         TdDataTableComponent,
         TdDialogService,
         IPageChangeEvent,
         ITdDataTableSortChangeEvent } from '@covalent/core';
import { MatDialogRef, MatDialog } from '@angular/material';
import { Router } from '@angular/router';
import * as _ from 'lodash';

import { TranslateService } from '@ngx-translate/core';
import { ${variables.etoName?cap_first}DataGridService } from '../services/${variables.etoName?lower_case}.service';
import { AuthService } from '../../core/security/auth.service';

import { ${variables.etoName?cap_first}DialogComponent } from '../${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';
import { Pagination } from '../../core/interfaces/pagination';

@Component({
  selector: 'app-${variables.etoName?lower_case}-grid',
  templateUrl: './${variables.etoName?lower_case}-grid.component.html',
  styleUrls: ['./${variables.etoName?lower_case}-grid.component.scss']
})
export class ${variables.etoName?cap_first}GridComponent implements OnInit {

  @ViewChild('dataTable') dataTable: TdDataTableComponent;

  data: any = [];
  columns: ITdDataTableColumn[] = [
  <#list pojo.fields as field>
    { name: '${field.name}', label: this.getTranslation('${variables.component?lower_case}.${variables.etoName?cap_first}.columns.${field.name}') }<#if field?has_next>,</#if>
  </#list>
  ];

  private pagination: Pagination = {
    size: 8,
    page: 1,
    total: 1,
  };
  
  private sorting: any[] = [];
  pageSize = 8;
  pageSizes: number[] = [8, 16, 24];
  selectedRow: any;
  dialogRef: MatDialogRef<${variables.etoName?cap_first}DialogComponent>;
  totalItems: number;
  searchTerms: any = {
  <#list pojo.fields as field>
    ${field.name}: null<#if field?has_next>,</#if>
  </#list>
  };

  constructor(
    private translate: TranslateService,
    public dialog: MatDialog,
    public authService: AuthService,
    public router: Router,
    private dataGridService: ${variables.etoName?cap_first}DataGridService,
    private _dialogService: TdDialogService,
  ) { }

  ngOnInit() {
    this.get${variables.etoName?cap_first}();
  }

  get${variables.etoName?cap_first}() {
    this.dataGridService.get${variables.etoName?cap_first}(this.pageSize, this.pagination.page, this.searchTerms, this.sorting)
      .subscribe((res: any) => {
        this.data = res.result;
        this.totalItems = res.pagination.total;
        this.dataTable.refresh();
      },
      (error: any) => {
        setTimeout(() => {
          this._dialogService.openAlert({
            message: error.message,
            title: this.getTranslation('ERROR'),
            closeButton: 'CLOSE'
          });
        });
      });
  }

  getTranslation(text: string): string {
    let value: string;

    this.translate.get(text).subscribe((res: string) => {
      value = res;
    });

    this.translate.onLangChange.subscribe(() => {
      this.columns.forEach(column => {
        this.translate.get('${variables.component?lower_case}.${variables.etoName?cap_first}.columns.' + column.name).subscribe((res: string) => {
          column.label = res;
        });
      });
      this.dataTable.refresh();
    });

    return value;
  }

  page(pagingEvent: IPageChangeEvent): void {
    this.pagination = {
      size: pagingEvent.pageSize,
      page: pagingEvent.page,
      total: 1,
    };
    this.get${variables.etoName?cap_first}();
  }

  sort(sortEvent: ITdDataTableSortChangeEvent): void {
    this.sorting = [];
    this.sorting.push({ 'name': sortEvent.name.split('.').pop(), 'direction': '' + sortEvent.order });
    this.get${variables.etoName?cap_first}();
  }

  openDialog(): void {
    this.dialogRef = this.dialog.open(${variables.etoName?cap_first}DialogComponent);

    this.dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.dataGridService.save${variables.etoName?cap_first}(result).subscribe(() => {
          this.get${variables.etoName?cap_first}();
        }, (error) => {
          this._dialogService.openAlert({
            message: JSON.parse(error.text()).message,
            title: this.getTranslation('${variables.component?lower_case}.alert.title')
          }).afterClosed().subscribe((accept: boolean) => {
            if (accept) {
              this.authService.setLogged(false);
              this.router.navigate(['/login']);
            }
          });
        });
      }
    });
  }

  openEditDialog(): void {
    this.dialogRef = this.dialog.open(${variables.etoName?cap_first}DialogComponent, {
      data: this.selectedRow,
    });

    this.dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.dataGridService.save${variables.etoName?cap_first}(result).subscribe(() => {
          this.get${variables.etoName?cap_first}();
          this.selectedRow = undefined;
        }, (error) => {
          this._dialogService.openAlert({
            message: JSON.parse(error.text()).message,
            title: this.getTranslation('${variables.component?lower_case}.alert.title')
          }).afterClosed().subscribe((accept: boolean) => {
            if (accept) {
              this.authService.setLogged(false);
              this.router.navigate(['/login']);
            }
          });
        });
      }
    });
  }

  openConfirm(): void {
    this._dialogService.openConfirm({
      message: this.getTranslation('${variables.component?lower_case}.alert.message'),
      title: this.getTranslation('${variables.component?lower_case}.alert.title'),
      cancelButton: this.getTranslation('${variables.component?lower_case}.alert.cancelBtn'),
      acceptButton: this.getTranslation('${variables.component?lower_case}.alert.acceptBtn'),
    }).afterClosed().subscribe((accept: boolean) => {
      if (accept) {
        this.dataGridService.delete${variables.etoName?cap_first}(this.selectedRow.id).subscribe(() => {
          this.get${variables.etoName?cap_first}();
          this.selectedRow = undefined;
        }, (error) => {
          this._dialogService.openAlert({
            message: JSON.parse(error.text()).message,
            title: this.getTranslation('${variables.component?lower_case}.alert.title')
          }).afterClosed().subscribe((acceptance: boolean) => {
            if (acceptance) {
              this.authService.setLogged(false);
              this.router.navigate(['/login']);
            }
          });
        });
      }
    });
  }
  
  selectEvent(e): void {
    e.selected ? this.selectedRow = e.row : this.selectedRow = undefined;
  }
  
  clearFilters(form): void {
    form.reset();
    this.getSampleData();
  }
}
