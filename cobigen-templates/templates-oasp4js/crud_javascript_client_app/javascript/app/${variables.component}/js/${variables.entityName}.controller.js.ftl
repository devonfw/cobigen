/*jslint todo: true */
angular.module('app.${variables.component}')
    .controller('${variables.entityName}Cntl', function ($scope, ${variables.entityName}, initial${variables.entityName}Entries, globalSpinner) {
        'use strict';
        var selectedLogEntry = function () {
            return $scope.selectedItems && $scope.selectedItems.length ? $scope.selectedItems[0] : undefined;
        };
        
        $scope.selectedItems = [];
        $scope.gridOptions = {
            data: initial${variables.entityName}Entries,
            multiSelect: false
        };
        /* do we need this for the generic case, or just use the angular-js filter option?
        $scope.buttonDefs = [  
            {
                label: 'Filtern',
                onClick: function () {
                    globalSpinner.decorateCallOfFunctionReturningPromise(function () {
                    	$scope.selectedItems = [];
                        return syslog.filter($scope.fehlerlabel, $scope.fehlerartOption);
                    });
                },
                isActive: function () {
                    return true;
                }
            }
        ];

        $scope.fehlerartOptionen = [ // only necessary if we keep the filter
                        	    {
                        	        "id": "alle",
                        	        "label": "alle"
                        	    },
                        	    {
                        	        "id": "Nutzer gesperrt",
                        	        "label": "Nutzer gesperrt"
                        	    },
                        	    {
                        	        "id": "Signaturfehler",
                        	        "label": "Signaturfehler"
                        	    },
                        	    {
                        	        "id": "Verbindungsfehler",
                        	        "label": "Verbindungsfehler"
                        	    }
                        	];*/

    });