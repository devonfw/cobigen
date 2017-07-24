import { SecurityService } from '../../security/security.service';
import { TranslateService } from 'ng2-translate/src/translate.service';
import { ${variables.etoName?cap_first}AddDialogComponent } from '../${variables.etoName}AddDialog/${variables.etoName}AddDialog.component';
import { MdDialog, MdDialogRef } from '@angular/material';
import { Component, OnInit, ViewChild } from '@angular/core';
import {
    IPageChangeEvent,
    ITdDataTableColumn,
    ITdDataTableSortChangeEvent
} from '@covalent/core';
import { TdDialogService } from '@covalent/core';
import { ${variables.etoName?cap_first}DataGridService } from './${variables.etoName}DataGrid.service';
import * as _ from 'lodash';

@Component({
    templateUrl: './${variables.etoName}DataGrid.component.html'
})

export class ${variables.etoName?cap_first}DataGridComponent implements OnInit {

    @ViewChild('dataTable') dataTable;
    
    data: any = [];
    cobigen_columns: any = [<#list pojo.fields as field>
            <#if field?has_next>
            {name: '${field.name}', label: this.getTranslation('${variables.etoName}DataGrid.cobigen_columns.${field.name}')},
            <#else>
            {name: '${field.name}', label: this.getTranslation('${variables.etoName}DataGrid.cobigen_columns.${field.name}')}
            </#if>
          </#list>];  
    selectedRow: any;
    language: string;

    dialogRef: MdDialogRef<${variables.etoName?cap_first}AddDialogComponent>;

    dataTotal: number;
    searchBox: boolean = false;
    currentPage: number = 1;
    pageSize: number = 5;
    sorting: any[] = [];
    cobigen_searchTerms: any = {
     <#list pojo.fields as field>
      <#if field?has_next>
        ${field.name}: null,
      <#else>
        ${field.name}: null
      </#if>
    </#list>
    };

    constructor(public dialog: MdDialog,
                private dataGridService: ${variables.etoName?cap_first}DataGridService,
                private _dialogService: TdDialogService,
                private translate: TranslateService,
                private securityService: SecurityService) {
    }

    ngOnInit(): void {
        this.getData();
    }
    
    ngDoCheck() {
        if (this.language !== this.translate.currentLang) {
            this.language = this.translate.currentLang;
            this.cobigen_columns = [<#list pojo.fields as field>
            <#if field?has_next>
            {name: '${field.name}', label: this.getTranslation('${variables.etoName}DataGrid.cobigen_columns.${field.name}')},
            <#else>
            {name: '${field.name}', label: this.getTranslation('${variables.etoName}DataGrid.cobigen_columns.${field.name}')}
            </#if>
          </#list>];
        }
    }

    getTranslation(text: string): string {
        let value: string;
        this.translate.get(text).subscribe( (res) => {
            value = res;
        });
        return value;
    }

    getData(): void {
       let me = this;
       this.dataGridService.getData(this.pageSize, this.currentPage, this.cobigen_searchTerms, this.sorting)
                           .subscribe((res) => {
                               me.data = res.result;
                               me.dataTotal = res.pagination.total;
                           }, (error) =>{
                                this._dialogService.openConfirm({
                                    message: JSON.parse(error.text()).message,
                                    title: this.getTranslation('${variables.etoName}DataGrid.alert.title')
                                })
                           });
    }

    sort(sortEvent: ITdDataTableSortChangeEvent): void {
        this.sorting = _.reject(this.sorting, { 'name': sortEvent.name });
        this.sorting.push({"name": sortEvent.name, "direction": sortEvent.order});
        this.getData();
    }

    clearSorting() {
        this.dataTable._sortBy = null;
        this.sorting = [];
        this.getData();
    }

    search(searchForm): void {
        _.forIn(searchForm.value, function(value, key) {
            if(value == "") {
                searchForm.value[key] = null;
            }
        });
        this.cobigen_searchTerms = searchForm.value;
        this.getData();
    }
    
    searchReset(form): void {
      form.reset();
      this.search(form);
    }

    openSearchBox(){
        this.searchBox = !this.searchBox;
    }

    page(pagingEvent: IPageChangeEvent): void {
        this.pageSize = pagingEvent.pageSize;
        this.currentPage = pagingEvent.page;
        this.getData();
    }

    selectEvent(e): void {
        e.selected ? this.selectedRow = e.row : this.selectedRow = undefined;
    }

    openDialog(): void {
        this.dialogRef = this.dialog.open(${variables.etoName?cap_first}AddDialogComponent);
        this.dialogRef.componentInstance.title = this.getTranslation("${variables.etoName}DataGrid.addTitle");

        this.dialogRef.afterClosed()
                      .subscribe(result => {
                          if (result) {
                            this.dataGridService.saveData(result)
                                                .subscribe( () => {
                                                    this.getData();
                                                }, (error) => {
                                                    this._dialogService.openAlert({
                                                        message: JSON.parse(error.text()).message,
                                                        title: this.getTranslation('${variables.etoName}DataGrid.alert.title')
                                                    })
                                                });
                          }
                      });
    }

    openEditDialog(): void {
        this.dialogRef = this.dialog.open(${variables.etoName?cap_first}AddDialogComponent);
        this.dialogRef.componentInstance.cobigen_item = this.selectedRow;
        this.dialogRef.componentInstance.title = this.getTranslation("${variables.etoName}DataGrid.editTitle");

        this.dialogRef.afterClosed()
                      .subscribe(result => {
                        if (result) {
                            this.dataGridService.saveData(result)
                                                .subscribe( () => {
                                                    this.getData();
                                                }, (error) => {
                                                    this._dialogService.openAlert({
                                                        message: JSON.parse(error.text()).message,
                                                        title: this.getTranslation('${variables.etoName}DataGrid.alert.title')
                                                    })
                                                });
                        }
                      });
    }

    openConfirm(): void {
        this._dialogService.openConfirm({
            message: this.getTranslation('${variables.etoName}DataGrid.alert.message'),
            title: this.getTranslation('${variables.etoName}DataGrid.alert.title'),
            cancelButton: this.getTranslation('${variables.etoName}DataGrid.alert.cancelBtn'),
            acceptButton: this.getTranslation('${variables.etoName}DataGrid.alert.acceptBtn'),
        }).afterClosed().subscribe((accept: boolean) => {
            if (accept) {
                this.dataGridService.deleteData(this.selectedRow.id)
                                    .subscribe( () => {
                                        this.getData();
                                    }, (error) => {
                                        this._dialogService.openAlert({
                                            message: JSON.parse(error.text()).message,
                                            title: this.getTranslation('${variables.etoName}DataGrid.alert.title')
                                        })
                                    });
            }
        });
    }
}
