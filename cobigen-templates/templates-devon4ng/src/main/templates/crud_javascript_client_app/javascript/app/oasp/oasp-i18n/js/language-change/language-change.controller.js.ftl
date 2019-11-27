angular.module('oasp.oaspI18n').controller('LanguageChangeCntl', function ($scope, $translate, oaspTranslation) {
    'use strict';
    $scope.supportedLanguages = oaspTranslation.getSupportedLanguages();

    $scope.changeLanguage = function (lang) {
        $translate.use(lang);
    };
    $scope.getCurrentLanguage = function () {
        return $translate.use();
    };
});