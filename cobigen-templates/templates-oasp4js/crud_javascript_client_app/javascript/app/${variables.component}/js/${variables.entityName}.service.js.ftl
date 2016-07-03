angular.module('app.${variables.component}').factory('${variables.entityName?lower_case}s', function (${variables.entityName?lower_case}ManagementRestService) {
    'use strict';
    var paginated${variables.entityName}s = {};
    return {
      getPaginated${variables.entityName}s: function (pagenumber, pagesize) {
            return ${variables.entityName?lower_case}ManagementRestService.getPaginated${variables.entityName}s(pagenumber, pagesize).then(function (response) {
                angular.copy(response.data, paginated${variables.entityName}s);
                return paginated${variables.entityName}s;
            });
        }

    };
});
