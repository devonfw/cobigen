angular.module('oasp.oaspUi.buttonBar')
    .directive('buttonBar', function () {
        'use strict';
        return {
            restrict: 'EA',
            replace: true,
            templateUrl: 'oasp/oasp-ui/html/button-bar/button-bar.html',
            scope: {
                buttonDefs: '='
            },
            link: function ($scope) {
                $scope.onButtonClick = function (buttonDef) {
                    if (buttonDef && angular.isFunction(buttonDef.onClick)) {
                        buttonDef.onClick.apply(undefined, arguments);
                    }
                };
                $scope.isButtonDisabled = function (buttonDef) {
                    if (buttonDef && angular.isFunction(buttonDef.isActive)) {
                        return !buttonDef.isActive.apply(undefined, arguments);
                    }
                    if (buttonDef && angular.isFunction(buttonDef.isNotActive)) {
                        return buttonDef.isNotActive.apply(undefined, arguments);
                    }
                    return true;
                };
            }
        };
    });
