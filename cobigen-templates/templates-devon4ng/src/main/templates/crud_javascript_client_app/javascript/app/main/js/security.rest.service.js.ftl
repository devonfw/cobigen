angular.module('app.main').factory('securityRestService', function ($http, currentContextPath) {
    'use strict';

    var servicePath = currentContextPath.get() + 'services/rest/';

    return {
        getCurrentUser: function () {
            return $http.get(servicePath + 'security/v1/currentuser/');
        },
        getCsrfToken: function () {
            return $http.get(servicePath + 'security/v1/csrftoken/');
        },
        login: function (username, password) {
            /*jshint -W106*/
            return $http.post(servicePath + 'login', {
                j_username: username,
                j_password: password
            });
            /*jshint +W106*/
        },
        logout: function () {
            return $http.get(servicePath + 'logout');
        }
    };
});
