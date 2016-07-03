angular.module('oasp.oaspI18n').service('templateLoadTranslationInterceptor', function ($rootScope, oaspTranslation) {
    'use strict';
    var regexp = new RegExp('/?([^/]+)/html/');
    return {
        'request': function (config) {
            if (config.url) {
                var matches = regexp.exec(config.url);
                if (matches && matches.length > 1 && oaspTranslation.moduleHasTranslations(matches[1])) {
                    $rootScope.$emit('translationPartChange', matches[1]);
                }
            }
            return config;
        }
    };
});
