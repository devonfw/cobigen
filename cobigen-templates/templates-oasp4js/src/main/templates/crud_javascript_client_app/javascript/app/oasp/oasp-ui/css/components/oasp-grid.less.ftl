.tr-ng-grid {
  .tr-ng-grid-footer .pagination {
    margin: 0;
  }

  > thead > tr > th {
    text-align: left;
    vertical-align: top;
  }

  > thead > tr > th,
  > tbody > tr > td,
  > thead > tr > th > .tr-ng-cell,
  > tbody > tr > td > .tr-ng-cell {
    -webkit-touch-callout: none;
    -webkit-user-select: none;
    -khtml-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }

  > tbody > tr > td > .tr-ng-cell {
    overflow: visible;
  }

  .tr-ng-column-header {
  }

  .tr-ng-sort {
    cursor: pointer;
    position: absolute;
    top: 0px;
    left: 0px;
    width: 100%;
    height: 100%;
  }

  .tr-ng-sort .tr-ng-sort-active, .tr-ng-sort .tr-ng-sort-inactive {
    position: absolute;
    top: 1px;
    right: 1px;
    width: 0.8em;
    height: 0.8em;
  }

  .tr-ng-column-header .tr-ng-title {
    position: relative;
  }

  .tr-ng-column-header .tr-ng-column-filter {
    margin-top: 0.5em;
    margin-bottom: 0.1em;
  }
}

.table.raw-table {
  > tfoot {
    display: none;
  }
}
