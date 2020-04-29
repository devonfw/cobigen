<div layout="row" class="page-container">
  <mat-card>
    <mat-card-header>
      <mat-card-title>{{
        '${variables.component?lower_case}.${variables.etoName?cap_first}.title' | transloco
      }}</mat-card-title>
      <mat-card-subtitle>{{
        '${variables.component?lower_case}.${variables.etoName?cap_first}.subtitle' | transloco
      }}</mat-card-subtitle>
    </mat-card-header>
    <mat-card-content>
      <button
        mat-button
        class="data-action-button"
        [matTooltip]="'buttons.addItem' | transloco"
        (click)="openDialog()"
      >
        <mat-icon>add</mat-icon>
      </button>
      <button
        mat-button
        class="data-action-button"
        [disabled]="!selectedRow"
        [matTooltip]="'buttons.editItem' | transloco"
        (click)="openEditDialog()"
      >
        <mat-icon>mode_edit</mat-icon>
      </button>
      <button
        mat-button
        class="data-action-button"
        [disabled]="!selectedRow"
        [matTooltip]="'buttons.deleteItem' | transloco"
        (click)="openConfirm()"
      >
        <mat-icon>delete</mat-icon>
      </button>

      <form (ngSubmit)="filter()" #filterForm="ngForm">
        <mat-expansion-panel>
          <mat-expansion-panel-header>
            <mat-panel-title>Filters</mat-panel-title>
          </mat-expansion-panel-header>
          <div class="filter-form-fields">
            <div class="filter-form-fields-mobile justify-space-around">
			<#list model.properties as property>
              <mat-form-field color="accent">
                <input
                  matInput
				  <#if JavaUtil.getAngularType(property.type) == 'number'>type="number"</#if>
                  placeholder="${property.name?cap_first}"
                  [(ngModel)]="searchTerms.${property.name?uncap_first}"
                  name="${property.name?uncap_first}"
                />
              </mat-form-field>
		 </#list>

            </div>
            <div class="filter-form-fields-desktop justify-space-around">
		     <#list model.properties as property>
              <mat-form-field color="accent">
                <input
                  matInput
                  placeholder="${property.name?cap_first}"
                  [(ngModel)]="searchTerms.${property.name?uncap_first}"
                  name="${property.name?uncap_first}"
                />
              </mat-form-field>
            </#list>
            </div>
          </div>
          <div class="align-right">
            <button
              mat-button
              type="button"
              (click)="searchReset(filterForm)"
              class="text-upper"
            >
              Clear filters
            </button>
            <button
              mat-raised-button
              type="submit"
              color="accent"
              class="text-upper"
            >
              Apply filters
            </button>
          </div>
        </mat-expansion-panel>
      </form>
      <mat-divider></mat-divider>
      <div class="table-container" style="width:100%; overflow:auto;">
        <table
          mat-table
          [dataSource]="data"
          matSort
          (matSortChange)="sort($event)"
          style="width:100%"
        >
          <!-- Checkbox Column -->
          <ng-container matColumnDef="select">
            <th mat-header-cell *matHeaderCellDef style="width:42px;"></th>
            <td mat-cell *matCellDef="let row">
              <mat-checkbox
                (click)="$event.stopPropagation()"
                (change)="selectEvent(row)"
                [checked]="selection.isSelected(row)"
                [aria-label]="checkboxLabel(row)"
              >
              </mat-checkbox>
            </td>
          </ng-container>
          <#list model.properties as property>
          <!-- ${property.name?capitalize} Column -->
          <ng-container matColumnDef="${property.name?uncap_first}">
            <th
              mat-header-cell
              *matHeaderCellDef
              mat-sort-header
              style="width:197px;"
            >
              {{ columns[${property?index}].label | transloco }}
            </th>
            <td mat-cell *matCellDef="let element">{{ element.${property.name?uncap_first} }}</td>
          </ng-container>
		   </#list>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
        </table>
      </div>
      <div
        class="mat-padding"
        *ngIf="data.length === 0"
        layout="row"
        layout-align="center center"
      >
        <h3>No results to display.</h3>
      </div>
      <mat-paginator
        #pagingBar
        [length]="totalItems"
        [pageSize]="pageSize"
        [pageSizeOptions]="pageSizes"
        [showFirstLastButtons]="true"
        (page)="page($event)"
      >
      </mat-paginator>
    </mat-card-content>
  </mat-card>
</div>