describe('Module: \'app.main\', Service: \'homePageRedirector\'', function () {
    'use strict';
    var appContext, currentUserPromise, $scope, successCallback, failureCallback, homePageRedirector,
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
        appContext = {
            getCurrentUser: function () {
                return currentUserPromise;
            }
        };
        module('app.main', function ($provide) {
            $provide.value('appContext', appContext);
        });
    });

    beforeEach(inject(function ($rootScope, $q, _homePageRedirector_) {
        currentUserPromise = $q.when({
            getHomeDialogPath: function () {
                return user.homeDialogPath;
            },
            isLoggedIn: function () {
                return user.loggedIn;
            }
        });
        $scope = $rootScope;
        homePageRedirector = _homePageRedirector_;
        successCallback = jasmine.createSpy('success');
        failureCallback = jasmine.createSpy('failure');
    }));

    it('rejects and redirects to the user\'s home dialog if the user is logged in', inject(function ($location) {
        // given
        var homeDialogPath = '/userHomePath';

        $location.url('/currentPath');
        user.isLoggedIn()
            .andHisHomeDialogIs(homeDialogPath);
        // when
        homePageRedirector.rejectAndRedirectToHomePageIfUserLoggedIn().then(successCallback, failureCallback);
        $scope.$apply();
        // then
        expect(successCallback).not.toHaveBeenCalled();
        expect(failureCallback).toHaveBeenCalled();
        expect($location.url()).toBe(homeDialogPath);
    }));

    it('resolves if the user is anonymous', function () {
        // given
        user.isAnonymous();
        // when
        homePageRedirector.rejectAndRedirectToHomePageIfUserLoggedIn().then(successCallback, failureCallback);
        $scope.$apply();
        // then
        expect(successCallback).toHaveBeenCalled();
        expect(failureCallback).not.toHaveBeenCalled();
    });
});
