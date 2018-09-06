angular.module('oasp.oaspSecurity')
    .provider('oaspSecurityService', function () {
        'use strict';
        var config = {
            securityRestServiceName: 'securityRestService',
            appContextServiceName: 'appContext'
        };

        return {
            setSecurityRestServiceName: function (securityRestServiceName) {
                config.securityRestServiceName = securityRestServiceName || config.securityRestServiceName;
            },
            setAppContextServiceName: function (appContextServiceName) {
                config.appContextServiceName = appContextServiceName || config.appContextServiceName;
            },
            $get: function ($injector, $http, $q) {
                var currentCsrfProtection = {
                        set: function (headerName, token) {
                            this.headerName = headerName;
                            this.token = token;
                        },
                        invalidate: function () {
                            this.headerName = undefined;
                            this.token = undefined;
                        }
                    },
                    currentCsrfProtectionWrapper = (function () {
                        return {
                            hasToken: function () {
                                return currentCsrfProtection.headerName && currentCsrfProtection.token ? true : false;
                            },
                            getHeaderName: function () {
                                return currentCsrfProtection.headerName;
                            },
                            getToken: function () {
                                return currentCsrfProtection.token;
                            }
                        };
                    }()),
                    currentUserProfileHandler = (function () {
                        var currentUserProfile,
                            profileBeingInitialized = false,
                            deferredUserProfileRetrieval;

                        return {
                            initializationStarts: function () {
                                profileBeingInitialized = true;
                                deferredUserProfileRetrieval = $q.defer();
                            },
                            initializationSucceeded: function (newUserProfile) {
                                currentUserProfile = newUserProfile;
                                profileBeingInitialized = false;
                                deferredUserProfileRetrieval.resolve(currentUserProfile);
                                deferredUserProfileRetrieval = undefined;
                            },
                            initializationFailed: function () {
                                currentUserProfile = undefined;
                                profileBeingInitialized = false;
                                deferredUserProfileRetrieval.resolve(currentUserProfile);
                                deferredUserProfileRetrieval = undefined;
                            },
                            userLoggedOff: function () {
                                currentUserProfile = undefined;
                            },
                            getProfile: function () {
                                return profileBeingInitialized ? deferredUserProfileRetrieval.promise : $q.when(currentUserProfile);
                            }
                        };
                    }()),
                    getSecurityRestService = function () {
                        return $injector.get(config.securityRestServiceName);
                    },
                    getAppContextService = function () {
                        return $injector.get(config.appContextServiceName);
                    },
                    enableCsrfProtection = function () {
                        return getSecurityRestService().getCsrfToken()
                            .then(function (response) {
                                var csrfProtection = response.data;
                                // from now on a CSRF token will be added to all HTTP requests
                                $http.defaults.headers.common[csrfProtection.headerName] = csrfProtection.token;
                                currentCsrfProtection.set(csrfProtection.headerName, csrfProtection.token);
                                return csrfProtection;
                            }, function () {
                                return $q.reject('Requesting a CSRF token failed');
                            });
                    };

                return {
                    logIn: function (username, password) {
                        var logInDeferred = $q.defer();
                        currentUserProfileHandler.initializationStarts();
                        getSecurityRestService().login(username, password)
                            .then(function () {
                                $q.all([
                                    getSecurityRestService().getCurrentUser(),
                                    enableCsrfProtection()
                                ]).then(function (allResults) {
                                    var userProfile = allResults[0].data;
                                    currentUserProfileHandler.initializationSucceeded(userProfile);
                                    getAppContextService().onLoggingIn(userProfile);
                                    logInDeferred.resolve();
                                }, function (reject) {
                                    currentUserProfileHandler.initializationFailed();
                                    logInDeferred.reject(reject);
                                });
                            }, function () {
                                currentUserProfileHandler.initializationFailed();
                                logInDeferred.reject('Authentication failed');
                            });
                        return logInDeferred.promise;
                    },
                    logOff: function () {
                        return getSecurityRestService().logout()
                            .then(function () {
                                currentCsrfProtection.invalidate();
                                currentUserProfileHandler.userLoggedOff();
                                getAppContextService().onLoggingOff();
                            });
                    },
                    checkIfUserIsLoggedInAndIfSoReinitializeAppContext: function () {
                        currentUserProfileHandler.initializationStarts();
                        getSecurityRestService().getCurrentUser()
                            .then(function (response) {
                                var userProfile = response.data;
                                enableCsrfProtection().then(function () {
                                    currentUserProfileHandler.initializationSucceeded(userProfile);
                                    getAppContextService().onLoggingIn(userProfile);
                                }, function () {
                                    currentUserProfileHandler.initializationFailed();
                                });
                            }, function () {
                                currentUserProfileHandler.initializationFailed();
                            });
                    },
                    getCurrentCsrfToken: function () {
                        return currentCsrfProtectionWrapper;
                    },
                    getCurrentUserProfile: function () {
                        return currentUserProfileHandler.getProfile();
                    }
                };
            }
        };
    });