angular.module('app.${variables.component}').factory('${variables.entityName?lower_case}ManagementRestService', function ($http, currentContextPath) {
    'use strict';

    var servicePath = currentContextPath.get() + 'services/rest/${variables.component}/v1/';
//    var servicePath = currentContextPath.get() + 'services/rest/administration/syslog/v1/administration'; REMOVE?

    return {
        getPaginated${variables.entityName}s: function (pagenumber, pagesize) {
            var ${variables.entityName?lower_case}SearchCriteria = {
                pagination: {
                    size: pagesize,
                    page: pagenumber,
                    total: true
                }
            };
            return $http.post(servicePath + '/${variables.entityName?lower_case}/search', ${variables.entityName?lower_case}SearchCriteria);
        }
    };
});
