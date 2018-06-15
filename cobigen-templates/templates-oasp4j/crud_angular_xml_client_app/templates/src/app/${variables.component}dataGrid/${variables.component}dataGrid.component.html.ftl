<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>

<#include '/functions.ftl'>
<div layout="row" layout-align="start center" style="background-color:#eee">
  <span flex class="md-title" style ="text-align: center">{{'${variables.component}datagrid.title' | translate}}</span>

  <button
    md-button
    flex
    class="push-right-sm"
    [mdTooltip]="'${variables.component}datagrid.searchTip' | translate"
    (click) = "openSearchBox()">
    <md-icon>search</md-icon>
  </button>

  <button
    md-button
    flex
    class="push-right-sm"
    [mdTooltip]="'buttons.addItem' | translate"
    (click) = "openDialog()">
    <md-icon>add</md-icon>
  </button>

  <button
    md-button
    flex
    class="push-right-sm"
    [disabled]="!selectedRow"
    [mdTooltip]="'buttons.editItem' | translate"
    (click) = "openEditDialog()">
    <md-icon>mode_edit</md-icon>
  </button>

  <button
    md-button
    flex
    class="push-right-sm"
    [disabled]="!selectedRow"
    [mdTooltip]="'buttons.deleteItem' | translate"
    (click) = "openConfirm()">
    <md-icon>delete</md-icon>
  </button>

</div>

<div id="${variables.etoName?lower_case}dataGrid-div" *ngIf="searchBox" layout="row" class="pad-left-sm pad-right-sm" style="background-color:#eee">
  <form #searchForm="ngForm">
      <@getNG2Type_Grid_Search/>

    <button md-button (click) = "search(searchForm.form)"> {{'buttons.search' | translate}} </button>
    <button md-button (click) = "searchReset(searchForm.form)"> {{'buttons.clean' | translate}} </button>
  </form>

</div>

<md-divider></md-divider>
<td-data-table
  #dataTable
  [data]="data"
  [columns]="columns"
  [sortable]="true"
  [selectable]="true"
  [multiple]="false"
  [sortBy]="sortBy"
  [sortOrder]="sortOrder"
  (rowSelect)="selectEvent($event)"
  (sortChange)="sort($event)">
</td-data-table>
<td-paging-bar #pagingBar [pageSizes]="[5, 10, 20]" [total]="dataTotal" (change)="page($event)">
  <span td-paging-bar-label hide-xs>Rows per page:</span>
  {{pagingBar.range}} <span hide-xs>of {{pagingBar.total}}</span>
</td-paging-bar>
