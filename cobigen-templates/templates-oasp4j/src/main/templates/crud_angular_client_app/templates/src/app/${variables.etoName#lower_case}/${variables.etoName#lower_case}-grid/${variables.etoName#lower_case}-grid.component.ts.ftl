import { Component, OnInit, ViewChild } from '@angular/core';
import {
  ITdDataTableColumn,
  TdDataTableComponent,
  TdDialogService,
  IPageChangeEvent,
  ITdDataTableSortChangeEvent,
  TdPagingBarComponent,
} from '@covalent/core';
import { MatDialogRef, MatDialog } from '@angular/material';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ${variables.etoName?cap_first}Service } from '../services/${variables.etoName?lower_case}.service';
import { AuthService } from '../../core/security/auth.service';

import { ${variables.etoName?cap_first}DialogComponent } from '../${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';
import { Pageable } from '../../core/interfaces/pageable';

@Component({
  selector: 'public-${variables.etoName?lower_case}-grid',
  templateUrl: './${variables.etoName?lower_case}-grid.component.html',
  styleUrls: ['./${variables.etoName?lower_case}-grid.component.scss'],
})
export class ${variables.etoName?cap_first}GridComponent implements OnInit {
    @ViewChild('pagingBar')
    pagingBar: TdPagingBarComponent;
    
    private pageable: Pageable = {
        pageSize: 8,
        pageNumber: 0,
        sort: [{
            property: '${pojo.fields[0].name!}',
            direction: 'ASC'
        }]
    };
  private sorting: any[] = [];

  @ViewChild('dataTable') dataTable: TdDataTableComponent;

  data: any = [];
  columns: ITdDataTableColumn[] = [
  <#list pojo.fields as field>
    {
      name: '${field.name}',
      label: this.getTranslation('${variables.component?lower_case}.${variables.etoName?cap_first}.columns.${field.name}'),
    },
  </#list>
  ];

  pageSize: number = 8;
  pageSizes: number[] = [8, 16, 24];
  selectedRow: any;
  dialogRef: MatDialogRef<${variables.etoName?cap_first}DialogComponent>;
  totalItems: number;
  searchTerms: any = {
  <#list pojo.fields as field>
    ${field.name}: undefined,
  </#list>
  };

  constructor(
    private translate: TranslateService,
    public dialog: MatDialog,
    public authService: AuthService,
    public router: Router,
    private dataGridService: ${variables.etoName?cap_first}Service,
    private _dialogService: TdDialogService,
  ) { }

  ngOnInit(): void {
    this.get${variables.etoName?cap_first}();
  }

  get${variables.etoName?cap_first}(): void {
    this.dataGridService
      .get${variables.etoName?cap_first}(
        this.pageable.pageSize,
        this.pageable.pageNumber,
        this.searchTerms,
        this.pageable.sort = this.sorting,
      )
      .subscribe(
        (res: any) => {
          this.data = res.content;
          this.totalItems = res.totalElements;
          this.dataTable.refresh();
        },
        (error: any) => {
          setTimeout(() => {
            this._dialogService.openAlert({
              message: error.message,
              title: this.getTranslation('ERROR'),
              closeButton: 'CLOSE',
            });
          });
        },
      );
  }

  getTranslation(text: string): string {
    let value: string;
    this.translate.get(text).subscribe((res: string) => {
      value = res;
    });
    this.translate.onLangChange.subscribe(() => {
      this.columns.forEach((column: any) => {
        if (text.endsWith(column.name)) {
          this.translate
            .get('${variables.component?lower_case}.${variables.etoName?cap_first}.columns.' + column.name)
            .subscribe((res: string) => {
              column.label = res;
            });
        }
      });
      this.dataTable.refresh();
    });
    return value;
  }

  page(pagingEvent: IPageChangeEvent): void {
    this.pageable = {
        pageSize: pagingEvent.pageSize,
        pageNumber: pagingEvent.page - 1,
        sort: this.pageable.sort,
    };
    this.get${variables.etoName?cap_first}();
  }

  sort(sortEvent: ITdDataTableSortChangeEvent): void {
    this.sorting = [];
    this.sorting.push({
     property: sortEvent.name.split('.').pop(),
     direction: '' + sortEvent.order,
    });
    this.get${variables.etoName?cap_first}();
  }

  openDialog(): void {
    this.dialogRef = this.dialog.open(${variables.etoName?cap_first}DialogComponent);

    this.dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        this.dataGridService.save${variables.etoName?cap_first}(result).subscribe(
          () => {
            this.get${variables.etoName?cap_first}();
          },
          (error: any) => {
            this._dialogService
              .openAlert({
                message: JSON.parse(error.text()).message,
                title: this.getTranslation('${variables.component?lower_case}.alert.title'),
              })
              .afterClosed()
              .subscribe((accept: boolean) => {
                if (accept) {
                  this.authService.setLogged(false);
                  this.router.navigate(['/login']);
                }
              });
          },
        );
      }
    });
  }
  selectEvent(e: any): void {
    e.selected ? (this.selectedRow = e.row) : (this.selectedRow = undefined);
  }
  openEditDialog(): void {
    this.dialogRef = this.dialog.open(${variables.etoName?cap_first}DialogComponent, {
      data: this.selectedRow,
    });

    this.dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        this.dataGridService.save${variables.etoName?cap_first}(result).subscribe(
          () => {
            this.get${variables.etoName?cap_first}();
          },
          (error: any) => {
            this._dialogService
              .openAlert({
                message: JSON.parse(error.text()).message,
                title: this.getTranslation('${variables.component?lower_case}.alert.title'),
              })
              .afterClosed()
              .subscribe((accept: boolean) => {
                if (accept) {
                  this.authService.setLogged(false);
                  this.router.navigate(['/login']);
                }
              });
          },
        );
      }
    });
  }

  openConfirm(): void {
    this._dialogService
      .openConfirm({
        message: this.getTranslation('${variables.component?lower_case}.alert.message'),
        title: this.getTranslation('${variables.component?lower_case}.alert.title'),
        cancelButton: this.getTranslation(
          '${variables.component?lower_case}.alert.cancelBtn',
        ),
        acceptButton: this.getTranslation(
          '${variables.component?lower_case}.alert.acceptBtn',
        ),
      })
      .afterClosed()
      .subscribe((accept: boolean) => {
        if (accept) {
          this.dataGridService.delete${variables.etoName?cap_first}(this.selectedRow.id).subscribe(
            () => {
              this.get${variables.etoName?cap_first}();
              this.selectedRow = undefined;
            },
            (error: any) => {
              this._dialogService
                .openAlert({
                  message: JSON.parse(error.text()).message,
                  title: this.getTranslation(
                    '${variables.component?lower_case}.alert.title',
                  ),
                })
                .afterClosed()
                .subscribe((acceptance: boolean) => {
                  if (acceptance) {
                    this.authService.setLogged(false);
                    this.router.navigate(['/login']);
                  }
                });
            },
          );
        }
      });
  }
  
  filter(): void {
    this.pagingBar.firstPage();
  }

  searchReset(form: any): void {
    form.reset();
    this.get${variables.etoName?cap_first}();
  }
}
