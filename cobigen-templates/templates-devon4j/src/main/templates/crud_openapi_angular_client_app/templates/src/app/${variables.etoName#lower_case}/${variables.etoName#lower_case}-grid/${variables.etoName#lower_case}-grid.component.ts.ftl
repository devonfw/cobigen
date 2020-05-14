import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { TranslocoService, AvailableLangs } from '@ngneat/transloco';
import { Pageable } from '../../core/interfaces/pageable';
import { AuthService } from '../../core/security/auth.service';
import { ${variables.etoName?cap_first}AlertComponent } from '../${variables.etoName?lower_case}-alert/${variables.etoName?lower_case}-alert.component';
import { ${variables.etoName?cap_first}DialogComponent } from '../${variables.etoName?lower_case}-dialog/${variables.etoName?lower_case}-dialog.component';
import { ${variables.etoName?cap_first}Service } from '../services/${variables.etoName?lower_case}.service';

@Component({
  selector: 'public-${variables.etoName?lower_case}-grid',
  templateUrl: './${variables.etoName?lower_case}-grid.component.html',
  styleUrls: ['./${variables.etoName?lower_case}-grid.component.scss'],
})
export class ${variables.etoName?cap_first}GridComponent implements OnInit {
  currentLanguage: string;
  langs: AvailableLangs;
  private pageable: Pageable = {
      pageSize: 8,
      pageNumber: 0,
  };
  private sorting: any[] = [];

  @ViewChild('pagingBar', { static: true })
  pagingBar: MatPaginator;

  data: any = [];
  columns: any[] = [
<#list model.properties as property>
    {
      name: '${property.name?uncap_first}',
      label: '${variables.component?lower_case}.${variables.etoName?cap_first}.columns.${property.name?uncap_first}',
    },
  </#list>
  ];
  displayedColumns: string[] = [
    'select',
    <#list model.properties as field>
      '${field.name?uncap_first}',
    </#list>
    ];
  pageSize = 8;
  pageSizes: number[] = [8, 16, 24];
  selectedRow: any;

  dialogRef: MatDialogRef<${variables.etoName?cap_first}DialogComponent>;
  totalItems: number;
  searchTerms: any = {
  <#list model.properties as field>
    ${field.name?uncap_first}: undefined,
  </#list>
  };
  selection: SelectionModel<any> = new SelectionModel<any>(false, []);
  constructor(
    private translocoService: TranslocoService,
    public dialog: MatDialog,
    public authService: AuthService,
    public router: Router,
    private dataGridService: ${variables.etoName?cap_first}Service,
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
        (this.pageable.sort = this.sorting),
      )
      .subscribe(
        (res: any) => {
          this.data = res.content;
          this.totalItems = res.totalElements;
        },
        (error: any) => {
          setTimeout(() => {
            this.dialog.open(${variables.etoName?cap_first}AlertComponent, {
              width: '400px',
              data: {
                confirmDialog: false,
                message: this.translocoService.translate(error.message),
                title: this.translocoService.translate('ERROR'),
                cancelButton: this.translocoService.translate('CLOSE'),
              },
            });
          });
        },
      );
  }

  page(pagingEvent: PageEvent): void {
    this.pageable = {
        pageSize: pagingEvent.pageSize,
        pageNumber: pagingEvent.pageIndex,
        sort: this.pageable.sort,
    };
    this.get${variables.etoName?cap_first}();
  }
  sort(sortEvent: Sort): void {
    this.sorting = [];
    if (sortEvent.direction) {
      this.sorting.push({
       property: sortEvent.active.split('.').pop(),
       direction: '' + sortEvent.direction,
      });
    }
    this.get${variables.etoName?cap_first}();
  }
  checkboxLabel(row?: any): string {
    return ${r"`${
      this.selection.isSelected(row) ? 'deselect' : 'select'
    } row ${row.position + 1}`"};
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
            this.dialog.open(${variables.etoName?cap_first}AlertComponent, {
              width: '400px',
              data: {
                confirmDialog: false,
                  message: this.translocoService.translate(error.message),
                  title: this.translocoService.translate(
                    '${variables.component?lower_case}.alert.title',
                  ),
                  cancelButton: this.translocoService.translate('CLOSE'),
                },
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
    this.dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        this.dataGridService.save${variables.etoName?cap_first}(result).subscribe(
          () => {
            this.get${variables.etoName?cap_first}();
            this.selectedRow = undefined;
          },
          (error: any) => {
            this.dialog.open(${variables.etoName?cap_first}AlertComponent, {
                width: '400px',
                data: {
                  confirmDialog: false,
                  message: this.translocoService.translate(error.message),
                  title: this.translocoService.translate(
                    '${variables.component?lower_case}.alert.title',
                  ),
                  cancelButton: this.translocoService.translate('CLOSE'),
                },
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
    this.dialog
      .open(${variables.etoName?cap_first}AlertComponent, {
      width: '400px',
      data: {
        confirmDialog: true,
          message: this.translocoService.translate(
            '${variables.component?lower_case}management.alert.message',
          ),
          title: this.translocoService.translate(
            '${variables.component?lower_case}management.alert.title',
          ),
          cancelButton: this.translocoService.translate(
            '${variables.component?lower_case}management.alert.cancelBtn',
          ),
          acceptButton: this.translocoService.translate(
            '${variables.component?lower_case}management.alert.acceptBtn',
          ),
      },
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
            this.dialog.open(${variables.etoName?cap_first}AlertComponent, {
              width: '400px',
              data: {
                confirmDialog: false,
                    message: this.translocoService.translate(error.message),
                    title: this.translocoService.translate(
                      '${variables.component?lower_case}management.alert.title',
                    ),
                    cancelButton: this.translocoService.translate('CLOSE'),
                  },
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
    this.get${variables.etoName?cap_first}();
    this.pagingBar.firstPage();
  }

  searchReset(form: any): void {
    form.reset();
    this.get${variables.etoName?cap_first}();
  }
}
