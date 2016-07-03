angular.module('app.main')
    .factory('authenticator', function ($modal) {
        'use strict';

        return {
            execute: function () {
                return $modal.open({
                    templateUrl: 'main/html/sign-in-modal.html',
                    backdrop: 'static',
                    keyboard: false,
                    controller: 'SignInModalCntl',
                    size: 'sm'
                }).result;
            }
        };
    });
