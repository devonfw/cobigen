<#import '/variables.ftl' as class>
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ${variables.entityName?cap_first}Service } from '../services/${variables.entityName?lower_case}.service';
import { AuthService } from '../../core/security/auth.service';

import { Pageable } from '../../core/interfaces/pageable';
import { ${variables.entityName?cap_first}DialogComponent } from '../${variables.entityName?lower_case}-dialog/${variables.entityName?lower_case}-dialog.component';
import { SelectionModel } from '@angular/cdk/collections';
import { ${variables.entityName?cap_first}AlertComponent } from '../${variables.entityName?lower_case}-alert/${variables.entityName?lower_case}-alert.component';

@Component({
  selector: 'public-${variables.entityName?lower_case}-grid',
  templateUrl: './${variables.entityName?lower_case}-grid.component.html',
  styleUrls: ['./${variables.entityName?lower_case}-grid.component.scss'],
})
export class ${variables.entityName?cap_first}GridComponent implements OnInit {

  private pageable: Pageable = {
      pageSize: 8,
      pageNumber: 0,
  };
  private sorting: any[] = [];

  @ViewChild('pagingBar', { static: true })
  pagingBar: MatPaginator;

  data: any = [];
  columns: any[] = [
  <#list class.properties as property>
    {  
      name: '${property.identifier?uncap_first}',
      label: this.getTranslation('${variables.component?lower_case}.${variables.entityName?cap_first}.columns.${property.identifier?uncap_first}'),
    },
  </#list>
  ];
  displayedColumns: string[] = [
    'select',
    <#list class.properties as property>
      '${property.identifier?uncap_first}',
    </#list>
    ];
  pageSize: number = 8;
  pageSizes: number[] = [8, 16, 24];
  selectedRow: any;
  dialogRef: MatDialogRef<${variables.entityName?cap_first}DialogComponent>;
  totalItems: number;
  searchTerms: any = {
  <#list class.properties as property>
    ${property.identifier?uncap_first}: undefined,
  </#list>
  };
  selection: SelectionModel<any> = new SelectionModel<any>(false, []);
  constructor(
    private translate: TranslateService,
    public dialog: MatDialog,
    public authService: AuthService,
    public router: Router,
    private dataGridService: ${variables.entityName?cap_first}Service,
  ) { }

  ngOnInit(): void {
    this.get${variables.entityName?cap_first}();
  }

  get${variables.entityName?cap_first}(): void {
    this.removeEmptySearchTerms();  
    this.dataGridService
      .get${variables.entityName?cap_first}(
        this.pageable.pageSize,
        this.pageable.pageNumber,
        this.searchTerms,
        this.sorting,
      )
      .subscribe(
        (res: any) => {
          this.data = res.data;
          this.totalItems = res.total;
        },
        (error: any) => {
          setTimeout(() => {
            this.dialog.open(${variables.entityName?cap_first}AlertComponent, {
              width: '400px',
              data: {
                confirmDialog: false,
                message: this.getTranslation(error.message),
                title: this.getTranslation('ERROR'),
                cancelButton: this.getTranslation('CLOSE'),
              },
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
            .get('${variables.component?lower_case}.${variables.entityName?cap_first}.columns.' + column.name)
            .subscribe((res: string) => {
              column.label = res;
            });
        }
      });
    });
    return value;
  }
  
  page(pagingEvent: PageEvent): void {
    this.pageable = {
        pageSize: pagingEvent.pageSize,
        pageNumber: pagingEvent.pageIndex,
        sort: this.pageable.sort,
    };
    this.get${variables.entityName?cap_first}();
  }
  
  sort(sortEvent: Sort): void {
    this.sorting = [];
    if (sortEvent.direction) {
      this.sorting.push({
       property: sortEvent.active.split('.').pop(),
       direction: '' + sortEvent.direction,
      });
    }
    this.get${variables.entityName?cap_first}();
  }
  
  checkboxLabel(row?: any): string {
    return 'row ' + row.id;
  }
  
  openDialog(): void {
    this.dialogRef = this.dialog.open(${variables.entityName?cap_first}DialogComponent);

    this.dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        this.dataGridService.save${variables.entityName?cap_first}(result).subscribe(
          () => {
            this.get${variables.entityName?cap_first}();
          },
          (error: any) => {
            const additionalError = error.error && error.error.message[0] && error.error.message[0].constraints;
            const errorMessage = JSON.stringify(additionalError) + '\n' + error.message;
            
            this.dialog.open(${variables.entityName?cap_first}AlertComponent, {
              width: '400px',
              data: {
                confirmDialog: false,
                message: this.getTranslation(errorMessage),
                title: this.getTranslation('${variables.component?lower_case}.alert.title'),
                cancelButton: this.getTranslation('CLOSE'),
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
    this.selection.isSelected(row) ? (this.selectedRow = row) : (this.selectedRow = undefined);
  }
  
  openEditDialog(): void {
    this.dialogRef = this.dialog.open(${variables.entityName?cap_first}DialogComponent, {
      data: this.selectedRow,
    });
    this.dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        this.dataGridService.save${variables.entityName?cap_first}(result).subscribe(
          () => {
            this.get${variables.entityName?cap_first}();
            this.selectedRow = undefined;
          },
          (error: any) => {
            this.dialog.open(${variables.entityName?cap_first}AlertComponent, {
                width: '400px',
                data: {
                  confirmDialog: false,
                  message: this.getTranslation(error.message),
                  title: this.getTranslation('${variables.component?lower_case}.alert.title'),
                  cancelButton: this.getTranslation('CLOSE'),
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
    this.dialog.open(${variables.entityName?cap_first}AlertComponent, {
      width: '400px',
      data: {
        confirmDialog: true,
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
    .subscribe((accept: boolean) => {
      if (accept) {
        this.dataGridService.delete${variables.entityName?cap_first}(this.selectedRow.id).subscribe(
          () => {
            this.get${variables.entityName?cap_first}();
            this.selectedRow = undefined;
          },
          (error: any) => {
            this.dialog.open(${variables.entityName?cap_first}AlertComponent, {
              width: '400px',
              data: {
                confirmDialog: false,
                message: this.getTranslation(error.message),
                title: this.getTranslation('${variables.component?lower_case}.alert.title'),
                cancelButton: this.getTranslation('CLOSE'),
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
    this.get${variables.entityName?cap_first}();
    this.pagingBar.firstPage();
  }

  searchReset(form: any): void {
    form.reset();
    this.searchTerms = {
    <#list class.properties as property>
      ${property.identifier?uncap_first}: undefined,
    </#list>
    };
    this.get${variables.entityName?cap_first}();
  }
  
  removeEmptySearchTerms(): void {
    for (let key of Object.keys(this.searchTerms)) {
      if (this.searchTerms[key] === ''){
        this.searchTerms[key] = undefined;
      }
    }
  }  
}
