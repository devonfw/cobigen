angular.module('app.${variables.component}')
    .controller('${variables.entityName}Cntl', function ($scope, ${variables.entityName?lower_case}s, paginated${variables.entityName}List, globalSpinner) {
        'use strict';
        var selected${variables.entityName}s = function () {
            return $scope.selectedItems && $scope.selectedItems.length ? $scope.selectedItems[0] : undefined;
        };

        $scope.selectedItems = [];
        $scope.maxSize = 4;
        $scope.totalItems = paginated${variables.entityName}List.pagination.total;
        $scope.numPerPage = paginated${variables.entityName}List.pagination.size;
        $scope.currentPage = paginated${variables.entityName}List.pagination.page;

        $scope.gridOptions = {
            data: paginated${variables.entityName}List.result
        };

        $scope.reloadEntries = function () {
            ${variables.entityName?lower_case}s.getPaginated${variables.entityName}s($scope.currentPage, $scope.numPerPage).then(function (paginated${variables.entityName}s) {
                return paginated${variables.entityName}s;
            }).then(function (res) {
                paginated${variables.entityName}List = res;
                $scope.gridOptions.data = paginated${variables.entityName}List.result;
            });
        };

        $scope.$watch('currentPage', function () {
            $scope.reloadEntries();
        });
    });