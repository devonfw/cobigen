angular.module('app.${variables.component}').factory('${variables.entityName}RestService', function ($http, currentContextPath) {
    'use strict';

    var servicePath = currentContextPath.get() + 'services/rest/${variables.component}';
//    var servicePath = currentContextPath.get() + 'services/rest/administration/syslog/v1/administration'; REMOVE?

    return {
        get${variables.entityName}: function () {
            return $http.get(servicePath + '/${variables.entityName?lower_case}/');
        },
        /* only needed for custom filter
        filter: function (params) {
            return $http.get(servicePath + '/syslog/filter/', {params: params});
        }*/
    };
});
