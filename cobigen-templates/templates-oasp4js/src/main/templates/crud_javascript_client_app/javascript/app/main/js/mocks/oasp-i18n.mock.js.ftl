angular.module('oasp.oaspI18n').factory('$translatePartialLoader', function ($q) {
    'use strict';
    var service = function () {
        return $q.when({});
    };
    service.addPart = angular.noop;
    return service;
});