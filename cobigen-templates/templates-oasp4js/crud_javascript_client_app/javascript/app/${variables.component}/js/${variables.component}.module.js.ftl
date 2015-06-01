angular.module('app.${variables.component}', ['app.main'], function ($routeProvider, oaspTranslationProvider) {
	'use strict';
    oaspTranslationProvider.enableTranslationForModule('${variables.component}');

    $routeProvider.when('/${variables.component}/${variables.entityName}', {
        templateUrl: '${variables.component}/html/${variables.entityName}.html',
        controller: '${variables.entityName}Cntl',
        resolve: {
        	initial${variables.entityName}Entries: ['${variables.entityName}', function (${variables.entityName}) {
                return ${variables.entityName}.get${variables.entityName}();
            }]
        }
    });

});