angular.module('oasp.oaspUi.spinner')
    .factory('globalSpinner', function ($rootScope, $q) {
        'use strict';
        var that = {};
        that.show = function () {
            $rootScope.globalSpinner = true;
        };
        that.hide = function () {
            $rootScope.globalSpinner = false;
        };
        that.showOnRouteChangeStartAndHideWhenComplete = function () {
            /*jslint unparam: true*/
            $rootScope.$on('$routeChangeStart', function (event, currentRoute) {
                if (currentRoute.resolve) {
                    that.show();
                }
            });
            /*jslint unparam: false*/
            $rootScope.$on('$routeChangeSuccess', function () {
                that.hide();
            });
            $rootScope.$on('$routeChangeError', function () {
                that.hide();
            });
        };
        that.decorateCallOfFunctionReturningPromise = function (fn) {
            that.show();
            return fn().then(function (value) {
                that.hide();
                return value;
            }, function (value) {
                that.hide();
                return $q.reject(value);
            });
        };

        return that;
    });