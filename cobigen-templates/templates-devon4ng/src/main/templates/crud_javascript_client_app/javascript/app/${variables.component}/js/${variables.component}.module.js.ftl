angular.module('app.${variables.component}', ['app.main'], function ($routeProvider, oaspTranslationProvider) {
	'use strict';
    oaspTranslationProvider.enableTranslationForModule('${variables.component}');

    $routeProvider.when('/${variables.component}/${variables.entityName}', {
        templateUrl: '${variables.component}/html/${variables.entityName}.html',
        controller: '${variables.entityName}Cntl',
        resolve: {
        	paginated${variables.entityName}List: ['${variables.entityName?lower_case}s', function (${variables.entityName?lower_case}s) {
                return ${variables.entityName?lower_case}s.getPaginated${variables.entityName}s(1, 4).then(function(paginated${variables.entityName}s) {
                    return paginated${variables.entityName}s;
                });
            }]
        }
    });

});