angular.module('app.${variables.component}').factory('${variables.entityName}', function (${variables.entityName}RestService) {
    'use strict';
    var ${variables.entityName} = [];
    return {
    	get${variables.entityName} : function () {
            return ${variables.entityName}RestService.get${variables.entityName}().then(function (response) {
                angular.copy(response.data, ${variables.entityName});
                return ${variables.entityName};
            });
        },
/* only needed for custom filter
        filter: function (pSyslogId, pLabel) {
        	if(pLabel == "alle")
        		pLabel = "";
            return syslogRestService.filter({syslogId: pSyslogId, label: pLabel}).then(function (response) {
            	angular.copy(response.data, syslog);
                return syslog;
            });
        }*/

    };
});
