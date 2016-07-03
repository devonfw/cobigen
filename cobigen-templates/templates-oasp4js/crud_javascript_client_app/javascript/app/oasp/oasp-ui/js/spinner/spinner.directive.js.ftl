angular.module('oasp.oaspUi.spinner')
    .constant('spinnerOptions', {
        lines: 13,
        length: 20,
        width: 4,
        radius: 16,
        corners: 1,
        rotate: 0,
        color: '#ffffff',
        speed: 1.2,
        trail: 54,
        shadow: false,
        hwaccel: false,
        zIndex: 2e9
    })
    .directive('spinner', function (spinnerOptions) {
        'use strict';

        return {
            restrict: 'A',
            replace: true,
            templateUrl: 'oasp/oasp-ui/html/spinner/spinner.html',
            scope: {
                spinnerVisible: '=spinner'
            },
            link: function (scope) {
                scope.spinnerOptions = spinnerOptions;
            }
        };
    });
