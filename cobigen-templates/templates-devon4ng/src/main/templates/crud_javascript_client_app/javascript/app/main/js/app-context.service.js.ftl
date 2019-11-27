angular.module('app.main')
    .factory('appContext', function (oaspSecurityService, $q) {
        'use strict';
        var currentUserInternal = {
                isLoggedIn: false
            },
            currentUserExternal = function (currentUser) {
                return {
                    isLoggedIn: function () {
                        return currentUser.isLoggedIn;
                    },
                    getUserName: function () {
                        var userName = '';
                        if (currentUser.profile && currentUser.profile.firstName && currentUser.profile.lastName) {
                            userName = currentUser.profile.firstName + ' ' + currentUser.profile.lastName;
                        }
                        return userName;
                    },
                    getUserId: function () {
                        return currentUser.profile && currentUser.profile.id;
                    },
                    getHomeDialogPath: function () {
                        return (currentUser.profile && currentUser.profile.homeDialogPath) || '';
                    }
                };
            },
            updateUserProfile = function (userProfile) {
                currentUserInternal.isLoggedIn = true;
                currentUserInternal.profile = userProfile;
                // TODO remove it once implemented on the server
                if (angular.isUndefined(userProfile.homeDialogPath)) {
                        // TODO: add rest (+ default?) roles and dialogs
                        currentUserInternal.profile.homeDialogPath = '/main/welcome';
//                    }
                }
            },
            switchToAnonymousUser = function () {
                currentUserInternal.isLoggedIn = false;
                currentUserInternal.profile = undefined;
            };

        return {
            getCurrentUser: function () {
                var deferred = $q.defer();
                oaspSecurityService.getCurrentUserProfile()
                    .then(function (userProfile) {
                        if (userProfile) {
                            updateUserProfile(userProfile);
                        } else {
                            switchToAnonymousUser();
                        }
                        deferred.resolve(currentUserExternal(currentUserInternal));
                    });
                return deferred.promise;
            },
            onLoggingIn: function (userProfile) {
                updateUserProfile(userProfile);
            },
            onLoggingOff: function () {
                switchToAnonymousUser();
            }
        };
    });
