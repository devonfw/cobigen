import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { SecurityService } from '../../security/security.service';
import { TranslateService } from 'ng2-translate/src/translate.service';
import { ${variables.component?cap_first}AddDialogComponent } from '../${variables.component}AddDialog/${variables.component}AddDialog.component';
import { MdDialog, MdDialogRef } from '@angular/material';
import { Component, OnInit, ViewChild } from '@angular/core';
import {
    IPageChangeEvent,
    ITdDataTableColumn,
    ITdDataTableSortChangeEvent
} from '@covalent/core';
import { TdDialogService } from '@covalent/core';
import { ${variables.component?cap_first}DataGridService } from './${variables.component}DataGrid.service';
import * as _ from 'lodash';

@Component({
    templateUrl: './${variables.component}DataGrid.component.html'
})

export class ${variables.component?cap_first}DataGridComponent implements OnInit {

    @ViewChild('dataTable') dataTable;
    
    data: any = [];
    columns: any = [<#list pojo.fields as field>
            <#if field?has_next>
            {name: '${field.name}', label: this.getTranslation('${variables.component}DataGrid.columns.${field.name}')},
            <#else>
            {name: '${field.name}', label: this.getTranslation('${variables.component}DataGrid.columns.${field.name}')}
            </#if>
          </#list>];  
    selectedRow: any;
    language: string;

    dialogRef: MdDialogRef<${variables.component?cap_first}AddDialogComponent>;

    dataTotal: number;
    searchBox: boolean = false;
    currentPage: number = 1;
    pageSize: number = 5;
    sorting: any[] = [];
    searchTerms: any = {
     <#list pojo.fields as field>
      <#if field?has_next>
        ${field.name}: null,
      <#else>
        ${field.name}: null
      </#if>
    </#list>
    };

    constructor(public dialog: MdDialog,
                private dataGridService: ${variables.component?cap_first}DataGridService,
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
            this.columns = [<#list pojo.fields as field>
            <#if field?has_next>
            {name: '${field.name}', label: this.getTranslation('${variables.component}DataGrid.columns.${field.name}')},
            <#else>
            {name: '${field.name}', label: this.getTranslation('${variables.component}DataGrid.columns.${field.name}')}
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
       this.dataGridService.getData(this.pageSize, this.currentPage, this.searchTerms, this.sorting)
                           .subscribe((res) => {
                               me.data = res.result;
                               me.dataTotal = res.pagination.total;
                           }, (error) =>{
                                this._dialogService.openConfirm({
                                    message: JSON.parse(error.text()).message,
                                    title: this.getTranslation('${variables.component}DataGrid.alert.title')
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
        this.searchTerms = searchForm.value;
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
        this.dialogRef = this.dialog.open(${variables.component?cap_first}AddDialogComponent);
        this.dialogRef.componentInstance.title = this.getTranslation("${variables.component}DataGrid.addTitle");

        this.dialogRef.afterClosed()
                      .subscribe(result => {
                          if (result) {
                            this.dataGridService.saveData(result)
                                                .subscribe( () => {
                                                    this.getData();
                                                }, (error) => {
                                                    this._dialogService.openAlert({
                                                        message: JSON.parse(error.text()).message,
                                                        title: this.getTranslation('${variables.component}DataGrid.alert.title')
                                                    })
                                                });
                          }
                      });
    }

    openEditDialog(): void {
        this.dialogRef = this.dialog.open(${variables.component?cap_first}AddDialogComponent);
        this.dialogRef.componentInstance.item = this.selectedRow;
        this.dialogRef.componentInstance.title = this.getTranslation("${variables.component}DataGrid.editTitle");

        this.dialogRef.afterClosed()
                      .subscribe(result => {
                        if (result) {
                            this.dataGridService.saveData(result)
                                                .subscribe( () => {
                                                    this.getData();
                                                }, (error) => {
                                                    this._dialogService.openAlert({
                                                        message: JSON.parse(error.text()).message,
                                                        title: this.getTranslation('${variables.component}DataGrid.alert.title')
                                                    })
                                                });
                        }
                      });
    }

    openConfirm(): void {
        this._dialogService.openConfirm({
            message: this.getTranslation('${variables.component}DataGrid.alert.message'),
            title: this.getTranslation('${variables.component}DataGrid.alert.title'),
            cancelButton: this.getTranslation('${variables.component}DataGrid.alert.cancelBtn'),
            acceptButton: this.getTranslation('${variables.component}DataGrid.alert.acceptBtn'),
        }).afterClosed().subscribe((accept: boolean) => {
            if (accept) {
                this.dataGridService.deleteData(this.selectedRow.id)
                                    .subscribe( () => {
                                        this.getData();
                                    }, (error) => {
                                        this._dialogService.openAlert({
                                            message: JSON.parse(error.text()).message,
                                            title: this.getTranslation('${variables.component}DataGrid.alert.title')
                                        })
                                    });
            }
        });
    }
}
