angular.module('app.main').factory('homePageRedirector', function ($location, appContext, $q) {
    'use strict';

    return {
        rejectAndRedirectToHomePageIfUserLoggedIn: function () {
            var deferredCheck = $q.defer();

            appContext.getCurrentUser().then(function (currentUser) {
                var homeDialogPath;
                if (currentUser.isLoggedIn()) {
                    deferredCheck.reject();
                    homeDialogPath = currentUser.getHomeDialogPath();
                    $location.url(homeDialogPath);
                } else {
                    deferredCheck.resolve();
                }
            });

            return deferredCheck.promise;
        }
    };
});