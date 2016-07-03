describe('Module: \'app.main\', RedirectorCntl', function () {
    'use strict';
    var appContext, $scope,
        user = {
            loggedIn: false,
            homeDialogPath: '',

            isLoggedIn: function () {
                this.loggedIn = true;
                return this;
            },
            isAnonymous: function () {
                this.loggedIn = false;
            },
            andHisHomeDialogIs: function (path) {
                this.homeDialogPath = path;
            }
        };

    beforeEach(function () {
        module('app.main');
    });

    beforeEach(inject(function ($rootScope, $q) {
        appContext = {
            getCurrentUser: function () {
                return $q.when({
                    getHomeDialogPath: function () {
                        return user.homeDialogPath;
                    },
                    isLoggedIn: function () {
                        return user.loggedIn;
                    }
                });
            }
        };
        $scope = $rootScope;

    }));

    it('redirects to the user\'s home dialog if the user is logged in', inject(function ($controller, $location) {
        // given
        var homeDialogPath = '/userHomePath';

        $location.url('/currentPath');
        user.isLoggedIn()
            .andHisHomeDialogIs(homeDialogPath);
        // when
        $controller('RedirectorCntl', {appContext: appContext});
        $scope.$apply();
        // then
        expect($location.url()).toBe(homeDialogPath);
    }));

    it('redirects to the \'Sign In\' dialog if the user is anonymous', inject(function ($controller, $location, SIGN_IN_DLG_PATH) {
        // given
        $location.url('/currentPath');
        user.isAnonymous();
        // when
        $controller('RedirectorCntl', {appContext: appContext});
        $scope.$apply();
        // then
        expect($location.url()).toBe(SIGN_IN_DLG_PATH);
    }));
});
