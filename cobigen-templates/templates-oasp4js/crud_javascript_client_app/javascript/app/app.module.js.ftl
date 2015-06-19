angular.module('app', ['ui.select', 'ngRoute',  'app.main', 'app.${variables.component}'])
    .config(function ($locationProvider, uiSelectConfig) {
        'use strict';
        $locationProvider.html5Mode(false);
        uiSelectConfig.theme = 'bootstrap';
    })
    .run(function (globalSpinner) {
        'use strict';
        globalSpinner.showOnRouteChangeStartAndHideWhenComplete();
    });