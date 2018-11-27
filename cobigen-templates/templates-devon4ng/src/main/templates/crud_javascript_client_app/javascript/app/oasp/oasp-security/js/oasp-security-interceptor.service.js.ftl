angular.module('oasp.oaspSecurity')
    .factory('oaspSecurityInterceptor', function ($q, oaspUnauthenticatedRequestResender) {
        'use strict';

        return {
            responseError: function (response) {
                var originalRequest;
                if (response.status === 403) {
                    originalRequest = response.config;
                    return oaspUnauthenticatedRequestResender.addRequest(originalRequest);
                }
                return $q.reject(response);
            }
        };
    });
