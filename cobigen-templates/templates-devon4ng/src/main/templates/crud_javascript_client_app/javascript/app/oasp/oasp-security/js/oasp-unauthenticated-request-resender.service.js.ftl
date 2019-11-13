angular.module('oasp.oaspSecurity')
    .provider('oaspUnauthenticatedRequestResender', function () {
        'use strict';
        var config = {
            authenticatorServiceName: 'authenticator'
        };

        return {
            setAuthenticatorServiceName: function (authenticatorServiceName) {
                config.authenticatorServiceName = authenticatorServiceName || config.authenticatorServiceName;
            },
            $get: function ($q, $injector) {
                var requestQueue = {
                        queue: [],
                        push: function (requestToResend) {
                            this.queue.push(requestToResend);
                        },
                        resendAll: function (csrfProtection) {
                            while (this.queue.length) {
                                this.queue.shift().resend(csrfProtection);
                            }
                        },
                        cancelAll: function () {
                            while (this.queue.length) {
                                this.queue.shift().cancel();
                            }
                        }
                    },
                    getOaspSecurityService = function () {
                        return $injector.get('oaspSecurityService');
                    },
                    getAuthenticator = function () {
                        return $injector.get(config.authenticatorServiceName);
                    },
                    authenticate = (function () {
                        var authenticatorNotCalledYet = true;

                        return function (successCallbackFn, failureCallbackFn) {
                            if (authenticatorNotCalledYet) {
                                getAuthenticator().execute()
                                    .then(function () {
                                        successCallbackFn();
                                        authenticatorNotCalledYet = true;
                                    }, function () {
                                        failureCallbackFn();
                                        authenticatorNotCalledYet = true;
                                    });
                                authenticatorNotCalledYet = false;
                            }
                        };
                    }());

                return {
                    addRequest: function (request) {
                        var deferredRetry = $q.defer(),
                            requestToResend = {
                                resend: function (csrfProtection) {
                                    var resendRequestUpdatingItsCsrfProtectionData =
                                        function (request, csrfProtection) {
                                            var $http = $injector.get('$http');
                                            request.headers[csrfProtection.headerName] = csrfProtection.token;
                                            return $http(request);
                                        };

                                    resendRequestUpdatingItsCsrfProtectionData(request, csrfProtection)
                                        .then(function (value) {
                                            deferredRetry.resolve(value);
                                        }, function (value) {
                                            deferredRetry.reject(value);
                                        });
                                },
                                cancel: function () {
                                    deferredRetry.reject();
                                }
                            },
                            resendAllAwaitingRequestsOnSuccess = function () {
                                var currentCsrfToken = getOaspSecurityService().getCurrentCsrfToken();

                                requestQueue.resendAll({
                                    headerName: currentCsrfToken.getHeaderName(),
                                    token: currentCsrfToken.getToken()
                                });
                            },
                            cancelAllAwaitingRequestsOnFailure = function () {
                                requestQueue.cancelAll();
                            };

                        requestQueue.push(requestToResend);
                        authenticate(resendAllAwaitingRequestsOnSuccess, cancelAllAwaitingRequestsOnFailure);
                        return deferredRetry.promise;
                    }
                };
            }
        };
    });
