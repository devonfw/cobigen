describe('Controller: LanguageChangeCntl', function () {
    'use strict';
    var $scope;

    beforeEach(module('oasp.oaspI18n'));

    beforeEach(inject(function ($rootScope, $controller) {
        //given
        $scope = $rootScope.$new();
        $controller('LanguageChangeCntl', {$scope: $scope});
    }));
    it('should set api', function () {
        expect($scope.changeLanguage).toBeDefined();
        expect($scope.getCurrentLanguage).toBeDefined();
    });

    it('should call $translate.use with the lang parameter when $scope.changeLanguage is called', inject(function ($translate) {
        //given
        var lang = 'en';
        spyOn($translate, 'use');
        //when
        $scope.changeLanguage(lang);
        //then
        expect($translate.use).toHaveBeenCalledWith(lang);
    }));

    it('should call $translate.use when $scope.getCurrentLanguage  is called', inject(function ($translate) {
        //given
        spyOn($translate, 'use');
        //when
        $scope.getCurrentLanguage();
        //then
        expect($translate.use).toHaveBeenCalledWith();
    }));
});