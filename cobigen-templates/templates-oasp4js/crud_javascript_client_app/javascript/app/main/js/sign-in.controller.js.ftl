angular.module('app.main')
    .controller('SignInCntl', function ($scope, $location, appContext, signIn) {
        'use strict';
        signIn($scope, function () {
            appContext.getCurrentUser().then(function (currentUser) {
                $location.url(currentUser.getHomeDialogPath());
            });
        });
    });