angular.module('app.main')
    .controller('SignInModalCntl', function ($scope, signIn) {
        'use strict';
        signIn($scope, function () {
            $scope.$close();
        });
    });
