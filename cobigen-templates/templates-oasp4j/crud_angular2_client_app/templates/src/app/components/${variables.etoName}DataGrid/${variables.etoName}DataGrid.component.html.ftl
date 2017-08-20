<#include '/functions.ftl'>
<div layout="row" layout-align="start center" style="background-color:#eee">
  <span flex class="md-title" style ="text-align: center">{{'${variables.etoName}DataGrid.title' | translate}}</span>

  <button
    md-button
    flex
    class="push-right-sm"
    [mdTooltip]="'${variables.etoName}DataGrid.searchTip' | translate"
    (click) = "openSearchBox()">
    <md-icon>search</md-icon>
  </button>

  <button
    md-button
    flex
    class="push-right-sm"
    [disabled]="sorting.length === 0"
    [mdTooltip]="'${variables.etoName}DataGrid.sortTip' | translate"
    (click) = "clearSorting()">
    <md-icon>import_export</md-icon>
  </button>

  <button
    md-icon-button
    flex
    class="push-right-sm"
    [mdTooltip]="'buttons.addItem' | translate"
    (click) = "openDialog()">
    <md-icon>add</md-icon>
  </button>

  <button
    md-icon-button
    flex
    class="push-right-sm"
    [disabled]="!selectedRow"
    [mdTooltip]="'buttons.editItem' | translate"
    (click) = "openEditDialog()"> 
    <md-icon>mode_edit</md-icon>
  </button>

  <button
    md-icon-button
    flex
    class="push-right-sm"
    [disabled]="!selectedRow"
    [mdTooltip]="'buttons.deleteItem' | translate"
    (click) = "openConfirm()">
    <md-icon>delete</md-icon>
  </button>

</div>

<div id="${variables.etoName?lower_case}DataGrid-div" *ngIf="searchBox" layout="row" class="pad-left-sm pad-right-sm" style="background-color:#eee">
  <form #searchForm="ngForm">
   <@getNG2Type_Grid_Search/>

    <button md-icon-button (click) = "search(searchForm.form)"> {{'buttons.search' | translate}} </button>
    <button md-icon-button (click) = "searchReset(searchForm.form)"> {{'buttons.clean' | translate}} </button>
  </form>

</div>

<md-divider></md-divider>
<td-data-table
  #dataTable
  [data]="data"
  [columns]="cobigen_columns"
  [sortable]="true"
  [selectable]="true"
  [multiple]="false"
  [sortBy]="sortBy"
  [sortOrder]="sortOrder"
  (rowSelect)="selectEvent($event)"
  (sortChange)="sort($event)">
</td-data-table>
<td-paging-bar [pageSizes]="[5, 10, 20]" [total]="dataTotal" (change)="page($event)"></td-paging-bar>
