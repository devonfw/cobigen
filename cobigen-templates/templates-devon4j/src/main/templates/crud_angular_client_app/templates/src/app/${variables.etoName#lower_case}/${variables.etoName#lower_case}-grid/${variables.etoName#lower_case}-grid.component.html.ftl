<div layout="row" class="pad-sm" flex>
  <mat-card flex>
    <mat-card-header>
      <mat-card-title>{{ '${variables.component?lower_case}.${variables.etoName?cap_first}.title' | translate }}</mat-card-title>
      <mat-card-subtitle>{{ '${variables.component?lower_case}.${variables.etoName?cap_first}.subtitle' | translate }}</mat-card-subtitle>
    </mat-card-header>
    <mat-card-content>
      <button mat-button flex class="push-right-sm" [matTooltip]="'buttons.addItem' | translate" (click)="openDialog()">
        <mat-icon>add</mat-icon>
      </button>
      <button mat-button flex class="push-right-sm" [disabled]="!selectedRow" [matTooltip]="'buttons.editItem' | translate" (click)="openEditDialog()">
        <mat-icon>mode_edit</mat-icon>
      </button>
      <button mat-button flex class="push-right-sm" [disabled]="!selectedRow" [matTooltip]="'buttons.deleteItem' | translate" (click)="openConfirm()">
        <mat-icon>delete</mat-icon>
      </button>

      <form (ngSubmit)="filter()" #filterForm="ngForm">
        <td-expansion-panel label="Filters">
          <div layout="row" class="pad-left-md pad-right-md" style="align-items:center; border-bottom: 1px solid lightgrey" flex>
            <div layout-xs="column" class="justify-space-around" style="align-items:center" hide-gt-xs flex>
            <#list pojo.fields as field>
              <mat-form-field color="accent">
                <input matInput placeholder="${field.name?cap_first}" [(ngModel)]="searchTerms.${field.name}" name="${field.name}">
              </mat-form-field>
            </#list>
            </div>
            <div layout-gt-xs="row" class="justify-space-around" style="align-items:center" hide-xs flex>
            <#list pojo.fields as field>
              <mat-form-field color="accent" class="pad-sm" flex>
                <input matInput type="${JavaUtil.getAngularType(field.type)}" placeholder="${field.name?cap_first}" [(ngModel)]="searchTerms.${field.name}" name="${field.name}">
              </mat-form-field>
            </#list>
            </div>
          </div>
          <div class="align-right">
            <button mat-button type="button" (click)="searchReset(filterForm)" class="text-upper property-text-bold">Clear filters</button>
            <button mat-raised-button type="submit" color="accent" class="text-upper property-text-bold">Apply filters</button>
          </div>
        </td-expansion-panel>
      </form>
      <mat-divider></mat-divider>
      <td-data-table #dataTable
        [data]="data"
        [columns]="columns"
        [sortable]="true"
        [selectable]="true"
        [multiple]="false"
        (rowSelect)="selectEvent($event)"
        (sortChange)="sort($event)">
      </td-data-table>
      <div class="mat-padding" *ngIf="!dataTable.hasData" layout="row" layout-align="center center">
        <h3>No results to display.</h3>
      </div>
      <td-paging-bar #pagingBar [pageSize]="pageSize" [total]="totalItems" (change)="page($event)">
        <span hide-xs>Rows per page:</span>
        <mat-select [style.width.px]="50" [(ngModel)]="pageSize">
          <mat-option *ngFor="let size of pageSizes" [value]="size">
            {{ size }}
          </mat-option>
        </mat-select>
        <span>{{pagingBar.range}}
          <span hide-xs hide-sm hide-md>of {{pagingBar.total}}</span>
        </span>
      </td-paging-bar>
    </mat-card-content>
  </mat-card>
</div>